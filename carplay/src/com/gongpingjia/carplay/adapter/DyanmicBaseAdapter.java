package com.gongpingjia.carplay.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gongpingjia.carplay.CarPlayApplication;
import com.gongpingjia.carplay.CarPlayValueFix;
import com.gongpingjia.carplay.R;
import com.gongpingjia.carplay.activity.active.ActiveDetailsActivity2;
import com.gongpingjia.carplay.activity.chat.ChatActivity;
import com.gongpingjia.carplay.activity.chat.VoiceCallActivity;
import com.gongpingjia.carplay.activity.my.PersonDetailActivity2;
import com.gongpingjia.carplay.api.API2;
import com.gongpingjia.carplay.bean.PointRecord;
import com.gongpingjia.carplay.bean.User;
import com.gongpingjia.carplay.util.CarPlayUtil;
import com.gongpingjia.carplay.view.AnimButtonView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import net.duohuo.dhroid.util.ViewUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.greenrobot.event.EventBus;
import jp.wasabeef.blurry.internal.BlurFactor;
import jp.wasabeef.blurry.internal.BlurTask;


/**
 * 活动动态
 * Created by Administrator on 2015/10/20.
 */
public class DyanmicBaseAdapter extends BaseAdapter {
    private final Context mContext;
    //    String activityId;
    private List<JSONObject> data;

    User user = User.getInstance();

    public DyanmicBaseAdapter(Context context) {
        mContext = context;

    }

    public void setData(List<JSONObject> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        JSONObject item = null;
        if (null != data) {
            item = data.get(i);
        }
        return item;
    }


