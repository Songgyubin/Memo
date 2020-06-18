package com.android.myapplication;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
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

        // 권한 체크
        int permiCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permiCheck == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        showCustomLayoutNotification();

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
                });
            }
        });

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

    private void showCustomLayoutNotification(){
        Log.d(TAG, "showCustomLayoutNotification: ");
        NotificationCompat.Builder mBuilder = createNotification();

        //커스텀 화면 만들기
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.activity_memo_notify);
        remoteViews.setImageViewResource(R.mipmap.ic_launcher, R.mipmap.ic_launcher);
        remoteViews.setTextViewText(R.id.title, "Title");
        remoteViews.setTextViewText(R.id.message, "message");

        //노티피케이션에 커스텀 뷰 장착
        mBuilder.setContent(remoteViews);
        mBuilder.setContentIntent(createPendingIntent());

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

    }
    /**
     * 노티피케이션을 누르면 실행되는 기능을 가져오는 노티피케이션
     *
     * 실제 기능을 추가하는 것
     * @return
     */
    private PendingIntent createPendingIntent(){
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        return stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    /**
     * 노티피케이션 빌드
     * @return
     */
    private NotificationCompat.Builder createNotification(){
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(icon)
                .setContentTitle("StatusBar Title")
                .setContentText("StatusBar subTitle")
                .setSmallIcon(R.mipmap.ic_launcher/*스와이프 전 아이콘*/)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        }
        return builder;
    }

    private static final String TAG = "MainActivity";


}