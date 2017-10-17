package com.mydblibrary;

import android.content.Context;

/**
 * Author: Created by Real_Man.
 * Version: 1.0
 * Data: 2016/9/29.
 * Other:
 */

public class DatabaseUtils {
    private static MyOpenHelper mHelper;

    private DatabaseUtils(){
    }

    public static void initHelper(Context context, Class<?> table){
        if(mHelper == null){
            mHelper = new MyOpenHelper(context,table);
        }
    }
    public static MyOpenHelper getHelper(){
        if(mHelper == null){
            new RuntimeException("MyOpenHelper is null,No init it");
        }
        return mHelper;
    }
}
