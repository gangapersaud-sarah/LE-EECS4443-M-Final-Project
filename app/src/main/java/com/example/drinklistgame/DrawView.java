package com.example.drinklistgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DrawView extends View {
    private Paint paint;
    private Path path;
    private List<PointF> points;
    private static final float TOUCH_TOLERANCE = 4;


    public DrawView(Context context) {
        super(context);
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(10);

        path = new Path();
        points = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                points.add(new PointF(x, y));
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - points.get(points.size() - 1).x);
                float dy = Math.abs(y - points.get(points.size() - 1).y);
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    path.quadTo(points.get(points.size() - 1).x, points.get(points.size() - 1).y, (x + points.get(points.size() - 1).x) / 2, (y + points.get(points.size() - 1).y) / 2);
                    points.add(new PointF(x, y));
                }
                break;
            case MotionEvent.ACTION_UP:
                path.lineTo(x, y);
                Toast.makeText(getContext(), "Line drawn from (" + points.get(0).x + ", " + points.get(0).y +
                        ") to (" + points.get(points.size() - 1).x + ", " + points.get(points.size() - 1).y + ")", Toast.LENGTH_SHORT).show();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }
}
