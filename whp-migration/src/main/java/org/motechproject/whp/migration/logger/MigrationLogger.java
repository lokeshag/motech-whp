package org.motechproject.whp.migration.logger;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class MigrationLogger {

    private Logger logger = Logger.getLogger(MigrationLogger.class);

    public void warn(String message) {
        logger.log(Level.WARN, message);
    }

    public void info(String message) {
        logger.log(Level.INFO, message);
    }

    public void error(String message) {
        logger.log(Level.ERROR, message);
    }

    public void error(Exception exception) {
        ByteArrayOutputStream stackTrace = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(stackTrace);
        exception.printStackTrace(printStream);
        printStream.flush();
    }
}
