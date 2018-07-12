package oboard.timer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.text.SimpleDateFormat;
import android.animation.ValueAnimator;

public class ClockSurfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable {
    private SurfaceHolder mHolder;
    private Canvas mCanvas;//绘图的画布
    private boolean mIsDrawing;//控制绘画线程的标志位
    private Bitmap blurBitmap;
    private int blurAlpha = 0;

    public ClockSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ClockSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public ClockSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
    }

    private void initView() {
        mHolder = getHolder();//获取SurfaceHolder对象
        mHolder.addCallback(this);//注册SurfaceHolder的回调方法
        mHolder.setFormat(PixelFormat.TRANSPARENT);//设置背景透明  
        setFocusable(true);
        setZOrderOnTop(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);
    }

    public void fresh() {
        //刷新动画
        blurBitmap = this.getDrawingCache();
        blurBitmap = FastBlur.rsBlur(getContext(), blurBitmap, 25);
        ValueAnimator v = ValueAnimator.ofInt(0, 255).setDuration(225);
        v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator v) {
                blurAlpha = v.getAnimatedValue();
            }
        });
        v = ValueAnimator.ofInt(255, 0).setDuration(225);
        v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator v) {
                    blurAlpha = v.getAnimatedValue();
                }
            });
    }
    

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsDrawing = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder,
                               int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsDrawing = false;
    }



    //绘图操作
    private void draw() {
        try {
            mCanvas = mHolder.lockCanvas();//获取mCanvas对象进行绘制
            //SurfaceView背景
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//透明
            mCanvas.drawColor(Color.argb(50, 0, 0, 0));
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            final float mw = mCanvas.getWidth(), mh = mCanvas.getHeight();
            RectF rectF = new RectF(
                mw / 10,
                (mh - mw + mw / 5) / 2,
                mw - mw / 10,
                (mh + mw - mw / 5) / 2
            );
            RectF rectF2 = new RectF(
                (mw - mw / 10) / 2,
                (mh + mw) / 2 - mw / 5f - 100,
                (mw + mw / 10) / 2,
                (mh + mw) / 2 - mw / 10f - 100
            );
            paint.setColor(Color.argb(50, 255, 255, 255));
            //mCanvas.drawOval(rectF, paint);
            //圆圈边框
            paint.setStrokeWidth(mw / 50);
            mCanvas.drawOval(rectF2, paint);
            paint.setStrokeWidth(mw / 20);
            for (int i = 0; i < 360; i++) {
                mCanvas.drawArc(rectF, i, 0.5f, false, paint);
            }
            
            //大圈指示
            paint.setColor(Color.WHITE);
            long time = MainActivity.time;
            int arca1 = (int)(360 * (time % 60000) / 60000);
            mCanvas.drawArc(rectF, arca1, 0.5f, false, paint);
            
            //小圈指示
            paint.setStrokeWidth(mw / 50);
            float arca2 = 360 * (time % 1000) / 1000;
            mCanvas.drawArc(rectF2, arca2, mw / 50, false, paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(100);
            paint.setTextAlign(Paint.Align.CENTER);
            mCanvas.drawText(new SimpleDateFormat("mm:ss:SS").format(time), mw / 2, mh / 2, paint);

            
            if(blurAlpha != 0) {
                paint.setAlpha(blurAlpha);
                mCanvas.drawBitmap(blurBitmap, 0, 0, paint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);//保证绘制的画布内容提交
            }
        }
    }

    /**每30帧刷新一次屏幕**/
    public static final int TIME_IN_FRAME = 30;
    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        while (mIsDrawing) {
            draw();
        }

        /**取得更新结束的时间**/
        long endTime = System.currentTimeMillis();

        /**计算出一次更新的毫秒数**/
        int diffTime  = (int)(endTime - startTime);

        /**确保每次更新时间为30帧**/
        while (diffTime <= TIME_IN_FRAME) {
            diffTime = (int)(System.currentTimeMillis() - startTime);
            /**线程等待**/
            Thread.yield();
        }
	}

}
