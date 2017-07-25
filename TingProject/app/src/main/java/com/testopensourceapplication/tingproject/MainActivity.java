package com.testopensourceapplication.tingproject;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.category.Category;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.tag.Tag;
import com.ximalaya.ting.android.opensdk.model.tag.TagList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public String TAG ="MainActivity";
    public HorizontalScrollView ting_header;
    private CommonRequest mXimalaya;
    private String mAppSecret = "c4fce2b85e8f354e071904eb8f595795";
    private XmPlayerManager mPlayerManager;
    private boolean mOnDmandLoading = false;//是否加载点播模块
    private int mPageId = 1;
    private int index = 0;
    private AlbumList mAlbumList;//标签下的专辑列表
    private Category mCategory = null;//点播类别
    FinalBitmap mFinalBitmap;
    /**
     * 更改textview状态
     */
    List<TextView> textViews = new ArrayList<TextView>();

    LinearLayout ting_choose;   //动态生成的存放类别的布局
    GridView mTagGridView;   //类别下的标签展示列表
    ListView mTingTrackListView;    //  标签后的listView展示布局


    private List<Category> mCategoryList = new ArrayList<Category>();//类别列表  有声书 、音乐、娱乐。。。。。

    private List<Tag> mTagList = new ArrayList<Tag>();//类别下的标签列表    悬疑、 言情  幻想  等

    private List<Track> mTrackList = new ArrayList<Track>();//标签后   专辑下的声音列表


    private TingTagAdapter mTingTagAdapter;//类别列表后的 标签适配器
     private TingTracksAdapter mTingTrackAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        CategoryData();
    }

    //初始化喜马拉雅
    public void initData(){
        mFinalBitmap = FinalBitmap.create(MainActivity.this);
        mFinalBitmap.configLoadfailImage(R.drawable.ting_default_icon);
        mFinalBitmap.configLoadingImage(R.drawable.ting_default_icon);

        mXimalaya = CommonRequest.getInstanse();
        mXimalaya.init(MainActivity.this, mAppSecret);

        mXimalaya.setDefaultPagesize(50);
        mPlayerManager = XmPlayerManager.getInstance(this);
        mPlayerManager.init();
        mPlayerManager.addPlayerStatusListener(mPlayerStatusListener);
    }

    //初始化控件
    public void initView(){
        ting_header = (HorizontalScrollView) findViewById(R.id.ting_header);
        ting_choose = (LinearLayout) findViewById(R.id.ting_choose);    //存放分类的布局
        mTagGridView = (GridView) findViewById(R.id.gv_tag_list);    //类别下的展示列表
        mTingTrackListView = (ListView) findViewById(R.id.gv_category_item_list);

        mTingTagAdapter = new TingTagAdapter(mTagList, MainActivity.this);
        mTagGridView.setAdapter(mTingTagAdapter);


       //设置类别声音适配器
        mTingTrackAdapter = new TingTracksAdapter(mTrackList, MainActivity.this, mFinalBitmap, mPlayerManager);
        mTingTrackListView.setAdapter(mTingTrackAdapter);

    }


    //点击事件
    public void CategoryData(){
        loadOnDamendCategory();

        //标签的点击事件  //如点击  悬疑  言情  之类的
        //点击标签时掩藏  头部（分类的布局），标签的布局展示 标签下的listViwe 的布局
        mTagGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tag mTag = mTagList.get(position);

                mTagGridView.setVisibility(View.GONE);   //掩藏  标签的布局
                mTingTrackListView.setVisibility(View.VISIBLE);
                ting_header.setVisibility(View.GONE);

                //获取点击标签时  当前点击的标签  下面的listView 的数据
                getAlbums((int) mCategory.getId(), mTag.getTagName());

            }
        });


        mTingTrackListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.e(TAG, "时长：" + mPlayerManager.getDuration() + "DDDDDDDDDDDDDDDDD");
                Log.e(TAG, "是否在播放：" + mPlayerManager.isPlaying() + "DDDDDDDDDDDDDDDDD");
                Log.e(TAG, "点击mTingTrackListView：" + position + "--------------------------"+mTrackList.toString());
                 index = position;
                 mPlayerManager.playList(mTrackList, position);  //此方法用来播放普通声音列表： list :声音列表    startIndex：播放index指定处的声音

                TingApplication.isTingPlaying = true;
                TingApplication.tingOrMusic = "ting";

         /*       Intent intent =new Intent(MainActivity.this,PlayActivity.class);
                startActivity(intent);*/
                //mPlayerManager.play();
            }
        });


    }

    //加载点播列表
    public void loadOnDamendCategory(){
        if (mOnDmandLoading) {
            return;
        }
        mOnDmandLoading = true;

        //获取喜马拉雅内容分类  接口
        Map<String,String>  map =new HashMap<String,String>();
        CommonRequest.getCategories(map, new IDataCallBack<CategoryList>() {
            @Override
            public void onSuccess(CategoryList object) {
                if (object != null && object.getCategories() != null) {
                    mCategoryList.clear();
                    mCategoryList.addAll(object.getCategories());

                    head(mCategoryList);
                    setTextViewsClick(textViews);
                }
                mOnDmandLoading = false;
            }

            @Override
            public void onError(int i, String s) {
                Log.e(TAG, "获取内容分类失败" + i + "eeee" + s);

            }
        });

    }


    //   根据分类ID  和 标签名来获取 标签后的ListView 的展示数据

    public void getAlbums(int mCatrgoryId,String mTagName){

       Map<String,String> map = new HashMap<String,String>();
        map.put(DTransferConstants.CATEGORY_ID, "" + mCatrgoryId);
        map.put(DTransferConstants.TAG_NAME, mTagName);
        map.put(DTransferConstants.PAGE, "" + mPageId);
        CommonRequest.getAlbums(map, new IDataCallBack<AlbumList>() {
            @Override
            public void onSuccess(AlbumList object) {
                if (object != null && object.getAlbums() != null) {
                    if (mAlbumList == null) {
                        mAlbumList = object;
                    } else {
                        mAlbumList.getAlbums().clear();
                        mAlbumList.getAlbums().addAll(object.getAlbums());
                    }

                 /*   Log.e(TAG, "getAlbums mAlbumList:----------------------- " + mAlbumList);*/

                    getTracks((int) mAlbumList.getAlbums().get(0).getId());
                }


            }

            @Override
            public void onError(int i, String s) {

                Log.e(TAG, "获取标签后的ListView的展示数据失败"+s.toString()+"-----------"+i);
            }
        });

    }


    public void getTracks(int mAlbumId){
        Map<String,String> map =new HashMap<String,String>();
        map.put(DTransferConstants.ALBUM_ID,""+mAlbumId);  //专辑ID
        map.put(DTransferConstants.SORT, "asc");   //asc-正序或desc-倒序，默认为asc-正序
        map.put(DTransferConstants.PAGE, "" + mPageId);
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList object) {
                if(object!=null &&object.getTracks()!=null &&object.getTracks().size()!=0){

                    mTrackList.clear();
                    mTrackList.addAll(object.getTracks());
                    mTingTrackAdapter.notifyDataSetChanged();

                    mPlayerManager.playList(mTrackList, 0);  //用来播放  我设置第一次进来时默认播放第一条


                    index = 0;
                    TingApplication.isTingPlaying = true;
                    TingApplication.tingOrMusic = "ting";
                }


            }

            @Override
            public void onError(int i, String s) {
                Log.e(TAG, "222222222获取标签后的ListView的展示数据失败"+s.toString()+"-----------"+i);
            }
        });
    }




    //  测试用
    // 根据分类和标签获取某个分类某个标签下的专辑列表（最火/最新/最多播放）





    //动态添加分类列表
   public void head(List<Category>  mCategoryList){

       if(mCategoryList.size()!=0){
           for(int i=0;i<mCategoryList.size();i++){
               TextView tv =new TextView(this);
               LinearLayout.LayoutParams tv_lp =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
               tv.setText(mCategoryList.get(i).getCategoryName());


               tv.setTextColor(getResources().getColor(R.color.radingtv));
               tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.nav_strategy_text_size));
               tv.setGravity(Gravity.CENTER);
               tv.setSingleLine(true);
               tv.setId(i);
               tv.setFocusable(true);
               ting_choose.addView(tv);


             //  Log.e(TAG,"ting_choose:"+ting_choose);
               //为最左边加边距

               if(i==0){
                   tv_lp.setMargins((int) getResources().getDimension(R.dimen.nav_strategy_text_margin_left_right), 0, 0, 0);
                   tv.setLayoutParams(tv_lp);
                   tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.nav_strategysize));
                   //第一次进這个页面时。默认显示第一条  分类的标签类容  第一个分类id  有声书是3
                   getCategoryTag(3);
                   mTagGridView.setVisibility(View.VISIBLE);
                   mCategory = mCategoryList.get(0);
                   tv.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.choose_blue));
               }

               //为最右边加边距
               if (i == mCategoryList.size() - 1) {
                   tv_lp.setMargins(0, 0, (int) getResources().getDimension(R.dimen.nav_strategy_text_margin_left_right), 0);
                   tv.setLayoutParams(tv_lp);
               }

               /**
                * 添加中间分割小view
                */
               if (i != mCategoryList.size() - 1) {
                   View view_split = new View(this);
                   LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(8, (int) getResources().getDimension(R.dimen.nav_strategy_split_view_height));
                   layoutParams.gravity = Gravity.CENTER;
                   view_split.setBackgroundResource(R.drawable.xiegang);
                   layoutParams.setMargins((int) getResources().getDimension(R.dimen.nav_strategy_text_margin_left_right), 0,
                           (int) getResources().getDimension(R.dimen.nav_strategy_text_margin_left_right), 0);
                   view_split.setLayoutParams(layoutParams);
                   ting_choose.addView(view_split);
               }
               textViews.add(tv);

           }

       }

   }

    //设置电台类别的点击事件  点击后加载类别下的标签
    private void setTextViewsClick(final List<TextView> tvLst){
        if(tvLst != null){
            for(int id =0;id<tvLst.size();id++){
                tvLst.get(id).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(int j=0;j<tvLst.size();j++){
                            if(v.getId()==tvLst.get(j).getId()){
                                tvLst.get(j).setTextColor(ContextCompat.getColor(MainActivity.this, R.color.choose_blue));
                                tvLst.get(j).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.nav_strategysize));
                                mTagGridView.setVisibility(View.VISIBLE);
                                mCategory = mCategoryList.get(tvLst.get(j).getId());

                               getCategoryTag((int)mCategory.getId());

                            }else{
                                tvLst.get(j).setTextColor(ContextCompat.getColor(MainActivity.this, R.color.radingtv));
                                tvLst.get(j).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.nav_strategy_text_size));
                            }

                        }

                    }
                });

            }

        }

    }



    //获取分类列表后的标签

    public void getCategoryTag(int mCatrgoryId){
        Map<String,String> map = new HashMap<String,String>();
        map.put(DTransferConstants.CATEGORY_ID, "" + mCatrgoryId);
        map.put(DTransferConstants.TYPE, "" + 0);
        CommonRequest.getTags(map, new IDataCallBack<TagList>() {
            @Override
            public void onSuccess(TagList object) {
                if(object !=null &&object.getTagList() !=null){
                    mTagList.clear();
                    mTagList.addAll(object.getTagList());

                    Log.e(TAG, "悬疑：" + object.getTagList().get(0).getTagName().toString());
                    Log.e(TAG, "悬疑：" + object.getTagList().get(0).getKind().toString());
                    mTingTagAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }




    //播放器回调
    public IXmPlayerStatusListener mPlayerStatusListener =new IXmPlayerStatusListener() {
        //开始播放
        @Override
        public void onPlayStart() {
            Log.e(TAG, "================onPlayStart");

            TingApplication.isTingPlaying = true;
        }

        //暂停播放
        @Override
        public void onPlayPause() {
            Log.e(TAG,"====================onPlayPause");
        }

        //停止播放
        @Override
        public void onPlayStop() {
            Log.e(TAG,"onPlayStop");
        }

        //播放完成
        @Override
        public void onSoundPlayComplete() {
            Log.e(TAG,"=======================onSoundPlayComplete");
        }

        //播放器准备完毕
        @Override
        public void onSoundPrepared() {
            Log.e(TAG,"========================onSoundPrepared");
        }

        //切歌
        @Override
        public void onSoundSwitch(PlayableModel playableModel, PlayableModel playableModel1) {
            Log.e(TAG,"===================onSoundSwitch");
            if(mTingTrackAdapter !=null){
                mTingTrackAdapter.notifyDataSetChanged();
                mTingTrackListView.setSelection(index);
            }
            PlayableModel model = mPlayerManager.getCurrSound();

            Log.e(TAG,"*********************"+model);
            if(model!=null){
                String title = null;
                String msg = null;
                String coverUrl = null;
                String coverSmall = null;
                if (model instanceof Track) {
                    Track info = (Track) model;
                    title = info.getTrackTitle();
                    msg = info.getAnnouncer() == null ? "" : info
                            .getAnnouncer().getNickname();
                    coverUrl = info.getCoverUrlLarge();
                    coverSmall = info.getCoverUrlSmall();
                } else if (model instanceof Radio) {
                    Radio radio = (Radio) model;
                    Log.e(TAG,"radio:"+radio.getRadioName());
                    title = radio.getRadioName();
                    msg = radio.getRadioDesc();
                    coverUrl = radio.getCoverUrlLarge();
                    coverSmall = radio.getCoverUrlSmall();
                }
            }



        }


        //开始开始缓冲
        @Override
        public void onBufferingStart() {
            Log.e(TAG,"====================onBufferingStart");
        }

        //结束缓冲
        @Override
        public void onBufferingStop() {
            Log.e(TAG,"===================onBufferingStop");
        }

        //缓冲进度回调
        @Override
        public void onBufferProgress(int i) {
            Log.e(TAG,"===================onBufferProgress");
        }

        //播放进度回调
        @Override
        public void onPlayProgress(int i, int i1) {
            Log.e(TAG,"==========================onPlayProgress");




        }

        //播放器错误
        @Override
        public boolean onError(XmPlayerException e) {
            Log.e(TAG,"===========================onError"+e.getMessage().toString());
            return false;
        }
    };

}
