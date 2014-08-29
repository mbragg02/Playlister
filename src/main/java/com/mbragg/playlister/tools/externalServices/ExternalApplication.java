package com.mbragg.playlister.tools.externalServices;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Class for launching an external application on the underlying operating system.
 *
 * @author Michael Bragg
 */
public final class ExternalApplication {

    private ExternalApplication() {
        // hidden
    }

    /**
     * Launch method. Open a given file by an external application. Default application for given file type.
     *
     * @param file The file to open.
     * @return boolean. True if successful, false otherwise.
     */
    public static boolean launch(File file) throws IOException {

            String cmd = getCommand(file);
            if (!cmd.isEmpty()) {
                Runtime.getRuntime().exec(cmd);
                return true;
            } else {
                // Unknown OS, try Java awt "Desktop" approach
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                    return true;
                }
            }

        return false;
    }

    // "open" commands for Windows, Mac and Linux operating systems.
    private static String getCommand(File file) {
        if (OperatingSystemDetector.isMac()) return String.format("open %s", file);
        if (OperatingSystemDetector.isWindows()) return String.format("cmd /c start %s", file);
        if (OperatingSystemDetector.isLinux()) return String.format("xdg-open %s", file);
        return "";
    }
}
