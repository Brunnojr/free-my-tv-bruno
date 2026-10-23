package io.freemytv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import java.util.LinkedHashSet;
import java.util.Locale;

final class ProviderMarks {
    private static Drawable netflix, max, prime;
    static String name(String raw) {
        String lower = raw.toLowerCase(Locale.ROOT);
        if (lower.contains("netflix")) return "Netflix";
        if (lower.contains("max") || lower.contains("hbo")) return "HBO Max";
        if (lower.contains("disney")) return "Disney+";
        if (lower.contains("apple")) return "Apple TV+";
        if (lower.contains("globo")) return "Globoplay";
        if (lower.contains("crunchyroll")) return "Crunchyroll";
        if (lower.contains("paramount")) return "Paramount+";
        if (lower.contains("prime") || lower.equals("amazon video")) return "Prime Video";
        return raw.replaceAll("(?i)\\s+(amazon channel|apple tv channel|roku premium channel)$", "").trim();
    }
    static String[] names(String raw) {
        LinkedHashSet<String> names = new LinkedHashSet<>();
        for (String value : raw.split(" · ")) if (!value.trim().isEmpty()) names.add(name(value));
        return names.toArray(new String[0]);
    }
    static void prepare(Context context) {
        if (netflix == null) netflix = context.getDrawable(R.drawable.provider_netflix);
        if (max == null) max = context.getDrawable(R.drawable.provider_max);
        if (prime == null) prime = context.getDrawable(R.drawable.provider_prime);
    }
    static boolean hasMark(String name) {
        return name.equals("Netflix") || name.equals("HBO Max") || name.equals("Prime Video");
    }
    static void draw(Canvas c, String name, float x, float y, float size, Paint ink) {
        Drawable mark = name.equals("Netflix") ? netflix : name.equals("HBO Max") ? max : name.equals("Prime Video") ? prime : null;
        if (mark != null) {
            float ratio = (float) mark.getIntrinsicWidth() / Math.max(1, mark.getIntrinsicHeight());
            float width = Math.min(size, size * ratio), height = Math.min(size, size / ratio);
            mark.setBounds(Math.round(x + (size - width) / 2), Math.round(y + (size - height) / 2),
                    Math.round(x + (size + width) / 2), Math.round(y + (size + height) / 2));
            mark.draw(c);
        }
    }
}
