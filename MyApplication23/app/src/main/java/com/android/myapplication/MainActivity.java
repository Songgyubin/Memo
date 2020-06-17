package com.android.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MemoAdapter adapter;
    ArrayList<Memo> memos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // fun addMemos


        if (memos.isEmpty()){
            memos.add(new Memo(0,"SampleTitle","SampleContent","","20200202",false));
            adapter = new MemoAdapter(memos);
        }else{

            adapter = new MemoAdapter(memos);
        }

        recyclerView.setAdapter(adapter);
    }

    private void addMemos(){

    }

}