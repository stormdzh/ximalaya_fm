package com.testopensourceapplication.tingproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

/**
 * 喜马拉雅的广播接收
 *
 * @author HL
 * @date 2016年12月23日
 */
public class PlayerControlReceiver extends BroadcastReceiver {
    //喜马拉雅模块
    public static final String ACTION_CONTROL_PLAY_PAUSE = "com.infisight.ting.ACTION_CONTROL_PLAY_PAUSE";
    public static final String ACTION_CONTROL_PLAY_PRE = "com.infisight.ting.ACTION_CONTROL_PLAY_PRE";
    public static final String ACTION_CONTROL_PLAY_NEXT = "com.infisight.ting.ACTION_CONTROL_PLAY_NEXT";
    @Override
    public void onReceive(Context context, Intent intent) {
        XmPlayerManager manager = XmPlayerManager.getInstance(context);
        String action = intent.getAction();
        if (ACTION_CONTROL_PLAY_PAUSE.equals(action)) {
            if (manager.isPlaying()) {
                manager.pause();
            } else {
                manager.play();
            }
        } else if (ACTION_CONTROL_PLAY_NEXT.equals(action)) {
            manager.playNext();
        } else if (ACTION_CONTROL_PLAY_PRE.equals(action)) {
            manager.playPre();
        }
    }

}
