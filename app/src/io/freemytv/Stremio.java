package io.freemytv;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

/** Opens a title page only; playback always remains the user's choice. */
final class Stremio {
    static void open(Context context, Recs.Pick pick) {
        if (pick == null) return;
        if (pick.imdb == null || !pick.imdb.matches("tt[0-9]+")) {
            Toast.makeText(context, Ui.tr("Title details unavailable. Try again later."), Toast.LENGTH_LONG).show();
            return;
        }
        String type = "tv".equals(pick.kind) ? "series" : "movie";
        String video = "movie".equals(type) ? pick.imdb : "";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("stremio:///detail/" + type + "/"
                + pick.imdb + "/" + video + "?autoPlay=false"))
                .addCategory(Intent.CATEGORY_BROWSABLE).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (RuntimeException e) {
            Toast.makeText(context, Ui.tr("Stremio is unavailable or cannot open this title."), Toast.LENGTH_LONG).show();
        }
    }
}
