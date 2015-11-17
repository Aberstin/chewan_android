package com.gongpingjia.carplay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gongpingjia.carplay.CarPlayValueFix;
import com.gongpingjia.carplay.R;
import com.gongpingjia.carplay.util.CarPlayUtil;
import com.gongpingjia.carplay.view.RoundImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.util.ViewUtil;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2015/10/17.
 */
public class BeSubscribedAdaptertwo extends BaseAdapter {

    private Context mContext;

    private SubscribeListener mSubscribeListener;

    List<JSONObject> mDatum;

    int type = 0;

    public void setSubscribeListener(SubscribeListener listener) {
        mSubscribeListener = listener;
    }

    public BeSubscribedAdaptertwo(Context context) {
        mContext = context;
    }

    public BeSubscribedAdaptertwo(Context context, int type) {
        mContext = context;
        this.type = type;
    }


    @Override
    public int getCount() {

        return mDatum == null ? 0 : mDatum.size();
    }

    public void setData(List<JSONObject> mDatum) {
        this.mDatum = mDatum;
        notifyDataSetChanged();

    }

    @Override
    public JSONObject getItem(int position) {
        return mDatum.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final JSONObject obj = getItem(position);
        final JSONObject carjo = JSONUtil.getJSONObject(obj,"car");
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_be_subscribedtwo, parent, false);
            holder = new ViewHolder();
            holder.visitors_time = (TextView) convertView.findViewById(R.id.visitors_time);
            holder.textDistance = (TextView) convertView.findViewById(R.id.tv_distance);
//            holder.heartView = (HeartView) convertView.findViewById(R.id.iv_heart);
            holder.roundImageView = (RoundImageView) convertView.findViewById(R.id.iv_avatar);
            holder.textNickname = (TextView) convertView.findViewById(R.id.tv_nickname);
            holder.textAge = (TextView) convertView.findViewById(R.id.tv_age);
            holder.sexbgR = (RelativeLayout) convertView.findViewById(R.id.layout_sex_and_age);
            holder.sexI = (ImageView) convertView.findViewById(R.id.iv_sex);
            holder.dynamic_carlogoI = (ImageView) convertView.findViewById(R.id.dynamic_carlogo);
            holder.headstatusI = (ImageView) convertView.findViewById(R.id.headstatus);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.roundImageView.setTag(JSONUtil.getString(obj, "userId"));
        holder.textNickname.setText(JSONUtil.getString(obj, "nickname"));
        int distances = (int) Math.floor(JSONUtil.getDouble(obj, "distance"));
        holder.textDistance.setText(CarPlayUtil.numberWithDelimiter(distances));
        holder.textAge.setText(JSONUtil.getString(obj, "age"));
        String sex = JSONUtil.getString(obj, "gender");
        if (("男").equals(sex)) {
            holder.sexbgR.setBackgroundResource(R.drawable.radio_sex_man_normal);
            holder.sexI.setBackgroundResource(R.drawable.icon_man3x);
        } else {
            holder.sexbgR.setBackgroundResource(R.drawable.radion_sex_woman_normal);
            holder.sexI.setBackgroundResource(R.drawable.icon_woman3x);
        }

        String licenseAuthStatus  = JSONUtil.getString(obj,"licenseAuthStatus");
        //车主认证
        if ("认证通过".equals(licenseAuthStatus)) {
            holder.dynamic_carlogoI.setVisibility(View.VISIBLE);
            ViewUtil.bindNetImage(holder.dynamic_carlogoI, JSONUtil.getString(obj, "carjo"), "default");
        } else {
            holder.dynamic_carlogoI.setVisibility(View.GONE);
        }
        //头像认证
        holder.headstatusI.setVisibility("认证通过".equals(JSONUtil.getString(obj,"photoAuthStatus")) ? View.VISIBLE : View.GONE);
//        holder.heartView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mSubscribeListener != null) {
//                    try {
//                        mSubscribeListener.onSubscribe(obj.getString("userId"));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//
//                    }
//                }
//            }
//        });
//        boolean subscribeFlag = JSONUtil.getBoolean(obj, "subscribeFlag");
//        holder.heartView.setVisibility(type == 0 ? View.VISIBLE : View.GONE);
        ImageLoader.getInstance().displayImage(JSONUtil.getString(obj, "avatar"), holder.roundImageView);
        long time = JSONUtil.getLong(obj, "subscribeTime");
        holder.visitors_time.setText(CarPlayValueFix.converTime(time));
        return convertView;
    }

    class ViewHolder {
        RoundImageView roundImageView;
        TextView textNickname;
        TextView textDistance;
//        HeartView heartView;
        TextView textAge;
        TextView visitors_time;
        private RelativeLayout sexbgR;
        ImageView sexI,dynamic_carlogoI,headstatusI;
    }

    private void attention() {
//        DhNet dhNet = new DhNet(API2.getFollowPerson(User.getInstance().getUserId(), User.getInstance().getToken()));
//        dhNet.addParam("targetUserId", targetId);
//        dhNet.doPostInDialog(new NetTask(self) {
//            @Override
//            public void doInUI(Response response, Integer transfer) {
//                if (response.isSuccess()) {
//                    //取消关注成功
//                    refresh();
//                }
//            }
//        });
    }


}