    @Override
    public int getItemViewType(int position) {

        JSONObject jo = data.get(position);
        if (JSONUtil.getString(jo, "activityCategory").equals("官方活动")) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;

        OfficialHolder officialHolder = null;

        int type = getItemViewType(i);

        //
        if (view == null) {
            if (type == 0) {
                holder = new ViewHolder();
                view = LayoutInflater.from(mContext).inflate(R.layout.item_activelist, viewGroup, false);
                holder.layoutV = (RelativeLayout) view.findViewById(R.id.layout);
                holder.yingyaohou = (LinearLayout) view.findViewById(R.id.yingyaohou);
                holder.yingyao_layout = (LinearLayout) view.findViewById(R.id.yingyao_layout);
                holder.titlelayoutL = (LinearLayout) view.findViewById(R.id.titlelayout);
                holder.invitation = (LinearLayout) view.findViewById(R.id.invitation);
                holder.titleT = (TextView) view.findViewById(R.id.dynamic_title);
                holder.dynamic_typeT = (TextView) view.findViewById(R.id.dynamic_type);
                holder.dynamic_carname = (TextView) view.findViewById(R.id.dynamic_carname);
                holder.pay_type = (TextView) view.findViewById(R.id.pay_type);
                holder.travelmode = (TextView) view.findViewById(R.id.travelmode);
                holder.activity_place = (TextView) view.findViewById(R.id.activity_place);


                holder.sexbgR = (RelativeLayout) view.findViewById(R.id.layout_sex_and_age);
                holder.sexI = (ImageView) view.findViewById(R.id.iv_sex);
                holder.ageT = (TextView) view.findViewById(R.id.tv_age);
                holder.inviteT = (TextView) view.findViewById(R.id.inviteT);
                holder.invitationT = (TextView) view.findViewById(R.id.invitationT);

                holder.dynamic_carlogo = (ImageView) view.findViewById(R.id.dynamic_carlogo);
                holder.certification_achievement = (ImageView) view.findViewById(R.id.certification_achievement);
                holder.activity_beijing = (ImageView) view.findViewById(R.id.activity_beijing);


                holder.dyanmic_one = (AnimButtonView) view.findViewById(R.id.dyanmic_one);
                holder.dyanmic_two = (AnimButtonView) view.findViewById(R.id.dyanmic_two);
                holder.yingyao = (AnimButtonView) view.findViewById(R.id.yingyao);
                holder.hulue = (AnimButtonView) view.findViewById(R.id.hulue);
                holder.hulue = (AnimButtonView) view.findViewById(R.id.hulue);
                holder.invitationI = (AnimButtonView) view.findViewById(R.id.invitationI);

                holder.activity_distance = (TextView) view.findViewById(R.id.active_distance);
                view.setTag(holder);
                FrameLayout.LayoutParams pam = (FrameLayout.LayoutParams) holder.layoutV.getLayoutParams();
                pam.height = CarPlayApplication.getInstance().getImageHeight();
                holder.layoutV.setLayoutParams(pam);
            } else {
                view = LayoutInflater.from(mContext).inflate(R.layout.item_offical, viewGroup, false);
                officialHolder = new OfficialHolder();
                officialHolder.titleT = (TextView) view.findViewById(R.id.title);
                officialHolder.locationT = (TextView) view.findViewById(R.id.location);
                officialHolder.priceT = (TextView) view.findViewById(R.id.price);
                officialHolder.pricerightT = (TextView) view.findViewById(R.id.priceright);
                officialHolder.priceDescT = (TextView) view.findViewById(R.id.priceDesc);
                officialHolder.picI = (ImageView) view.findViewById(R.id.pic);
                officialHolder.headI = (ImageView) view.findViewById(R.id.head);
                officialHolder.invitation = (LinearLayout) view.findViewById(R.id.invitation);
                officialHolder.invitationI = (AnimButtonView) view.findViewById(R.id.invitationI);
                officialHolder.invitationT = (TextView) view.findViewById(R.id.invitationT);
//                officialHolder.maleLimitT = (TextView) view.findViewById(R.id.maleLimit);
//                officialHolder.maleNumT = (TextView) view.findViewById(R.id.maleNum);
//                officialHolder.femaleLimitT = (TextView) view.findViewById(R.id.femaleLimit);
//                officialHolder.femaleNumT = (TextView) view.findViewById(R.id.femaleNum);
                officialHolder.limitedlayoutL = (LinearLayout) view.findViewById(R.id.limitedlayout);
                officialHolder.unlimitedlayoutL = (LinearLayout) view.findViewById(R.id.unlimitedlayout);
                officialHolder.participate_womanT = (TextView) view.findViewById(R.id.participate_woman);
                officialHolder.participate_manT = (TextView) view.findViewById(R.id.participate_man);
                officialHolder.unparticipateT = (TextView) view.findViewById(R.id.unparticipate);
                officialHolder.cityT = (TextView) view.findViewById(R.id.city);
                officialHolder.layoutV = (RelativeLayout) view.findViewById(R.id.layout);
                view.setTag(officialHolder);
                LinearLayout.LayoutParams pam = (LinearLayout.LayoutParams) officialHolder.layoutV.getLayoutParams();
                pam.height = CarPlayApplication.getInstance().getImageHeight();
                officialHolder.layoutV.setLayoutParams(pam);

            }
        } else {
            if (type == 0) {
                holder = (ViewHolder) view.getTag();
            } else {
                officialHolder = (OfficialHolder) view.getTag();
            }

        }

        JSONObject jo = (JSONObject) getItem(i);
        if (type == 0) {

            final JSONObject js = JSONUtil.getJSONObject(jo, "applicant");
            JSONObject json = JSONUtil.getJSONObject(jo, "destination");
            JSONObject ob = JSONUtil.getJSONObject(js, "car");
            //活动id
            final String activityId = JSONUtil.getString(jo, "activityId");
            int status = JSONUtil.getInt(jo, "status");
            final String appointmentId = JSONUtil.getString(jo, "appointmentId");
            Boolean isApplicant = JSONUtil.getBoolean(jo, "isApplicant");
//        View[] views = {holder.dyanmic_one, holder.dyanmic_two, holder.yingyao_layout, holder.yingyaohou};
//        View[] viewstwo = {holder.hulue, holder.yingyao, holder.yingyao_layout, holder.yingyaohou};
//
//        CarPlayUtil.bindActiveButton("邀请中", appointmentId, mContext, views);
//        CarPlayUtil.bindActiveButton("应邀", appointmentId, mContext, viewstwo);
//                CarPlayUtil.bindActiveButton("拒绝".views);
            holder.ageT.setText(JSONUtil.getString(js, "age"));
            holder.dyanmic_one.startScaleAnimation();
            holder.dyanmic_two.startScaleAnimation();
            holder.yingyao.startScaleAnimation();
            holder.hulue.startScaleAnimation();
            holder.invitationI.startScaleAnimation();


            CarPlayUtil.bindActiveButton2("邀请中", appointmentId, mContext, holder.yingyao_layout, holder.yingyaohou);
            String typeT = JSONUtil.getString(jo, "type");
            String name = JSONUtil.getString(js, "nickname");

            if (status == 1) {
                if (isApplicant == true) {
//                System.out.println("我应邀别人。。。。。。。。。。。。");
                    holder.invitation.setVisibility(View.VISIBLE);
                    holder.yingyao_layout.setVisibility(View.GONE);
                    holder.yingyaohou.setVisibility(View.GONE);
                    holder.titleT.setText("你邀请" + name + "去");
                    holder.invitationT.setText("邀请中");
                    holder.invitationI.setResourseAndBg(R.drawable.dynamic_grey
                            , R.drawable.dynamic_grey);
                } else {
//                System.out.println("别人应邀我。。。。。。。。。。。。");
                    holder.yingyao_layout.setVisibility(View.VISIBLE);
                    holder.invitation.setVisibility(View.GONE);
                    holder.yingyaohou.setVisibility(View.GONE);
                    holder.titleT.setText(name + "想邀请你");
                    holder.invitationI.setResourseAndBg(R.drawable.btn_red_fillet
                            , R.drawable.btn_red_fillet);
                }
            } else if (status == 2) {
                if (isApplicant == true) {
                    holder.yingyao_layout.setVisibility(View.GONE);
                    holder.yingyaohou.setVisibility(View.VISIBLE);
                    holder.invitation.setVisibility(View.GONE);
                    holder.titleT.setText("你邀请" + name + "去");
//                    holder.invitationT.setText("邀请中");
//                    holder.invitationI.setResourseAndBg(R.drawable.dynamic_grey
//                            , R.drawable.dynamic_grey);
                } else {
                    holder.yingyao_layout.setVisibility(View.GONE);
                    holder.invitation.setVisibility(View.GONE);
                    holder.yingyaohou.setVisibility(View.VISIBLE);
                    holder.titleT.setText(name + "想邀请你");
                }
            }
            holder.dynamic_typeT.setText(typeT);
            if ("邀请同去".equals(JSONUtil.getString(jo, "activityCategory"))) {
                holder.dynamic_typeT.setTextColor(mContext.getResources().getColor(R.color.text_orange));
                holder.titlelayoutL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ActiveDetailsActivity2.class);
                        intent.putExtra("activityId", activityId);
                        mContext.startActivity(intent);
                    }
                });
            } else {
                holder.dynamic_typeT.setTextColor(mContext.getResources().getColor(R.color.text_black));
                holder.titlelayoutL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent it = new Intent(mContext, PersonDetailActivity2.class);
                        String userId = JSONUtil.getString(js, "userId");
                        it.putExtra("userId", userId);
                        mContext.startActivity(it);
                    }
                });
            }


