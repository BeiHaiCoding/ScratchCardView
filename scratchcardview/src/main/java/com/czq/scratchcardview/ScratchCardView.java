package com.czq.scratchcardview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ScratchCardView extends View {

    private Bitmap scratchCard, grayCard;

    private Paint mPaint, mPathPaint;

    private Canvas grayCanvas;

    private Path mPath;

    int mWidth, mHeight;

    float mMoveX, mMoveY;

    //监听划出结果的线程
    private Thread mThread;

    //是否完成了控件测绘
    private boolean mIsInit;

    //当前刮开区域的像素值大小
    float mScratchSize;

    /**
     * 底部图片资源ID
     */
    private int mResId;
    /**
     * 刮开的路径的粗细
     */
    private int mScratchRadius;
    /**
     * 灰色蒙层的颜色
     */
    private int mMarkColor;

    /**
     * 刮出的结果的比例
     */
    private int mScratcheRate;


    //scratchCard的宽
    private int scratchCardWidth;

    //scratchCard的高
    private int scratchCardHeight;


    private boolean scratchFinished;


    public ScratchCardView(Context context) {
        this(context, null);
    }

    public ScratchCardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScratchCardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        handleStyleable(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        if (mScratcheRate <= 0 || mScratcheRate > 100) {
            mScratcheRate = 85;
        }
        //初始化
        mPaint = new Paint();
        mPaint.setColor(mMarkColor);

        mPath = new Path();

        mPathPaint = new Paint();
        mPathPaint.setColor(mMarkColor);
        mPathPaint.setStrokeWidth(mScratchRadius);
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setStrokeJoin(Paint.Join.ROUND);
        mPathPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));

        if (mResId != -1) {
            scratchCard = BitmapFactory.decodeResource(getResources(), mResId);
            scratchCardHeight = scratchCard.getHeight();
            scratchCardWidth = scratchCard.getWidth();
        }

    }


    private void handleStyleable(Context context, AttributeSet attrs, int defStyle) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ScratchCardView, defStyle, 0);
        try {
            mResId = ta.getResourceId(R.styleable.ScratchCardView_scratch_drawable, -1);
            mScratchRadius = ta.getDimensionPixelSize(R.styleable.ScratchCardView_scratch_radius, 40);
            mMarkColor = ta.getColor(R.styleable.ScratchCardView_scratch_mark_color, Color.LTGRAY);
            mScratcheRate = ta.getInteger(R.styleable.ScratchCardView_scratch_finish_rate, 85);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ta.recycle();
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = right - left;
        mHeight = bottom - top;
        initGrayCard();
        mIsInit = true;
    }

    private void initGrayCard() {
        grayCard = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        grayCanvas = new Canvas(grayCard);
        grayCanvas.drawColor(Color.GRAY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        //底层图片的缩放机制
        if (mResId != -1) {
            if (scratchCardWidth > mWidth && scratchCardHeight > mHeight) {
                canvas.drawBitmap(dealBitmap(scratchCard, mWidth / (scratchCardWidth + 0f),
                        mHeight / (scratchCardHeight + 0f)), 0, 0, null);
            } else if (scratchCardWidth > mWidth && scratchCardHeight < mHeight) {
                canvas.drawBitmap(dealBitmap(scratchCard, mWidth / (scratchCardWidth + 0f),
                        1), 0, (mHeight - scratchCardHeight) / 2, null);
            } else if (scratchCardWidth < mWidth && scratchCardHeight > mHeight) {
                canvas.drawBitmap(dealBitmap(scratchCard, 1, mHeight / (scratchCardHeight + 0f)),
                        (mWidth - scratchCardWidth) / 2, 0, null);
            } else if (scratchCardWidth <= mWidth && scratchCardHeight <= mHeight) {
                canvas.drawBitmap(scratchCard, (mWidth - scratchCardWidth) / 2,
                        (mHeight - scratchCardHeight) / 2, null);
            }
        }

        canvas.drawBitmap(grayCard, 0, 0, mPaint);

        grayCanvas.drawRect(0, 0, mWidth, mHeight, mPaint);
        grayCanvas.drawPath(mPath, mPathPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN: {
                mMoveX = event.getX();
                mMoveY = event.getY();
                mPath.moveTo(mMoveX, mMoveY);
                invalidate();
                return true;
            }

            case MotionEvent.ACTION_MOVE: {
                float endX = event.getX();
                float endY = event.getY();
                mPath.quadTo(endX, endY, endX, endY);
                invalidate();
                return true;
            }

            case MotionEvent.ACTION_UP: {
                float endX = event.getX();
                float endY = event.getY();
                if (endX == mMoveX && endY == mMoveY) {
                    mPath.quadTo(15 + endX, 15 + endY, endX, endY);
                    invalidate();
                }
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            while (!scratchFinished) {
                if (mThread.isInterrupted()) {
                    return;
                }
                SystemClock.sleep(500);
                if (mIsInit) {
                    for (int i = 0; i < mWidth; i++) {
                        for (int j = 0; j < mHeight; j++) {
                            if (grayCard.getPixel(i, j) == 0) {
                                mScratchSize++;
                            }
                        }
                    }
                    checkScratchSize();
                }
                mScratchSize = 0;
            }

        }
    };


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            if (mThread != null) {
                mThread.start();
            } else {
                mThread = new Thread(mRunnable);
                mThread.start();
            }
        } else {
            if (mThread != null) {
                mThread.interrupt();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mThread != null) {
            mThread.interrupt();
        }
    }

    private void checkScratchSize() {
        float totalArea = mHeight * mWidth;
        if (mScratchSize / totalArea >= (mScratcheRate / 100f)) {
            post(new Runnable() {
                @Override
                public void run() {
                    if (onScratchFinishedListener != null) {
                        onScratchFinishedListener.finish();
                    }
                }
            });
            scratchFinished = true;
        }
    }

    private OnScratchFinishedListener onScratchFinishedListener;

    public void setOnScratchFinishedListener(OnScratchFinishedListener onScratchFinishedListener) {
        this.onScratchFinishedListener = onScratchFinishedListener;
    }


    public interface OnScratchFinishedListener {
        void finish();
    }


    /**
     * 处理Bitmap的方法
     */

    private Bitmap dealBitmap(Bitmap bitmap, float widthRate, float heightRate) {
        Matrix matrix = new Matrix();
        matrix.postScale(widthRate, heightRate);  //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }


}
