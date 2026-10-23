package io.freemytv;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.widget.LinearLayout;
import java.text.SimpleDateFormat;

/** One read-only weekly overview; all labels are prepared before drawing. */
final class Forecast {
    static void showWeek(Activity activity, HomeData.Weather weather) {
        if (weather.days.isEmpty()) {
            new AlertDialog.Builder(activity).setTitle(Prefs.city()).setMessage(Ui.tr("No weather data right now."))
                    .setPositiveButton(android.R.string.ok, null).show();
            return;
        }
        float density = activity.getResources().getDisplayMetrics().density;
        LinearLayout week = new LinearLayout(activity);
        week.setOrientation(LinearLayout.HORIZONTAL);
        week.setPadding(Math.round(16 * density), 0, Math.round(16 * density), 0);
        for (int i = 0; i < Math.min(7, weather.days.size()); i++) {
            final int day = i;
            String date = weather.days.get(day);
            try {
                date = new SimpleDateFormat("EEE dd/MM", Ui.locale()).format(
                        new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ROOT).parse(date));
            } catch (Exception ignored) { }
            final String label = date;
            final String high = HomeData.degrees(weather.dayMax.get(day));
            final String low = HomeData.degrees(weather.dayMin.get(day));
            final boolean hasRain = day < weather.dayRain.size() && !Double.isNaN(weather.dayRain.get(day));
            final String rain = hasRain ? String.format(Ui.locale(), "%.0f%%", weather.dayRain.get(day)) : "—";
            TextPaint rainInk = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            rainInk.setColor(0xFFB3CCDC);
            rainInk.setTextSize(11);
            final StaticLayout rainText = StaticLayout.Builder.obtain(
                    Ui.trf("Rain chance {value}", "value", rain), 0,
                    Ui.trf("Rain chance {value}", "value", rain).length(), rainInk, 86)
                    .setAlignment(Layout.Alignment.ALIGN_CENTER).setIncludePad(false).build();
            View item = new View(activity) {
                final Paint ink = new Paint(Paint.ANTI_ALIAS_FLAG);
                @Override protected void onMeasure(int widthSpec, int heightSpec) {
                    super.onMeasure(widthSpec, heightSpec);
                    // Match the drawing scale and reserve bottom padding after every rain label line.
                    int height = (int) Math.ceil((166 + rainText.getHeight() + 20) * getMeasuredWidth() / 100f);
                    setMeasuredDimension(getMeasuredWidth(), resolveSize(height, heightSpec));
                }
                @Override protected void onDraw(Canvas canvas) {
                    float s = getWidth() / 100f;
                    canvas.save(); canvas.scale(s, s);
                    ink.setStyle(Paint.Style.FILL); ink.setColor(0x08FFFFFF);
                    canvas.drawRoundRect(4, 8, 96, getHeight() / s - 8, 10, 10, ink);
                    ink.setStyle(Paint.Style.STROKE); ink.setStrokeWidth(0.6f);
                    ink.setColor(0x20FFFFFF);
                    canvas.drawRoundRect(4, 8, 96, getHeight() / s - 8, 10, 10, ink);
                    ink.setStyle(Paint.Style.FILL);
                    ink.setTextAlign(Paint.Align.CENTER); ink.setColor(Color.WHITE); ink.setTextSize(12);
                    canvas.drawText(label, 50, 28, ink);
                    WeatherArt.draw(canvas, weather.dayCode.get(day), true, 50, 77, 52);
                    ink.setTextSize(19); canvas.drawText(high, 50, 126, ink);
                    ink.setTextSize(15); ink.setColor(0xFFB3CCDC); canvas.drawText(low, 50, 151, ink);
                    canvas.translate(7, 166);
                    rainText.draw(canvas);
                    canvas.restore();
                }
            };
            item.setFocusable(false);
            item.setClickable(false);
            item.setContentDescription(label + ". " + HomeData.condition(weather.dayCode.get(day), true)
                    + ". " + Ui.trf("High {max}, low {min}.", "max", high, "min", low)
                    + (hasRain ? ". " + Ui.trf("Rain chance {value}", "value", rain) : ""));
            week.addView(item, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        }
        AlertDialog dialog = new AlertDialog.Builder(activity).setTitle(Prefs.city()).setView(week)
                .setPositiveButton(android.R.string.ok, null).create();
        dialog.setOnShowListener(ignored -> {
            style(activity, dialog);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).requestFocus();
        });
        dialog.show();
    }

    private static void style(Activity activity, AlertDialog dialog) {
        if (dialog.getWindow() == null) return;
        GradientDrawable panel = new GradientDrawable();
        panel.setColor(0xF0172B3C);
        panel.setCornerRadius(22 * activity.getResources().getDisplayMetrics().density);
        panel.setStroke(2, 0x668FD9FF);
        dialog.getWindow().setBackgroundDrawable(panel);
        dialog.getWindow().setLayout(Math.round(activity.getResources().getDisplayMetrics().widthPixels * 0.9f),
                android.view.WindowManager.LayoutParams.WRAP_CONTENT);
    }
}