//            else if(status == 4){
//                if (isApplicant == true) {
//                    holder.yingyao_layout.setVisibility(View.GONE);
//                    holder.yingyaohou.setVisibility(View.GONE);
//                    holder.invitation.setVisibility(View.VISIBLE);
//                    holder.titleT.setText("你邀请" + name + "去" + typeT);
//                    holder.invitationT.setText("已失效");
//                    holder.invitationI.setResourseAndBg(R.drawable.dynamic_grey
//                            , R.drawable.dynamic_grey);
//                } else {
//                    holder.yingyao_layout.setVisibility(View.GONE);
//                    holder.yingyaohou.setVisibility(View.GONE);
//                    holder.invitation.setVisibility(View.VISIBLE);
//                    holder.invitationT.setText("已失效");
//                    holder.invitationI.setResourseAndBg(R.drawable.dynamic_grey
//                            , R.drawable.dynamic_grey);
//                    holder.titleT.setText(name + "想邀请你" + typeT);
//                }
//            }


            String licenseAuthStatus = JSONUtil.getString(js, "licenseAuthStatus");
            String photoAuthStatus = JSONUtil.getString(js, "photoAuthStatus");
//            if (photoAuthStatus.equals("认证通过")) {
//                holder.certification_achievement.setImageResource(R.drawable.headaut_dl);
//            } else if (photoAuthStatus.equals("未认证")) {
//                holder.certification_achievement.setImageResource(R.drawable.headaut_no);
//            }
            holder.certification_achievement.setImageResource("认证通过".equals(photoAuthStatus) ? R.drawable.headaut_dl : R.drawable.headaut_no);
            String gender = JSONUtil.getString(js, "gender");

