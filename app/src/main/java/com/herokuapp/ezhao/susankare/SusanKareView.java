package com.herokuapp.ezhao.susankare;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SusanKareView extends View {
    private final String TAG = "SusanKareView";
    private final int GRIDSIZE = 32;
    private int pixelColor;
    private Paint pixelPaint;
    private Paint guidePaint;
    private int pixelSize;
    private int[][] points;
    private Path guidePath;

    public SusanKareView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Setup attributes
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.SusanKareView, 0, 0);
        try {
            pixelColor = typedArray.getColor(R.styleable.SusanKareView_pixelColor, Color.BLACK);
        } finally {
            typedArray.recycle();
        }

        // Setup paint
        pixelPaint = new Paint();
        pixelPaint.setStyle(Paint.Style.FILL);
        pixelPaint.setColor(pixelColor);

        guidePaint = new Paint();
        guidePaint.setStyle(Paint.Style.STROKE);
        guidePaint.setStrokeWidth(1);
        guidePaint.setColor(Color.LTGRAY);

        points = new int[GRIDSIZE][GRIDSIZE];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float canvasSize = getCanvasSize();
        float offsetLeft = offsetLeft();
        float offsetTop = offsetTop();
        float pixelSizePrecise = canvasSize / GRIDSIZE;

        pixelSize = Math.round(canvasSize / GRIDSIZE);

        // Draw guides
        if (guidePath == null) {
            guidePath = new Path();
            for (int i=0; i <= GRIDSIZE; i++) {
                guidePath.moveTo(offsetLeft, offsetTop+i*pixelSizePrecise);
                guidePath.lineTo(offsetLeft+canvasSize, offsetTop+i*pixelSizePrecise);

                guidePath.moveTo(offsetLeft+i*pixelSizePrecise, offsetTop);
                guidePath.lineTo(offsetLeft+i*pixelSizePrecise, offsetTop+canvasSize);
            }
        }
        canvas.drawPath(guidePath, guidePaint);

        // Draw pixels
        float coordX, coordY;
        for (int x=0; x < GRIDSIZE; x++) {
            for (int y=0; y < GRIDSIZE; y++) {
                if (points[x][y] == 1) {
                    coordX = canvasSize/ GRIDSIZE * x + offsetLeft;
                    coordY = canvasSize/ GRIDSIZE * y + offsetTop;
                    canvas.drawRect(coordX, coordY, coordX+pixelSize, coordY+pixelSize, pixelPaint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = Math.round((event.getX()-offsetLeft())/getCanvasSize()* GRIDSIZE);
        int y = Math.round((event.getY()-offsetTop())/getCanvasSize()* GRIDSIZE);
        if(x < GRIDSIZE && y < GRIDSIZE && x >= 0 && y >= 0) {
            points[x][y] = 1;
        }
        postInvalidate();
        return true;
    }

    private float getCanvasSize() {
        return Math.min(getWidth(), getHeight());
    }

    private float offsetTop() {
        if(getWidth() < getHeight()) {
            return (getHeight() - getWidth()) / 2;
        }
        return 0;
    }

    private float offsetLeft() {
        if(getWidth() > getHeight()) {
            return (getWidth() - getHeight()) / 2;
        }
        return 0;
    }
}
