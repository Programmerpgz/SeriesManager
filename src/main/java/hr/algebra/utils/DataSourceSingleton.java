package hr.algebra.utils;

import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceSingleton {
    private static final Logger log = LoggerFactory.getLogger(DataSourceSingleton.class);
    private static DataSource instance;
    private static AppConfig appConfig;
    private DataSourceSingleton() {}
    public static DataSource getInstance() {
        if (instance == null) {
            AppConfig config = getAppConfig();
            if (config == null) {
                throw new IllegalStateException("Configuration could not be loaded!");
            }
            PGSimpleDataSource ds = new PGSimpleDataSource();
            ds.setUrl(config.getDbUrl());

            String dbUser = System.getenv("DB_USER");
            String dbPassword = System.getenv("DB_PASSWORD");

            if (dbUser == null || dbPassword == null) {
                throw new IllegalStateException("Error: Environment variables DB_USER or DB_PASSWORD are not set!");
            }

            ds.setUser(dbUser);
            ds.setPassword(dbPassword);
            instance = ds;
            log.info("Database DataSource successfully initialized.");
            }
        return instance;
    }
    public static Connection getConnection() throws SQLException {
        return getInstance().getConnection();
    }
    public static void closeConnection() {
        // treba
    }
    public static AppConfig getAppConfig() {
        if (appConfig == null) {
            try {
                appConfig = XmlUtils.loadFromXml("config.xml", AppConfig.class);
            } catch (IOException e) {
               log.error("Error loading config.xml: {}", e.getMessage());
            }
        }
        return appConfig;
    }
}
