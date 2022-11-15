import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

public class BlobStoreTriggerFunction {
    @FunctionName("BlobStore-Java")
    public void run( @BlobTrigger(name = "blob", path = "samples-workitems/{name}", dataType = "binary") byte[] content,
                     @BindingName("name") String filename,
                     final ExecutionContext context) {
        /*
        context.getLogger().info("Java Blob trigger function executed.");
        context.getLogger().info("Name: " + name);
        context.getLogger().info("Size: " + content.length + " Bytes");
        */
        try(Jedis jedis = Redis)
    }
}
