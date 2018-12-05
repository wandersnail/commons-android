package cn.zfs.commonsdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.zfs.commons.AppHolder;


/**
 * Created by zeng on 2016/6/16.
 */
public class BaseActivity extends AppCompatActivity {
    protected InputMethodManager imm;
    public boolean visible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
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
        AppHolder.getMainHandler().removeCallbacks(showIMRunnable);
        super.onStop();
    }

    public void postShowInputMethod(EditText et) {
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        AppHolder.getMainHandler().postDelayed(showIMRunnable, 500);
    }

    private Runnable showIMRunnable = new Runnable() {
        @Override
        public void run() {
            showInputMethod();
        }
    };

    /**
     * 打开软键盘
     */
    public void showInputMethod() {
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 关闭键盘
     */
    public void  hideInputMethod(EditText editText){
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0); //隐藏
    }
}
