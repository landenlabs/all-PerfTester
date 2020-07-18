/**
 * Copyright (c) 2017 Dennis Lang (LanDen Labs) landenlabs@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Dennis Lang  (1/10/2017)
 * @see http://landenlabs.com
 *
 */

package com.landenlabs.allperfimages.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.landenlabs.allperfimages.R;

/**
 * @author Dennis Lang (LanDen Labs)
 * @see <a href="http://landenlabs.com/android/index-m.html"> author's web-site </a>
 */

public class CompatSeekBar extends androidx.appcompat.widget.AppCompatSeekBar {

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    int mTickColor = 0xc0c08080;
    float mMinTic = 0;
    float mMaxTic = getMax();
    float mTickStep = 10;
    float mTickWidth = 20;
    boolean mTickUnder = false;

    int mTickMark = -1;
    Drawable mTickMarkDr;


    public CompatSeekBar(Context context) {
        super(context);
        initCompSeekkBar(null, 0);
    }

    /*
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CompatSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initCompSeekkBar(attrs, defStyleAttr);
    }
    */

    public CompatSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCompSeekkBar(attrs, defStyleAttr);
    }

    public CompatSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCompSeekkBar(attrs, 0);
    }

    private void initCompSeekkBar(AttributeSet attrs, int defStyleAttr) {
        if (isInEditMode())
            return;
        // mMaxTic = getMax();

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.custom_SeekBar, defStyleAttr,
                android.R.style.Widget_SeekBar);

        mMinTic = a.getInt(R.styleable.custom_SeekBar_tickMin, 0);
        mMaxTic = a.getInt(R.styleable.custom_SeekBar_tickMax, getMax());
        mTickStep = a.getInt(R.styleable.custom_SeekBar_tickStep, getMax());

        mTickWidth = a.getInt(R.styleable.custom_SeekBar_tickWidth, 20);
        mTickUnder = a.getBoolean(R.styleable.custom_SeekBar_tickUnder, false);

        mTickMark = a.getResourceId(R.styleable.custom_SeekBar_tickMark, -1);
        int defColor = (mTickMark == -1) ? 0xff101010 : 0;
        mTickColor = a.getColor(R.styleable.custom_SeekBar_tickColor, defColor);

        a.recycle();

        if (mTickMark != -1) {
            mTickMarkDr = getResources().getDrawable(mTickMark);
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        if (!isInEditMode()) {
        }

        if (mTickUnder)
            drawTicks(canvas);

        super.onDraw(canvas);

        if (!mTickUnder)
            drawTicks(canvas);
    }

    protected  void drawTicks(Canvas canvas) {
        if (mTickWidth > 0 && (mTickColor != 0 || mTickMarkDr != null)) {
            float dX = mTickWidth / 2;
            float ticHeight = getHeight();

            int pixelWidth = (getWidth() - getPaddingLeft() - getPaddingRight());
            float per1 = mMinTic / getMax();
            float per2 = mMaxTic / getMax();
            float perStep =  mTickStep / getMax();

            paint.setColor(mTickColor);

            if (perStep > 0)
            for (float xPer = per1; xPer < per2; xPer += perStep) {
                float x = xPer * pixelWidth + getPaddingLeft();
                drawTick(canvas, new RectF(x - dX, 0, x + dX, ticHeight), paint);
            }

            float x = per2 * pixelWidth + getPaddingLeft();
            drawTick(canvas, new RectF(x - dX, 0, x + dX, ticHeight), paint);
        }
    }

    protected  void drawTick(Canvas canvas, RectF rectF, Paint paint) {
        if (mTickMarkDr == null) {
            canvas.drawOval(rectF, paint);
        } else {
            mTickMarkDr.setBounds((int)rectF.left, (int)rectF.top, (int)rectF.right, (int)rectF.bottom);
            mTickMarkDr.draw(canvas);
        }
    }
}
