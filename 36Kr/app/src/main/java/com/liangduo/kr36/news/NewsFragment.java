package com.liangduo.kr36.news;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.liangduo.kr36.R;
import com.liangduo.kr36.base.BaseFragment;
import com.liangduo.kr36.bean.NewsBean;
import com.liangduo.kr36.my.MyAdapter;
import com.liangduo.kr36.tool.GsonRequest;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

/**
 * Created by liangduo on 16/5/9.
 */
public class NewsFragment extends BaseFragment {
    private NewsAdapter newsAdapter;
    private ListView newsListView;
    private AutoScrollViewPager autoViewPager;

    @Override
    protected int initLayout() {
        return R.layout.fragment_news;
    }

    @Override
    protected void initView() {
        newsListView = bindView(R.id.news_fragment_lv);
        View autoVp = LayoutInflater.from(getContext()).inflate(R.layout.news_lv_head, null);
        autoViewPager = (AutoScrollViewPager) autoVp.findViewById(R.id.view_pager);
        newsListView.addHeaderView(autoVp);
    }

    @Override
    protected void initData() {
        //解析网络数据
        analysisData();

        initCyclePlayPicture();

        newsAdapter = new NewsAdapter(getContext());
        newsListView.setAdapter(newsAdapter);

    }


    public void analysisData() {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());//初始化队列
        GsonRequest<NewsBean> gsonRequest = new GsonRequest<>(Request.Method.GET,//请求数据类型
                "https://rong.36kr.com/api/mobi/news?pageSize=20&columnId=all&pagingAction=up",//url
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //错误时回调
                        Log.d("NewsFragment", "没打出来");
                    }
                }, new Response.Listener<NewsBean>() {
            @Override
            public void onResponse(NewsBean response) {
                //成功时的回调
                newsAdapter.setNewsBean(response);
                Log.d("---------------------", "response.getData().getPageSize():" + response.getData().getPageSize());
            }
        }, NewsBean.class);//实体类

        requestQueue.add(gsonRequest);
    }


    private void initCyclePlayPicture() {
        //创建一个集合去装轮播的图片
        final List<ImageView> images = new ArrayList<>();
        //ScaleType决定了图片在View上显示时的样子
        //FIT_XY不按比例缩放图片，把图片塞满整个View。
        //第一个图
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.mipmap.tabbar_icon_mine);
        images.add(imageView);
        //第二个图
        imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.mipmap.tabbar_icon_news);
        images.add(imageView);
        //第三个图
        imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.mipmap.tabbar_icon_discovery);
        images.add(imageView);
        //第四个图
        imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.mipmap.tabbar_icon_equity);
        images.add(imageView);


        class TheAdapter extends PagerAdapter {
            // 获取要滑动的控件的数量，图片的ImageView数量
            @Override
            public int getCount() {
                return Integer.MAX_VALUE;
            }

            // 来判断显示的是否是同一张图片，这里我们将两个参数相比较返回即可
            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            // 当要显示的图片可以进行缓存的时候，会调用这个方法进行显示图片的初始化，
            // 我们将要显示的ImageView加入到ViewGroup中，然后作为返回值返回即可
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(images.get(position % images.size()));
                return images.get(position % images.size());
            }

            // PagerAdapter只缓存3张要显示的图片，如果滑动的图片超出了缓存的范围，就会调用这个方法，将图片销毁
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        }
        autoViewPager.setAdapter(new TheAdapter());
        //解决最后一个跳转到第一个闪动问题
//        autoViewPager.setCurrentItem((Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % images.size()));


        //设置延时时间
        autoViewPager.setInterval(4000);
        //设置轮播的方向 AutoScrollViewPager.RIGHT/AutoScrollViewPager.LEFT
        autoViewPager.setDirection(AutoScrollViewPager.RIGHT);
        //设置是否自动循环轮播，默认为true
        //注意：一旦设为true，则不能和ViewPagerIndicator一起使用
        autoViewPager.setCycle(true);
        //设置切换动画的时长
        autoViewPager.setScrollDurationFactor(1);
        //设置当滑动到最后一个或者第一个时，如何切换下一张
        /**
         * SLIDE_BORDER_MODE_NONE：不能再滑动
         * SLIDE_BORDER_MODE_TO_PARENT：移动父视图的Pager
         * SLIDE_BORDER_MODE_CYCLE：循环
         * 默认为SLIDE_BORDER_MODE_NONE
         */
        autoViewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        //当滑动到最后一张或第一张时是否开启动画，默认为true
        autoViewPager.setBorderAnimation(false);
        //当触摸的时候，停止轮播
        autoViewPager.setStopScrollWhenTouch(true);



    }
    @Override
    public void onResume() {
        super.onResume();
        //开启自动轮播，延时时间为getInterval()
        autoViewPager.startAutoScroll();
        //开启自动轮播，并设置延时
        //auto_view_pager.startAutoScroll(delayTime);
    }

    @Override
    public void onPause() {
        super.onPause();
        //停止轮播
        autoViewPager.stopAutoScroll();
    }



}


