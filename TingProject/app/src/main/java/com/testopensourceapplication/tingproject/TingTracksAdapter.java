package com.testopensourceapplication.tingproject;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HL on 2016/12/22.
 */
public class TingTracksAdapter extends BaseAdapter {

    private List<Track> mTrackList = new ArrayList<Track>();
    private Context mContext;
    private FinalBitmap mFinalBitmap;
    private XmPlayerManager mPlayerManager;


    public TingTracksAdapter(List<Track> mTrackList,Context mContext,FinalBitmap mFinalBitmap,XmPlayerManager mPlayerManager){
        this.mTrackList = mTrackList;
        this.mContext   =  mContext;
        this.mFinalBitmap = mFinalBitmap;
        this.mPlayerManager = mPlayerManager;

    }

    @Override
    public int getCount() {
        if(mTrackList ==null){
            return 0;
        }
        return mTrackList.size();
    }

    @Override
    public Object getItem(int position) {
        return mTrackList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TingViewHolder holder;
        if(convertView == null){
            convertView =LayoutInflater.from(mContext).inflate(R.layout.ting_track_content,parent,false);
            holder = new TingViewHolder();
            holder.content = (ViewGroup) convertView;
            holder.cover = (ImageView) convertView.findViewById(R.id.ting_imageview);
            holder.title = (TextView) convertView.findViewById(R.id.trackname);

            holder.intro = (TextView) convertView.findViewById(R.id.intro);
            convertView.setTag(holder);
        }else{
            holder =(TingViewHolder)convertView.getTag();
        }
        Track  sound =mTrackList.get(position);

        holder.title.setText(sound.getTrackTitle());
        holder.intro.setText(sound.getAnnouncer() == null ? sound.getTrackTags() : sound.getAnnouncer().getNickname());
        mFinalBitmap.display(holder.cover, sound.getCoverUrlSmall());
        PlayableModel curr = mPlayerManager.getCurrSound();
        if(sound.equals(curr)){
            holder.content.setBackgroundResource(R.drawable.ting_item_bg);
        }else{
            holder.content.setBackgroundColor(Color.BLACK);
        }
        return convertView;
    }
}
