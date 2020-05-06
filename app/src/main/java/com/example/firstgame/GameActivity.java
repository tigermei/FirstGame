package com.example.firstgame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

public class GameActivity extends AppCompatActivity {
    public final static String tag = "GameActivity";

    GridView mGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        controlsEventListener();
    }

    private void controlsEventListener(){
        Button button = findViewById(R.id.left);
        button.setOnClickListener(mLeftListener);

        button = findViewById(R.id.right);
        button.setOnClickListener(mRightListener);

        button = findViewById(R.id.down);
        button.setOnClickListener(mDownListener);

        button = findViewById(R.id.change);
        button.setOnClickListener(mChangeListener);

        mGame = findViewById(R.id.game);
        fillItemData();
    }

    private void fillItemData(){
        BlockAdapter.ItemData []itemdataList = new BlockAdapter.ItemData[150];
        for(int i = 0; i < 150; ++i){
            BlockAdapter.ItemData item = new BlockAdapter.ItemData(R.color.red_1);
            itemdataList[i] = item;
        }
        BlockAdapter adapter = new BlockAdapter(itemdataList, GameActivity.this);
        mGame.setAdapter(adapter);
    }

    View.OnClickListener mLeftListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            Log.v(tag, "Left");
        }
    };

    View.OnClickListener mRightListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            Log.v(tag, "Right");
        }
    };

    View.OnClickListener mChangeListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            Log.v(tag, "Change");
        }
    };

    View.OnClickListener mDownListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            Log.v(tag, "Down");
        }
    };
}