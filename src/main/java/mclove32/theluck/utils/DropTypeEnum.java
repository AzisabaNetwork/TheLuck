package mclove32.theluck.utils;

public enum DropTypeEnum {
    MUSTDROP, COMMON, UNCOMMON, DEFAULT, UNIQUE, RARE, EPIC, LEGENDARY, MYTHIC, GODLY;

    public static DropTypeEnum get(String s) {

        if (s == null) return DropTypeEnum.DEFAULT;
        try {
            return DropTypeEnum.valueOf(s.toUpperCase());
        } catch (Exception e) {
            return DropTypeEnum.DEFAULT;
        }
    }
}
