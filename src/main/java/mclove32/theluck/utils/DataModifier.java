package mclove32.theluck.utils;

import mclove32.theluck.config.ReadMythicConfig;
import mclove32.theluck.database.DBDrops;
import mclove32.theluck.database.DBDropsAll;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class DataModifier {

    public static synchronized void dataPut(ReadMythicConfig config, String mobID, String item, ItemStack stack) {

        for (Map<String, Map<String, Long>> maps : DBDrops.dataGeneral) {

            if (!maps.containsKey(mobID)) continue;
            if (!maps.get(mobID).containsKey(item)) continue;

            long l = stack.getAmount() + maps.get(mobID).get(item);
            DataModifier.datePut(DBDrops.dataGeneral, maps, mobID, item, l);
            break;
        }

        for (Map<String, Map<String, Long>> maps : DBDropsAll.dataAllGeneral) {

            if (!maps.containsKey(mobID)) continue;
            if (!maps.get(mobID).containsKey(item)) continue;

            long l = stack.getAmount() + maps.get(mobID).get(item);
            DataModifier.datePut(DBDropsAll.dataAllGeneral, maps, mobID, item, l);
            break;
        }

        for (Map<String, Map<String, Long>> maps : DBDrops.dataExpect) {

            if (!maps.containsKey(mobID)) continue;
            if (!maps.get(mobID).containsKey(item)) continue;

            long l = config.getExpect(item);
            DataModifier.datePut(DBDrops.dataExpect, maps, mobID, item, l);
            break;
        }
    }

    public static void datePut(@NotNull List<Map<String, Map<String, Long>>> list, Map<String, Map<String, Long>> maps, String mobID, String item, long l) {
        list.remove(maps);
        list.add(Map.of(mobID, Map.of(item, l)));
    }

    public static synchronized void sendMessage(@NotNull CommandSender sender, @NotNull ReadMythicConfig config, String mmid, String item, double d) {

        sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("ItemMMID: &7" + item + " &bレアリティ&f: &e&l" + config.getType(item).toString()));
        sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("ドロップ確率: &a&l" + d + "%"));
        if (sender.hasPermission("TheLuck.command.theLuck.more")) {

            long amount = 0;
            long all = 0;

            for (Map<String, Map<String, Long>> map : DBDrops.dataGeneral) {
                if (!map.containsKey(mmid)) continue;
                if (!map.get(mmid).containsKey(item)) continue;
                amount = map.get(mmid).get(item);
            }

            for (Map<String, Map<String, Long>> map : DBDropsAll.dataAllGeneral) {
                if (!map.containsKey(mmid)) continue;
                if (!map.get(mmid).containsKey(item)) continue;
                all = map.get(mmid).get(item);
            }
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("過去1カ月の総ドロップ数: &c" + amount + "&f 総ドロップ数: &5" + all));
        }
        sender.sendMessage(Component.text(" "));
    }
}
