package com.testopensourceapplication.tingproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ximalaya.ting.android.opensdk.model.tag.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HL on 2016/12/21.
 */
public class TingTagAdapter extends BaseAdapter {
    private List<Tag> mTagList =new ArrayList<Tag>();
    private Context context;

    public TingTagAdapter(List<Tag> mTagList,Context context){
        this.mTagList = mTagList;
        this.context = context;

    }
    @Override
    public int getCount() {
        return mTagList.size();
    }

    @Override
    public Object getItem(int position) {
        return mTagList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TingViewHolder holder;
        //Log.e("TAG","mTagList"+mTagList.get(position).getTagName()+"0000000000000000000000000000");
        if(convertView == null){
            convertView =LayoutInflater.from(context).inflate(R.layout.ting_item_content,parent,false);
            holder =new TingViewHolder();
            holder.content =(ViewGroup)convertView;
            holder.cover = (ImageView) convertView.findViewById(R.id.iv_item_picture);
            holder.cover.setVisibility(View.GONE);
            holder.title = (TextView) convertView.findViewById(R.id.tv_item_name);
            convertView.setTag(holder);
        }else{
            holder = (TingViewHolder) convertView.getTag();
        }

        Tag tag = mTagList.get(position);

       /* if(tag.getTagName().length()>4){
            String  tagname = tag.getTagName();
            String Fristindex =tagname.substring(0,4);
            String endIndex = tagname.substring()
            Log.e("TAG", "Fristindex:" + Fristindex.toString());
       }*/
        holder.title.setText(tag.getTagName());

        return convertView;
    }

}
