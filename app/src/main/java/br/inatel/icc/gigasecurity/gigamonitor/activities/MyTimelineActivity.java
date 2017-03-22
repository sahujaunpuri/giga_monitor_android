package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.WindowManager;
import android.widget.TableLayout;

import br.inatel.icc.gigasecurity.gigamonitor.R;

/**
 * Created by zappts on 3/22/17.
 */

public class MyTimelineActivity extends ActionBarActivity {
    private TableLayout tableLayout;
    public Context mContext;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        tableLayout = (TableLayout) findViewById(R.id.table_layout);
        mContext = this;
    }
}
