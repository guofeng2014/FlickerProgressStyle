package com.flickerprogressstyle.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;

import com.flickerprogressstyle.R;

/**
 * 作者：guofeng
 * ＊ 日期:16/10/11
 */

public class FlickerLoadingProgress extends View implements Runnable {

    private float MAX_PROGRESS = 100;
    private float curProgress;
    private boolean isStart = true;
    private final int dividerColor;
    private final int progressColor;
    private Paint mPaint;
    private Paint fPaint;
    private int leftMargin;
    private Canvas mCanvas;
    private Bitmap flickBitmap;
    private Bitmap cBitmap;
    private Xfermode mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);

    public FlickerLoadingProgress(Context context) {
        this(context, null);
    }

    public FlickerLoadingProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlickerLoadingProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dividerColor = getResources().getColor(android.R.color.holo_blue_bright);
        progressColor = getResources().getColor(android.R.color.holo_purple);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = 0;
        switch (heightSpecMode) {
            case MeasureSpec.AT_MOST:
                height = 100;
                break;
            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
                height = heightSpecSize;
                break;
        }
        setMeasuredDimension(widthSpecSize, height);
        init();
        new Thread(this).start();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(false);
        mPaint.setColor(dividerColor);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.STROKE);

        fPaint = new Paint();
        fPaint.setStyle(Paint.Style.FILL);
        fPaint.setAntiAlias(false);
        fPaint.setXfermode(mode);
        mPaint.setStrokeWidth(0);

        flickBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_flicker);
        cBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(cBitmap);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBorder(canvas);
        drawProgress();
        canvas.drawBitmap(cBitmap, 0, 0, null);
    }


    private void drawBorder(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth()-2, getHeight()-2, mPaint);
    }

    private void drawProgress() {
        float right = curProgress / MAX_PROGRESS * getMeasuredWidth();
        mCanvas.save(Canvas.CLIP_SAVE_FLAG);
        mCanvas.clipRect(0, 0, right, getMeasuredHeight());
        mCanvas.drawColor(progressColor);
        mCanvas.restore();
        mCanvas.drawBitmap(flickBitmap, leftMargin, 0, fPaint);
    }

    public void setCurProgress(float curProgress) {
        this.curProgress = curProgress;
        postInvalidate();
    }

    public float getCurProgress() {
        return curProgress;
    }

    public void stop() {
        isStart = false;
    }

    @Override
    public void run() {
        int width = flickBitmap.getWidth();
        while (isStart) {
            leftMargin += 10;
            float right = curProgress / MAX_PROGRESS * getMeasuredWidth();
            if (leftMargin >= right) {
                leftMargin = -width;
            }
            postInvalidate();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
