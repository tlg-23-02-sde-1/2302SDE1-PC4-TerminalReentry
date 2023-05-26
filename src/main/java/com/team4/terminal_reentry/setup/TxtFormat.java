package com.team4.terminal_reentry.setup;

import java.util.Optional;

public class TxtFormat {
    private TxtFormat() {}
    public static final String INDENT = "        "; // 8 spaces
    public static final int MAX_WIDTH = 66 + INDENT.length();

    public static String wordWrap(String words) {
        StringBuilder formatted = new StringBuilder();
        formatted.append(INDENT);
        String[] wordArray = words.split("\\s+");

        int currentLength = INDENT.length();

        for (String word : wordArray) {
            // Check if adding the current word would exceed maxLength
            if (currentLength + word.length() > MAX_WIDTH) {
                formatted.append("\n" + INDENT); // Insert line break and indent
                currentLength = 0; // Reset current length
            }

            formatted.append(word).append(" "); // Append word and a space
            currentLength += word.length() + 1; // Update current length
        }

        return formatted.toString();
    }
}