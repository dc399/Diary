package com.example.secondactivity;

import android.Manifest;
import android.app.AppComponentFactory;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;

public class DiaryContentActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;
    private EditText diaryTitleText;
    private EditText diaryContentText;
    private TextView diaryAuthor;
    private int id;
    private Bitmap bitmap;
    private String author="";
    private ImageView picture;
    private Uri imageUri;
    private String picture_path="false";
    private TextView picture_show;
    public static final int TAKE_PHOTO=1;
    public static final int CHOOSE_PHOTO=2;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_content);
        dbHelper = new MyDatabaseHelper(this, "DiaryStore.db", null, 1);
        Intent intent = getIntent();
        final int diaryIdInt = intent.getIntExtra("diary_id", -1);
        final String diaryTitleString = intent.getStringExtra("diary_title");
        final String diaryContentString = intent.getStringExtra("diary_content");
        author = intent.getStringExtra("author");
        picture_path = intent.getStringExtra("picture");
        id=diaryIdInt;
        picture=(ImageView)findViewById(R.id.picture);
        if(picture_path.equals("true")) {
            BufferedReader reader=null;
            try {
                FileInputStream in = openFileInput("image"+id );
                StringBuilder content = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while((line = reader.readLine())!=null) {
                            content.append(line);
                    }
                    picture_path = content.toString();
                    bitmap=stringToBitmap(picture_path);
                    if(bitmap!=null)
                        picture.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(reader!=null){
                    try{
                        reader.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
            //picture.setImageBitmap(stringToBitmap(picture_path));
        }

        boolean isFocus=intent.getBooleanExtra("isFocus",false);
        diaryTitleText=(EditText)findViewById(R.id.diary_title);
        diaryContentText=(EditText)findViewById(R.id.diary_content1);
        diaryTitleText.setText(diaryTitleString);
        diaryContentText.setText(diaryContentString);
        diaryAuthor=(TextView)findViewById(R.id.diary_author);
        diaryAuthor.setText("作者："+author);
        id=diaryIdInt;
        if(!isFocus) {
            diaryTitleText.setFocusableInTouchMode(false);
            diaryContentText.setFocusableInTouchMode(false);
            diaryTitleText.setEnabled(false);
            diaryContentText.setEnabled(false);
        }
        Button deleteDiary=(Button)findViewById(R.id.delete_button);
        deleteDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db=dbHelper.getWritableDatabase();
                db.delete("diary","id=?",new String[]{""+id});
                Intent intent=new Intent();
                setResult(2,intent);
                finish();
            }
        });
        Button editDiary=(Button)findViewById(R.id.edit_button);
        editDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diaryTitleText.setFocusableInTouchMode(true);
                diaryContentText.setFocusableInTouchMode(true);
                diaryTitleText.setEnabled(true);
                diaryContentText.setEnabled(true);
            }
        });
        Button saveDiary=(Button)findViewById(R.id.save_button);
        saveDiary.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if(id!=-1){
                    SQLiteDatabase db=dbHelper.getWritableDatabase();
                    ContentValues values=new ContentValues();
                    values.put("title",diaryTitleText.getText().toString());
                    values.put("content",diaryContentText.getText().toString());
                    Date date=new Date();
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String dateString=sdf.format(date);
                    values.put("date", dateString);
                    values.put("author", author);
                    values.put("picture","true");
                    db.update("diary",values,"id=?",new String[]{id+""});
                    values.clear();


                    //尝试一下
                    BufferedWriter writer=null;
                    try {
                        FileOutputStream out=openFileOutput("image"+id,Context.MODE_PRIVATE);


                         writer=new BufferedWriter(new OutputStreamWriter(out));
                        writer.write(bitmapToString(bitmap));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            if(writer!=null){
                                writer.close();
                            }}
                            catch (IOException e){
                                e.printStackTrace();
                            }

                    }

                    diaryTitleText.setFocusableInTouchMode(false);
                    diaryContentText.setFocusableInTouchMode(false);
                    diaryTitleText.setEnabled(false);
                    diaryContentText.setEnabled(false);
                }
                else{
                    SQLiteDatabase db=dbHelper.getWritableDatabase();
                    ContentValues values=new ContentValues();
                    if(diaryTitleText.getText().length()==0) {
                        Toast.makeText(DiaryContentActivity.this, "The title can not be empty!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    values.put("title",diaryTitleText.getText().toString());
                    values.put("content",diaryContentText.getText().toString());
                    Date date=new Date();
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String dateString=sdf.format(date);
                    values.put("date", dateString);
                    values.put("author",author);
                    values.put("picture","true");
                    db.insert("diary",null,values);
                    values.clear();
                    Cursor cursor=db.query("diary",null,null,null,
                            null,null,null);
                    cursor.moveToLast();
                    id=cursor.getInt(cursor.getColumnIndex("id"));
                    cursor.close();

                    //尝试一下
                    BufferedWriter writer = null;
                    try {
                        FileOutputStream out=openFileOutput("image"+id,Context.MODE_PRIVATE);
                        writer=new BufferedWriter(new OutputStreamWriter(out));
                        writer.write(bitmapToString(bitmap));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            if(writer!=null){
                                writer.close();
                            }}
                        catch (IOException e){
                            e.printStackTrace();
                        }

                    }
                    diaryTitleText.setFocusableInTouchMode(false);
                    diaryContentText.setFocusableInTouchMode(false);
                    diaryTitleText.setEnabled(false);
                    diaryContentText.setEnabled(false);
                }

            }
        });
        Button returnDiary=(Button)findViewById(R.id.return_button);
        returnDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent=new Intent(DiaryContentActivity.this,MainActivity.class);
                //startActivity(intent);
                Intent intent=new Intent();
                setResult(2,intent);
                //dbHelper.close();
                finish();
            }
        });


        Button takephoto=(Button)findViewById(R.id.add_button1);
        picture=(ImageView)findViewById(R.id.picture);
        takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File outputImage=new File(getFilesDir(),"output_image.png");
                try{
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch(IOException e){
                    e.fillInStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24){
                    imageUri= FileProvider.getUriForFile(DiaryContentActivity.this,
                            "com.example.secondactivity.fileprovider",outputImage);
                }
                else{
                    imageUri= Uri.fromFile(outputImage);
                }
                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);
