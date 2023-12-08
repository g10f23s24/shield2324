package com.trust.home.security.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CircleHoleView extends View {
    private static final float MIN_POS = 10f;
    private static final float MAX_POS = 60f;
    private static final long DURATION = 50L;
    private static final float JUMP = 2.5f;
    private Paint paint;
    private Paint pWhite;
    private Path path;
    private float pos = 5f;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    public CircleHoleView(Context context) {
        super(context);
        init(context);
    }

    public CircleHoleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CircleHoleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        pWhite = new Paint();
        pWhite.setColor(Color.WHITE);
        pWhite.setStrokeWidth(1f);
        pWhite.setStyle(Paint.Style.STROKE);
        path = new Path();
//        startAnimation();
    }

    private void startAnimation() {
        runnable = new Runnable() {
            @Override
            public void run() {
                updatePos();
                onUpdate(pos);
                handler.postDelayed(this, DURATION);
            }
        };
        handler.post(runnable);
    }

    private void stopAnimation() {
        if(runnable != null) {
            handler.removeCallbacks(runnable);
            runnable = null;
        }
    }

    private void updatePos() {
        if(pos >= MAX_POS) pos = MIN_POS;
        else pos += JUMP;
    }

    private void onUpdate(float pos) {
        path.reset();
        path.addRect(0f, 0f, 5f, pos, Path.Direction.CCW);
        pWhite.setPathEffect(new PathDashPathEffect(path, 45, 0, PathDashPathEffect.Style.MORPH));
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth() / 2f;
        canvas.drawCircle(w, w, w, paint);
        canvas.drawCircle(w, w, w, pWhite);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }
}
