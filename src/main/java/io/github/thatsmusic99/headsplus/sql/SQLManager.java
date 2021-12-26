package io.github.thatsmusic99.headsplus.sql;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.MainConfig;

import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
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

    protected synchronized <T> CompletableFuture<T> createConnection(SQLFunction<T> run, boolean async, String action) {
        Supplier<T> runnable = () -> {
            try (Connection connection = implementConnection()) {
                HeadsPlus.get().getLogger().warning("AAAA");
                return run.applyWithSQL(connection);
            } catch (SQLException | ExecutionException ex) {
                HeadsPlus.get().getLogger().warning("Failed to " + action + " - an internal error occurred. " +
                        "Please report the below stacktrace and error to the developer.");
                ex.printStackTrace();
            } catch (InterruptedException ex) {
                HeadsPlus.get().getLogger().warning("Failed to " + action + " - interrupted thread. Please try again or restart the server. " +
                        "If none of the above works, please consult the necessary support services (e.g. hosting).");
            }
            return null;
        };
        if (async) {
            return CompletableFuture.supplyAsync(runnable, HeadsPlus.async).thenApplyAsync(result -> result, HeadsPlus.sync);
        } else {
            return CompletableFuture.completedFuture(runnable.get());
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

    public interface SQLSupplier<T> extends Supplier<T> {

        T getWithSQL() throws ExecutionException, InterruptedException;

        default T get() {
            throw new UnsupportedOperationException("Get outta here with that crap!");
        }
    }

    interface SQLFunction<T> extends Function<java.sql.Connection, T> {

        T applyWithSQL(Connection connection) throws SQLException, ExecutionException, InterruptedException;

        default T apply(Connection connection) {
            throw new UnsupportedOperationException("Get outta here with that crap!");
        }
    }
}
