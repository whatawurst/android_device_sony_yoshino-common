package com.android.setupwizardlib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import java.lang.ref.SoftReference;

public class GlifPatternDrawable extends Drawable {
    @SuppressLint({"InlinedApi"})
    private static final int[] ATTRS_PRIMARY_COLOR = new int[]{16843827};
    private static final float COLOR_ALPHA = 0.8f;
    private static final int COLOR_ALPHA_INT = 204;
    private static final float MAX_CACHED_BITMAP_SCALE = 1.5f;
    private static final int NUM_PATHS = 7;
    private static final float SCALE_FOCUS_X = 0.146f;
    private static final float SCALE_FOCUS_Y = 0.228f;
    private static final float VIEWBOX_HEIGHT = 768.0f;
    private static final float VIEWBOX_WIDTH = 1366.0f;
    private static SoftReference<Bitmap> sBitmapCache;
    private static int[] sPatternLightness;
    private static Path[] sPatternPaths;
    private int mColor;
    private Paint mTempPaint = new Paint(1);

    public GlifPatternDrawable(int i) {
        setColor(i);
    }

    public static GlifPatternDrawable getDefault(Context context) {
        int i = 0;
        if (VERSION.SDK_INT >= 21) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(ATTRS_PRIMARY_COLOR);
            i = obtainStyledAttributes.getColor(0, -16777216);
            obtainStyledAttributes.recycle();
        }
        return new GlifPatternDrawable(i);
    }

    @VisibleForTesting
    public static void invalidatePattern() {
        sBitmapCache = null;
    }

    private void renderOnCanvas(Canvas canvas, float f) {
        canvas.save();
        canvas.scale(f, f);
        this.mTempPaint.setXfermode(new PorterDuffXfermode(Mode.SRC));
        if (sPatternPaths == null) {
            sPatternPaths = new Path[NUM_PATHS];
            sPatternLightness = new int[]{10, 40, 51, 66, 91, 112, 130};
            Path[] pathArr = sPatternPaths;
            Path path = new Path();
            pathArr[0] = path;
            path.moveTo(1029.4f, 357.5f);
            path.lineTo(VIEWBOX_WIDTH, 759.1f);
            path.lineTo(VIEWBOX_WIDTH, 0.0f);
            path.lineTo(1137.7f, 0.0f);
            path.close();
            Path[] pathArr2 = sPatternPaths;
            Path path2 = new Path();
            pathArr2[1] = path2;
            path2.moveTo(1138.1f, 0.0f);
            path2.rLineTo(-144.8f, VIEWBOX_HEIGHT);
            path2.rLineTo(372.7f, 0.0f);
            path2.rLineTo(0.0f, -524.0f);
            path2.cubicTo(1290.7f, 121.6f, 1219.2f, 41.1f, 1178.7f, 0.0f);
            path2.close();
            pathArr2 = sPatternPaths;
            path2 = new Path();
            pathArr2[2] = path2;
            path2.moveTo(949.8f, VIEWBOX_HEIGHT);
            path2.rCubicTo(92.6f, -170.6f, 213.0f, -440.3f, 269.4f, -768.0f);
            path2.lineTo(585.0f, 0.0f);
            path2.rLineTo(2.1f, 766.0f);
            path2.close();
            pathArr2 = sPatternPaths;
            path2 = new Path();
            pathArr2[3] = path2;
            path2.moveTo(471.1f, VIEWBOX_HEIGHT);
            path2.rMoveTo(704.5f, 0.0f);
            path2.cubicTo(1123.6f, 563.3f, 1027.4f, 275.2f, 856.2f, 0.0f);
            path2.lineTo(476.4f, 0.0f);
            path2.rLineTo(-5.3f, VIEWBOX_HEIGHT);
            path2.close();
            pathArr2 = sPatternPaths;
            path2 = new Path();
            pathArr2[4] = path2;
            path2.moveTo(323.1f, VIEWBOX_HEIGHT);
            path2.moveTo(777.5f, VIEWBOX_HEIGHT);
            path2.cubicTo(661.9f, 348.8f, 427.2f, 21.4f, 401.2f, 25.4f);
            path2.lineTo(323.1f, VIEWBOX_HEIGHT);
            path2.close();
            pathArr2 = sPatternPaths;
            path2 = new Path();
            pathArr2[5] = path2;
            path2.moveTo(178.44286f, 766.8571f);
            path2.lineTo(308.7f, VIEWBOX_HEIGHT);
            path2.cubicTo(381.7f, 604.6f, 481.6f, 344.3f, 562.2f, 0.0f);
            path2.lineTo(0.0f, 0.0f);
            path2.close();
            pathArr2 = sPatternPaths;
            path2 = new Path();
            pathArr2[6] = path2;
            path2.moveTo(146.0f, 0.0f);
            path2.lineTo(0.0f, 0.0f);
            path2.lineTo(0.0f, VIEWBOX_HEIGHT);
            path2.lineTo(394.2f, VIEWBOX_HEIGHT);
            path2.cubicTo(327.7f, 475.3f, 228.5f, 201.0f, 146.0f, 0.0f);
            path2.close();
        }
        for (int i = 0; i < NUM_PATHS; i++) {
            this.mTempPaint.setColor(sPatternLightness[i] << 24);
            canvas.drawPath(sPatternPaths[i], this.mTempPaint);
        }
        canvas.restore();
        this.mTempPaint.reset();
    }

    @VisibleForTesting
    public Bitmap createBitmapCache(int i, int i2) {
        float min = Math.min(MAX_CACHED_BITMAP_SCALE, Math.max(((float) i) / VIEWBOX_WIDTH, ((float) i2) / VIEWBOX_HEIGHT));
        Bitmap createBitmap = Bitmap.createBitmap((int) (VIEWBOX_WIDTH * min), (int) (VIEWBOX_HEIGHT * min), Config.ALPHA_8);
        renderOnCanvas(new Canvas(createBitmap), min);
        return createBitmap;
    }

    public void draw(@NonNull Canvas canvas) {
        Rect bounds = getBounds();
        int width = bounds.width();
        int height = bounds.height();
        Bitmap bitmap = sBitmapCache != null ? (Bitmap) sBitmapCache.get() : null;
        if (bitmap != null) {
            int width2 = bitmap.getWidth();
            int height2 = bitmap.getHeight();
            if (width > width2 && ((float) width2) < 2049.0f) {
                bitmap = null;
            } else if (height > height2 && ((float) height2) < 1152.0f) {
                bitmap = null;
            }
        }
        if (bitmap == null) {
            this.mTempPaint.reset();
            bitmap = createBitmapCache(width, height);
            sBitmapCache = new SoftReference(bitmap);
            this.mTempPaint.reset();
        }
        canvas.save();
        canvas.clipRect(bounds);
        scaleCanvasToBounds(canvas, bitmap, bounds);
        canvas.drawColor(-16777216);
        this.mTempPaint.setColor(-1);
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, this.mTempPaint);
        canvas.drawColor(this.mColor);
        canvas.restore();
    }

    public int getColor() {
        return Color.argb(255, Color.red(this.mColor), Color.green(this.mColor), Color.blue(this.mColor));
    }

    public int getOpacity() {
        return 0;
    }

    @VisibleForTesting
    public void scaleCanvasToBounds(Canvas canvas, Bitmap bitmap, Rect rect) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float width2 = ((float) rect.width()) / ((float) width);
        float height2 = ((float) rect.height()) / ((float) height);
        canvas.scale(width2, height2);
        if (height2 > width2) {
            canvas.scale(height2 / width2, 1.0f, ((float) width) * SCALE_FOCUS_X, 0.0f);
        } else if (width2 > height2) {
            canvas.scale(1.0f, width2 / height2, 0.0f, ((float) height) * SCALE_FOCUS_Y);
        }
    }

    public void setAlpha(int i) {
    }

    public void setColor(int i) {
        this.mColor = Color.argb(COLOR_ALPHA_INT, Color.red(i), Color.green(i), Color.blue(i));
        invalidateSelf();
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }
}
