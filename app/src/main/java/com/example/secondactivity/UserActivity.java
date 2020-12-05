package com.example.secondactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UserActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private EditText nameText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.author_layout);
        nameText=(EditText)findViewById(R.id.name_text);
        Button submit=(Button)findViewById(R.id.submit_button);
        Button defaultB=(Button)findViewById(R.id.default_button);
        pref= getSharedPreferences("author",0);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor=pref.edit();
                editor.putString("author_name",nameText.getText().toString());
                editor.apply();
                Intent intent=new Intent(UserActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        defaultB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 editor=pref.edit();
                 editor.clear();
                 editor.apply();
                Intent intent=new Intent(UserActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

}