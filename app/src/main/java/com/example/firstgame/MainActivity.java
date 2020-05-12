package com.example.firstgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public final static String tag = "MainActivity";

    public enum Gradle  {
        GRADLE_1, GRADLE_2,GRADLE_3, GRADLE_4,GRADLE_5, GRADLE_6
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onBtnListner();
    }

    View.OnClickListener mBtnLister = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Gradle gradle = Gradle.GRADLE_1;
            if (R.id.gradle1 == v.getId()) {
                Log.v(tag, "gradle1");
                gradle = Gradle.GRADLE_1;
            } else if (R.id.gradle2 == v.getId()) {
                Log.v(tag, "gradle2");
                gradle = Gradle.GRADLE_2;
            } else if (R.id.gradle3 == v.getId()) {
                Log.v(tag, "gradle3");
                gradle = Gradle.GRADLE_3;
            } else if (R.id.gradle4 == v.getId()) {
                Log.v(tag, "gradle4");
                gradle = Gradle.GRADLE_4;
            } else if (R.id.gradle5 == v.getId()) {
                Log.v(tag, "gradle5");
                gradle = Gradle.GRADLE_5;
            } else if (R.id.gradle6 == v.getId()) {
                Log.v(tag, "gradle6");
                gradle = Gradle.GRADLE_6;
            }

            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("gradle", gradle.ordinal());

            startActivity(intent);
        }
    };

    private void onBtnListner(){
    Button button = findViewById(R.id.gradle1);
    button.setOnClickListener(mBtnLister);

    button = findViewById(R.id.gradle2);
    button.setOnClickListener(mBtnLister);

    button = findViewById(R.id.gradle3);
    button.setOnClickListener(mBtnLister);

    button = findViewById(R.id.gradle4);
    button.setOnClickListener(mBtnLister);

    button = findViewById(R.id.gradle5);
    button.setOnClickListener(mBtnLister);

    button = findViewById(R.id.gradle6);
    button.setOnClickListener(mBtnLister);

    }
}
