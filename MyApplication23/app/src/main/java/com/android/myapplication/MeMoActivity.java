package com.android.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class MemoActivity extends AppCompatActivity {

    TextView edit_toolbar_date_tv;
    Button edit_toolbar_save_btn;
    EditText edit_title_ed;
    EditText edit_content_ed;
    ImageButton camera_btn;
    ImageButton gallery_btn;
    ImageView edit_iv_picture;
    AppDatabase db;


    String imgUrl = "";
    Uri photoUri;
    String imageFilePath;

    // Edit
    String title="";
    String content = "";
    int id = Integer.MIN_VALUE;
    boolean isChecked = false;

    String type = "";


    // localDate.now 함수 사용하기 위한 버전 관리
    @RequiresApi(api = Build.VERSION_CODES.O)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        // 수정모드 or 작성모드
        type = getIntent().getStringExtra("type");


        edit_toolbar_date_tv = (TextView) findViewById(R.id.edit_toolbar_date_tv);
//        edit_toolbar_save_btn = (Button) findViewById(R.id.edit_toolbar_save_btn);
        edit_title_ed = (EditText) findViewById(R.id.edit_title_ed);
        edit_content_ed = (EditText) findViewById(R.id.edit_content_ed);
        edit_iv_picture = (ImageView) findViewById(R.id.edit_iv_picture);


        db = AppDatabase.getInstance(this);

        // 오늘 날짜
        String date = LocalDate.now().toString().replace('-', '.');
        edit_toolbar_date_tv.setText(date);


        // 수정모드
        if (type.equals("edit")){
            Log.d(TAG, "onCreate: edit");

            Memo memo = (Memo) getIntent().getSerializableExtra("memo");
            edit_title_ed.setText(memo.getTitle());
            edit_content_ed.setText(memo.getContent());
            id = memo.getId();
            isChecked = memo.isChecked();
            if (TextUtils.isEmpty(memo.getImage())) {
                Picasso.get().load(R.mipmap.ic_launcher).into(edit_iv_picture);
            } else {
                Picasso.get().load(memo.getImage())
                        .centerCrop()
                        .fit()
                        .error(R.drawable.noavailable)
                        .into(edit_iv_picture);
                imgUrl = memo.getImage();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 카메라 작동시
        if (requestCode == CAMERA) {
            if (TextUtils.isEmpty(photoUri.toString())) {
                Picasso.get().load(R.mipmap.ic_launcher).into(edit_iv_picture);
            } else {
                Picasso.get().load(photoUri.toString())
                        .centerCrop()
                        .fit()
                        .error(R.drawable.noavailable)
                        .into(edit_iv_picture);
                imgUrl = photoUri.toString();
            }
        }
        // 갤러리 작동시
        else if ( requestCode ==GALLERY){
            photoUri = data.getData();
            if (TextUtils.isEmpty(photoUri.toString())) {
                Picasso.get().load(R.mipmap.ic_launcher).into(edit_iv_picture);
            } else {

                Picasso.get().load(photoUri.toString())
                        .centerCrop()
                        .fit()
                        .error(R.drawable.noavailable)
                        .into(edit_iv_picture);
                imgUrl = photoUri.toString();
            }
        }

    }

    // 각 버튼 클릭 메소드
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_toolbar_save_btn:
                saveMemo(id);
                break;

            case R.id.camera_btn:
                sendTakePhotoIntent();
                break;

            case R.id.gallery_btn:
                getPhoto();
                break;
            case R.id.edit_toolbar_back_btn:
                finish();
                break;

        }
    }

    //https://developer.android.com/training/camera/photobasics?hl=ko#java
   // 카메라에서 바로 사진 가져오기 start
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".jpg",         /* suffix */
                storageDir          /* directory */
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void sendTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, CAMERA);
            }
        }
    }
    // end



    // 갤러리에서 사진 가져오기
    private void getPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent,GALLERY);
    }


    private void saveMemo(int id) {
            this.id = id;
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

            if (id == Integer.MIN_VALUE){
            new InsertAsyncTask(db.memoDao()).execute(new Memo(0,title, content, imgUrl, date, false));
            }
            else{
                new InsertAsyncTask(db.memoDao()).execute(new Memo(id,title,content,imgUrl,date,isChecked));
            }
            Toast.makeText(this, "저장되었습니다", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    // Room에 메모 백그라운드 저장
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

    private static final String TAG = "MeMoActivity";
    private static final int CAMERA = 1;
    private static final int GALLERY = 2;

}
