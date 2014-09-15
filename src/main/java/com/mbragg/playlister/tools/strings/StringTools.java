package com.mbragg.playlister.tools.strings;

import org.apache.commons.lang3.StringUtils;

/**
 * Class containing static methods relating to Strings.
 *
 * @author Michael Bragg
 */
public final class StringTools {

    private static final int EDIT_DISTANCE_THRESHOLD = 1;
    private static final int INVALID_EDIT_DISTANCE = -1;
    private static final String I_TUNES_MEDIA_MUSIC = "/Music/iTunes/iTunes Media/Music";

    private StringTools() {
        // Hide utility class
    }

    /**
     * Levenshtein distance between two strings.
     *
     * @param a         String.
     * @param b         String.
     * @param threshold int.
     * @return int. Levenshtein distance (edit distance) between the two strings.
     */
    public static int levenshteinDistance(String a, String b, int threshold) {
        // wrapper for org.apache.commons.lang3 getLevenshteinDistance
        return StringUtils.getLevenshteinDistance(a, b, threshold);
    }

    /**
     * "Fuzzy equals". If two strings a the same, but a small degree of error.
     *
     * @param a String
     * @param b String
     * @return boolean
     */
    public static boolean fuzzyEquals(String a, String b) {
        return levenshteinDistance(a, b, EDIT_DISTANCE_THRESHOLD) != INVALID_EDIT_DISTANCE;
    }

    /**
     * String formatter.
     * <p>
     * Removes any character that is not a-z or A-Z (including whitespace)
     * Transforms string to lowercase
     *
     * @param s String to format
     * @return formatted string
     */
    public static String formatter(String s) {
        return s.replaceAll("[^a-zA-Z]", "").toLowerCase();
    }

    public static String defaultMacMusicDirectory() {
        String userDirectoryString = System.getProperty("user.home");
        return userDirectoryString + I_TUNES_MEDIA_MUSIC;
    }
}