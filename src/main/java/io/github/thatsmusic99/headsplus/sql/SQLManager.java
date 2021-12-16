package io.github.thatsmusic99.headsplus.sql;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.MainConfig;

import java.sql.*;
import java.util.function.Supplier;

public abstract class SQLManager {

    protected static String tablePrefix;
    protected static volatile boolean usingSqlite;

    public static void setupPrefix() {
        // TODO - table prefix
        tablePrefix = "headsplus";
        if (!tablePrefix.matches("^[_A-Za-z0-9]+$")) {
            HeadsPlus.get().getLogger().warning("Table prefix " + tablePrefix + " is not alphanumeric. Using " +
                    "headsplus...");
            tablePrefix = "headsplus";
        }
    }

    private Connection loadSqlite() {
        // Load JDBC
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + HeadsPlus.get().getDataFolder() +
                    "/data.db");
            usingSqlite = true;
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Connection implementConnection() {
        synchronized (this) {
            Connection connection;
            if (MainConfig.get().getMySQL().ENABLE_MYSQL) {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection("jdbc:mysql://"
                                    + MainConfig.get().getMySQL().MYSQL_HOST + ":"
                                    + MainConfig.get().getMySQL().MYSQL_PORT + "/"
                                    + MainConfig.get().getMySQL().MYSQL_DATABASE + "?useSSL=false&autoReconnect=true",
                            MainConfig.get().getMySQL().MYSQL_USERNAME,
                            MainConfig.get().getMySQL().MYSQL_PASSWORD);
                    usingSqlite = false;
                    return connection;
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                    connection = loadSqlite();
                }
            } else {
                connection = loadSqlite();
            }
            return connection;
        }
    }

    public abstract void createTable();

    public abstract void transferOldData();

    public static String getTablePrefix() {
        return tablePrefix;
    }

    public String getStupidAutoIncrementThing() {
        return usingSqlite ? "AUTOINCREMENT" : "AUTO_INCREMENT";
    }

    protected ResultSet executeQuery(PreparedStatement statement) throws SQLException {
        return syncDatabaseOperation(statement::executeQuery);
    }

    protected void executeUpdate(PreparedStatement statement) throws SQLException {
        syncDatabaseOperation(() -> {
            statement.executeUpdate();
            return null;
        });
    }

    protected synchronized ResultSet syncDatabaseOperation(SQLSupplier<ResultSet> supplier) throws SQLException {
        return supplier.getWithSQL();
    }

    private interface SQLSupplier<T> extends Supplier<T> {

        ResultSet getWithSQL() throws SQLException;

        default T get() {
            throw new UnsupportedOperationException("Get outta here with that crap!");
        }
    }
}
