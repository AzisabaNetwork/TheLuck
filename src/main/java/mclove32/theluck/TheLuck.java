package mclove32.theluck;

import io.lumine.mythic.bukkit.MythicBukkit;
import mclove32.dbutils.DBUtils;
import mclove32.theluck.commands.TheLuckCommand;
import mclove32.theluck.compatibility.PluginCompatibility;
import mclove32.theluck.database.DBCon;
import mclove32.theluck.database.DBDrops;
import mclove32.theluck.database.DBDropsAll;
import mclove32.theluck.utils.GenericType;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Random;

public final class TheLuck extends JavaPlugin {

    private static TheLuck theLuck;
    public static final Random ran = new Random();

    @Override
    public void onEnable() {
        // Plugin startup logic
        theLuck = this;
        saveDefaultConfig();

        long tick = getConfig().getLong("General.SaveDataTick", 72000);
        Bukkit.getScheduler().runTaskTimer(this, ()-> save(false), tick, tick);
        Bukkit.getScheduler().runTaskAsynchronously(this, ()-> new DBDrops(this).removeData());

        PluginManager pm = getServer().getPluginManager();
        new PluginCompatibility(this).init(pm);

        Objects.requireNonNull(getCommand("theLuck")).setExecutor(new TheLuckCommand(this));
    }

    public static TheLuck inst() {return theLuck;}

    public void save(boolean force) {

        if (force) {

            for (Map<String, Map<String, Long>> map : DBDropsAll.dataAllGeneral) {
                for (String mmid: map.keySet()) {
                    for (String item : map.get(mmid).keySet()) {
                        new DBDropsAll(this).saveData(mmid, item, map.get(mmid).get(item));
                    }
                }
            }

            int i = 0;
            int count = 0;
            for (Map<String, Map<String, Long>> map : DBDrops.dataGeneral) {
                for (String mmid : map.keySet()) {
                    for (String item : map.get(mmid).keySet()) {
                        i+= new DBDrops(this).saveData(mmid, item, map.get(mmid).get(item), DBDrops.dataExpect.get(count).get(mmid).get(item), i);
                    }
                }
                count++;
            }
            getLogger().info(i + "件のドロップ数の変更を保存しました。お疲れ様です。");

        } else {

            Bukkit.getScheduler().runTaskAsynchronously(this, ()-> {

                for (Map<String, Map<String, Long>> map : DBDropsAll.dataAllGeneral) {
                    for (String mmid: map.keySet()) {
                        for (String item : map.get(mmid).keySet()) {
                            new DBDropsAll(this).saveData(mmid, item, map.get(mmid).get(item));
                        }
                    }
                }

                int count = 0;
                for (Map<String, Map<String, Long>> map: DBDrops.dataGeneral) {
                    for (String mmid : map.keySet()) {
                        for (String item : map.get(mmid).keySet()) {

                            new DBDropsAll(this).saveData(mmid, item, map.get(mmid).get(item));
                            new DBDrops(this).saveData(mmid, item, map.get(mmid).get(item), DBDrops.dataExpect.get(count).get(mmid).get(item));
                        }
                    }
                    count++;
                }
            });
        }
    }

    @Override
    public void onDisable() {

        save(true);
        DBCon.close();
    }

    public @Nullable GenericType<?> hasPlugin(String pluginName) {

        PluginManager pm = getServer().getPluginManager();
        Plugin pl = pm.getPlugin(pluginName);

        if (pl instanceof MythicBukkit mm) {
            return new GenericType<>(mm);

        } else if (pl instanceof DBUtils dbUtils) {
            return new GenericType<>(dbUtils);
        }
        return null;
    }
}
