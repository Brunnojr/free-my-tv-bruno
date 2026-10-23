package io.freemytv;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/** Small vector drawings: no downloads, bitmap decoding, or animation. */
final class WeatherArt {
    private static final Paint ink = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Path bolt = new Path();
    static {
        bolt.moveTo(51, 65); bolt.lineTo(40, 84); bolt.lineTo(52, 81);
        bolt.lineTo(45, 98); bolt.lineTo(68, 72); bolt.lineTo(55, 75); bolt.close();
        ink.setStrokeCap(Paint.Cap.ROUND);
    }
    static void draw(Canvas canvas, int code, boolean day, float x, float y, float size) {
        canvas.save();
        canvas.translate(x - size / 2, y - size / 2);
        canvas.scale(size / 100, size / 100);
        ink.setStyle(Paint.Style.FILL);
        if (code <= 2) {
            if (day) {
                canvas.save(); canvas.translate(39, 36);
                ink.setColor(0xFFFFCF4B); ink.setStrokeWidth(3.5f);
                for (int i = 0; i < 8; i++) { canvas.drawLine(0, -27, 0, -33, ink); canvas.rotate(45); }
                canvas.restore();
                ink.setColor(0xFFFFB82E); canvas.drawCircle(39, 36, 21, ink);
                ink.setColor(0xFFFFD45E); canvas.drawCircle(35, 32, 17, ink);
            } else {
                ink.setColor(0xFFB9D8FF); canvas.drawCircle(39, 36, 24, ink);
                ink.setColor(0xFF7BADD7); canvas.drawCircle(47, 29, 18, ink);
            }
        }
        if (code >= 2) {
            ink.setColor(0xFFB3CADB);
            canvas.drawRoundRect(16, 48, 91, 73, 12, 12, ink);
            canvas.drawCircle(38, 48, 20, ink); canvas.drawCircle(65, 44, 24, ink);
            ink.setColor(0xFFF0F6FA);
            canvas.drawRoundRect(12, 44, 88, 67, 12, 12, ink);
            canvas.drawCircle(33, 43, 19, ink); canvas.drawCircle(59, 38, 23, ink);
        }
        if (code == 45 || code == 48) {
            ink.setColor(0xFFB0CADC); ink.setStrokeWidth(4);
            canvas.drawLine(16, 81, 82, 81, ink); canvas.drawLine(24, 91, 74, 91, ink);
        } else if (code >= 95) {
            ink.setColor(0xFFFFC83D); canvas.drawPath(bolt, ink);
        } else if ((code >= 71 && code <= 77) || code == 85 || code == 86) {
            ink.setColor(0xFFD5F3FF);
            for (int i = 0; i < 3; i++) canvas.drawCircle(30 + i * 22, 84, 4, ink);
        } else if (code >= 51) {
            ink.setColor(0xFF38BDF5); ink.setStrokeWidth(5);
            for (int i = 0; i < 3; i++) canvas.drawLine(31 + i * 21, 78, 26 + i * 21, 90, ink);
        }
        canvas.restore();
    }
}
