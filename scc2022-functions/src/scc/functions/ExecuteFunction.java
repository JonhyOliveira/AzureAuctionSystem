package scc.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExecuteFunction {

    private static Logger logger = Logger.getLogger(ExecuteFunction.class.getName());

    /**
     * Runs one of the functions in the current package
     * @param args something like [function name]
     */
    public static void main(String[] args) {

        // find every TimerTrigger method in this package
        Map<String, Method> functions = new Reflections(ExecuteFunction.class.getPackage().getName(), new SubTypesScanner(false))
                .getSubTypesOf(Object.class)
                .stream()
                .filter(aClass -> !aClass.equals(ExecuteFunction.class))
                .flatMap(aClass -> Arrays.stream(aClass.getMethods()))
                .filter(method -> method.isAnnotationPresent(FunctionName.class))
                .filter(method -> Arrays.stream(method.getParameters())
                        .anyMatch(parameter -> parameter.isAnnotationPresent(TimerTrigger.class)))
                .collect(Collectors.toMap(method -> method.getAnnotation(FunctionName.class).value(), method -> method));

        if (args.length != 1 || !functions.containsKey(args[0])) {
            System.out.println("Incorrect argument. Provided argument should be one of " + functions.keySet() + ".");
            /* show found functions details *
            System.out.println("Available timer triggered functions:\n"
                    + String.format("%20s | %-70s | %15s\n", "Function Name", "Path", "Schedule")
                    + String.format("%20s | %-70s | %15s\n", "", "", "")
                    + functions.entrySet().stream()
                    .map(stringMethodEntry -> String.format("%20s | %-70s | %15s", stringMethodEntry.getKey(),
                            stringMethodEntry.getValue().getDeclaringClass().getName() + "." + stringMethodEntry.getValue().getName(),
                            Arrays.stream(stringMethodEntry.getValue().getParameterAnnotations())
                                    .flatMap(Arrays::stream)
                                    .map(annotation -> {
                                        if (annotation.annotationType().equals(TimerTrigger.class)) {
                                            return ((TimerTrigger) annotation).schedule();
                                        }
                                        else
                                            return null;
                                    })
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.joining())))
                    .collect(Collectors.joining("\n"))); /* */
            System.exit(1);
        }

        String functionName = args[0];
        Method toInvoke = functions.get(functionName);

        try {
            logger.info(String.format("Executiong %s..", functionName));
            toInvoke.invoke(toInvoke.getDeclaringClass().getConstructor().newInstance(), null, new FakeExecutionContext(functionName));
            logger.info("Done.");
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
            logger.log(Level.SEVERE, "Error executing function.", e);
        }

    }

    private static class FakeExecutionContext implements ExecutionContext {

        private final String functionName;

        public FakeExecutionContext(String functionName) {
            this.functionName = functionName;
        }

        @Override
        public Logger getLogger() {
            return Logger.getLogger(functionName);
        }

        @Override
        public String getInvocationId() {
            return null;
        }

        @Override
        public String getFunctionName() {
            return functionName;
        }
    }

}
