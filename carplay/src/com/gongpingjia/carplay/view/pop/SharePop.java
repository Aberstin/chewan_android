package com.gongpingjia.carplay.view.pop;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gongpingjia.carplay.R;
import com.gongpingjia.carplay.api.Constant;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 * Created by Administrator on 2015/11/19.
 */
public class SharePop implements View.OnClickListener {

    Activity context;

    private Bundle bundle;

    //0为官方活动,1为个人活动
    private int type;

    View contentV;

    PopupWindow pop;

    static LookAroundPop instance;

    LinearLayout layout_share_weixin, layout_share_wxcircle;
    TextView tv_cancel;

    // umeng分享
    private UMSocialService mController = UMServiceFactory
            .getUMSocialService("com.umeng.share");

    // 微信id(正式版)
    // static String sAppId = "wx4c127cf07bd7d80b";
    // static String sAppSecret = "315ce754c5a1096c5188b4b69a7b9f04";

    // 微信(测试版)
    static String sAppId = Constant.WX_APP_KEY;
    static String sAppSecret = Constant.WX_APP_SECRET;

    // 微信好友
    private UMWXHandler wxHandler;

    // 微信朋友圈
    private UMWXHandler wxCircleHandler;

    public SharePop(Activity context, Bundle bundle,int type) {
        this.context = context;
        this.bundle = bundle;
        this.type = type;
        contentV = LayoutInflater.from(context).inflate(R.layout.pop_share, null);
        pop = new PopupWindow(contentV, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, true);
        // 需要设置一下此参数，点击外边可消失
        pop.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击窗口外边窗口消失
        pop.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        pop.setFocusable(true);
        pop.setAnimationStyle(R.style.PopupLookAround);
        pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        initView();

    }

    public static LookAroundPop getInstance(Activity context) {
        if (instance == null) {
            instance = new LookAroundPop(context);
        }
        return instance;
    }

    private void initView() {

        layout_share_weixin = (LinearLayout) contentV.findViewById(R.id.layout_share_weixin);
        layout_share_wxcircle = (LinearLayout) contentV.findViewById(R.id.layout_share_wxcircle);
        tv_cancel = (TextView) contentV.findViewById(R.id.tv_cancel);

        layout_share_weixin.setOnClickListener(this);
        layout_share_wxcircle.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);

        setupShare();
    }

    public void show() {
        pop.showAtLocation(contentV.getRootView(), Gravity.CENTER, 0, 0);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //好友
            case R.id.layout_share_weixin:
                shareToWeixinFriend();
                break;
            //朋友圈
            case R.id.layout_share_wxcircle:
                shareToWeixinCircle();
                break;
            case R.id.tv_cancel:
                pop.dismiss();
                break;
        }
    }

    private void setupShare() {
        // 微信朋友圈
        wxCircleHandler = new UMWXHandler(context, sAppId, sAppSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
        // 微信好友
        wxHandler = new UMWXHandler(context, sAppId, sAppSecret);
        wxHandler.addToSocialSDK();
    }

    /**
     * 分享到微信好友
     */
    private void shareToWeixinFriend() {
        if (bundle != null) {
            // TODO Auto-generated method stub
            WeiXinShareContent wxContent = new WeiXinShareContent();
            wxContent.setTargetUrl(bundle.get("shareUrl").toString());
            wxContent.setTitle(bundle.get("shareTitle").toString());
            wxContent.setShareContent(bundle.get("shareContent").toString());
            wxContent.setShareImage(new UMImage(context, bundle.get("image").toString()));
            mController.setShareMedia(wxContent);
            mController.postShare(context,
                    SHARE_MEDIA.WEIXIN,
                    new SnsPostListener() {
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onComplete(
                                SHARE_MEDIA arg0,
                                int arg1,
                                SocializeEntity arg2) {
                            pop.dismiss();
                        }
                    });
        }
    }

    /**
     * 分享到微信朋友圈
     */
    private void shareToWeixinCircle() {
        if (bundle != null) {
            // TODO Auto-generated method stub
            CircleShareContent ccContent = new CircleShareContent();
            ccContent.setTargetUrl(bundle.get("shareUrl").toString());
            ccContent.setShareContent(bundle.get("shareContent").toString());
            ccContent.setTitle(bundle.get("shareTitle").toString());
            ccContent.setShareImage(new UMImage(context, bundle.get("image").toString()));
            mController.setShareMedia(ccContent);
            mController.postShare(context,
                    SHARE_MEDIA.WEIXIN_CIRCLE,
                    new SnsPostListener() {
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onComplete(
                                SHARE_MEDIA arg0,
                                int arg1,
                                SocializeEntity arg2) {
                            pop.dismiss();
                        }
                    });
        }
    }
}
