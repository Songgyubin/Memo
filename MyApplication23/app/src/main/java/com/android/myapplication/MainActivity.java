package com.android.myapplication;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "checkedmemo";
    FloatingActionButton floating_btn;

    RecyclerView recyclerView;
    MemoAdapter adapter;

    NotificationManager notificationManager;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 권한 체크
        int permiCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permiCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }


        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
        db = AppDatabase.getInstance(this);

        floating_btn = (FloatingActionButton) findViewById(R.id.floating_btn);

        recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 메모 추가 버튼
        floating_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), MemoActivity.class);
                intent.putExtra("type", "new");
                startActivity(intent);
            }
        });

        // fun showMemos
        // RoomDB example
        // https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#4
        //
        db.memoDao().getAllMemos().observe(this, new Observer<List<Memo>>() {
            @Override
            public void onChanged(List<Memo> memos) {
//
                adapter = new MemoAdapter((ArrayList<Memo>) memos);
                recyclerView.setAdapter(adapter);

                // 원 클릭 시 수정 화면
                adapter.setOnItemClickListener(new MemoAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Intent intent = new Intent(MainActivity.this, MemoActivity.class);
                        intent.putExtra("memo", memos.get(position));
                        intent.putExtra("type", "edit");

                        startActivity(intent);

                    }

                    // 롱클릭 시 삭제
                    @Override
                    public void onItemLongClick(View v, int position) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("알림").setMessage("삭제를 원합니까?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new DeleteAsyncTask(db.memoDao()).execute(memos.get(position));
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }

                    @Override
                    public void onCheckClick(View v, int position) {
                        new UpdateAsyncTask(db.memoDao()).execute(memos.get(position));
                        adapter.notifyDataSetChanged();
                        /*if (!memos.get(position).isChecked()){
                            showCustomLayoutNotification(memos.get(position));}
                        else {
                            notificationManager.cancelAll();
                        }*/
                    }
                });
            }
        });

    }

    public class UpdateAsyncTask extends AsyncTask<Memo, Void, Void> {
        private MemoDao memoDao;
        private Memo memo;

        public UpdateAsyncTask(MemoDao memoDao) {
            this.memoDao = memoDao;
        }

        @Override
        protected Void doInBackground(Memo... memos) {
            memo = memos[0];
            if (memo.isChecked()) {
                memo.setChecked(false);
            } else {
                memo.setChecked(true);
            }
            memoDao.insertMemo(memo);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (memo.isChecked())
                showCustomLayoutNotification(memo);
            else
                notificationManager.cancelAll();

        }
    }

    public static class DeleteAsyncTask extends AsyncTask<Memo, Void, Void> {
        private MemoDao memoDao;

        public DeleteAsyncTask(MemoDao memoDao) {
            this.memoDao = memoDao;
        }

        @Override
        protected Void doInBackground(Memo... memos) {
            memoDao.deleteMemoById(memos[0].getId());
            return null;
        }

    }

    public void showCustomLayoutNotification(Memo memo) {
        Log.d(TAG, "showCustomLayoutNotification: ");

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.activity_memo_notify);
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        contentView.setTextViewText(R.id.title, "Custom notification");
        contentView.setTextViewText(R.id.text, "This is a custom layout");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.checked_star)
                .setContentTitle(memo.getTitle())
                .setContentText(memo.getContent())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        notificationManager.notify(1, notification);




        /*NotificationCompat.Builder mBuilder = createNotification();

        //커스텀 화면 만들기
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.activity_memo_notify);
        remoteViews.setImageViewResource(R.mipmap.ic_launcher, R.mipmap.ic_launcher);

        //노티피케이션에 커스텀 뷰 장착
        mBuilder.setContent(remoteViews);
        mBuilder.setContentIntent(createPendingIntent());

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());*/

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static final String TAG = "MainActivity";


}