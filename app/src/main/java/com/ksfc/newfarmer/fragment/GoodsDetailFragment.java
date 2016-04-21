package com.ksfc.newfarmer.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.activitys.BigImageActivity;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.beans.GetGoodsDetail;
import com.ksfc.newfarmer.utils.ScreenUtil;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.CirclePageIndicator;
import com.ksfc.newfarmer.widget.VerticalScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.yangentao.util.app.App;

import java.util.List;

/**
 * Created by HePeng on 2016/1/7.
 */
public class GoodsDetailFragment extends BaseFragment implements ViewPager.OnPageChangeListener {
    private GetGoodsDetail.GoodsDetail detail;
    private VerticalScrollView scrollView;
    private WebView web;
    private TextView guild_1;
    private TextView guild_2;
    private TextView guild_3;
    private TextView bar_guild_1;
    private TextView bar_guild_2;
    private TextView bar_guild_3;
    private TextView current_count;

    @Override
    public View InItView() {
        Bundle bundle = getArguments();
        detail = (GetGoodsDetail.GoodsDetail) bundle.getSerializable("detail");
        int position = bundle.getInt("position", 0);//当前Viewpager的index
        int count = bundle.getInt("count", 1);//Viewpager的item 用于控制是否需要监听滑到底部
        if (position == 0) {
            View view = inflater.inflate(R.layout.goods_detail_top_layout, null);
            if (count == 2) {//监听scrollView是否滑动到底部
                scrollView = (VerticalScrollView) view.findViewById(R.id.scrollView_goods_detail);
                scrollView.setOnScrollToBottomLintener(new VerticalScrollView.OnScrollToBottomListener() {
                    @Override
                    public void onScrollBottomListener(boolean isBottom) {
                        if (isBottom) {
                            scrollView.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View arg0, MotionEvent arg1) {
                                    return arg1.getAction() == MotionEvent.ACTION_UP;
                                }
                            });
                        }
                    }
                });
            }
            ViewPager viewPager = (ViewPager) view.findViewById(R.id.goods_detail_top_viewpager);

            View goods_detail_top_viewpager_rel = view.findViewById(R.id.goods_detail_top_viewpager_rel);
            ScreenUtil.setHeight(App.getApp().getApplicationContext(),goods_detail_top_viewpager_rel,360);

            TextView good_xianjia = (TextView) view.findViewById(R.id.product_price);//xianjia:价格
            CirclePageIndicator indicator = (CirclePageIndicator) view.findViewById(R.id.circlePageIndicator);//Viewpager的指示器Indicator
            current_count = (TextView) view.findViewById(R.id.current_count);//当前Viewpager/item
            TextView good_name = (TextView) view.findViewById(R.id.good_name); //商品名
            TextView product_dingjing = (TextView) view.findViewById(R.id.product_dingjin_price);//定金
            RelativeLayout product_description_rel = (RelativeLayout) view.findViewById(R.id.product_description_rel);//描述rel
            TextView product_description = (TextView) view.findViewById(R.id.product_description);//描述
            TextView product_dingjing_name = (TextView) view.findViewById(R.id.product_dingjin_name);//"定金"
            RelativeLayout detail_detail = (RelativeLayout) view.findViewById(R.id.goods_detail_detail);//继续上滑，可以加载更多
            LinearLayout market_price_ll = (LinearLayout) view.findViewById(R.id.market_price_ll);//市场价 lin
            TextView market_price_tv = (TextView) view.findViewById(R.id.market_price_tv);//市场价
            market_price_tv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//市场价的中划线
            TextView product_newFarmer_price = (TextView) view.findViewById(R.id.product_newFarmer_price);//新农价
            RelativeLayout circlePageIndicator_rel = (RelativeLayout) view.findViewById(R.id.circlePageIndicator_rel);//商品多个主图时Indicator的背景
            if (StringUtil.checkStr(detail.name)) {
                good_name.setText(detail.name);
            }
            if (StringUtil.checkStr(detail.description)) {
                product_description.setText(detail.description);
            }else {
                product_description_rel.setVisibility(View.GONE);
            }
            if (detail.pictures != null) {

                MyPagerAdapter myPagerAdapter = new MyPagerAdapter(detail.pictures);
                viewPager.setAdapter(myPagerAdapter);

                if (detail.pictures.size() > 1) {
                    viewPager.addOnPageChangeListener(this);
                    current_count.setText("1/" + detail.pictures.size());
                    indicator.setViewPager(viewPager);
                } else {
                    viewPager.addOnPageChangeListener(null);
                    circlePageIndicator_rel.setVisibility(View.GONE);
                    current_count.setVisibility(View.GONE);
                    indicator.setVisibility(View.GONE);
                }

            }

            //根据有无商品详情的url,隐藏继续滑动
            if (!TextUtils.isEmpty(detail.app_body_url)) {
                detail_detail.setVisibility(View.VISIBLE);
            } else {
                detail_detail.setVisibility(View.INVISIBLE);
            }
            //是否有订金
            if (!detail.deposit.equals("0")) {
                product_dingjing_name.setVisibility(View.VISIBLE);
                product_dingjing.setText("¥" + detail.deposit);
            }
            //下架的商品也不展示订金
            if (!detail.online) {
                product_dingjing.setVisibility(View.GONE);
                product_dingjing_name.setVisibility(View.GONE);
            }
            //是否预售
            if (detail.presale) {
                product_dingjing.setVisibility(View.GONE);
                product_dingjing_name.setVisibility(View.GONE);
                product_newFarmer_price.setTextColor(Color.GRAY);
                product_newFarmer_price.setText("即将上线");
            } else {
                //新农价
                if (detail.SKUPrice != null && detail.online) {
                    if (StringUtil.checkStr(detail.SKUPrice.min) && StringUtil.checkStr(detail.SKUPrice.max)) {
                        if (!detail.SKUPrice.min.equals(detail.SKUPrice.max)) {
                            good_xianjia.setText("¥" + detail.SKUPrice.min + "-" + detail.SKUPrice.max);
                        } else {
                            good_xianjia.setText("¥" + detail.SKUPrice.min);
                        }
                    }
                } else {
                    if (detail.referencePrice != null) {
                        if (!detail.referencePrice.min.equals(detail.referencePrice.max)) {
                            good_xianjia.setText("¥" + detail.referencePrice.min + "-" + detail.referencePrice.max);
                        } else {
                            good_xianjia.setText("¥" + detail.referencePrice.min);
                        }
                    }
                }
            }

            //市场价

            if (detail.SKUMarketPrice != null
                    && StringUtil.checkStr(detail.SKUMarketPrice.min)
                    && StringUtil.checkStr(detail.SKUMarketPrice.max)
                    && !detail.SKUMarketPrice.min.equals("0")
                    && !detail.SKUMarketPrice.max.equals("0")) {
                market_price_ll.setVisibility(View.VISIBLE);
                if (!detail.SKUMarketPrice.min.equals(detail.SKUMarketPrice.max)) {
                    market_price_tv.setText("¥" + detail.SKUMarketPrice.min + "-" + detail.SKUMarketPrice.max);
                } else {
                    market_price_tv.setText("¥" + detail.SKUMarketPrice.min);
                }
            } else {
                market_price_ll.setVisibility(View.GONE);
            }

            return view;
        } else {

            View view = inflater.inflate(R.layout.goods_detail_buttom_layout, null);
            web = (WebView) view.findViewById(R.id.goods_detail_list);
            guild_1 = (TextView) view.findViewById(R.id.tv_guid1);
            guild_2 = (TextView) view.findViewById(R.id.tv_guid2);
            guild_3 = (TextView) view.findViewById(R.id.tv_guid3);
            bar_guild_1 = (TextView) view.findViewById(R.id.bar_guid1);
            bar_guild_2 = (TextView) view.findViewById(R.id.bar_guid2);
            bar_guild_3 = (TextView) view.findViewById(R.id.bar_guid3);
            WebSettings settings = web.getSettings();
            settings.setDomStorageEnabled(true);
            settings.setJavaScriptEnabled(true);
            settings.setSupportZoom(true); // 设置可以支持缩放
            settings.setBuiltInZoomControls(true);
            settings.setDisplayZoomControls(false); //影藏缩放控件
            settings.setUseWideViewPort(true);//设定支持viewport
            web.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
            guild_1.setOnClickListener(this);
            guild_2.setOnClickListener(this);
            guild_3.setOnClickListener(this);
            guild_1.setTextColor(App.getApp().getResources().getColor(R.color.green));
            bar_guild_1.setVisibility(View.VISIBLE);
            web.loadUrl(detail.app_body_url);
            return view;
        }


    }

    @Override
    public void onResponsed(Request req) {

    }



    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.tv_guid1:
                guild_1.setTextColor(App.getApp().getResources().getColor(R.color.green));
                guild_2.setTextColor(App.getApp().getResources().getColor(R.color.main_index_gary));
                guild_3.setTextColor(App.getApp().getResources().getColor(R.color.main_index_gary));
                initBar();
                bar_guild_1.setVisibility(View.VISIBLE);
                web.loadUrl(detail.app_body_url);
                break;
            case R.id.tv_guid2:
                guild_1.setTextColor(App.getApp().getResources().getColor(R.color.main_index_gary));
                guild_2.setTextColor(App.getApp().getResources().getColor(R.color.green));
                guild_3.setTextColor(App.getApp().getResources().getColor(R.color.main_index_gary));
                initBar();
                bar_guild_2.setVisibility(View.VISIBLE);
                web.loadUrl(detail.app_standard_url);
                break;
            case R.id.tv_guid3:
                guild_1.setTextColor(App.getApp().getResources().getColor(R.color.main_index_gary));
                guild_2.setTextColor(App.getApp().getResources().getColor(R.color.main_index_gary));
                guild_3.setTextColor(App.getApp().getResources().getColor(R.color.green));
                initBar();
                bar_guild_3.setVisibility(View.VISIBLE);
                web.loadUrl(detail.app_support_url);
                break;
        }
    }

    private void initBar() {
        bar_guild_1.setVisibility(View.INVISIBLE);
        bar_guild_2.setVisibility(View.INVISIBLE);
        bar_guild_3.setVisibility(View.INVISIBLE);
    }

    //当前是第几个item
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (current_count != null && detail.pictures != null) {
            StringBuilder builder = new StringBuilder();
            builder.append(position + 1).append("/").append(detail.pictures.size());
            current_count.setText(builder);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    class MyPagerAdapter extends PagerAdapter {

        private List<GetGoodsDetail.GoodsDetail.Pictures> pictures;

        public MyPagerAdapter(List<GetGoodsDetail.GoodsDetail.Pictures> pictures) {
            this.pictures = pictures;
        }

        @Override
        public int getCount() {
            return pictures.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {

            return object == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            View view = inflater.inflate(R.layout.pic_layout, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            if (StringUtil.checkStr(pictures.get(position).imgUrl)) {
                ImageLoader.getInstance().displayImage(MsgID.IP + pictures.get(position).imgUrl, imageView);
            }
            container.addView(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), BigImageActivity.class);
                    intent.putExtra("detail", detail);
                    intent.putExtra("position", position);
                    getActivity().startActivity(intent);
                }
            });
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

        }
    }
}
