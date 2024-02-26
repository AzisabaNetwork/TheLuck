package mclove32.theluck.config;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import mclove32.theluck.utils.DropTypeEnum;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReadMythicConfig {

    private final MythicConfig config;
    private final String mob;
    private final String luck = "TheLuck.";

    public ReadMythicConfig(MythicConfig config, String mob) {

        this.config = config;
        this.mob = mob;
    }
    public static final List<Map<String, Map<String, String>>> dataMapList = new ArrayList<>();

    public boolean isEnabledLuck() {
        if (!config.isSet("TheLuck")) return false;
        if (config.isSet(luck + "Enabled")) {
            return config.getBoolean(luck + "Enabled", false);
        }
        return false;
    }

    public @Nullable List<String> getMMIDs() {

        List<String> idList = new ArrayList<>();
        List<String> list = config.getStringList(luck + "Drops");
        if (list == null) return null;

        for (String s :list) {
            String item = s.substring(0, s.indexOf(" "));
            idList.add(item);
            dataMapList.add(Map.of(mob, Map.of(item, s.substring(s.indexOf(" ") + 1))));
        }
        return idList;
    }

    public int getExpect(String item) {

        if (dataMapList.isEmpty()) return 1;
        for (Map<String, Map<String, String>> maps : dataMapList) {

            if (!maps.containsKey(mob)) continue;
            if (!maps.get(mob).containsKey(item)) continue;

            String s = maps.get(mob).get(item);
            if (!s.contains("~")) return 1000;

            String s1 = s.substring(s.indexOf("~") + 1);
            if (s1.contains(" ")) {
                s1 = s1.substring(0, s1.indexOf(" "));
            }

            return Integer.parseInt(PlaceholderString.of(s1).get());
        }
        return 1000;
    }

    public int getAmount(String item) {

        if (dataMapList.isEmpty()) return 1;
        for (Map<String, Map<String, String>> maps : dataMapList) {

            if (!maps.containsKey(mob)) continue;
            if (!maps.get(mob).containsKey(item)) continue;

            String s = maps.get(mob).get(item);
            if (!s.contains("+")) return 1;

            String s1 = s.substring(s.indexOf("+") + 1);
            if (s1.contains(" ")) {
                s1 = s1.substring(0, s1.indexOf(" "));
            }
            return Integer.parseInt(PlaceholderString.of(s1).get());
        }
        return 1;
    }

    public DropTypeEnum getType(String item) {

        if (dataMapList.isEmpty()) return DropTypeEnum.DEFAULT;
        for (Map<String, Map<String, String>> maps : dataMapList) {

            if (!maps.containsKey(mob)) continue;
            if (!maps.get(mob).containsKey(item)) continue;

            String s = maps.get(mob).get(item);
            if (!s.contains("@")) return DropTypeEnum.DEFAULT;

            String s1 = s.substring(s.indexOf("@") + 1);
            if (s1.contains(" ")) {
                s1 = s1.substring(0, s1.indexOf(" "));
            }
            return DropTypeEnum.get(s1);
        }
        return DropTypeEnum.DEFAULT;
    }
}
