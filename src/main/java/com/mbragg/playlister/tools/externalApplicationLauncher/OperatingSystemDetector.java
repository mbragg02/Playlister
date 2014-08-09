package com.mbragg.playlister.tools.externalApplicationLauncher;

/**
 * Provides static methods to check what is the underlying current operating system.
 *
 * @author Michael Bragg
 */
public class OperatingSystemDetector {

    private static boolean isWindows = false;
    private static boolean isLinux = false;
    private static boolean isMac = false;

    static {
        String operatingSystem = System.getProperty("os.name").toLowerCase();

        isMac = operatingSystem.contains("mac");
        isWindows = operatingSystem.contains("win");
        isLinux = operatingSystem.contains("nux") || operatingSystem.contains("nix");
    }

    public static boolean isMac() {
        return isMac;
    }

    public static boolean isWindows() {
        return isWindows;
    }

    public static boolean isLinux() {
        return isLinux;
    }

}
