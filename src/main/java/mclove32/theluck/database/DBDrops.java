package mclove32.theluck.database;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DBDrops extends DBCon {

    public DBDrops(JavaPlugin plugin) {
        super(plugin);
    }

    public static final List<Map<String, Map<String, Long>>> dataGeneral = new ArrayList<>();
    public static final List<Map<String, Map<String, Long>>> dataExpect = new ArrayList<>();

    public int saveData(String mob, String item, long amount, long expect, int count) {

        LocalDateTime time = LocalDateTime.now();
        try {
            try (Connection con = dataSource.getConnection()) {
                try (PreparedStatement state = con.prepareStatement("INSERT INTO " + luckTable + " (mmidMob, mmidItem, day, currentDrop, expectDrop) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE currentDrop = ?, expectDrop = ?;")) {

                    state.setString(1, mob);
                    state.setString(2, item);
                    state.setInt(3, time.getDayOfMonth());
                    state.setLong(4, amount);
                    state.setLong(5, expect);
                    state.setLong(6, amount);
                    state.setLong(7, expect);
                    state.executeUpdate();

                    count++;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return count;
    }

    public void saveData(String mob, String item, long amount, long expect) {

        LocalDateTime time = LocalDateTime.now();
        try {
            try (Connection con = dataSource.getConnection()) {
                try (PreparedStatement state = con.prepareStatement("INSERT INTO " + luckTable + " (mmidMob, mmidItem, day, currentDrop, expectDrop) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE currentDrop = ?, expectDrop = ?;")) {

                    state.setString(1, mob);
                    state.setString(2, item);
                    state.setInt(3, time.getDayOfMonth());
                    state.setLong(4, amount);
                    state.setLong(5, expect);
                    state.setLong(6, amount);
                    state.setLong(7, expect);
                    state.executeUpdate();

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadData(String mob, @NotNull List<String> items) {

        LocalDateTime time = LocalDateTime.now();
        try {
            try (Connection con = dataSource.getConnection()) {
                for (String s : items) {

                    try (PreparedStatement state = con.prepareStatement("SELECT currentDrop, expectDrop FROM " + luckTable + " WHERE mmidMob = ? AND mmidItem = ? AND day = ?")) {

                        state.setString(1, mob);
                        state.setString(2, s);
                        state.setInt(3, time.getDayOfMonth());
                        ResultSet set = state.executeQuery();

                        long c = 0;
                        long e = 1000;
                        if (set.next()) {
                            c = set.getLong("currentDrop");
                            e = set.getLong("expectDrop");
                        }
                        dataGeneral.add(Map.of(mob, Map.of(s, c)));
                        dataExpect.add(Map.of(mob, Map.of(s, e)));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //起動時にのみやる
    public void removeData() {

        LocalDateTime time = LocalDateTime.now();
        LocalDateTime past = time.minusDays(28);

        try {
            try (Connection con = dataSource.getConnection()) {

                //あるかcheck
                try (PreparedStatement state = con.prepareStatement("SELECT mmidMob FROM " + luckTable + " WHERE day = ?")) {

                    state.setInt(1, past.getDayOfMonth());
                    ResultSet set = state.executeQuery();

                    if (!set.next()) return;
                }

                //ある場合は継続処理 delete
                try (PreparedStatement state = con.prepareStatement("DELETE FROM " + luckTable + " WHERE day = ?;")) {

                    state.setInt(1, past.getDayOfMonth());
                    state.executeUpdate();

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
