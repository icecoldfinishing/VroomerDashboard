package connexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;
import java.util.Properties;
import java.io.InputStream;

public class Connexion {
    private static volatile Properties cfg;

    private static void loadConfigIfNeeded() {
        if (cfg != null) return;
        synchronized (Connexion.class) {
            if (cfg != null) return;
            Properties p = new Properties();
            // Charger depuis le classpath: /application.properties (copié depuis project/src/main/resources)
            try (InputStream is = Connexion.class.getClassLoader().getResourceAsStream("application.properties")) {
                if (is != null) {
                    p.load(is);
                }
            } catch (Exception ignored) {
            }
            cfg = p;
        }
    }

    private static String getProp(String key, String def) {
        loadConfigIfNeeded();
        String env = System.getenv(key.replace('.', '_').toUpperCase()); // ex: SPRING_DATASOURCE_URL
        if (env != null && !env.isBlank()) return env;
        if (cfg == null) return def;
        String v = cfg.getProperty(key);
        return (v == null || v.isBlank()) ? def : v.trim();
    }

    public static Connection connect() throws Exception {
        try {
            String url = getProp("spring.datasource.url", "jdbc:postgresql://localhost:5432/pg11");
            String user = getProp("spring.datasource.username", "postgres");
            String pass = getProp("spring.datasource.password", "postgres");
            String driver = getProp("spring.datasource.driver-class-name", "org.postgresql.Driver");

            if (driver != null && !driver.isBlank()) {
                Class.forName(driver);
            }

            return DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            throw e;
        }
    }
}