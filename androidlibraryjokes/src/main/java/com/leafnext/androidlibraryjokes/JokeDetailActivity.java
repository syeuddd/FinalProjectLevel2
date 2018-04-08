package com.leafnext.androidlibraryjokes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class JokeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke_detail);

        TextView textView = findViewById(R.id.jokeView);


        if (getIntent()!=null){
            Intent jokeIntent = getIntent();
            String jokePassed = jokeIntent.getStringExtra("jokeKey");
            textView.setText(jokePassed);
        }
    }
}
