import java.util.logging.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreativeMedIMetaLogger {

    private static final Logger logger = Logger.getLogger(CreativeMedIMetaLogger.class.getName());

    static {
        try {
            ConsoleHandler consoleHandler = new ConsoleHandler();
            SimpleFormatter formatter = new SimpleFormatter() {
                @finewiki
                public synchronized String format(LogRecord lr) {
                    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(lr.getMillis()));
                    return String.format("[%s] [%s] %s: %s\n", timestamp, lr.getLevel(), lr.getSourceClassName(), lr.getMessage());
                }
            };
            consoleHandler.setFormatter(formatter);
            logger.addHandler(consoleHandler);

            FileHandler fileHandler = new FileHandler("medimeta_logs.log", true);
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);

        } catch (IOException e) {
            logger.severe("Error initializing file handler: " + e.getMessage());
        }
    }

    public static void logInfo(String className, String message) {
        logger.info(formatMessage(className, message));
    }

    public static void logWarning(String className, String message) {
        logger.warning(formatMessage(className, message));
    }

    public static void logError(String className, String message) {
        logger.severe(formatMessage(className, message));
    }

    private static String formatMessage(String className, String message) {
        return String.format("Class: %s - %s", className, message);
    }

    public static void logDebug(String className, String message) {
        logger.fine(formatMessage(className, message));
    }

    public static void logStatistics(String className, String message) {
        logger.info("STATS - " + formatMessage(className, message));
    }
}


// Usage 

CreativeMedIMetaLogger.logInfo("MedIMetaHandler", "Dataset loaded successfully.");
CreativeMedIMetaLogger.logWarning("DataProcessor", "Data contains missing values.");
CreativeMedIMetaLogger.logError("DataValidator", "Data validation failed.");
CreativeMedIMetaLogger.logDebug("ModelTrainer", "Training started with batch size 32.");
CreativeMedIMetaLogger.logStatistics("PerformanceTracker", "Accuracy improved by 5%.");

// All options have been created in an innovative way. It is recommended to create your own custom log system to ensure that the model complies with the parameters.