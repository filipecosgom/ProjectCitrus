package pt.uc.dei.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

/**
 * A wrapper around Log4j2 Logger that auto-injects client IP from MDC.
 */
public class IPLogger {

    private final Logger LOGGER;

    public static IPLogger getLogger(Class<?> clazz) {
        return new IPLogger(LogManager.getLogger(clazz));
    }

    private IPLogger(Logger logger) {
        this.LOGGER = logger;
    }

    private String enrichMessage(String msg) {
        String ip = ThreadContext.get("clientIP");
        return String.format("[IP: %s] - %s", ip != null ? ip : "unknown", msg);
    }

    public void info(String msg, Object... params) {
        LOGGER.info(enrichMessage(msg), params);
    }

    public void debug(String msg, Object... params) {
        LOGGER.debug(enrichMessage(msg), params);
    }

    public void warn(String msg, Object... params) {
        LOGGER.warn(enrichMessage(msg), params);
    }

    public void error(String msg, Object... params) {
        LOGGER.error(enrichMessage(msg), params);
    }

    public void trace(String msg, Object... params) {
        LOGGER.trace(enrichMessage(msg), params);
    }

    public void fatal(String msg, Object... params) {
        LOGGER.fatal(enrichMessage(msg), params);
    }
}