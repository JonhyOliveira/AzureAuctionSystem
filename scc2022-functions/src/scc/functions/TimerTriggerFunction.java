package scc.functions;

import java.time.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with Timer trigger.
 * Schedule is: second minute hour ...
 */
public class TimerTriggerFunction {

    /**
     * This function will be invoked every minute on the 3rd second
     */
    @FunctionName("TimerTrigger-Java")
    public void run(
        @TimerTrigger(name = "timerInfo", schedule = "3 * * * * *") String timerInfo,
        final ExecutionContext context
    ) {
        context.getLogger().info("Java Timer '"+ timerInfo +"' trigger function executed at: " + LocalDateTime.now());
    }
}