//        String jied = JSONUtil.getString(json, "street");
            String locationS = JSONUtil.getString(json, "province") + JSONUtil.getString(json, "city") + JSONUtil.getString(json, "district") + JSONUtil.getString(json, "street") + JSONUtil.getString(json, "detail");
            locationS = locationS.replace("null", "");
            String district = JSONUtil.getString(json, "district");
            String street = JSONUtil.getString(json, "street");
            if (TextUtils.isEmpty(locationS)) {
                holder.activity_place.setText("地点待定");
            } else {
                if (district.equals(street)) {
                    holder.activity_place.setText(JSONUtil.getString(json, "city") + JSONUtil.getString(json, "district"));
                } else {
                    holder.activity_place.setText(JSONUtil.getString(json, "city") + JSONUtil.getString(json, "district") + JSONUtil.getString(json, "street"));
                }
            }


            int distance = (int) Math.floor(JSONUtil.getDouble(jo, "distance"));
//        DecimalFormat df = new DecimalFormat("0.00");
            holder.activity_distance.setText(CarPlayUtil.numberWithDelimiter(distance));
            if (("男").equals(gender)) {
                holder.sexbgR.setBackgroundResource(R.drawable.radio_sex_man_normal);
                holder.sexI.setBackgroundResource(R.drawable.icon_man3x);
            } else {
                holder.sexbgR.setBackgroundResource(R.drawable.radion_sex_woman_normal);
                holder.sexI.setBackgroundResource(R.drawable.icon_woman3x);
            }

            if (licenseAuthStatus.equals("认证通过")) {
                ViewUtil.bindNetImage(holder.dynamic_carlogo, JSONUtil.getString(ob, "logo"), "default");
//                holder.dynamic_carname.setText(JSONUtil.getString(ob, "model"));
            } else {
                holder.dynamic_carlogo.setImageResource(R.drawable.no_car);
            }
