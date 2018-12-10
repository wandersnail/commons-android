package cn.zfs.commonsdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


/**
 * Created by zeng on 2016/6/16.
 */
public class BaseActivity extends AppCompatActivity {
    public boolean visible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        visible = true;
    }

    @Override
    protected void onStop() {
        visible = false;
        super.onStop();
    }
}
