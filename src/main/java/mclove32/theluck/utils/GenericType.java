package mclove32.theluck.utils;

import org.bukkit.plugin.java.JavaPlugin;

public class GenericType<T extends JavaPlugin> {

    private final T value;

    public GenericType(T value) {
        this.value = value;
    }
    public T get() {return value;}
}