//            holder.titleT.setText(name + "想邀请你" + typeT);

            holder.pay_type.setText(JSONUtil.getString(jo, "pay"));

            Boolean transfer = JSONUtil.getBoolean(jo, "transfer");
            if (transfer == true) {
                holder.travelmode.setText("包接送");
            } else {
                holder.travelmode.setText("");
            }


            final ViewHolder finalHolder2 = holder;
            ImageLoader.getInstance().loadImage(JSONUtil.getString(js, "cover"), CarPlayValueFix.optionsDefault, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    super.onLoadingStarted(imageUri, view);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view,
                                              final Bitmap bitmap) {

                    if (bitmap != null) {
                        final ImageView img = finalHolder2.activity_beijing;
                        if (!user.isHasAlbum() && !user.getUserId().equals(JSONUtil.getString(js, "userId"))) {
                            ViewTreeObserver
                                    vto = img.getViewTreeObserver();
                            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                                @Override
                                public boolean onPreDraw() {
                                    BlurFactor factor = new BlurFactor();
                                    img.getViewTreeObserver().removeOnPreDrawListener(this);
                                    factor.width = img.getMeasuredWidth();
                                    factor.height = img.getMeasuredHeight();
                                    factor.radius = 10;
                                    factor.sampling = 8;
                                    final Bitmap newBitmap = CarPlayUtil.zoomImage(bitmap, factor.width, factor.height);
                                    BlurTask task = new BlurTask(img, factor, newBitmap, new BlurTask.Callback() {
                                        @Override
                                        public void done(BitmapDrawable drawable) {
                                            img.setImageDrawable(drawable);
                                            newBitmap.recycle();
                                        }
                                    });
                                    task.execute();

                                    return true;
                                }


                            });
                        } else {
                            img.setImageBitmap(bitmap);
                        }
                    }
                }

                @Override
                public void onLoadingFailed(String imageUri, View view,
                                            FailReason failReason) {
                }
            });

//            ViewUtil.bindNetImage(holder.activity_beijing, JSONUtil.getString(js, "avatar"), "default");
            try {
                JSONArray coversJSa = jo.getJSONArray("covers");
                String picUrl = coversJSa.getString(0);
                ViewUtil.bindNetImage(holder.activity_beijing, picUrl, "default");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String message = JSONUtil.getString(jo, "message");
            if (!message.isEmpty()) {
                holder.inviteT.setVisibility(View.VISIBLE);
                holder.inviteT.setText(message);
            } else {
                holder.inviteT.setVisibility(View.GONE);
            }

            final ViewHolder finalHolder = holder;
            final ViewHolder finalHolder1 = holder;
            holder.yingyao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    JSONObject jo = getItem(i);
//                    if (TextUtils.isEmpty(user.getPhone())) {
//                        System.out.println("获取:" + user.getPhone());
//                        ActiveDialog dialog = new ActiveDialog(mContext, appointmentId);
//                        dialog.setOnPickResultListener(new ActiveDialog.OnPickResultListener() {
//
//                            @Override
//                            public void onResult(int result) {
//                                if (result == 1) {
//                                    finalHolder1.yingyao_layout.setVisibility(View.GONE);
//                                    finalHolder1.yingyaohou.setVisibility(View.VISIBLE);
//                                }
//                            }
//                        });
//                        dialog.show();
//                    } else {
                        DhNet net = new DhNet(API2.CWBaseurl + "/application/" + appointmentId + "/process?userId=" + user.getUserId() + "&token=" + user.getToken());
//                    DhNet net = new DhNet(API2.CWBaseurl + "application/" + appointmentId + "/process?userId=5609eb6d0cf224e7d878f695&token=a767ead8-7c00-4b90-b6de-9dcdb4d5bc41");
                        net.addParam("accept", true);
                        net.doPostInDialog(new NetTask(mContext) {
                            @Override
                            public void doInUI(Response response, Integer transfer) {
                                if (response.isSuccess()) {
                                    finalHolder.yingyao_layout.setVisibility(View.GONE);
                                    finalHolder.yingyaohou.setVisibility(View.VISIBLE);
                                    finalHolder.invitation.setVisibility(View.GONE);
//                                    notifyDataSetChanged();
                                    EventBus.getDefault().post("刷新活动动态");
                                    System.out.println("应邀：" + response.isSuccess());
                                }
                            }
                        });
//                    }
                }
            });
            holder.hulue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    JSONObject jo = getItem(position);
                    DhNet net = new DhNet(API2.CWBaseurl + "application/" + appointmentId + "/process?userId=" + user.getUserId() + "&token=" + user.getToken());
