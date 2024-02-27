package mclove32.theluck.listener;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import mclove32.theluck.TheLuck;
import mclove32.theluck.config.ReadMythicConfig;
import mclove32.theluck.database.DBDrops;
import mclove32.theluck.database.DBDropsAll;
import mclove32.theluck.utils.ChanceUtil;
import mclove32.theluck.utils.DataModifier;
import mclove32.theluck.utils.DropUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MythicDeathListener implements Listener {

    private final TheLuck luck;

    public MythicDeathListener(TheLuck luck) {
        this.luck = luck;
    }


    @EventHandler
    public void onDeath(@NotNull MythicMobDeathEvent e) {

        ActiveMob mob = e.getMob();
        MythicMob mm = e.getMobType();

        ReadMythicConfig config = new ReadMythicConfig(mm.getConfig(), mm.getInternalName());
        if (!(config.isEnabledLuck())) return;

        List<String> mmidS = config.getMMIDs();
        if (mmidS == null || mmidS.isEmpty()) return;

        String mobID = mm.getInternalName();
        load(mobID, mmidS);


        Location loc = mob.getEntity().getBukkitEntity().getLocation();
        List<Player> playerList = new ArrayList<>();

        if (mob.hasThreatTable()) {
            ActiveMob.ThreatTable table = mob.getThreatTable();
            for (AbstractEntity ab : table.getAllThreatTargets()) {

                if (!ab.isPlayer()) continue;
                Player p = BukkitAdapter.adapt(ab.asPlayer());
                playerList.add(p);
            }

        } else {
            AbstractEntity ab = mob.getEntity().getTarget();
            if (ab == null) return;
            if (ab.isPlayer()) {
                Player p = BukkitAdapter.adapt(ab.asPlayer());
                playerList.add(p);
            }
        }

        Bukkit.getScheduler().runTaskLater(luck, ()-> {

            for (Player p : playerList) {
                for (String item : mmidS) {

                    double chance = ChanceUtil.getChance(config.getType(item), mobID, item);
                    ItemStack stack = ChanceUtil.chance(item, config.getAmount(item), chance);
                    if (stack == null) continue;

                    DropUtil.drop(p, stack, loc);
                    Bukkit.getScheduler().runTaskAsynchronously(luck, ()-> DataModifier.dataPut(config, mobID, item, stack));

                    double m = chance * 100;
                    p.sendMessage(MiniMessage.miniMessage().deserialize("<rainbow><bold>[TheLuck]</bold></rainbow> ")
                            .append(stack.displayName()).append(Component.text(" ×" + stack.getAmount(), NamedTextColor.GRAY)
                                    .append(Component.text("を獲得しました。", NamedTextColor.WHITE))
                                    .hoverEvent(HoverEvent.showText(Component.text("ドロップ率 " + m + "%")))));
                }
            }
        }, 5L);
    }

    public void load(String mmid, List<String> items) {


        if (!DBDrops.dataGeneral.isEmpty()) {
            for (Map<String, Map<String, Long>> map : DBDrops.dataGeneral) {
                if (map.containsKey(mmid)) return;
            }
        }
        Bukkit.getScheduler().runTaskAsynchronously(luck, () -> {

            new DBDrops(luck).loadData(mmid, items);
            new DBDropsAll(luck).loadData(mmid, items);
        });
    }
}
