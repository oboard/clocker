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

public class MainActivity extends Activity
{
    public static ClockSurfaceView local;
    Handler handler = new Handler();
    public static boolean paused = true;
    public static long time = 0, time2 = 0;

    private void Pause()
    {
        this.paused = true;
    }

    private void Start()
    {
        if (this.time == 0)
            this.time2 = System.currentTimeMillis();
        this.paused = false;
        this.handler.postDelayed(this.runnable, 1);
    }

    private void Stop()
    {
        this.Pause();
        this.time = 0;
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
        local = (ClockSurfaceView)this.findViewById(R.id.surface);
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
            handler.postDelayed(this, 0);
            time = System.currentTimeMillis() - time2;

        }
    };

}
