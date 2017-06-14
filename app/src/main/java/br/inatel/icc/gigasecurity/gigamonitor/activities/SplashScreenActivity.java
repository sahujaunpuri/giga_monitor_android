package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import java.util.Timer;
import java.util.TimerTask;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;

public class SplashScreenActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        } else if (getActionBar() != null ) {
            getActionBar().hide();
        }


/*        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        };

        task.execute();*/

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(SplashScreenActivity.this, DeviceListActivity.class);
//                intent.setClass(SplashScreenActivity.this, MyTimelineActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);

    }


}
