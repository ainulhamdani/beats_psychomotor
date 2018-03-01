package org.beats.psychomotor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Dani on 19/02/2018.
 */
public class TouchView extends View implements View.OnTouchListener {
    Paint mPaint;
    float mX;
    float mY;
    TextView mTVCoordinates;

    public TouchView(Context context,AttributeSet attributeSet){
        super(context,attributeSet);

        /** Initializing the variables */
        mPaint = new Paint();
        mX = mY = -100;
        mTVCoordinates = null;
    }

    public void draw(float x,float y){
        mX = x;
        mY = y;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Setting the color of the circle
        mPaint.setColor(Color.GREEN);

        // Draw the circle at (x,y) with radius 15
        canvas.drawCircle(mX, mY, 15, mPaint);

        // Redraw the canvas
        invalidate();
    }

    public void setTextView(TextView tv){
        // Reference to TextView Object
        mTVCoordinates = tv;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        switch(event.getAction()){
//            // When user touches the screen
//            case MotionEvent.ACTION_DOWN:
//                // Getting X coordinate
//                mX = event.getX();
//                // Getting Y Coordinate
//                mY = event.getY();
//
//                // Setting the coordinates on TextView
//                if(mTVCoordinates!=null){
//                    mTVCoordinates.setText("X :" + mX + " , " + "Y :" + mY);
//                }
//        }
        return true;
    }

    public void setCoordinates(float x,float y){
        mX = x;
        mY = y;
        if(mTVCoordinates!=null){
            mTVCoordinates.setText("X :" + mX + " , " + "Y :" + mY);
        }
    }

    public String getCoordinates(){
        JSONObject json = new JSONObject();
        try {
            json.put("x", mX);
            json.put("y", mY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