//                DhNet net = new DhNet(API2.CWBaseurl + "application/" + appointmentId + "/process?userId=5609eb6d0cf224e7d878f695&token=a767ead8-7c00-4b90-b6de-9dcdb4d5bc41");
                    net.addParam("accept", false);
                    net.doPostInDialog(new NetTask(mContext) {
                        @Override
                        public void doInUI(Response response, Integer transfer) {
                            if (response.isSuccess()) {
                                data.remove(i);
                                notifyDataSetChanged();
                                System.out.println("忽略：" + response.isSuccess());
                            }
                        }
                    });
                }
            });
            holder.dyanmic_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PointRecord record = PointRecord.getInstance();
                    record.getActivityDynamicChatList().add(activityId);
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
                    intent.putExtra("activityId", activityId);
                    intent.putExtra("userId", JSONUtil.getString(js, "emchatName"));
                    mContext.startActivity(intent);

                }
            });
            holder.dyanmic_two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent it = new Intent(mContext, VoiceCallActivity.class);
                        it.putExtra("username", js.getString("emchatName"));
                        it.putExtra("isComingCall", false);
                        mContext.startActivity(it);
                        PointRecord record = PointRecord.getInstance();
                        record.getActivityDynamicCallList().add(activityId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, PersonDetailActivity2.class);
                    String userId = JSONUtil.getString(js, "userId");
                    it.putExtra("userId", userId);
                    mContext.startActivity(it);
                }
            });
        } else {

//            officialHolder.priceT.setText(JSONUtil.getString(jo, "price"));
            double price = JSONUtil.getDouble(jo, "price");
            if (((int) price) == 0) {
                officialHolder.priceT.setText("免费");
                officialHolder.pricerightT.setVisibility(View.GONE);
            } else {
                officialHolder.priceT.setText(price + "");
                officialHolder.pricerightT.setVisibility(View.VISIBLE);
            }


//            officialHolder.priceDescT.setText(JSONUtil.getString(jo, "priceDesc"));
//            String priceDesc = JSONUtil.getString(jo, "subsidyPrice");
//            if (priceDesc.isEmpty()) {
//                officialHolder.priceDescT.setVisibility(View.GONE);
//            } else {
//                officialHolder.priceDescT.setVisibility(View.VISIBLE);
//                officialHolder.priceDescT.setText("官方补贴" + JSONUtil.getString(jo, "subsidyPrice") + "元/人");
//            }
//            officialHolder.priceDescT.setText(JSONUtil.getString(jo, "subsidyPrice"));
//            officialHolder.infoT.setText(JSONUtil.getString(jo, "title"));
//            officialHolder.maleLimitT.setText(JSONUtil.getString(jo, "maleLimit"));
//            officialHolder.maleNumT.setText(JSONUtil.getString(jo, "maleNum") + "/");
//            officialHolder.femaleLimitT.setText(JSONUtil.getString(jo, "femaleLimit"));
//            officialHolder.femaleNumT.setText(JSONUtil.getString(jo, "femaleNum") + "/");

            JSONObject locationJo = JSONUtil.getJSONObject(jo, "destination");
            officialHolder.locationT.setText(JSONUtil.getString(locationJo, "detail"));
//            officialHolder.cityT.setText("[" + JSONUtil.getString(locationJo, "city") + "]");

            String citystr = "[" + JSONUtil.getString(locationJo, "city") + "]  ";
            String title = citystr + JSONUtil.getString(jo, "title");
            ViewUtil.bindView(officialHolder.cityT, CarPlayUtil.setTextColor(mContext, citystr, title, R.color.text_orange));

            JSONObject organizerJo = JSONUtil.getJSONObject(jo, "organizer");
            officialHolder.titleT.setText(JSONUtil.getString(organizerJo, "nickname"));
//            int officstatus = JSONUtil.getInt(jo, "status");
//            if (officstatus == 4) {
//                officialHolder.invitation.setVisibility(View.VISIBLE);
//                officialHolder.invitationT.setText("已失效");
//                officialHolder.invitationI.setResourseAndBg(R.drawable.dynamic_grey
//                        , R.drawable.dynamic_grey);
//            } else {
//                officialHolder.invitation.setVisibility(View.GONE);
//            }
            //0:无限制 1：限制总人数 2：限制男女人数
            int limitType = JSONUtil.getInt(jo, "limitType");
            //男生,女生数量,总量
            if (limitType == 1) {
                officialHolder.limitedlayoutL.setVisibility(View.GONE);
                officialHolder.unlimitedlayoutL.setVisibility(View.VISIBLE);
                ViewUtil.bindView(officialHolder.unparticipateT, CarPlayUtil.setTextColor(mContext, JSONUtil.getInt(jo, "nowJoinNum") + " / ", JSONUtil.getInt(jo, "nowJoinNum") + " / " + JSONUtil.getInt(jo, "totalLimit"), R.color.text_grey));
            } else if (limitType == 2) {
                officialHolder.limitedlayoutL.setVisibility(View.VISIBLE);
                officialHolder.unlimitedlayoutL.setVisibility(View.GONE);
                ViewUtil.bindView(officialHolder.participate_womanT, CarPlayUtil.setTextColor(mContext, JSONUtil.getInt(jo, "femaleNum") + " / ", JSONUtil.getInt(jo, "femaleNum") + " / " + JSONUtil.getInt(jo, "femaleLimit"), R.color.text_grey));
                ViewUtil.bindView(officialHolder.participate_manT, CarPlayUtil.setTextColor(mContext, JSONUtil.getInt(jo, "maleNum") + " / ", JSONUtil.getInt(jo, "maleNum") + " / " + JSONUtil.getInt(jo, "maleLimit"), R.color.text_grey));
            } else {
                officialHolder.limitedlayoutL.setVisibility(View.GONE);
                officialHolder.unlimitedlayoutL.setVisibility(View.VISIBLE);
                ViewUtil.bindView(officialHolder.unparticipateT, CarPlayUtil.setTextColor(mContext, JSONUtil.getInt(jo, "nowJoinNum") + " / ", JSONUtil.getInt(jo, "nowJoinNum") + " / " + "人数不限", R.color.text_grey));
            }
//            ViewUtil.bindNetImage(officialHolder.picI, JSONUtil.getString(organizerJo, "avatar"), "default");
            try {
                JSONArray coversJSa = jo.getJSONArray("covers");
                String picUrl = coversJSa.getString(0);
                ViewUtil.bindNetImage(officialHolder.picI, picUrl, "default");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject jo = (JSONObject) getItem(i);
                    Intent it = new Intent(mContext, ActiveDetailsActivity2.class);
                    it.putExtra("activityId", JSONUtil.getString(jo, "officialActivityId"));
                    mContext.startActivity(it);
                }
            });
        }


        return view;
    }

    class ViewHolder {
        TextView titleT, dynamic_carname, pay_type, travelmode, activity_place, activity_distance, ageT, inviteT, invitationT, dynamic_typeT;
        ImageView dynamic_carlogo, activity_beijing, certification_achievement, sexI;
        AnimButtonView dyanmic_one, dyanmic_two, yingyao, hulue, invitationI;
        LinearLayout yingyao_layout, yingyaohou, invitation, titlelayoutL;

        RelativeLayout layoutV;
        private RelativeLayout sexbgR;
    }


    class OfficialHolder {
        //        TextView maleLimitT, maleNumT, femaleLimitT, femaleNumT;
        TextView titleT, locationT, priceT, priceDescT, cityT, participate_womanT, participate_manT, unparticipateT, pricerightT, invitationT;
        //        TextView maleLimitT, maleNumT, femaleLimitT, femaleNumT;
        ImageView picI, headI;
        LinearLayout invitation;
        AnimButtonView invitationI;
        LinearLayout limitedlayoutL, unlimitedlayoutL;
        RelativeLayout layoutV;

    }
}
