package com.example.onepiece.mainPage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2018/5/29 0029.
 */

public class MyVisualizerView extends View {
    private byte[] bytes;
    private float[] points;
    private Paint paint = new Paint();
    private Rect rect = new Rect();
    private byte type = 0;

    public MyVisualizerView(Context context) {
        super(context);
        bytes = null;
        paint.setStrokeWidth(1f);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
    }

    public void updateVisualizer(byte[] ftt) {
        bytes = ftt;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return false;
        }
        type++;
        if (type >= 3) {
            type = 0;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bytes == null) {
            return;
        }

        canvas.drawColor(Color.WHITE);
        rect.set(0, 0, getWidth(), getHeight());

        switch (type) {
            // 块状
            case 0:
                //根据波形值计算矩形
                for (int i = 0; i < bytes.length - 1; i++) {
                    float left = getWidth() * i / (bytes.length - 1);
                    float top = rect.height() - (byte)(bytes[i + 1] + 128) * rect.height() / 128;
                    float right = left + 1;
                    float bottom = rect.height();
                    canvas.drawRect(left, top, right, bottom, paint);
                }
                break;

            case 1:
                //柱状波形图
                for (int i = 0; i < bytes.length - 1; i += 18) {
                    float left = rect.width() * i / (bytes.length - 1);
                    float top = rect.height() - (byte)(bytes[i] + 128) * rect.height() / 128;
                    float right = left + 6;
                    float bottom = rect.height();
                    canvas.drawRect(left, top, right, bottom, paint);
                }
                break;

            case 2:
                //绘制曲线波形图
                if (points == null || points.length < bytes.length * 4) {
                    points = new float[bytes.length * 4];
                }
                for (int i = 0; i < bytes.length - 1; i++) {
                    //计算第i个点的x坐标
                    points[i * 4] = rect.width() * i / (bytes.length - 1);
                    //根据bytes[i]的值计算第i个点的y坐标
                    points[i * 4 + 1] = (rect.height() / 2) + ((byte)(bytes[i] + 128)) * 128 / (rect.height() / 2);
                    //计算第i+1个点的x坐标
                    points[i * 4 + 2] = rect.width() * (i + 1) / (bytes.length - 1);
                    //根据bytes[i]的值计算第i+1个点的y坐标
                    points[i * 4 + 3] = (rect.height() / 2 + ((byte)(bytes[i + 1] + 128)) * 12 / (rect.height() / 2));
                }
                canvas.drawLines(points, paint);
                break;
        }
    }
}
