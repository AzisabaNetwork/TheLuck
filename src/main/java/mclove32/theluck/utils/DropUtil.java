package mclove32.theluck.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DropUtil {

    public static void drop(@NotNull Player p, ItemStack item, Location loc) {

        for (ItemStack stack : p.getInventory().addItem(item).values()) {
            loc.getWorld().dropItem(loc, stack);
        }
    }
}
