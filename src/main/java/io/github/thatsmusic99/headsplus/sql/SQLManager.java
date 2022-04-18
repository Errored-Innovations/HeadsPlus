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

    public static void setupSQL() {
        createConnection(connection -> {
            HeadsPlus.debug("Setting up PlayerSQLManager");
            new PlayerSQLManager(connection);
            HeadsPlus.debug("Setting up ChallengeSQLManager");
            new ChallengeSQLManager(connection);
            HeadsPlus.debug("Setting up FavouriteHeadsSQLManager");
            new FavouriteHeadsSQLManager(connection);
            HeadsPlus.debug("Setting up PinnedChallengeManager");
            new PinnedChallengeManager(connection);
            HeadsPlus.debug("Setting up StatisticsSQLManager");
            new StatisticsSQLManager(connection);
            return null;
        }, true, "setting up SQL Managers");
    }

    private static Connection loadSqlite() {
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

    public static Connection implementConnection() {
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

    protected static synchronized <T> CompletableFuture<T> createConnection(SQLFunction<T> run, boolean async,
                                                                            String action) {
        // The runnable to be processed by CompletableFuture
        Supplier<T> runnable = () -> {
            // Create the connection to the database
            try (Connection connection = implementConnection()) {
                // Execute any necessary queries/updates with that connection
                return run.applyWithSQL(connection);
            } catch (SQLException | ExecutionException ex) { // Internal exception
                HeadsPlus.get().getLogger().warning("Failed to " + action + " - an internal error occurred. " +
                        "Please report the below stacktrace and error to the developer.");
                ex.printStackTrace();
            } catch (InterruptedException ex) { // The thread gets interrupted, especially if the server stops or
                // someone screws with threads
                HeadsPlus.get().getLogger().warning("Failed to " + action + " - interrupted thread. Please try again " +
                        "or restart the server. " +
                        "If none of the above works, please consult the necessary support services (e.g. hosting).");
            } catch (Exception ex) {
                HeadsPlus.get().getLogger().severe("Failed to " + action + " - generic issue that needs fixing by the developer.");
                ex.printStackTrace();
            }
            return null;
        };
        // If instructed to run async, run it async, otherwise do it without creating a new thread
        if (async) {
            return CompletableFuture.supplyAsync(runnable, HeadsPlus.async).thenApplyAsync(result -> result,
                    HeadsPlus.sync);
        } else {
            return CompletableFuture.completedFuture(runnable.get());
        }
    }

    public abstract void createTable(Connection connection) throws SQLException;

    public abstract void transferOldData(Connection connection) throws SQLException, ExecutionException,
            InterruptedException;

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
