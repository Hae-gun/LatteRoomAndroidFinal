package org.techtown.prototypeapplication.VO;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

public class SingletonHandler extends Handler {

    private static final SingletonHandler handler = new SingletonHandler();

    private SingletonHandler(){

    }

    public static SingletonHandler getInstance(){
        return handler;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);


    }
}
