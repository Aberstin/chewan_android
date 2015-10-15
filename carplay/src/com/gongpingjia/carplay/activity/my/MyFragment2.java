package com.gongpingjia.carplay.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gongpingjia.carplay.R;
import com.gongpingjia.carplay.activity.active.ActiveDetailsActivity2;
import com.gongpingjia.carplay.api.API2;
import com.gongpingjia.carplay.bean.User;
import com.gongpingjia.carplay.view.RoundImageView;
import com.gongpingjia.carplay.view.dialog.NearbyFilterDialog;

import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import net.duohuo.dhroid.util.ViewUtil;

import org.json.JSONObject;


/**
 * 我的页面
 * +* @author Administrator
 */
public class MyFragment2 extends Fragment implements OnClickListener {
    View mainV;
    static MyFragment2 instance;
    private RoundImageView headI;
    private ImageView sexI, photo_bgI;
    private TextView attestationT, nameT, ageT, completenessT;
    private Button perfectBtn;
    private RelativeLayout sexbgR;
    private LinearLayout myphotoL, myactiveL, myattentionL, headattestationL, carattestationL;

    public static MyFragment2 getInstance() {
        if (instance == null) {
            instance = new MyFragment2();
        }

        return instance;
    }

    User user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        mainV = inflater.inflate(R.layout.fragment_my2, null);
        user = User.getInstance();
        initView();
        return mainV;
    }

    private void initView() {
        headI = (RoundImageView) mainV.findViewById(R.id.head);
        attestationT = (TextView) mainV.findViewById(R.id.attestation);
        nameT = (TextView) mainV.findViewById(R.id.name);
        sexbgR = (RelativeLayout) mainV.findViewById(R.id.layout_sex_and_age);
        sexI = (ImageView) mainV.findViewById(R.id.iv_sex);
        ageT = (TextView) mainV.findViewById(R.id.tv_age);
        completenessT = (TextView) mainV.findViewById(R.id.txt_completeness);
        perfectBtn = (Button) mainV.findViewById(R.id.perfect);
        photo_bgI = (ImageView) mainV.findViewById(R.id.photo_bg);
        myphotoL = (LinearLayout) mainV.findViewById(R.id.myphoto);
        myactiveL = (LinearLayout) mainV.findViewById(R.id.myactive);
        myattentionL = (LinearLayout) mainV.findViewById(R.id.myattention);
        headattestationL = (LinearLayout) mainV.findViewById(R.id.headattestation);
        carattestationL = (LinearLayout) mainV.findViewById(R.id.carattestation);

        perfectBtn.setOnClickListener(this);
        myactiveL.setOnClickListener(this);
        myattentionL.setOnClickListener(this);
        headattestationL.setOnClickListener(this);
        carattestationL.setOnClickListener(this);
        headI.setOnClickListener(this);

        if (user.isLogin()) {
            getMyDetails();
        }
    }

    public void getMyDetails() {

        DhNet verifyNet = new DhNet(API2.CWBaseurl + "/user/" + user.getUserId()
                + "/info?viewUser=" + user.getUserId() + "&token=" + user.getToken());
        verifyNet.doGet(new NetTask(getActivity()) {

            @Override
            public void doInUI(Response response, Integer transfer) {
//                System.out.println(user.getUserId()+"---------"+user.getToken());
                if (response.isSuccess()) {
                    JSONObject jo = response.jSONFromData();

                    ViewUtil.bindView(nameT, JSONUtil.getString(jo, "nickname"));
                    String gender = JSONUtil.getString(jo, "gender");
                    if (gender.equals("男")) {
                        sexbgR.setBackgroundResource(R.drawable.radio_sex_man_normal);
                        sexI.setBackgroundResource(R.drawable.icon_man3x);
                    } else {
                        sexbgR.setBackgroundResource(R.drawable.radion_sex_woman_normal);
                        sexI.setBackgroundResource(R.drawable.icon_woman3x);
                    }

                    String headimg=JSONUtil.getString(jo, "avatar");

                    ViewUtil.bindNetImage(headI, headimg, "head");
                    ViewUtil.bindNetImage(photo_bgI, headimg, "head");
//                    photo_bgI.setBackgroundResource(R.drawable.vp_third);
                    ViewUtil.bindView(ageT, JSONUtil.getString(jo, "age"));
//                    Blurry.with(getActivity())
//                            .radius(10)
//                            .sampling(8)
//                            .async()
//                            .capture(photo_bgI)
//                            .into((ImageView) photo_bgI);

                    String licenseAuthStatus = JSONUtil.getString(jo, "licenseAuthStatus");
                    if (licenseAuthStatus.equals("未认证")) {
                        attestationT.setBackgroundResource(R.drawable.radio_sex_man_focused);
                        attestationT.setText("未认证");
                    } else {
                        attestationT.setBackgroundResource(R.drawable.btn_yellow_fillet);
                        attestationT.setText("已认证");
                    }


                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //编辑资料
            case R.id.head:

                break;
            //完善信息
            case R.id.perfect:
                NearbyFilterDialog nearbyFilterDialog=new NearbyFilterDialog(getActivity());
                nearbyFilterDialog.show();
                break;
            //我的活动
            case R.id.myactive:
                Intent it = new Intent(getActivity(), ActiveDetailsActivity2.class);
                startActivity(it);
                break;
            //我的关注
            case R.id.myattention:

                break;
            //头像认证
            case R.id.headattestation:

                break;
            //车主认证
            case R.id.carattestation:

                break;

            default:
                break;
        }
    }
}