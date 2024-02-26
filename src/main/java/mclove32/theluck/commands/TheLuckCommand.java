package mclove32.theluck.commands;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import mclove32.theluck.TheLuck;
import mclove32.theluck.config.ReadMythicConfig;
import mclove32.theluck.database.DBDrops;
import mclove32.theluck.listener.MythicDeathListener;
import mclove32.theluck.utils.ChanceUtil;
import mclove32.theluck.utils.DataModifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TheLuckCommand implements TabExecutor {

    private final TheLuck luck;
    public TheLuckCommand(TheLuck luck) {
        this.luck = luck;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (commandSender instanceof Player p && p.hasPermission("TheLuck.command.theLuck")) {

            if (strings.length != 1) {
                p.sendMessage(Component.text("/theLuck <MobMMID>"));
                return true;
            }
            run(commandSender, strings[0]);

        } else if (!(commandSender instanceof Player)) {

            if (strings.length != 1) {
                commandSender.sendMessage(Component.text("theLuck <MobMMID>"));
                return true;
            }
            run(commandSender, strings[0]);
        }
        return false;
    }

    public void run(CommandSender sender, String mmid) {

        Optional<MythicMob> op = MythicBukkit.inst().getMobManager().getMythicMob(mmid);
        if (op.isEmpty()) {
            sender.sendMessage(Component.text(mmid + "という名前のMobは存在しません。", NamedTextColor.RED));
            return;
        }
        MythicMob mm = op.get();
        ReadMythicConfig config = new ReadMythicConfig(mm.getConfig(), mmid);
        if (!(config.isEnabledLuck())) return;

        List<String> mmidS = config.getMMIDs();
        if (mmidS == null || mmidS.isEmpty()) return;

        sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("Mob名: &7" + mm.getDisplayName().get() + "&f MMID: &7" + mmid));
        sender.sendMessage(Component.text(" "));

        new MythicDeathListener(luck).load(mmid, mmidS);
        for (String item : mmidS) {

            Bukkit.getScheduler().runTaskLaterAsynchronously(luck, ()-> {

                double d = ChanceUtil.getChance(config.getType(item), mmid, item) * 100;
                DataModifier.sendMessage(sender, config, mmid, item, d);
            }, 10);
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (commandSender instanceof Player p && p.hasPermission("TheLuck.command.theLuck")) {

            if (strings.length == 1 && !DBDrops.dataGeneral.isEmpty()) {
                List<String> list = new ArrayList<>();
                for (Map<String, Map<String, Long>> maps : DBDrops.dataGeneral) {
                    list.addAll(maps.keySet());
                }
                return list;
            }
        }
        return null;
    }
}
