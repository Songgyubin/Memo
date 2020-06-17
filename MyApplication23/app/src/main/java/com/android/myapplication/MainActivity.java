package com.android.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton floating_btn;

    RecyclerView recyclerView;
    MemoAdapter adapter;


    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = AppDatabase.getInstance(this);

        floating_btn = (FloatingActionButton) findViewById(R.id.floating_btn);

        recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 메모 추가 버튼
        floating_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MeMoActivity.class);
                startActivity(intent);
            }
        });


        // fun showMemos
        showMemos();

        // 저장된 메모가 없을 때 Sample 데이터 적용
        /*if (memos.isEmpty()) {
            memos.add(new Memo("SampleTitle", "SampleContent", "", "20200202", false));
            adapter = new MemoAdapter(memos);
            recyclerView.setAdapter(adapter);
        } else {
            adapter = new MemoAdapter(memos);
        }
        recyclerView.setAdapter(adapter);*/
    }

    private void showMemos() {
        Log.d(TAG, "showMemos: ");
        db.memoDao().getAllMemos().observe(this, new Observer<List<Memo>>() {
            @Override
            public void onChanged(List<Memo> memos) {
                if (memos.isEmpty()) {
                    ArrayList<Memo> tmpMemos = new ArrayList<>();
                    tmpMemos.add(new Memo("SampleTitle", "SampleContent", "", "20200202", false));
                    adapter = new MemoAdapter(tmpMemos);
                } else {
                    adapter = new MemoAdapter((ArrayList<Memo>) memos);
                }
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);

            }
        });
    }

    private static final String TAG = "MainActivity";
}