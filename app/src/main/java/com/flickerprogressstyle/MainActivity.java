package com.flickerprogressstyle;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.flickerprogressstyle.view.FlickerLoadingProgress;

public class MainActivity extends AppCompatActivity implements Handler.Callback {

    private FlickerLoadingProgress flickBar;
    private Handler handler = new Handler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flickBar = (FlickerLoadingProgress) findViewById(R.id.flick_bar);
        flickBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessageDelayed(0, 200);
            }
        });
    }

    @Override
    public boolean handleMessage(Message msg) {

        float progress = flickBar.getCurProgress();
        if (progress > 100) {
            flickBar.stop();
            handler.removeMessages(0);
        } else {
            flickBar.setCurProgress(progress + 5);
            handler.sendEmptyMessageDelayed(0, 200);
        }

        return false;
    }
}
