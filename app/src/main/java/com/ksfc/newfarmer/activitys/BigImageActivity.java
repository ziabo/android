package com.ksfc.newfarmer.activitys;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.fragment.BigImageFragment;
import com.ksfc.newfarmer.http.Request;
import com.ksfc.newfarmer.http.beans.GetGoodsDetail;
import com.ksfc.newfarmer.utils.ActivityAnimationUtils;
import com.ksfc.newfarmer.widget.CirclePageIndicator;
import com.ksfc.newfarmer.widget.HackyViewPager;

import java.util.List;


/**
 * Created by HePeng on 2015/12/7.
 * 商品详情查看大图
 */
public class BigImageActivity extends BaseActivity {
    @Override
    public int getLayout() {
        return R.layout.activity_big_image;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {

        HackyViewPager viewPager = ((HackyViewPager) findViewById(R.id.viewPager_big_image));
        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.circlePageIndicator);
        GetGoodsDetail.GoodsDetail detail = (GetGoodsDetail.GoodsDetail) getIntent().getSerializableExtra("detail");
        int position = getIntent().getIntExtra("position", 0);
        if (detail != null && detail.pictures != null) {
            MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), detail.pictures);
            viewPager.setAdapter(myPagerAdapter);
            viewPager.setOffscreenPageLimit(detail.pictures.size());
            if (detail.pictures.size() > 1) {
                indicator.setViewPager(viewPager);
                viewPager.setCurrentItem(position);
            } else {
                indicator.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {

    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        private List<GetGoodsDetail.GoodsDetail.Pictures> pictures;

        public MyPagerAdapter(FragmentManager fm, List<GetGoodsDetail.GoodsDetail.Pictures> pictures) {
            super(fm);
            this.pictures = pictures;
        }

        @Override
        public Fragment getItem(int pos) {
            return BigImageFragment.newInstance(pictures.get(pos).originalUrl);
        }

        @Override
        public int getCount() {
            return pictures.size();
        }
    }


    @Override
    public void finish() {
        super.finish();
        ActivityAnimationUtils.setActivityAnimation(this, R.anim.animation_none, R.anim.zoom_exit);
    }
}
