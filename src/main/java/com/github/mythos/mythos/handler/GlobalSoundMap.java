package com.github.mythos.mythos.handler;

import java.util.HashMap;
import java.util.Map;

public class GlobalSoundMap {
    private static Map<String, String> REPLACEMENTS = new HashMap<>();

    public static void add(String o, String r) { REPLACEMENTS.put(o, r); }
    public static void remove(String o) { REPLACEMENTS.remove(o); }
    public static Map<String, String> getMap() { return REPLACEMENTS; }
    public static void setMap(Map<String, String> map) { REPLACEMENTS = map; }

    public static String getReplacement(String original) {
        return REPLACEMENTS.getOrDefault(original, original);
    }
}
