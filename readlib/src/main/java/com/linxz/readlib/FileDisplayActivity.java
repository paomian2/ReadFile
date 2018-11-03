package com.linxz.readlib;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.linxz.permissionlib.LinxzPermissionUtils;
import com.linxz.permissionlib.listener.PermissionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileDisplayActivity extends AppCompatActivity {

    private String TAG = "FileDisplayActivity";
    SuperFileView mSuperFileView;

    String filePath;
    private final static String PATH="path";
    private String fileName;
    private final static String NAME_FILE="fileName";
    private String folderPath;
    private final static String EXA_FOLDER_PATH="folderPath";

    public static void launch(Activity activity,String filePath,String folderPath,String fileName){
        Intent intent=new Intent(activity,FileDisplayActivity.class);
        intent.putExtra(PATH,filePath);
        intent.putExtra(EXA_FOLDER_PATH,folderPath);
        intent.putExtra(NAME_FILE,fileName);
        activity.startActivity(intent);
    }

    public void onGetBundle() {
        Intent intent = this.getIntent();
        String path = (String) intent.getSerializableExtra("path");
        folderPath=intent.getStringExtra(EXA_FOLDER_PATH);
        fileName=intent.getStringExtra(NAME_FILE);
        if (!TextUtils.isEmpty(path)) {
            TLog.d(TAG, "文件path:" + path);
            setFilePath(path);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_file_display);
        onGetBundle();
        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TLog.d("FileDisplayActivity-->onDestroy");
        if (mSuperFileView != null) {
            mSuperFileView.onStopDisplay();
        }
    }


    public void init() {
        mSuperFileView =  findViewById(R.id.mSuperFileView);
        mSuperFileView.setOnGetFilePathListener(new SuperFileView.OnGetFilePathListener() {
            @Override
            public void onGetFilePath(SuperFileView mSuperFileView2) {
                getFilePathAndShowFile(mSuperFileView2);
            }
        });
        @SuppressLint("InlinedApi") String[] perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        LinxzPermissionUtils.checkPermission(this, perms, new PermissionListener() {
            @Override
            public void onSucceed(int i, @NonNull String[] strings) {
                mSuperFileView.show();
            }

            @Override
            public void onFailed(int i, @NonNull String[] strings) {
                Toast.makeText(FileDisplayActivity.this,"授权失败",Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getFilePathAndShowFile(SuperFileView mSuperFileView2) {
        //网络地址要先下载
        if (getFilePath().contains("http")) {
            loadFile(getFilePath());
        } else {
            mSuperFileView2.displayFile(new File(getFilePath()));
        }
    }

    /**
     * 下载文件，使用glidej加载自带缓存
     */
    private void loadFile(String url) {
        Glide.with(this).downloadOnly().load(url).into(new SimpleTarget<File>() {
            @Override
            public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                mSuperFileView.displayFile(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                super.onLoadCleared(placeholder);
            }

            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                super.onLoadStarted(placeholder);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                Toast.makeText(FileDisplayActivity.this,"文件加载失败，请确认地址是否有效",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveFile2SDcard(File targetFile) {
        try {
            boolean isMounted = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
            if (!isMounted) {
                Log.d("SDCard错误", "未安装SDCard！");
                Toast.makeText(FileDisplayActivity.this,"未安装SDCard",Toast.LENGTH_SHORT).show();
                return;
            }
            File parentPath = Environment.getExternalStorageDirectory();
            File dir=null;
            if (!TextUtils.isEmpty(folderPath)){
                dir=new File(folderPath);
            }else{
                dir = new File(parentPath.getAbsoluteFile(), "电子保单");
            }
            if (!dir.exists()){
                dir.mkdir();
            }
            File file = new File(dir.getAbsoluteFile(), fileName);
            Log.d("文件路径", file.getAbsolutePath());
            // 创建这个文件，如果不存在
            file.createNewFile();
            //if (!file.exists()) {
            Log.d(TAG, "************文件不存在,文件创建");
            OutputStream myOutput = new FileOutputStream(file);
            InputStream myInput = new FileInputStream(targetFile);
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }
            myOutput.flush();
            myInput.close();
            myOutput.close();
            Log.d(TAG, "************拷贝成功");
        } catch (IOException e) {
            Log.d(TAG, "************拷贝失败");
            e.printStackTrace();
        }
    }


    public void setFilePath(String fileUrl) {
        this.filePath = fileUrl;
    }

    private String getFilePath() {
        return filePath;
    }
}
