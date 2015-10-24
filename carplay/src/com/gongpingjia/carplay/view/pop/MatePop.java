package com.gongpingjia.carplay.view.pop;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.gongpingjia.carplay.R;
import com.gongpingjia.carplay.bean.Matching;
import com.gongpingjia.carplay.view.AnimButtonView2;
import com.gongpingjia.carplay.view.dialog.MatchingDialog;
import com.gongpingjia.carplay.view.dialog.MateLayerDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/21.
 */
public class MatePop implements Runnable, View.OnClickListener {
    Activity context;

    View contentV;

    PopupWindow pop;

    static MatePop instance;

    AnimButtonView2 eatR;

    RelativeLayout vesselR;

    List<AnimButtonView2> list;
    AnimButtonView2 eatView, sportView, movieView, dogView, songView, nightEatView, nightShopView, shopView, coffeeView, beerView;


    public MatePop(final Activity context) {
        this.context = context;
        contentV = LayoutInflater.from(context).inflate(R.layout.activity_mate, null);

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

    public static MatePop getInstance(Activity context) {
        instance = new MatePop(context);
        return instance;

    }

    private void initView() {
        list = new ArrayList<AnimButtonView2>();

        vesselR = (RelativeLayout) contentV.findViewById(R.id.content_layout);

        for (int i = 1; i < vesselR.getChildCount(); i++) {
            AnimButtonView2 view = (AnimButtonView2) vesselR.getChildAt(i);
            list.add(view);
        }

        contentV.findViewById(R.id.btn_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LookAroundPop pop = LookAroundPop.getInstance(context);
                pop.show();

//                Intent it = new Intent(getActivity(), LookAroundActivity.class);
//                startActivity(it);
//                getActivity().overridePendingTransition(R.anim.zoom_in,
//                        R.anim.zoom_out);
            }
        });

        eatView = (AnimButtonView2) contentV.findViewById(R.id.eat);
        sportView = (AnimButtonView2) contentV.findViewById(R.id.exercise);
        songView = (AnimButtonView2) contentV.findViewById(R.id.sing);
        dogView = (AnimButtonView2) contentV.findViewById(R.id.dog);
        movieView = (AnimButtonView2) contentV.findViewById(R.id.film);

        shopView = (AnimButtonView2) contentV.findViewById(R.id.shop);
        nightEatView = (AnimButtonView2) contentV.findViewById(R.id.night_eat);
        nightShopView = (AnimButtonView2) contentV.findViewById(R.id.night_shop);
        coffeeView = (AnimButtonView2) contentV.findViewById(R.id.coffee);
        beerView = (AnimButtonView2) contentV.findViewById(R.id.beer);

        shopView.setOnClickListener(this);
        nightShopView.setOnClickListener(this);
        nightEatView.setOnClickListener(this);
        coffeeView.setOnClickListener(this);
        beerView.setOnClickListener(this);

        eatView.setOnClickListener(this);
        sportView.setOnClickListener(this);
        songView.setOnClickListener(this);
        dogView.setOnClickListener(this);
        movieView.setOnClickListener(this);

        new Thread(this).start();
    }

    public void show() {
        pop.showAtLocation(contentV.getRootView(), Gravity.CENTER, 0, 0);
        // if (addresssT != null) {
        // addresssT.setText("不限");
        // }
    }

    @Override
    public void run() {
        for (int i = 0; true; i++) {
            Message msg = new Message();
            msg.what = 1;
            Bundle bd = new Bundle();
            bd.putInt("index", i % list.size());
            msg.setData(bd);
            mHandler.sendMessage(msg);
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    int index = msg.getData().getInt("index");
                    list.get(index).startAnimation();

                    break;
            }
        }
    };


    /**
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exercise:
                showMatchingDialog("足球", "篮球", "羽毛球", "桌球", "健身");
                break;

            //不需要付费类型
            case R.id.dog:
                showMatchingDialog("遛狗");
                break;
            case R.id.shop:
                showMatchingDialog("购物");
                break;

            //需要付费类型
            case R.id.film:
                showMatchingDialog(context, "看电影");
                break;
            case R.id.sing:
                showMatchingDialog(context, "唱歌");
                break;
            case R.id.eat:
                showMatchingDialog(context, "吃饭");
                break;
            case R.id.coffee:
                showMatchingDialog(context, "咖啡");
                break;
            case R.id.night_eat:
                showMatchingDialog(context, "夜宵");
                break;
            case R.id.night_shop:
                showMatchingDialog(context, "夜店");
                break;
            case R.id.beer:
                showMatchingDialog(context, "喝酒");
                break;
        }
    }

    //发布匹配意向一个参数直接代表活动类型，多个参数代表可以选择的运动类型
    private void showMatchingDialog(String... names) {
        Matching matching;
        List<Matching> data = new ArrayList<>();
        for (String type : names) {
            matching = new Matching();
            matching.setName(type);
            data.add(matching);
        }
        if (data.size() == 1) {
            data.get(0).setIsChecked(true);
        }
        MatchingDialog dlg = new MatchingDialog(context, data);
        dlg.show();
    }

    private void showMatchingDialog(Context context, String type) {
        MateLayerDialog dlg = new MateLayerDialog(context, type);
        dlg.show();
    }

}