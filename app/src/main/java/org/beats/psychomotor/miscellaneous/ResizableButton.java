package org.beats.psychomotor.miscellaneous;

import android.content.Context;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatButton;

/**
 * Created by Dani on 21/02/2018.
 */

public class ResizableButton extends AppCompatButton {
    public ResizableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, width);
    }
}
