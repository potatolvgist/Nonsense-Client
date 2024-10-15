package net.minecraft.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.logging.log4j.Logger;

public class Util {
    public static Util.EnumOS getOSType() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
            return EnumOS.WINDOWS;
        }

        if (osName.contains("mac")) {
            return EnumOS.OSX;
        }

        if (osName.contains("solaris") || osName.contains("sunos")) {
            return EnumOS.SOLARIS;
        }

        if (osName.contains("linux") || osName.contains("unix")) {
            return EnumOS.LINUX;
        }

        return EnumOS.UNKNOWN;
    }

    public static <V> V runTask(FutureTask<V> task, Logger logger) {
        try {
            task.run();
            return task.get();
        } catch (ExecutionException executionexception) {
            logger.fatal("Error executing task", executionexception);

            if (executionexception.getCause() instanceof OutOfMemoryError) {
                throw (OutOfMemoryError) executionexception.getCause();
            }
        } catch (InterruptedException interruptedexception) {
            logger.fatal("Error executing task", interruptedexception);
        }

        return null;
    }

    public enum EnumOS {
        LINUX,
        SOLARIS,
        WINDOWS,
        OSX,
        UNKNOWN
    }
}
