package mclove32.theluck.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import mclove32.theluck.TheLuck;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBCon {

    private final JavaPlugin plugin;
    protected static HikariDataSource dataSource;
    protected static String luckTable = TheLuck.inst().getConfig().getString("Database.luckTable");
    protected static String luckAllTable = TheLuck.inst().getConfig().getString("Database.luckAllTable");

    public DBCon(JavaPlugin plugin) {
        this.plugin = plugin;
        if (dataSource == null) {

            openConnection(new HikariConfig());
            try {
                try (Connection con = dataSource.getConnection()) {
                    try (Statement state = con.createStatement()) {

                        ResultSet set1 = state.executeQuery("SHOW TABLES LIKE '" + luckTable + "'");
                        if (!set1.next())
                            state.executeUpdate("CREATE TABLE IF NOT EXISTS " + luckTable + " (mmidMob VARCHAR(100) NOT NULL, mmidItem VARCHAR(100) NOT NULL, day TINYINT NOT NULL, currentDrop BIGINT NOT NULL DEFAULT 0, expectDrop BIGINT NOT NULL DEFAULT 1000, PRIMARY KEY (mmidMob, mmidItem, day))");

                        ResultSet set2 = state.executeQuery("SHOW TABLES LIKE '" + luckAllTable + "'");
                        if (!set2.next()) {
                            state.executeUpdate("CREATE TABLE IF NOT EXISTS " + luckAllTable + " (mmidMob VARCHAR(100) NOT NULL, mmidItem VARCHAR(100) NOT NULL, currentDrop BIGINT NOT NULL DEFAULT 0, PRIMARY KEY (mmidMob, mmidItem))");
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void openConnection(@NotNull HikariConfig config) {

        String host = plugin.getConfig().getString("Database.host");
        int port = plugin.getConfig().getInt("Database.port");
        String database = plugin.getConfig().getString("Database.database");
        String username = plugin.getConfig().getString("Database.username");
        String password = plugin.getConfig().getString("Database.password");

        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);

        dataSource = new HikariDataSource(config);
    }

    public static void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
