package com.dovar.borderradius;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by heweizong on 2018/4/10.
 */

public class BorderView extends View {
    private Paint mPaint;
    private Path mPath;

    private int borderColor = Color.BLACK;
    private
    @ORI
    int mPathOri = ORI_RIGHT_BOTTOM;

    public BorderView(Context context) {
        super(context);
        init(null);
    }

    public BorderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BorderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.BorderView);
            mPathOri = array.getInt(R.styleable.BorderView_borderOri, 0);
            borderColor = array.getColor(R.styleable.BorderView_borderColor, Color.BLACK);
            array.recycle();
        }
        setupPaint();
    }

    public void setBorderColor(int mBorderColor) {
        borderColor = mBorderColor;
        mPaint.setColor(borderColor);
        postInvalidate();
    }

    private void setupPaint() {
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setColor(borderColor);
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.FILL);
        }
    }

    private void setupPath() {
        if (mPath == null) {
            mPath = new Path();
        } else {
            mPath.reset();
        }
        float left = getPaddingLeft();
        float top = getPaddingTop();
        float right = left + getWidth();
        float bottom = top + getHeight();
        switch (mPathOri) {
            case ORI_LEFT_TOP:
                mPath.moveTo(right, top);
                mPath.arcTo(new RectF(right - 2 * getWidth(), bottom - 2 * getHeight(), right, bottom), 0, 90);
                mPath.lineTo(right, bottom);
                mPath.close();
                break;
            case ORI_LEFT_BOTTOM:
                mPath.moveTo(left, top);
                mPath.arcTo(new RectF(right - 2 * getWidth(), top, right, top + 2 * getHeight()), 270, 90);
                mPath.lineTo(right, top);
                mPath.close();
                break;
            case ORI_RIGHT_TOP:
                mPath.moveTo(getPaddingLeft(), getPaddingTop());
                mPath.lineTo(getPaddingLeft(), getPaddingTop() + getHeight());
                mPath.lineTo(getPaddingLeft() + getWidth(), getPaddingTop() + getHeight());
                mPath.arcTo(new RectF(getPaddingLeft(), getPaddingTop() - getHeight(), getPaddingLeft() + 2 * getWidth(), getPaddingTop() + getHeight()), 90, 90);
                mPath.close();
                break;
            case ORI_RIGHT_BOTTOM:
                mPath.moveTo(getPaddingLeft(), getPaddingTop());
                mPath.lineTo(getPaddingLeft(), getPaddingTop() + getHeight());
                mPath.arcTo(new RectF(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + 2 * getWidth(), getPaddingTop() + 2 * getHeight()), 180, 90);
                mPath.close();
                break;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setupPath();
        canvas.drawPath(mPath, mPaint);
    }

    public static final int ORI_LEFT_TOP = 0;
    public static final int ORI_LEFT_BOTTOM = 1;
    public static final int ORI_RIGHT_TOP = 2;
    public static final int ORI_RIGHT_BOTTOM = 3;

    @IntDef({ORI_LEFT_TOP, ORI_LEFT_BOTTOM, ORI_RIGHT_TOP, ORI_RIGHT_BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ORI {
    }
}
