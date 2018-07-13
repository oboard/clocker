package oboard.timer;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
    public static Bitmap blurBitmap;
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
        
       
        View view = getWindow().getDecorView();
        blurBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(blurBitmap);
        view.draw(canvas);
        blurBitmap = FastBlur.rsBlur(MainActivity.this, blurBitmap, 25);
        
        local.fresh();
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
         local.setLongClickable(true);
         local.setOnLongClickListener(new View.OnLongClickListener() {
             @Override
             public boolean onLongClick(View view) {
                 Stop();
                 return true;
             }
         });
         //local.setDrawingCacheEnabled(true);
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
