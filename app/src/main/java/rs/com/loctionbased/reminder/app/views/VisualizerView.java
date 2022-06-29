package rs.com.loctionbased.reminder.app.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class VisualizerView extends View {
    private static final int LINE_WIDTH = 2;
    private static final int LINE_SCALE = 220;
    private List<Float> amplitudes;
    private int width;
    private int height;
    private Paint linePaint;
    private int mLineColor = Color.GREEN;

    // constructor
    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        linePaint = new Paint(); // create Paint for lines
        linePaint.setColor(mLineColor); // set color to green
        linePaint.setStrokeWidth(LINE_WIDTH); // set stroke width
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;
        amplitudes = new ArrayList<Float>(width / LINE_WIDTH);
    }

    public void clear() {
        amplitudes.clear();
    }

    public void addAmplitude(float amplitude) {
        amplitudes.add(amplitude);

        if (amplitudes.size() * LINE_WIDTH >= width) {
            amplitudes.remove(0);
        }
    }

    public void setLineColor(int color) {
        mLineColor = color;
        linePaint.setColor(mLineColor);
    }

    @Override
    public void onDraw(Canvas canvas) {
        int middle = height / 2;
        float curX = 0;

        for (float power : amplitudes) {
            float scaledHeight = power / LINE_SCALE; // scale the power
            scaledHeight = (scaledHeight < 2 ? 2 : scaledHeight);
            curX += LINE_WIDTH; // increase X by LINE_WIDTH

            canvas.drawLine(curX, middle + scaledHeight / 2, curX, middle
                    - scaledHeight / 2, linePaint);
        }
    }

}
