package ru.it.lecm.base.beans;

import java.util.regex.Pattern;

/**
 * User: dbashmakov
 * Date: 26.01.2017
 * Time: 10:28
 */
public class FileNameValidator {
    /**
     * The bad file name pattern.
     */
    public static final String FILENAME_ILLEGAL_REGEX = "[\\\"\\*\\\\\\>\\<\\?\\/\\:\\|]|([\\.]+$)";
    private static final Pattern FILENAME_ILLEGAL_PATTERN_REPLACE = Pattern.compile(FILENAME_ILLEGAL_REGEX);

    public static boolean isValid(String name) {
        return !FILENAME_ILLEGAL_PATTERN_REPLACE.matcher(name).find();
    }

    /**
     * Replaces illegal filename characters with '_'
     */
    public static String getValidFileName(String fileName) {
        if (fileName == null || fileName.length() == 0) {
            throw new IllegalArgumentException("File name cannot be corrected if it is null or empty.");
        }
        return FILENAME_ILLEGAL_PATTERN_REPLACE.matcher(fileName).replaceAll("_").trim();
    }
}
