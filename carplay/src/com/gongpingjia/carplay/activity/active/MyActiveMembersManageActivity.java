package com.gongpingjia.carplay.activity.active;

import net.duohuo.dhroid.adapter.FieldMap;
import net.duohuo.dhroid.adapter.PSAdapter;
import net.duohuo.dhroid.ioc.IocContainer;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import net.duohuo.dhroid.util.DhUtil;
import net.duohuo.dhroid.util.ViewUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gongpingjia.carplay.R;
import com.gongpingjia.carplay.activity.CarPlayBaseActivity;
import com.gongpingjia.carplay.api.API;
import com.gongpingjia.carplay.bean.User;
import com.gongpingjia.carplay.util.CarPlayPerference;
import com.gongpingjia.carplay.util.CarPlayUtil;
import com.gongpingjia.carplay.util.CarSeatUtil;
import com.gongpingjia.carplay.util.CarSeatUtil.OnSeatClickListener;
import com.gongpingjia.carplay.view.RoundImageView;
import com.gongpingjia.carplay.view.dialog.SeatDialog;
import com.gongpingjia.carplay.view.dialog.SeatDialog.OnGradResultListener;
import com.gongpingjia.carplay.view.dialog.SeatDialog.OnPullDownResultListener;
import com.gongpingjia.carplay.view.swipeMenuListView.SwipeMenu;
import com.gongpingjia.carplay.view.swipeMenuListView.SwipeMenuCreator;
import com.gongpingjia.carplay.view.swipeMenuListView.SwipeMenuItem;
import com.gongpingjia.carplay.view.swipeMenuListView.SwipeMenuListView;
import com.gongpingjia.carplay.view.swipeMenuListView.SwipeMenuListView.OnMenuItemClickListener;
import com.gongpingjia.carplay.view.swipeMenuListView.SwipeMenuListView.OnRefreshListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class MyActiveMembersManageActivity extends CarPlayBaseActivity {
    SwipeMenuListView listV;

    PSAdapter adapter;

    View headV;

    String activityId;

    LinearLayout carLayoutV;

    CarSeatUtil carUtil;

    User user;

    /** 拉下座位 立即抢座 Dialog */
    SeatDialog pullDownDialog, grabDialog;

    CarPlayPerference per;

    // umeng分享
    private UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_active_members);

        setRightAction("邀请", -1, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                View shareView = LayoutInflater.from(self).inflate(R.layout.pop_share, null);

                final UMImage image = new UMImage(
                        self,
                        "http://cwapi.gongpingjia.com/v1/user/846de312-306c-4916-91c1-a5e69b158014/message/count?token=750dd49c-6129-4a9a-9558-27fa74fc4ce7");

                final PopupWindow popWin = new PopupWindow(shareView, LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
                // 分享到微信朋友
                shareView.findViewById(R.id.layout_share_weixin).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        WeiXinShareContent wxContent = new WeiXinShareContent();
                        wxContent.setTargetUrl("http://cwapi.gongpingjia.com/?activityId=XXXX");
                        wxContent.setTitle("title");
                        wxContent.setShareContent("content");
                        wxContent.setShareImage(image);
                        mController.setShareMedia(wxContent);
                        mController.postShare(self, SHARE_MEDIA.WEIXIN, new SnsPostListener() {

                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2) {
                                popWin.dismiss();
                            }
                        });
                    }
                });

                // 分享到朋友圈
                shareView.findViewById(R.id.layout_share_wxcircle).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        CircleShareContent ccContent = new CircleShareContent();
                        ccContent.setTargetUrl("http://cwapi.gongpingjia.com/?activityId=XXXX");
                        ccContent.setShareContent("content");
                        ccContent.setTitle("title");
                        ccContent.setShareImage(image);
                        mController.setShareMedia(ccContent);

                        mController.postShare(self, SHARE_MEDIA.WEIXIN_CIRCLE, new SnsPostListener() {

                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2) {
                                popWin.dismiss();
                            }
                        });
                    }
                });

                shareView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        switch (v.getId()) {
                        case R.id.layout_bg:
                            popWin.dismiss();
                            break;
                        }
                    }
                });
                popWin.showAsDropDown(findViewById(R.id.title_bar));
            }
        });
    }

    @Override
    public void initView() {
        per = IocContainer.getShare().get(CarPlayPerference.class);
        per.load();
        if (per.isShowMemberGuilde == 0) {
            findViewById(R.id.guide).setVisibility(View.VISIBLE);
        }

        findViewById(R.id.know).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                per.load();
                per.isShowMemberGuilde = 1;
                per.commit();
                findViewById(R.id.guide).setVisibility(View.GONE);
            }
        });

        setTitle("成员管理");
        user = User.getInstance();
        activityId = getIntent().getStringExtra("activityId");
        headV = LayoutInflater.from(self).inflate(R.layout.head_active_members, null);
        carLayoutV = (LinearLayout) headV.findViewById(R.id.car_layout);
        carUtil = new CarSeatUtil(self, carLayoutV);
        carUtil.setOnSeatClickListener(new OnSeatClickListener() {

            @Override
            public void onSeatClick(String carId, int childPosition) {
                // showGradDialog(carId, childPosition);
            }

            @Override
            public void onHeadClick(JSONObject headJo) {
                showRemoveSeatMemberDialog(headJo);
            }

            @Override
            public void seatCount(int totalCount, int emptyCount) {
                // TODO Auto-generated method stub

            }
        });

        listV = (SwipeMenuListView) findViewById(R.id.listView);
        listV.removeFootView();
        listV.addHeaderView(headV);

        adapter = new PSAdapter(self, R.layout.item_newmessage_list);
        adapter.addField(new FieldMap("nickname", R.id.name) {

            @Override
            public Object fix(View itemV, Integer position, Object o, Object jo) {
                JSONObject itemjo = (JSONObject) jo;

                RoundImageView headI = (RoundImageView) itemV.findViewById(R.id.head);
                ViewUtil.bindNetImage(headI, JSONUtil.getString(itemjo, "photo"), "default");
                headI.setTag(JSONUtil.getString(itemjo, "userId"));

                View sexBg = itemV.findViewById(R.id.sex);

                if (JSONUtil.getString(itemjo, "gender").equals("男")) {
                    sexBg.setBackgroundResource(R.drawable.man);
                } else {
                    sexBg.setBackgroundResource(R.drawable.woman);
                }

                ViewUtil.bindView(itemV.findViewById(R.id.age), JSONUtil.getString(itemjo, "age"));
                ViewUtil.bindView(itemV.findViewById(R.id.name), JSONUtil.getString(itemjo, "nickname"));
                CarPlayUtil.bindDriveAge(itemjo, (ImageView) itemV.findViewById(R.id.car_logo),
                        (TextView) itemV.findViewById(R.id.drive_age));
                // TODO Auto-generated method stub
                return o;
            }
        });
        listV.setAdapter(adapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
                openItem.setBackground(new ColorDrawable(getResources().getColor(R.color.text_orange)));
                openItem.setWidth(DhUtil.dip2px(self, 90));
                openItem.setTitle("删除");
                openItem.setTitleSize(18);
                openItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(openItem);

            }
        };
        listV.setMenuCreator(creator);

        listV.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                JSONObject jo = (JSONObject) adapter.getItem(position);
                deleteMember(JSONUtil.getString(jo, "userId"));
            }
        });

        listV.setonRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                getData();
            }
        });
        pullDownDialog = new SeatDialog(self);
        grabDialog = new SeatDialog(self);
        getData();
    }

    private void getData() {
        DhNet net = new DhNet(API.CWBaseurl + "/activity/" + activityId + "/members?userId=" + user.getUserId()
                + "&token=" + user.getToken());
        net.doGetInDialog(new NetTask(self) {

            @Override
            public void doInUI(Response response, Integer transfer) {
                adapter.clear();
                JSONArray jsa = response.jSONArrayFrom("data.members");
                adapter.addAll(jsa);
                JSONArray carJsa = response.jSONArrayFrom("data.cars");
                carUtil.addCar(carJsa);

                listV.onRefreshComplete();
                listV.removeFootView();
            }
        });
    }

    /**
     * 移除成员
     * 
     */
    private void deleteMember(String userId) {
        DhNet net = new DhNet(API.CWBaseurl + "/activity/" + activityId + "/member/remove?userId=" + user.getUserId()
                + "&token=" + user.getToken());
        net.addParam("member", userId);
        net.doPostInDialog(new NetTask(self) {

            @Override
            public void doInUI(Response response, Integer transfer) {
                if (response.isSuccess()) {
                    showToast("成员移除成功!");
                    getData();
                }
            }
        });
    }

    /** 拉下座位弹框 */
    private void showRemoveSeatMemberDialog(JSONObject memberJo) {

        pullDownDialog.setOnPullDownResultListener(new OnPullDownResultListener() {

            @Override
            public void click(String userid) {
                removeSeatMember(userid);
            }
        });
        pullDownDialog.show();
        pullDownDialog.setData(memberJo);
    }

    /** 抢座弹框 */
    private void showGradDialog(String carId, int seatIndex) {
        grabDialog.setOnGradResultListener(new OnGradResultListener() {

            @Override
            public void click(String carId, int seatIndex) {
                gradSeat(carId, seatIndex);
            }
        });
        grabDialog.show();
        grabDialog.setSeatData(carId, seatIndex);
    }

    /**
     * 
     * 拉下座位
     */
    private void removeSeatMember(String userId) {
        DhNet net = new DhNet(API.CWBaseurl + "/activity/" + activityId + "/seat/return?userId=" + user.getUserId()
                + "&token=" + user.getToken());
        net.addParam("member", userId);
        net.doPostInDialog(new NetTask(self) {

            @Override
            public void doInUI(Response response, Integer transfer) {
                if (response.isSuccess()) {
                    showToast("成员移除成功!");
                    getData();
                }
            }
        });
    }

    /**
     * 
     * 抢座
     */
    private void gradSeat(String carId, int seatIndex) {
        DhNet net = new DhNet(API.CWBaseurl + "/activity/" + activityId + "/seat/take?userId=" + user.getUserId()
                + "&token=" + user.getToken());
        net.addParam("carId", carId);
        net.addParam("seatIndex", seatIndex);
        net.doPostInDialog(new NetTask(self) {

            @Override
            public void doInUI(Response response, Integer transfer) {
                if (response.isSuccess()) {
                    showToast("抢座成功!");
                    getData();
                }
            }
        });
    }

}
