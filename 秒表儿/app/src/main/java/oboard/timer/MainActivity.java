package oboard.timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import oboard.timer.MainActivity;

public class MainActivity extends Activity {
    public static ClockSurfaceView local;
    Handler handler = new Handler();
    public static boolean paused = true;
    public static long time = 0, time2 = 0;
    private void Pause() {
        this.paused = true;
    }

    private void Start() {
        this.paused = false;
        this.handler.post(this.runnable);
        if (this.time == 0)
            this.time2 = System.currentTimeMillis();
    }

    private void Stop() {
        this.Pause();
        this.time = 0;

        local.fresh();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(R.layout.activitymain);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);            //竖屏
        if (Build.VERSION.SDK_INT >= 19) {
            WindowManager.LayoutParams layoutParams = this.getWindow().getAttributes();
            layoutParams.flags = 67108864 | layoutParams.flags;
        }
        local = (ClockSurfaceView)this.findViewById(R.id.surface);
        local.setClickable(true);
        local.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    if (paused) {
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
                    if (paused)
                        Stop();
                    return true;
                }
            });
        //local.setDrawingCacheEnabled(true);


        SharedPreferences shared = getSharedPreferences("is", MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        if (shared.getBoolean("isfer", true)) {
            //首次启动
            new AlertDialog.Builder(MainActivity.this)
                //    设置Title
                .setTitle("说明")
                //    设置Content来显示一个信息
                .setMessage("单击/n开始/暂停/n/n长按/n清零")
                //    设置一个PositiveButton
                .setPositiveButton("知道啦！", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "positive: " + which, Toast.LENGTH_SHORT).show();
                    }
                }).show();
            editor.putBoolean("isfer", false)
                .commit();
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (paused)
                return;
            handler.post(this);
            time = System.currentTimeMillis() - time2;

        }
    };

}
