package com.android.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;

public class MemoNotifyDialog extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_notify);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |

                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        //WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

    }
}