//                picture_path=imageUri.toString();
//                picture_show.setText("picture_path:"+picture_path);
            }
        });
        Button chooseFromAlbum=(Button)findViewById(R.id.add_button2);
        picture=(ImageView)findViewById(R.id.picture);
        chooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(DiaryContentActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(DiaryContentActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

                }
                else{
                    openAlbum();
                }

        }});
        }
        private void openAlbum(){
        Intent intent =new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
        }
        public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();;
                }
                break;
            default:
                break;
        }
        }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK)
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
//
//                        picture_path=imageUri.toString();
//                        picture_show.setText("picture_path:"+picture_path);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                break;
            case CHOOSE_PHOTO:
                if (resultCode==RESULT_OK){
                    if(Build.VERSION.SDK_INT>=19){
                        handleImageOnKitKat(data);
                    }
                    else{
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitKat(Intent data) {
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docId=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];
                String selection=MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://download/public_downloads"),
                        Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath=getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            imagePath=uri.getPath();
        }

//        picture_path=imagePath;
//        picture_show.setText("picture_path:"+picture_path);


        displayImage(imagePath);
    }
    private void handleImageBeforeKitKat(Intent data){
        Uri uri=data.getData();
        String imagePath=getImagePath(uri,null);

//        picture_path=imagePath;
//        picture_show.setText("picture_path:"+picture_path);


        displayImage(imagePath);
    }
    private String getImagePath(Uri uri,String selection){
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void displayImage(String imagePath){
        if(imagePath!=null){
            bitmap=BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
        }else{
            Toast.makeText(this,"failed to get image",Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed(){
        Intent intent=new Intent();
        setResult(2,intent);
        finish();
    }

    public static Bitmap stringToBitmap(String string) {
        Bitmap bitmap1 = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.URL_SAFE);
            bitmap1 = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap1;
    }

    /**
     * 图片转换成Base64字符串
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToString(Bitmap bitmap) {
        //将Bitmap转换成字符串
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.URL_SAFE);
        return string;
    }

}

