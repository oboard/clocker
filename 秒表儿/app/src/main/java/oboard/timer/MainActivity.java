package oboard.timer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class MainActivity
extends Activity
{
    public static FrameLayout local;
    Handler handler = new Handler();
    boolean paused = true;
    long time = 0;

    private void Pause()
    {
        this.paused = true;
    }

    private void Start()
    {
        this.paused = false;
        this.handler.postDelayed(this.runnable, 1);
    }

    private void Stop()
    {
        this.Pause();
        this.time = 0;
    }

    private void drawClock()
    {
        Bitmap clock = Bitmap.createBitmap((int)MainActivity.local.getWidth(), (int)MainActivity.local.getHeight(), (Bitmap.Config)Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(clock);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(-1);
        paint.setTextSize(50);
        canvas.drawColor(Color.argb(50, 0, 0, 0));
        paint.setStrokeWidth(3.0f);
        paint.setStyle(Paint.Style.STROKE);
        RectF rectF = new RectF();
        rectF.left = 100;
        rectF.top = (canvas.getHeight() - canvas.getWidth() + 200) / 2;
        rectF.right = -100 + canvas.getWidth();
        rectF.bottom = (canvas.getHeight() + canvas.getWidth() - 200) / 2;
        canvas.drawArc(rectF, 0, (float)360/(time % 60), false, paint);
        canvas.drawText(new StringBuffer().append("").append(this.time).toString(), canvas.getWidth() / 2, canvas.getHeight() / 2, paint);
        local.setBackground((Drawable)new BitmapDrawable(clock));
    }

    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        this.setContentView(R.layout.activitymain);
        if (Build.VERSION.SDK_INT >= 19)
        {
            WindowManager.LayoutParams layoutParams = this.getWindow().getAttributes();
            layoutParams.flags = 67108864 | layoutParams.flags;
        }
        local = (FrameLayout)this.findViewById(R.id.mainlayout);
        local.setClickable(true);
        local.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view)
                {
                    if (paused)
                    {
                        Start();
                        return;
                    }
                    Pause();
                }
            });
    }


    Runnable runnable = new Runnable() {

        @Override
        public void run()
        {
            if (paused)
                return;
            handler.postDelayed(this, 1);
            time++;
            
            drawClock();
        }
    };

}
