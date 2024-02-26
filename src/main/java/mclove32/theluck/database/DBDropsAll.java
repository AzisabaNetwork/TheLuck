package mclove32.theluck.database;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DBDropsAll extends DBCon {

    public DBDropsAll(JavaPlugin plugin) {
        super(plugin);
    }

    public static final List<Map<String, Map<String, Long>>> dataAllGeneral = new ArrayList<>();

    public void saveData(String mob, String item, long amount) {

        try {
            try (Connection con = dataSource.getConnection()) {
                try (PreparedStatement state = con.prepareStatement("INSERT INTO " + luckAllTable + " (mmidMob, mmidItem, currentDrop) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE currentDrop = ?;")) {

                    state.setString(1, mob);
                    state.setString(2, item);
                    state.setLong(3, amount);
                    state.setLong(4, amount);
                    state.executeUpdate();

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadData(String mob, @NotNull List<String> items) {

        try {
            try (Connection con = dataSource.getConnection()) {
                for (String item : items) {

                    try (PreparedStatement state = con.prepareStatement("SELECT currentDrop FROM " + luckAllTable + " WHERE mmidMob = ? AND mmidItem = ?")) {

                        state.setString(1, mob);
                        state.setString(2, item);
                        ResultSet set = state.executeQuery();

                        long c = 0;
                        if (set.next()) {
                            c = set.getLong("currentDrop");
                        }
                        dataAllGeneral.add(Map.of(mob, Map.of(item, c)));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
