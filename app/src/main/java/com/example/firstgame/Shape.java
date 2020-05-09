package com.example.firstgame;

import android.util.Log;

public class Shape {
    public final static String tag = "Shape";
    public final static int row = 4;
    public final static int column = 4;
    public final static int SHAPE_SIZE = 6;
    public static int[][][]shape = new int[][][]{
            {
                    {0, 1, 0, 0},
                    {0, 1, 0, 0},
                    {0, 1, 0, 0},
                    {0, 1, 0, 0}
            },
            {
                    {1, 0, 0, 0},
                    {1, 1, 0, 0},
                    {0, 1, 0, 0},
                    {0, 0, 0, 0},
            },
            {
                    {0, 1, 0, 0},
                    {1, 1, 0, 0},
                    {1, 0, 0, 0},
                    {0, 0, 0, 0},
            },
            {
                    {1, 0, 0, 0},
                    {1, 0, 0, 0},
                    {1, 1, 0, 0},
                    {0, 0, 0, 0},
            },
            {
                    {0, 1, 0, 0},
                    {1, 1, 1, 0},
                    {0, 0, 0, 0},
                    {0, 0, 0, 0},
            },
            {
                    {1, 1, 0, 0},
                    {1, 1, 0, 0},
                    {0, 0, 0, 0},
                    {0, 0, 0, 0}
            },

    };

    public static int [] next = new int[] {
        1,2,3,4
    };

    public static void moveTop(int [][]temp, int row, int column){
        for(int num = 0; num <Shape.row; ++num){
            //1.检查旋转后是否上移
            boolean top1All0 = true;
            for(int i = 0; i <Shape.column; ++i){
                if(0 != temp[0][i]){
                    top1All0 = false;
                    break;
                }
            }

            //2.如果第一排全是0，所有的方格向上移动1格
            if(top1All0){
                for(int j = 1; j< Shape.row; ++j){
                    for(int i = 0; i <Shape.column; ++i){
                        temp[j-1][i]=temp[j][i];
                    }
                }

                for(int i = 0; i <Shape.column; ++i){
                    temp[3][i]=0;
                }
            } else {
                break;
            }
        }
    }

    public static void moveLeft(int [][]temp, int row, int column){

        for(int num = 0; num < Shape.column; ++num){
            //2.检查旋转后是否上移或者左移
            boolean left1All0 = true;
            for(int j = 0; j< Shape.row; ++j){
                if(0 != temp[j][0]){
                    left1All0 = false;
                    break;
                }
            }

            //4.如果第一列全是0，所有的方格向左移动1格
            if(left1All0){
                for(int i = 1; i <Shape.column; ++i){
                    for(int j = 0; j< Shape.row; ++j){
                        temp[j][i-1]=temp[j][i];
                    }
                }

                for(int j = 0; j< Shape.row; ++j){
                    temp[j][3] = 0;
                }
            } else {
                break;
            }
        }

    }

    public static void rotateLeft90Angle(int [][]array, int row, int column){
        if(row != Shape.row || column != Shape.column){
            Log.e(tag, "row or column not 4, not support rotate left 90");
            return;
        }

        int [][] temp= new int[Shape.row][Shape.column];

        //1.先左旋转90度
        for(int j = 0; j< Shape.row; ++j){
            for(int i = 0; i <Shape.column; ++i){
                temp[Shape.column-1-i][j]=array[j][i];
            }
        }

        //2.检查旋转后是否上移或者左移
        moveTop(temp, Shape.row, Shape.column);
        moveLeft(temp, Shape.row, Shape.column);

        //3.交换2个矩阵
        for(int j = 0; j< Shape.row; ++j){
            for(int i = 0; i <Shape.column; ++i){
                array[j][i]=temp[j][i];
            }
        }

        return;
    }
}
