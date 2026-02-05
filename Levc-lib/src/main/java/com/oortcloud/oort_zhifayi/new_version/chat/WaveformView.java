package com.oortcloud.oort_zhifayi.new_version.chat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class WaveformView extends View {
    private Paint paint;
    private float amplitude = 0; // 当前振幅
    public WaveformView(Context context) {
        super(context);
        init();
    }

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveformView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
    }

    public void updateAmplitude(float amplitude) {
        this.amplitude = amplitude;
        invalidate(); // 触发重绘
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制波形（示例：柱状波形）
        float centerY = getHeight() / 2f;
        float barWidth = getWidth() / 20f; // 20 根柱子
        for (int i = 0; i < 20; i++) {
            float left = i * barWidth;
            float right = (i + 1) * barWidth;
            float height = (float) (Math.random() * amplitude * getHeight());
            canvas.drawRect(left, centerY - height, right, centerY + height, paint);
        }
    }
}
