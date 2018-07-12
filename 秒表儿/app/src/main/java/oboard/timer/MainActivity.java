package oboard.timer;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

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
        this.paused = false;
        this.handler.post(this.runnable);
        if (this.time == 0)
            this.time2 = System.currentTimeMillis();
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);            //竖屏
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
            handler.post(this);
            time = System.currentTimeMillis() - time2;

        }
    };

}
