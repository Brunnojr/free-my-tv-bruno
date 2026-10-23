package io.freemytv;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** Metadata only, requested on demand. No video downloading, player, or background playback. */
final class Trailers {
    interface Callback { void ready(String youtubeKey); }
    private static final Handler MAIN = new Handler(Looper.getMainLooper());
    // Keep only the running request and the latest queued card; abandoned cards never build an unlimited queue.
    private static final ThreadPoolExecutor IO = new ThreadPoolExecutor(1, 1, 30, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1), new ThreadPoolExecutor.DiscardOldestPolicy());

    static void find(Context context, Recs recs, Recs.Pick pick, Callback callback) {
        SharedPreferences cache = context.getSharedPreferences("trailers-v1", Context.MODE_PRIVATE);
        String slot = pick.kind + ":" + pick.id;
        IO.execute(() -> {
            String selected = "";
            try {
                long now = System.currentTimeMillis(), at = cache.getLong(slot + ":at", 0);
                if (at > 0 && now >= at && now - at < 24 * 3600_000L) {
                    selected = cache.getString(slot, "");
                } else {
                    int best = Integer.MAX_VALUE;
                    int completed = 0;
                    for (String language : new String[] {"pt-BR", "en-US"}) {
                        JSONArray videos;
                        try {
                            videos = recs.trailerVideos(pick, language).getJSONArray("results");
                            completed++;
                        } catch (Exception ignored) { continue; }
                        for (int i = 0; videos != null && i < videos.length(); i++) {
                            JSONObject video = videos.getJSONObject(i);
                            String key = video.optString("key");
                            int rank = TrailerChoice.rank(video.optString("site"), video.optString("type"),
                                    video.optString("iso_639_1"), video.optString("iso_3166_1"),
                                    video.optString("name"), video.optBoolean("official"), key);
                            if (rank < best || rank == best && rank != Integer.MAX_VALUE && key.compareTo(selected) < 0) {
                                best = rank;
                                selected = key;
                            }
                        }
                    }
                    if (completed == 2) {
                        SharedPreferences.Editor edit = cache.edit().putString(slot, selected).putLong(slot + ":at", now);
                        ArrayList<String> slots = new ArrayList<>();
                        for (String key : cache.getAll().keySet()) if (key.endsWith(":at") && !key.equals(slot + ":at")) slots.add(key);
                        slots.sort(Comparator.comparingLong(key -> cache.getLong(key, 0)));
                        while (slots.size() >= 60) {
                            String old = slots.remove(0);
                            edit.remove(old).remove(old.substring(0, old.length() - 3));
                        }
                        edit.apply();
                    }
                }
            } catch (Exception ignored) {
                // URLs may contain credentials: never log network exceptions here.
            }
            final String result = selected;
            MAIN.post(() -> callback.ready(result));
        });
    }

    static void open(Context context, String key) {
        if (!key.matches("[A-Za-z0-9_-]{11}")) return;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + key))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Use the installed TV player first; never send the user to a generic search result.
        for (String pkg : new String[] {"com.google.android.youtube.tv", "com.google.android.youtube"}) {
            try { context.startActivity(new Intent(intent).setPackage(pkg)); return; }
            catch (RuntimeException ignored) { }
        }
        Toast.makeText(context, Ui.tr("YouTube is unavailable."), Toast.LENGTH_LONG).show();
    }
}
