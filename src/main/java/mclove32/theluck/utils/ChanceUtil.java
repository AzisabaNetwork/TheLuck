package mclove32.theluck.utils;

import io.lumine.mythic.bukkit.MythicBukkit;
import mclove32.theluck.TheLuck;
import mclove32.theluck.database.DBDrops;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ChanceUtil {

    public static final double dropError = TheLuck.inst().getConfig().getDouble("Drops.DropErrorRange", 0.1);
    public static final double dropCorrection = TheLuck.inst().getConfig().getDouble("Drops.DropCorrection", 0.01);
    public static final int dropStack = TheLuck.inst().getConfig().getInt("Drops.DropStack", 15);

    public static double getConvert(@NotNull DropTypeEnum typeEnum) {

        return switch (typeEnum) {
            case DEFAULT -> 5;
            case COMMON -> 25;
            case UNCOMMON -> 10;
            case UNIQUE -> 1;
            case RARE -> 0.25;
            case EPIC -> 0.05;
            case LEGENDARY -> 0.005;
            case MYTHIC -> 0.0005;
            case GODLY -> 0.00005;
            case MUSTDROP -> 100.0;
        };
    }

    public static double getPlayerBase() {
        return TheLuck.inst().getConfig().getDouble("Drops.PlayerBase", 0.01);
    }

    public static double getConstChance(DropTypeEnum typeEnum) {
        return getPlayerBase() * getConvert(typeEnum);
    }

    public static double getDropError(String mob, String item) {

        double a = 0;
        double e = 1000;

        for (Map<String, Map<String, Long>> map : DBDrops.dataGeneral) {
            if (!map.containsKey(mob)) continue;
            if (!map.get(mob).containsKey(item)) continue;
            a = map.get(mob).get(item);
        }
        for (Map<String, Map<String, Long>> map : DBDrops.dataExpect) {
            if (!map.containsKey(mob)) continue;
            if (!map.get(mob).containsKey(item)) continue;
            e = map.get(mob).get(item);
        }
        return 1 - a / e;
    }

    public static double getCorrection(String mob, String item, int stack) {

        double error = getDropError(mob, item);

        if (error < 1 + (dropError * stack * -1)) return 1 / (1 + dropCorrection * stack);
        if (error > 1 + (dropError * stack)) return 1 + dropCorrection * stack;
        return 1;
    }

    public static double getChance(DropTypeEnum typeEnum, String mob, String item) {

        double correction = 1;
        for (int i = 1; i <= dropStack; i ++) {
            correction*= getCorrection(mob, item, i);
        }

        return getConstChance(typeEnum) * correction;
    }

    public static @Nullable ItemStack chance(String item, int amount, double chance) {

        double d = 1000000000000000000000D;
        double get = TheLuck.ran.nextDouble(d);
        double c = chance * d;

        if (get <= c) {
            return MythicBukkit.inst().getItemManager().getItemStack(item, amount);
        } else return null;
    }
}
