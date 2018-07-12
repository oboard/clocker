package oboard.timer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import java.text.SimpleDateFormat;

public class ClockSurfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable {
    private SurfaceHolder mHolder;
    private Canvas mCanvas;//绘图的画布
    private boolean mIsDrawing;//控制绘画线程的标志位

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
            
            paint.setStrokeWidth(3.0f);
            paint.setStyle(Paint.Style.STROKE);
            RectF rectF = new RectF(
                100,
                (mCanvas.getHeight() - mCanvas.getWidth() + 200) / 2,
                mCanvas.getWidth() - 100,
                (mCanvas.getHeight() + mCanvas.getWidth() - 200) / 2
            );
            RectF rectF2 = new RectF(
                (mCanvas.getWidth() - mCanvas.getWidth() / 10) / 2,
                (mCanvas.getHeight() + mCanvas.getWidth()) / 2 - mCanvas.getWidth() / 5f - 100,
                (mCanvas.getWidth() + mCanvas.getWidth() / 10) / 2,
                (mCanvas.getHeight() + mCanvas.getWidth()) / 2 - mCanvas.getWidth() / 10f - 100
            );
            paint.setColor(Color.argb(50, 255, 255, 255));
            mCanvas.drawOval(rectF, paint);
            mCanvas.drawOval(rectF2, paint);
            paint.setColor(Color.WHITE);
            long time = MainActivity.time;
            float arca1 = 360 * (time % 60000) / 60000;
            mCanvas.drawArc(rectF, arca1, 10, false, paint);
            float arca2 = 360 * (time % 1000) / 1000;
            mCanvas.drawArc(rectF2, arca2, 1, false, paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(100);
            paint.setTextAlign(Paint.Align.CENTER);
            mCanvas.drawText(new SimpleDateFormat("mm:ss:SSS").format(time), mCanvas.getWidth() / 2, mCanvas.getHeight() / 2, paint);

            
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
