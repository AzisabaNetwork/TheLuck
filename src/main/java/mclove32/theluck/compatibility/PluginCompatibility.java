package mclove32.theluck.compatibility;

import mclove32.theluck.TheLuck;
import mclove32.theluck.listener.MythicDeathListener;
import mclove32.theluck.utils.GenericType;
import org.bukkit.plugin.PluginManager;

public class PluginCompatibility {

    private final TheLuck luck;

    public PluginCompatibility(TheLuck luck) {
        this.luck = luck;
    }

    public void init(PluginManager pm) {

        GenericType<?> type = luck.hasPlugin("MythicMobs");
        if (type != null) {
            pm.registerEvents(new MythicDeathListener(luck), luck);

        }  else {
            luck.getLogger().warning("MythicMobsがない為、起動できませんでした。");
            pm.disablePlugin(luck);
        }

        GenericType<?> type2 = luck.hasPlugin("DBUtils");
        if (type2 == null) {
            luck.getLogger().warning("DBUtilsがない為、起動できませんでした。");
            pm.disablePlugin(luck);
        }
    }
}
