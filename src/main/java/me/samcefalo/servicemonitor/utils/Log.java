package me.samcefalo.servicemonitor.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
    private static Logger logger = Logger.getGlobal();

    public static void info(String s) {
        logger.info(s);
    }

    public static void warn(String s) {
        logger.warning(s);
    }

    public static void severe(String s) {
        logger.severe(s);
    }

    public static void warn(String s, Throwable t) {
        logger.log(Level.WARNING, s, t);
    }

    public static void severe(String s, Throwable t) {
        logger.log(Level.SEVERE, s, t);
    }
}
