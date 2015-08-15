package com.gongpingjia.carplay.adapter;

import java.util.Iterator;

import net.duohuo.dhroid.adapter.NetJSONAdapter;
import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.ioc.IocContainer;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import net.duohuo.dhroid.util.DhUtil;
import net.duohuo.dhroid.util.ViewUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gongpingjia.carplay.CarPlayValueFix;
import com.gongpingjia.carplay.R;
import com.gongpingjia.carplay.activity.active.ActiveMembersActivity;
import com.gongpingjia.carplay.activity.active.MyActiveMembersManageActivity;
import com.gongpingjia.carplay.adapter.ActiveAdapter.ViewHolder;
import com.gongpingjia.carplay.api.API;
import com.gongpingjia.carplay.bean.JoinEB;
import com.gongpingjia.carplay.bean.User;
import com.gongpingjia.carplay.manage.UserInfoManage;
import com.gongpingjia.carplay.manage.UserInfoManage.LoginCallBack;
import com.gongpingjia.carplay.util.CarPlayUtil;
import com.gongpingjia.carplay.util.PicLayoutUtil;
import com.gongpingjia.carplay.view.RoundImageView;
import com.gongpingjia.carplay.view.dialog.CarSeatSelectDialog;
import com.gongpingjia.carplay.view.dialog.CarSeatSelectDialog.OnSelectResultListener;

import de.greenrobot.event.EventBus;

public class HotAdapter extends NetJSONAdapter {
	LayoutInflater mLayoutInflater;

	int mResource;

	int piclayoutWidth;

	int headlayoutWidth;

	ViewHolder holder = null;

	MiddleViewHolder middleHolder;

	public HotAdapter(String api, Context context, int mResource) {
		super(api, context, mResource);
		mLayoutInflater = LayoutInflater.from(context);
		this.mResource = mResource;
		Display display = ((Activity) context).getWindowManager()
				.getDefaultDisplay();
		int width = display.getWidth();
		piclayoutWidth = width - DhUtil.dip2px(context, 59 + 12 * 2 + 10);
		headlayoutWidth = piclayoutWidth
				- DhUtil.dip2px(context, 77 + 10 + 8 * 2);

		EventBus.getDefault().register(this);
	}

