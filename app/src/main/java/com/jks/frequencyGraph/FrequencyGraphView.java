package com.jks.frequencyGraph;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FrequencyGraphView extends View {

    static final int MARGIN_DEFAULT_DP = 2;
    static final int ROUND_CORNERS_DEFAULT_DP = 3;

    protected enum ColorMode {
        Gradient,
        Solid
    }

    float marginPixels = convertDpToPixel(MARGIN_DEFAULT_DP);
    float roundCornersPixels = convertDpToPixel(ROUND_CORNERS_DEFAULT_DP);

    ColorMode currentMode = ColorMode.Solid;
    int colorResource = android.R.color.holo_blue_bright;
    int startColor = -1;
    int endColor = -1;

    Paint paint = new Paint();
    protected List<Integer> values = new LinkedList<>();

    public FrequencyGraphView(Context context) {
        super(context);
    }

    public FrequencyGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FrequencyGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FrequencyGraphView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        int valuesCount = values.size();

        float amountOfSpacingNecessary = marginPixels * (valuesCount + 1);
        float widthOfBars = (width - amountOfSpacingNecessary) / valuesCount;
        int maxValue = Collections.max(values);

        if (maxValue==0) {
            return;
        }

        float xIndex = 0;
        for (int value : values) {
            float height = (float) value / maxValue * ( getHeight() - getTotalVerticalPadding() );
            float yLocation = getPaddingTop() + ( (getHeight() - getTotalVerticalPadding() ) / 2.0f ) - ( height / 2.0f );
            int color = getColor(getContext(), (float) value/maxValue);
            paint.setColor(color);
            if (value != 0) { //Otherwise zeros will show up as a very small pixel
                canvas.drawRoundRect(xIndex, yLocation, xIndex+widthOfBars, yLocation + height, roundCornersPixels, roundCornersPixels, paint);
            }
            xIndex += (widthOfBars + marginPixels);
        }
    }

    public static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    private float getTotalVerticalPadding() {
        return getPaddingBottom() + getPaddingTop();
    }

    private int getColor(Context context, float percentInGradient) {
        if (currentMode == ColorMode.Solid) {
            return colorResource;
        } else {
            int color = ContextCompat.getColor(context, startColor);
            int color2 = ContextCompat.getColor(context, endColor);
            int o = (Integer) new ArgbEvaluator().evaluate(percentInGradient, color, color2);
            return o;
        }
    }

    public void setValues(List<Integer> values) {
        this.values = values;
        this.invalidate();
    }

    public void setGraphColor(int colorResource) {
        this.currentMode = ColorMode.Solid;
        this.colorResource = colorResource;
    }

    public void setColorGradients(int colorForSmallestValue, int colorForLargestValue) {
        this.currentMode = ColorMode.Gradient;
        this.startColor = colorForSmallestValue;
        this.endColor= colorForLargestValue;
    }

}
