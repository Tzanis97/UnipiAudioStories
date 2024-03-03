package com.example.unipiaudiostories;

import java.util.ArrayList;
import java.util.List;

public class TextSplit {
    // Method to split text into chunks of 4000 characters or less
    public static List<String> splitText(String text) {
        List<String> chunks = new ArrayList<>();
        int maxLength = 4000; // Maximum length of each chunk

        // Split the text into sentences based on the period delimiter
        String[] sentences = text.split("\\.");

        StringBuilder chunkBuilder = new StringBuilder();
        for (String sentence : sentences) {
            // If adding the current sentence to the current chunk exceeds the maximum length,
            // add the current chunk to the list and start a new chunk
            if (chunkBuilder.length() + sentence.length() + 1 > maxLength) {
                chunks.add(chunkBuilder.toString());
                chunkBuilder = new StringBuilder();
            }
            // Append the sentence to the current chunk
            if (chunkBuilder.length() > 0) {
                chunkBuilder.append(".");
            }
            chunkBuilder.append(sentence);
        }
        // Add the last chunk to the list
        chunks.add(chunkBuilder.toString());

        return chunks;
    }
}