	@Override
	public int getItemViewType(int position) {
		int count;
		if (position == 1) {
			count = 0;
		} else {
			JSONObject jo = mVaules.get(position);
			count = JSONUtil.getJSONArray(jo, "cover").length();
		}
		return count;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		// TODO Auto-generated method stub
		if (convertView == null) {
			if (type == 0) {
				convertView = mLayoutInflater.inflate(
						R.layout.item_hotlistview, null);
				middleHolder = new MiddleViewHolder();
				middleHolder.pager = (ViewPager) convertView
						.findViewById(R.id.page);
				convertView.setTag(middleHolder);
			} else {
				convertView = mLayoutInflater.inflate(mResource, null);
				holder = new ViewHolder();
				holder.addressT = (TextView) convertView
						.findViewById(R.id.address);
				holder.statusT = (TextView) convertView
						.findViewById(R.id.status);
				holder.seat_countT = (TextView) convertView
						.findViewById(R.id.seat_count);
				holder.nameT = (TextView) convertView.findViewById(R.id.name);
				holder.tv_publish_timeT = (TextView) convertView
						.findViewById(R.id.tv_publish_time);
				holder.contentT = (TextView) convertView
						.findViewById(R.id.content);
				holder.timeT = (TextView) convertView.findViewById(R.id.time);
				holder.joinT = (TextView) convertView.findViewById(R.id.join);
				holder.headI = (RoundImageView) convertView
						.findViewById(R.id.head);
				holder.car_logoI = (ImageView) convertView
						.findViewById(R.id.car_logo);
				holder.piclayoutV = (LinearLayout) convertView
						.findViewById(R.id.piclayout);
				holder.headlayoutV = (LinearLayout) convertView
						.findViewById(R.id.headlayout);
				holder.layout_sexV = convertView.findViewById(R.id.layout_sex);
				holder.ageT = (TextView) convertView.findViewById(R.id.age);
				holder.drive_ageT = (TextView) convertView
						.findViewById(R.id.drive_age);
				holder.seat_count_allT = (TextView) convertView
						.findViewById(R.id.seat_count_all);
				holder.member_layoutV = convertView
						.findViewById(R.id.member_layout);
				convertView.setTag(holder);
				PicLayoutUtil picUtil = new PicLayoutUtil(mContext, type, 5,
						holder.piclayoutV, piclayoutWidth);
				picUtil.addMoreChild();

				PicLayoutUtil headUtil = new PicLayoutUtil(mContext, 5, 5,
						holder.headlayoutV, headlayoutWidth);
				headUtil.AddAllHead();
			}
		} else {
			if (type == 0) {
				middleHolder = (MiddleViewHolder) convertView.getTag();
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
		}

		if (type == 0) {
			bindViewPageAdapter();
		} else {
			final JSONObject jo = mVaules.get(position);
			final JSONObject creater = JSONUtil.getJSONObject(jo, "organizer");

			int isOrganizer = JSONUtil.getInt(jo, "isOrganizer");
			int isMember = JSONUtil.getInt(jo, "isMember");
			final long startTime = JSONUtil.getLong(jo, "start");
			if (isOrganizer == 1) {
				holder.joinT.setText("管理");
				holder.joinT
						.setBackgroundResource(R.drawable.button_yanzheng_bg);
			} else {
				if (isMember == 1) {
					holder.joinT.setText("已加入");
					holder.joinT
							.setBackgroundResource(R.drawable.button_yanzheng_bg);
				} else if (isMember == 0) {
					holder.joinT.setText("我要去玩");
					holder.joinT
							.setBackgroundResource(R.drawable.button_yanzheng_bg);
				} else {
					holder.joinT.setText("申请中");
					holder.joinT
							.setBackgroundResource(R.drawable.btn_grey_dark_bg);
				}
			}
			holder.joinT.setVisibility(View.VISIBLE);

			final String activityId = JSONUtil.getString(jo, "activityId");
			holder.joinT.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					User user = User.getInstance();
					if (user.isLogin) {
						Intent it;
						if (holder.joinT.getText().equals("管理")) {
							JSONObject shareJo = getShareContent(jo);
							it = new Intent(mContext,
									MyActiveMembersManageActivity.class);
							it.putExtra("activityId", activityId);
							it.putExtra("isJoin", true);
							it.putExtra("shareContent", shareJo.toString());
							mContext.startActivity(it);

						} else if (holder.joinT.getText().toString()
								.equals("已加入")) {
							it = new Intent(mContext,
									ActiveMembersActivity.class);
							it.putExtra("activityId", activityId);
							it.putExtra("isJoin", true);
							mContext.startActivity(it);

						} else if (holder.joinT.getText().equals("我要去玩")) {
							isAuthen(activityId, jo);
						} else {

						}
					} else {
						UserInfoManage.getInstance().checkLogin(
								(Activity) mContext, new LoginCallBack() {

									@Override
									public void onisLogin() {
										refreshDialog();
									}

									@Override
									public void onLoginFail() {
										// TODO Auto-generated method stub

									}
								});
					}
				}
			});
			holder.headlayoutV.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					User user = User.getInstance();
					Intent it;
					if (user.isLogin()) {
						int isMember = JSONUtil.getInt(jo, "isMember");
						if (holder.joinT.getText().equals("管理")) {
							JSONObject shareJo = getShareContent(jo);
							it = new Intent(mContext,
									MyActiveMembersManageActivity.class);
							it.putExtra("activityId", activityId);
							it.putExtra("isJoin", isMember == 1 ? true : false);
							it.putExtra("shareContent", shareJo.toString());
							mContext.startActivity(it);
						} else {
							it = new Intent(mContext,
									ActiveMembersActivity.class);
							it.putExtra("activityId", activityId);
							it.putExtra("startTime", startTime);
							it.putExtra("isJoin", isMember == 1 ? true : false);
							mContext.startActivity(it);
						}
					} else {
						UserInfoManage.getInstance().checkLogin(
								(Activity) mContext, new LoginCallBack() {

									@Override
									public void onisLogin() {
										refreshDialog();
									}

									@Override
									public void onLoginFail() {
										// TODO Auto-generated method stub

									}
								});
					}
				}
			});
			ViewUtil.bindView(holder.nameT,
					JSONUtil.getString(creater, "nickname"));

			if (JSONUtil.getString(creater, "gender").equals("男")) {
				holder.layout_sexV.setBackgroundResource(R.drawable.man);
			} else {
				holder.layout_sexV.setBackgroundResource(R.drawable.woman);
			}

			ViewUtil.bindView(holder.ageT, JSONUtil.getString(creater, "age"));
			ViewUtil.bindView(holder.tv_publish_timeT,
					JSONUtil.getString(jo, "publishTime"), "neartime");

			CarPlayUtil.bindDriveAge(creater, holder.car_logoI,
					holder.drive_ageT);

			ViewUtil.bindView(holder.contentT,
					JSONUtil.getString(jo, "introduction"));
			JSONArray picJsa = JSONUtil.getJSONArray(jo, "cover");
			// holder.piclayoutV.removeAllViews();
			PicLayoutUtil util = new PicLayoutUtil(mContext);
			util.BindImageView(holder.piclayoutV, picJsa);
			// util.addMoreChild();
			// holder.headlayoutV.removeAllViews();
			JSONArray headJsa = JSONUtil.getJSONArray(jo, "members");
			PicLayoutUtil headUtil = new PicLayoutUtil(mContext);
			headUtil.BindHeadImage(holder.headlayoutV, headJsa);

			if (JSONUtil.getLong(jo, "start") == 0) {
				ViewUtil.bindView(holder.timeT, "不确定");
			} else {
				ViewUtil.bindView(holder.timeT, JSONUtil.getLong(jo, "start"),
						"time");
			}
			ViewUtil.bindView(holder.addressT,
					JSONUtil.getString(jo, "location"));
			ViewUtil.bindNetImage(holder.headI,
					JSONUtil.getString(creater, "photo"), "head");
			holder.headI.setTag(JSONUtil.getString(creater, "userId"));

			ViewUtil.bindView(holder.statusT, JSONUtil.getString(jo, "pay"));
			String pay = JSONUtil.getString(jo, "pay");
			holder.statusT.setVisibility(View.VISIBLE);
			if (pay.equals("我请客")) {
				holder.statusT.setBackgroundResource(R.drawable.button_radian);
			} else if (pay.equals("请我吧")) {
				holder.statusT.setBackgroundResource(R.drawable.grey_bg);
			} else {
				holder.statusT.setBackgroundResource(R.drawable.blue_light_bg);
			}

			if (JSONUtil.getString(jo, "holdingSeat").equals(
					JSONUtil.getString(jo, "totalSeat"))) {
				ViewUtil.bindView(holder.seat_countT, "已满");
				holder.seat_count_allT.setVisibility(View.GONE);
			} else {
				holder.seat_count_allT.setVisibility(View.VISIBLE);
				ViewUtil.bindView(holder.seat_countT,
						JSONUtil.getString(jo, "holdingSeat"));
				ViewUtil.bindView(holder.seat_count_allT,
						"/" + JSONUtil.getString(jo, "totalSeat") + "座");
			}
		}

		return convertView;
	}

	class ViewHolder {
		RoundImageView headI;

		ImageView car_logoI;

		TextView statusT, seat_countT, nameT, tv_publish_timeT;

		TextView contentT, timeT, addressT, joinT;

		LinearLayout piclayoutV, headlayoutV;

		View layout_sexV;

		TextView ageT, drive_ageT, seat_count_allT;

		View member_layoutV;

	}

	class MiddleViewHolder {
		ViewPager pager;
	}

	/**
	 * 加入活动
	 */
	private void joinActive(String activityId, int seatCount,
			final JSONObject jo) {
		User user = User.getInstance();
		DhNet net = new DhNet(API.CWBaseurl + "/activity/" + activityId
				+ "/join?userId=" + user.getUserId() + "&token="
				+ user.getToken());
		net.addParam("seat", seatCount);
		net.doPost(new NetTask(mContext) {

			@Override
			public void doInUI(Response response, Integer transfer) {
				if (response.isSuccess()) {
					IocContainer.getShare().get(IDialog.class)
							.showToastShort(mContext, "已提交加入活动申请,等待管理员审核!");
					try {
						jo.put("isMember", 2);
						notifyDataSetChanged();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

	private void isAuthen(final String activityId, final JSONObject jo) {
		User user = User.getInstance();
		DhNet mDhNet = new DhNet(API.availableSeat + user.getUserId()
				+ "/seats?token=" + user.getToken());
		mDhNet.doGet(new NetTask(mContext) {

			@Override
			public void doInUI(Response response, Integer transfer) {
				// TODO Auto-generated method stub
				if (response.isSuccess()) {
					JSONObject json = response.jSONFrom("data");
					try {
						User user = User.getInstance();
						user.setIsAuthenticated(json.getInt("isAuthenticated"));

						if (user.getIsAuthenticated() == 1) {
							CarSeatSelectDialog dialog = new CarSeatSelectDialog(
									mContext, activityId);
							dialog.setOnSelectResultListener(new OnSelectResultListener() {

								@Override
								public void click(int seatCount) {
									joinActive(activityId, seatCount, jo);
								}
							});

							dialog.show();
						} else {
							joinActive(activityId, 0, jo);

						}
						// 认证车主
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	/** 接受加入或者退出活动事件 */
	public void onEventMainThread(JoinEB join) {
		if (mVaules != null && mVaules.size() != 0) {
			for (Iterator iterator = mVaules.iterator(); iterator.hasNext();) {
				JSONObject jo = (JSONObject) iterator.next();
				if (JSONUtil.getString(jo, "activityId").equals(
						join.getActivityId())) {
					try {
						jo.put("isMember", join.getIsMember());
						if (join.getIsMember() == 0) {
							JSONArray newjsa = new JSONArray();
							JSONArray headJsa = JSONUtil.getJSONArray(jo,
									"members");
							for (int i = 0; i < headJsa.length(); i++) {
								JSONObject headJo = (JSONObject) headJsa.get(i);
								if (!JSONUtil.getString(headJo, "userId")
										.equals(User.getInstance().getUserId())) {
									newjsa.put(headJo);
								}
							}
							jo.put("members", newjsa);
						}
						notifyDataSetChanged();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}
	}

	public JSONObject getShareContent(JSONObject jo) {
		JSONArray picJsa = JSONUtil.getJSONArray(jo, "cover");
		JSONObject picjo;
		JSONObject shareJo = new JSONObject();
		try {
			picjo = picJsa.getJSONObject(0);
			String imgUrl = JSONUtil.getString(picjo, "thumbnail_pic");
			JSONObject userJo = JSONUtil.getJSONObject(jo, "organizer");
			String shareTitle = JSONUtil.getString(userJo, "nickname")
					+ "邀请您参加" + JSONUtil.getString(jo, "introduction") + "活动";
			long time = JSONUtil.getLong(jo, "start");
			String startTime = CarPlayValueFix.getStandardTime(time, "MM月dd日 ");
			String shareContent = "开始时间: " + startTime + "\n目的地: "
					+ JSONUtil.getString(jo, "location") + "\n费用: "
					+ JSONUtil.getString(jo, "pay");
			String shareUrl = API.share + "share.html?code="
					+ JSONUtil.getString(jo, "activityId");

			shareJo.put("imgUrl", imgUrl);
			shareJo.put("shareTitle", shareTitle);
			shareJo.put("shareContent", shareContent);
			shareJo.put("shareUrl", shareUrl);
			shareJo.put("activityId", JSONUtil.getString(jo, "activityId"));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return shareJo;
	}

	private void bindViewPageAdapter() {
		View leftV = mLayoutInflater.inflate(R.layout.item_hotview_viewpage,
				null);

		View middleV = mLayoutInflater.inflate(R.layout.item_hotview_viewpage,
				null);

		View rightV = mLayoutInflater.inflate(R.layout.item_hotview_viewpage,
				null);
		SimplePageAdapter middleAdapter = new SimplePageAdapter(leftV, middleV,
				rightV);
		middleHolder.pager.setAdapter(middleAdapter);
	}
}
