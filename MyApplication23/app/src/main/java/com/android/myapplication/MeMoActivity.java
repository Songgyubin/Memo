package com.android.myapplication;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;

public class MeMoActivity extends AppCompatActivity {

    TextView edit_toolbar_date_tv;
    Button edit_toolbar_save_btn;
    EditText edit_title_ed;
    EditText edit_content_ed;
    ImageButton camera_btn;
    ImageButton gallery_btn;

    AppDatabase db;

    String imgUrl = "";


    // localDate.now 함수 사용하기 위한 버전 관리
    @RequiresApi(api = Build.VERSION_CODES.O)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        edit_toolbar_date_tv = (TextView) findViewById(R.id.edit_toolbar_date_tv);
//        edit_toolbar_save_btn = (Button) findViewById(R.id.edit_toolbar_save_btn);
        edit_title_ed = (EditText) findViewById(R.id.edit_title_ed);
        edit_content_ed = (EditText) findViewById(R.id.edit_content_ed);
        /*camera_btn = (ImageButton) findViewById(R.id.camera_btn);
        gallery_btn = (ImageButton) findViewById(R.id.gallery_btn);*/

        db = AppDatabase.getInstance(this);

        // 오늘 날짜
        String date = LocalDate.now().toString().replace('-', '.');
        edit_toolbar_date_tv.setText(date);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_toolbar_save_btn:
                saveMemo();
                break;

            case R.id.camera_btn:

                break;

            case R.id.gallery_btn:

                break;

        }
    }
    private void saveMemo() {
        if (edit_content_ed.getText().toString().isEmpty()) {
            Toast.makeText(this, "내용을 입력하세요", Toast.LENGTH_SHORT).show();
        } else {
            String title = "";
            String content = "";
            String date = edit_toolbar_date_tv.getText().toString();

            // title empty
            if (edit_title_ed.getText().toString().isEmpty()) {
                title = edit_title_ed.getHint().toString();
            } else {
                title = edit_title_ed.getText().toString();
            }
            content = edit_content_ed.getText().toString();
            new InsertAsyncTask(db.memoDao()).execute(new Memo(title,content,imgUrl,date,false));
            Toast.makeText(this, "저장되었습니다", Toast.LENGTH_SHORT).show();
//            finish();
        }

    }

    public static class InsertAsyncTask extends AsyncTask<Memo, Void, Void> {
        private MemoDao memoDao;

        public InsertAsyncTask(MemoDao memoDao) {
            this.memoDao = memoDao;
        }

        @Override
        protected Void doInBackground(Memo... memos) {
            memoDao.insertMemo(memos[0]);
            return null;
        }
    }

}
