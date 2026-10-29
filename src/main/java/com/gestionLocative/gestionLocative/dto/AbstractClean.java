package com.gestionLocative.gestionLocative.dto;

public abstract class AbstractClean {
    public static String cleanText(String text) {
        return text != null ? text.replaceAll("\\s+", " ").trim() : null;
    }
}
