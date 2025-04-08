package JDBC;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCUtils implements AutoCloseable {
    private static final Logger logger = LogManager.getLogger(JDBCUtils.class);
    private Connection connection;
    private static String url;

    public JDBCUtils() {
        try {
            Properties prop = new Properties(System.getProperties());
            prop.load(JDBCUtils.class.getResourceAsStream("/server.properties"));
            System.setProperties(prop);
            url = System.getProperty("jdbc.url");
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            logger.error("Error connecting to database: {}", e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getNewConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);

        } catch (SQLException e) {
            logger.error("Error creating new database connection: {}", e.getMessage(), e);
        }
        return conn;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {

                connection = getNewConnection();
            }
        } catch (SQLException e) {
            logger.error("Error getting database connection: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();

            } catch (SQLException e) {
                logger.error("Error closing database connection: {}", e.getMessage(), e);
            }
        }
    }

    @Override
    public void close() {
        closeConnection();
    }
}
