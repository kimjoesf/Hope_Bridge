package com.hopebridge.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * SINGLETON - the application settings.
 *
 * The settings file is read once and the whole app shares that same copy,
 * so there is no reason to ever have two of these objects around that is why it can be considered a singleton.
 */
public final class AppConfig {

    private static AppConfig instance;

    private final Properties properties = new Properties();

    // The constructor is private: nobody outside can say "new AppConfig()".
    private AppConfig() {
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in == null) {
                throw new IllegalStateException("application.properties was not found on the classpath");
            }
            properties.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read application.properties", e);
        }
    }

    // The global point of access. We only build the object the first time someone asks for it.
    // It is synchronized because Tomcat serves requests on many threads at once.
    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    public String get(String key) {
        return System.getProperty(key, properties.getProperty(key, "")).trim();
    }

    public int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
