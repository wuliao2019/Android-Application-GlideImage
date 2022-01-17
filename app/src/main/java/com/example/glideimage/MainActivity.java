package com.example.glideimage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    int type = 2;
    int lastPosition = 0;
    ArrayList<Pics> picList = new ArrayList<>();
    GridViewAdapter gridViewAdapter;
    // 拍照回传码
    public final static int CAMERA_REQUEST_CODE = 0;
    // 相册选择回传吗
    public final static int GALLERY_REQUEST_CODE = 1;
    // 照片所在的Uri地址
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                takePhoto();
            else
                Toast.makeText(MainActivity.this, "无拍照权限", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                choosePhoto();
            else
                Toast.makeText(MainActivity.this, "无相册权限", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //当拍摄照片完成时会回调到onActivityResult 在这里处理照片的裁剪
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView fragment4ImageView0 = findViewById(R.id.imageView);
        if (resultCode == MainActivity.RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE: {
                    // 获得图片
                    try {
                        //该uri就是照片文件夹对应的uri
                        Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        int length = Math.min(bit.getHeight(), bit.getWidth());
                        bit = Bitmap.createBitmap(bit, 0, 0, length, length);
                        fragment4ImageView0.setImageBitmap(bit);
                        picList.get(lastPosition).bd = false;
                        lastPosition = 7;
                        picList.get(lastPosition).bd = true;
                        gridViewAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case GALLERY_REQUEST_CODE: {
                    // 获取图片
                    try {
                        //该uri是上一个Activity返回的
                        imageUri = data.getData();
                        if (imageUri != null) {
                            Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                            int length = Math.min(bit.getHeight(), bit.getWidth());
                            if (length > 800) {
                                Matrix matrix = new Matrix();
                                matrix.postScale((float) 800 / length, (float) 800 / length);
                                bit = Bitmap.createBitmap(bit, 0, 0, length, length, matrix, true);
                            } else
                                bit = Bitmap.createBitmap(bit, 0, 0, length, length);
                            fragment4ImageView0.setImageBitmap(bit);
                            picList.get(lastPosition).bd = false;
                            lastPosition = 7;
                            picList.get(lastPosition).bd = true;
                            gridViewAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //获取图片资源
    private int getResource(String imageName) {
        Class mipmap = R.drawable.class;
        try {
            Field field = mipmap.getField(imageName);
            return field.getInt(imageName);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            //如果没有在"drawable"下找到imageName,将会返回0
            return 0;
        }
    }

    private Bitmap readBitmap(Context context, int resId) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        InputStream is = context.getResources().openRawResource(resId);
        Bitmap bm = BitmapFactory.decodeStream(is, null, opts);
        int length = Math.min(bm.getHeight(), bm.getWidth());
        return Bitmap.createBitmap(bm, 0, 0, length, length);
    }

    private void takePhoto() {
        // 跳转到系统的拍照界面
        // 拍照的照片的存储位置
        String mTempPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpeg";
        File output = new File(mTempPhotoPath);
        try//判断图片是否存在，存在则删除在创建，不存在则直接创建
        {
            if (!Objects.requireNonNull(output.getParentFile()).exists())
                output.getParentFile().mkdirs();
            if (output.exists())
                output.delete();
            output.createNewFile();
            imageUri = FileProvider.getUriForFile(MainActivity.this, MainActivity.this.getApplicationContext().getPackageName() + ".my.provider", output);
            Intent intentToTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intentToTakePhoto, CAMERA_REQUEST_CODE);
            //调用会返回结果的开启方式，返回成功的话，则把它显示出来
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void choosePhoto() {
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型" 所有类型则写 "image/*"
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentToPickPic, GALLERY_REQUEST_CODE);
    }


    private void showChooseDialog() {
        String[] string = {"相机", "从相册中选取"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("选择图片");
        dialog.setItems(string, (dialog1, which) -> {
            switch (which) {
                case 0:
                    //第二个参数是需要申请的权限
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {   //权限还没有授予，需要在这里写申请权限的代码
                        // 第二个参数是一个字符串数组，里面是需要申请的权限 可以设置申请多个权限，最后一个参数标志这次申请的权限，该常量在onRequestPermissionsResult中使用到
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
                    } else { //权限已经被授予，在这里直接写要执行的相应方法即可
                        takePhoto();
                    }
                    break;
                case 1:
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {   //权限还没有授予，需要在这里写申请权限的代码
                        // 第二个参数是一个字符串数组，里面是需要申请的权限 可以设置申请多个权限，最后一个参数标志这次申请的权限，该常量在onRequestPermissionsResult中使用到
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_REQUEST_CODE);
                    } else { //权限已经被授予，在这里直接写要执行的相应方法即可
                        choosePhoto();
                    }
                    break;
                default:
                    break;
            }
        });
        dialog.show();
    }

    private void init() {
        String[] diff = {"2×2  简单", "3×3  正常", "4×4  困难", "5×5  挑战"};
        ImageView imageView = findViewById(R.id.imageView);
        Spinner spinner = findViewById(R.id.spinner);
        GridView gridView = findViewById(R.id.gdv);
        gridViewAdapter = new GridViewAdapter(this, picList);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, diff);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = position + 2;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        for (int i = 1; i < 9; i++) {
            Pics tempPic = new Pics();
            tempPic.bitmap = readBitmap(this, getResource("p" + i));
            tempPic.bd = (i == 1);
            picList.add(tempPic);
        }
        gridView.setAdapter(gridViewAdapter);

        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.list_anim);
        LayoutAnimationController layoutAnimationController = new LayoutAnimationController(animation, 0.1f);
        layoutAnimationController.setOrder(LayoutAnimationController.ORDER_NORMAL);
        gridView.setLayoutAnimation(layoutAnimationController);
        imageView.setAnimation(animation);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            picList.get(lastPosition).bd = false;
            if (position != 7) {
                lastPosition = position;
                imageView.setImageBitmap(picList.get(position).bitmap);
            } else {
                showChooseDialog();
            }
            picList.get(lastPosition).bd = true;
            gridViewAdapter.notifyDataSetChanged();
        });
        Button button = findViewById(R.id.start);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, JigsawActivity.class);
            intent.putExtra("type", type);
            intent.setData(imageUri);
            intent.putExtra("custom", lastPosition == 7);
            intent.putExtra("originPic", getResource("p" + (lastPosition + 1)));
            startActivity(intent);
        });
    }
}