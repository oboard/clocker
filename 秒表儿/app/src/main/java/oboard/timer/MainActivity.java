package oboard.timer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class MainActivity extends Activity 
{  
	FrameLayout local;
 	boolean paused = false;
  	long time = 0;
	Bitmap clock;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymain);

		//透明
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
			localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
		}

		local = (FrameLayout)findViewById(R.id.mainlayout);

		local.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view)
				{
					if (paused)
						Start();
				}
			});
    }


	Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run()
		{
			if (paused) return;
           	handler.postDelayed(this, 1000);
            time++;

			clock = Bitmap.createBitmap(local.getWidth(), local.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(clock);
			Paint p = new Paint();
			drawClock(c p);

        }
    };

    private void Start()
	{
		paused = true;
        handler.postDelayed(runnable, 1000);
    }
	private void Pause()
	{
        paused = false;
    }
	private void Stop()
	{
       	Pause();
		time = 0;
    }


	//DRAW
	private void drawClock(Canvas canvas, Paint paint)
	{
		paint.setAntiAlias(true);                       //设置画笔为无锯齿  
		paint.setColor(Color.BLACK);                    //设置画笔颜色  
		canvas.drawColor(Color.WHITE);                  //白色背景  
		paint.setStrokeWidth((float) 3.0);              //线宽  
		paint.setStyle(Paint.Style.STROKE);  

		RectF oval = new RectF();                     //RectF对象  
		oval.left = 100;                              //左边  
		oval.top = 100;                                   //上边  
		oval.right = 400;                             //右边  
		oval.bottom = 300;                                //下边  
		canvas.drawArc(oval, 225, 90, false, paint);    //绘制圆弧  
	}

}
