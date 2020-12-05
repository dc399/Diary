package com.example.secondactivity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Diary> diaryList=new ArrayList<Diary>();
    private MyDatabaseHelper dbHelper;
    private DiaryAdapter adapter;
    private ListView listView;
    private String author="";
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view_layout);
        SharedPreferences pref= getSharedPreferences("author",0);
        author=pref.getString("author_name","author");
        Log.d("MainActivity","author is"+author);
        dbHelper=new MyDatabaseHelper(this,"DiaryStore.db",null,1);
        initDiary();
        adapter=new DiaryAdapter(MainActivity.this,R.layout.diary_item,diaryList);
        listView=(ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Diary diary=diaryList.get(i);
                Intent intent =new Intent(MainActivity.this,DiaryContentActivity.class);
                intent.putExtra("diary_id",diary.getId());
                intent.putExtra("diary_title",diary.getTitle());
                intent.putExtra("diary_content",diary.getContent());
                intent.putExtra("author",diary.getAuthor());
                intent.putExtra("picture",diary.getPicture());
                intent.putExtra("isFocus",false);
                startActivityForResult(intent,1);
            }
        });
        Button addDiary=(Button)findViewById(R.id.add_button);
        addDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,DiaryContentActivity.class);
                intent.putExtra("diary_id",-1);
                intent.putExtra("diary_title","");
                intent.putExtra("diary_content","");
                intent.putExtra("author",author);
                intent.putExtra("picture","");
                intent.putExtra("isFocus",true);
                startActivityForResult(intent,1);
            }
        });
        Button backDiary=(Button)findViewById(R.id.back_button);
        backDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,UserActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == 2) {
                    RefreshNotesList();
                }
                break;
            default:
        }
    }
    public void RefreshNotesList() {

        int size = diaryList.size();
        if (size > 0) {
            diaryList.removeAll(diaryList);
            //initDiary();
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);
        }
        initDiary();
        adapter = new DiaryAdapter(MainActivity.this,R.layout.diary_item,diaryList);
        listView.setAdapter(adapter);
    }
    private void initDiary(){
        //diaryList.clear();
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Cursor cursor=db.query("diary",null,null,null,
                null,null,null);
        if(cursor.moveToFirst()){
            do{
                Diary diary=new Diary();
                diary.setId(cursor.getInt(cursor.getColumnIndex("id")));
                diary.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                diary.setContent(cursor.getString(cursor.getColumnIndex("content")));
                diary.setDate(cursor.getString(cursor.getColumnIndex("date")));
                diary.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
                diary.setPicture(cursor.getString(cursor.getColumnIndex("picture")));
                diaryList.add(diary);
            }while(cursor.moveToNext());
        }
        cursor.close();
    }
}