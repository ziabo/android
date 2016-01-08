package com.ksfc.newfarmer.activitys;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import com.alipay.sdk.pay.demo.AlipayClass;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.RndApplication;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.ServerInterface;
import com.ksfc.newfarmer.protocol.beans.ProFileResult;
import com.ksfc.newfarmer.protocol.beans.ProFileResult.Datas;
import com.ksfc.newfarmer.protocol.beans.UnionPayResponse;
import com.ksfc.newfarmer.protocol.beans.WaitingPay.Orders;
import com.ksfc.newfarmer.utils.RndLog;
import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import net.yangentao.util.app.App;

public class PaywayActivity extends BaseActivity implements Runnable {
    private ImageView alipay_img, bank_img, dianhui_img, pos_img;
    private int tag;
    private String price;
    private String orderId;
    private TextView payway_sumPrice, payWay_order_id;
    private Orders orderInfo;
    private int order_type;

    private static final String mMode = "00";// 设置测试模式:01为测试 00为正式环境
    private static final String TN_URL_01 = ApiType.url + "unionpay";// 自己后台需要实现的给予我们app的tn号接口
    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            /*
             * 接入指南：1:拷贝sdkStd目录下的UPPayAssistEx.jar到libs目录下
			 * 2:根据需要拷贝sdkStd/jar/data.bin（或sdkPro/jar/data.bin）至工程的assets目录下
			 * 3:根据需要拷贝sdkStd/jar/XXX/XXX.so（或sdkPro/jar/XXX/XXX.so）libs目录下
			 * 4:根据需要拷贝sdkStd
			 * /jar/UPPayPluginExStd.jar（或sdkPro/jar/UPPayPluginExPro
			 * .jar）到工程的libs目录下 5:获取tn后通过UPPayAssistEx.startPayByJar(...)方法调用控件
			 */
            if (msg.obj instanceof UnionPayResponse) {
                UnionPayResponse response = (UnionPayResponse) msg.obj;

                String tn = response.tn;
                if (TextUtils.isEmpty(tn)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            PaywayActivity.this);
                    builder.setTitle("错误提示");
                    builder.setMessage("网络连接失败,请重试!");
                    builder.setNegativeButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                } else {
                    doStartUnionPayPlugin(PaywayActivity.this, tn, mMode);
                }
            }
        }
    };

    /**
     * 启动支付界面
     */
    public void doStartUnionPayPlugin(Activity activity, String tn, String mode) {
        UPPayAssistEx.startPayByJAR(activity, PayActivity.class, null, null,
                tn, mode);
    }

    @Override
    public int getLayout() {
        return R.layout.payway_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        App.getApp().exit();
        RndApplication.tempDestroyActivityList.add(PaywayActivity.this);
        setTitle("支付方式");
        initView();
    }

    private void initView() {
        orderInfo = (Orders) getIntent().getSerializableExtra("orderInfo");
        alipay_img = (ImageView) findViewById(R.id.alipay_img);
        bank_img = (ImageView) findViewById(R.id.bank_img);
        dianhui_img = (ImageView) findViewById(R.id.dianhui_img);
        pos_img = (ImageView) findViewById(R.id.pos_img);
        payway_sumPrice = (TextView) findViewById(R.id.payway_sumPrice);
        payWay_order_id = (TextView) findViewById(R.id.payWay_order_id);

        if (orderInfo != null) {
            price = TextUtils.isEmpty(orderInfo.deposit) ? "0"
                    : orderInfo.deposit;
            orderId = orderInfo.orderId;
            order_type = orderInfo.payType;
        }
        payWay_order_id.setText(orderId);
        payway_sumPrice.setText("¥" + price);


        setViewClick(R.id.alipay_ll);
        setViewClick(R.id.bank_ll);
        setViewClick(R.id.bank_dianhui_ll);
        setViewClick(R.id.pos_ll);
        setViewClick(R.id.pay_sure_tv);
        init();
        switch (order_type) {
            case 1:
                // 支付宝支付
                alipay_img.setBackgroundResource(R.drawable.circle_green);
                tag = 1;
                break;
            case 2:
                // 银联支付
                bank_img.setBackgroundResource(R.drawable.circle_green);
                tag = 2;
                break;
            case 3:
                // 银行电汇
                dianhui_img.setBackgroundResource(R.drawable.circle_green);
                tag = 3;
                break;
            case 4:
                // pos支付
                pos_img.setBackgroundResource(R.drawable.circle_green);
                tag = 4;
                break;
            default:
                // 默认选中支付宝支付
                alipay_img.setBackgroundResource(R.drawable.circle_green);
                tag = 1;
                break;

        }
    }

    @Override
    public void OnViewClick(View v) {

        switch (v.getId()) {
            case R.id.alipay_ll://支付宝
                init();
                alipay_img.setBackgroundResource(R.drawable.circle_green);
                tag = 1;
                break;
            case R.id.bank_ll://银联
                init();
                bank_img.setBackgroundResource(R.drawable.circle_green);
                tag = 2;
                break;
            case R.id.bank_dianhui_ll://银行电汇
//                init();
//                dianhui_img.setBackgroundResource(R.drawable.circle_green);
//                tag = 3;
                break;
            case R.id.pos_ll://POS机
//                init();
//                pos_img.setBackgroundResource(R.drawable.circle_green);
//                tag = 4;
                break;
            case R.id.pay_sure_tv:
                if (tag == 1) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("price", price);
                    map.put("title", "新新农人");
                    map.put("message", "新新农人");
                    if (orderInfo != null) {
                        map.put("orderNo", orderInfo.orderNo);
                        map.put("orderId", orderInfo.orderId);
                        getAliPay(map);
                    }
                } else if (tag == 2) {
                    new Thread(this).start();
                } else {
                    showToast("请选择支付方式");
                }
                break;
            default:
                break;
        }

        RequestParams params = new RequestParams();
        params.put("userId", Store.User.queryMe().userid);
        if (orderInfo != null) {
            params.put("orderId", orderInfo.orderId);
        }
        params.put("payType", tag);
        execApi(ApiType.GET_UPDATPAYWAY, params);

    }

    /**
     * 调用支付宝支付
     */
    @SuppressWarnings("unused")
    private void getAliPay(HashMap<String, String> map) {
        new AlipayClass(map, this);
    }

    private void init() {
        alipay_img.setBackgroundResource(R.drawable.circle_gray);
        bank_img.setBackgroundResource(R.drawable.circle_gray);
        dianhui_img.setBackgroundResource(R.drawable.circle_gray);
        pos_img.setBackgroundResource(R.drawable.circle_gray);
    }

    @Override
    public void onResponsed(Request req) {
        if (ApiType.GET_UPDATPAYWAY == req.getApi()) {
            if (req.getData().getStatus().equals("1000")) {
                RndLog.i(TAG, "更改支付方式成功");
            }
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        Object response = null;
        Orders order = null;
        if (orderInfo != null) {
            order = orderInfo;
        } else {
            RndLog.e("unionPay", "Cannot get order information");
        }
        InputStream is;
        try {
            String url = TN_URL_01;
            URL myURL = new URL(url);
            URLConnection ucon = myURL.openConnection();
            ((HttpURLConnection) ucon).setRequestMethod("POST");

            byte[] b = ("consumer=app&responseStyle=v1.0&orderId=" + order.orderId)
                    .getBytes();
            ucon.getOutputStream().write(b, 0, b.length);
            ucon.getOutputStream().flush();
            ucon.getOutputStream().close();
            ucon.setConnectTimeout(120000);
            is = ucon.getInputStream();
            final String json = ServerInterface.toString(is, "UTF-8");
            is.close();
            response = ServerInterface.parseJson(json, UnionPayResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Message msg = mHandler.obtainMessage();
        msg.obj = response;
        mHandler.sendMessage(msg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
		 */
        String str = data.getExtras().getString("pay_result");
        RndLog.v(TAG, str);
        if (str != null) {
            if (str.equalsIgnoreCase("success")) {
                msg = "支付成功！";
                Intent intent = new Intent(PaywayActivity.this,
                        OrderSuccessActivity.class);
                intent.putExtra("orderId", orderInfo.orderId);
                intent.putExtra("OrderNo", orderInfo.orderNo);
                startActivity(intent);

            } else if (str.equalsIgnoreCase("fail")) {
                msg = "支付失败！";
            } else if (str.equalsIgnoreCase("cancel")) {
                msg = "用户取消了支付";
            }
        }
        // 支付完成,处理自己的业务逻辑!
        RndLog.v(TAG, msg);

    }

}
