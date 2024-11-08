package com.alan.plugin.projectautocustomconfig.utils;

public class Utils {

    public static String getOSName() {
        return System.getProperty("os.name");
    }

    public static boolean isWindows() {
        return getOSName().toLowerCase().contains("win");
    }

}
