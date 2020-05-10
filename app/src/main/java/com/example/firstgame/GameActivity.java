package com.example.firstgame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;


public class GameActivity extends AppCompatActivity {
    public final static String tag = "GameActivity";
    public final static int BLOCK_COLUMN_NUM = 10;
    public final static int BLOCK_ROW_NUM = 20;
    public final static int BLOCK_NUM = 200;

    public final static int MINI_BLOCK_COLUMN_NUM = 4;
    public final static int MINI_BLOCK_ROW_NUM = 4;
    public final static int MINI_BLOCK_NUM = 9;

    GridView mGame;
    BlockAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        controlsEventListener();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        adapter.onDestroy();
        adapter = null;
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

        button = findViewById(R.id.start);
        button.setOnClickListener(mStartListner);

        mGame = findViewById(R.id.game);
        fillItemData();
    }

    private void fillItemData(){
        BlockAdapter.ItemData []itemdataList = new BlockAdapter.ItemData[GameActivity.BLOCK_NUM];
        for(int i = 0; i < GameActivity.BLOCK_NUM; ++i){
            BlockAdapter.ItemData item = new BlockAdapter.ItemData(R.color.white, true);
            itemdataList[i] = item;
        }
        adapter = new BlockAdapter(itemdataList, GameActivity.this);
        mGame.setAdapter(adapter);
    }

    View.OnClickListener mLeftListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            Log.v(tag, "Left");
            if(null != adapter){
                adapter.left();
            }
        }
    };

    View.OnClickListener mRightListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            Log.v(tag, "Right");
            if(null != adapter){
                adapter.right();
            }
        }
    };

    View.OnClickListener mChangeListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            Log.v(tag, "Change");
            if(null != adapter){
                adapter.change();
            }
        }
    };

    View.OnClickListener mDownListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            Log.v(tag, "Down");
            if(null != adapter){
                adapter.down();
            }
        }
    };

    View.OnClickListener mStartListner = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            Log.v(tag, "start");

            if(null != adapter){
                adapter.start();
            }
            //testRotate();

        }
    };

    private void testRotate(){

        for(int p = 0; p < 5; ++p){

            int [][]array = new int[4][4];
            for(int j = 0; j< 4; ++j){
                for(int i = 0; i <4; ++i){
                    array[j][i]=Shape.shape[p][j][i];
                }
            }

            Log.i(tag,"-----rotate start-------");
            for(int j = 0; j < 4; ++j){
                Log.i(tag, array[j][0]+" "+array[j][1]+" "+array[j][2]+" "+array[j][3]);
            }

            Log.i(tag,"------first------");
            Shape.rotateLeft90Angle(array, 4,4);

            for(int j = 0; j < 4; ++j){
                Log.i(tag, array[j][0]+" "+array[j][1]+" "+array[j][2]+" "+array[j][3]);
            }

            Log.i(tag,"------second------");
            Shape.rotateLeft90Angle(array, 4,4);

            for(int j = 0; j < 4; ++j){
                Log.i(tag, array[j][0]+" "+array[j][1]+" "+array[j][2]+" "+array[j][3]);
            }

            Log.i(tag,"------third------");
            Shape.rotateLeft90Angle(array, 4,4);

            for(int j = 0; j < 4; ++j){
                Log.i(tag, array[j][0]+" "+array[j][1]+" "+array[j][2]+" "+array[j][3]);
            }

            Log.i(tag,"------four------");
            Shape.rotateLeft90Angle(array, 4,4);

            for(int j = 0; j < 4; ++j){
                Log.i(tag, array[j][0]+" "+array[j][1]+" "+array[j][2]+" "+array[j][3]);
            }

            Log.i(tag,"------rotate end------");
            Log.i(tag,"------rotate end------");
        }
    }

}
