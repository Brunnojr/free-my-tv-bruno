package io.freemytv;

import java.text.Normalizer;
import java.util.Locale;

/** Deterministic selection; TV never falls back to another season or the general series feed. */
final class TrailerChoice {
    static String path(String kind, int id) {
        return "tv".equals(kind) ? "/tv/" + id + "/season/1/videos" : "/movie/" + id + "/videos";
    }

    static int rank(String site, String type, String language, String country, String name, boolean official, String key) {
        if (!"YouTube".equals(site) || !"Trailer".equals(type) || !key.matches("[A-Za-z0-9_-]{11}")) return Integer.MAX_VALUE;
        String title = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
        boolean portuguese = "pt".equals(language);
        boolean subtitled = title.contains("legendado") || title.contains("legendada")
                || portuguese && (title.contains("legend") || title.contains("subtit"));
        boolean dubbed = portuguese && (title.contains("dublado") || title.contains("dublada") || title.contains("dubbed"));
        int priority = dubbed ? 0 : subtitled ? 1 : portuguese ? 2 : "en".equals(language) ? 3 : 99;
        if (priority == 99) return Integer.MAX_VALUE;
        return priority * 100 + (official ? 0 : 10) + ("BR".equals(country) ? 0 : 1);
    }
}
