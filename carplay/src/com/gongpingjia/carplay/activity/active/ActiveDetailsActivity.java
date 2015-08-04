package com.gongpingjia.carplay.activity.active;

import net.duohuo.dhroid.adapter.FieldMap;
import net.duohuo.dhroid.adapter.NetJSONAdapter;
import net.duohuo.dhroid.ioc.IocContainer;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import net.duohuo.dhroid.util.DhUtil;
import net.duohuo.dhroid.util.ViewUtil;
import net.duohuo.dhroid.view.INetRefreshAndMorelistView.OnRefreshListener;
import net.duohuo.dhroid.view.NetRefreshAndMoreListView;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gongpingjia.carplay.R;
import com.gongpingjia.carplay.activity.CarPlayBaseActivity;
import com.gongpingjia.carplay.api.API;
import com.gongpingjia.carplay.bean.User;
import com.gongpingjia.carplay.manage.UserInfoManage;
import com.gongpingjia.carplay.manage.UserInfoManage.LoginCallBack;
import com.gongpingjia.carplay.util.CarPlayPerference;
import com.gongpingjia.carplay.util.CarPlayUtil;
import com.gongpingjia.carplay.util.PicLayoutUtil;
import com.gongpingjia.carplay.view.RoundImageView;
import com.gongpingjia.carplay.view.dialog.CarSeatSelectDialog;
import com.gongpingjia.carplay.view.dialog.CarSeatSelectDialog.OnSelectResultListener;

/*
 *@author zhanglong
 *Email:1269521147@qq.com
 */
public class ActiveDetailsActivity extends CarPlayBaseActivity implements
		OnClickListener {

	private NetRefreshAndMoreListView mListView;

	private LayoutInflater mInflater;

	private NetJSONAdapter mJsonAdapter;

	String activityId;

	View headV;

	int piclayoutWidth;

	int headlayoutWidth;

	Button releaseB;

	EditText comment_contentE;

	User user;

	View headlayoutV;

	TextView joinT;

	long startTime;

	boolean isJoin = false;

	boolean islogin = false;

	TextView rightTitleT;

	CarPlayPerference per;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_active_details);
	}

	@Override
	public void initView() {

		per = IocContainer.getShare().get(CarPlayPerference.class);
		per.load();
		if (per.isShowDetailGuilde == 0) {
			findViewById(R.id.guide).setVisibility(View.VISIBLE);
		}

		findViewById(R.id.know).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				per.load();
				per.isShowDetailGuilde = 1;
				per.commit();
				findViewById(R.id.guide).setVisibility(View.GONE);
			}
		});
		user = User.getInstance();
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		piclayoutWidth = width - DhUtil.dip2px(self, 10 + 10);
		headlayoutWidth = piclayoutWidth - DhUtil.dip2px(self, 80 + 8 * 2 + 10);
		setTitle("活动详情");
		rightTitleT = (TextView) findViewById(R.id.right_text);
		// /activity/$activityId/subscribe?
		activityId = getIntent().getStringExtra("activityId");
		mListView = (NetRefreshAndMoreListView) findViewById(R.id.listview);
		mInflater = LayoutInflater.from(this);
		releaseB = (Button) findViewById(R.id.release);
		releaseB.setOnClickListener(this);
		comment_contentE = (EditText) findViewById(R.id.comment_content);
		headV = mInflater.inflate(R.layout.active_head_view, null);
		headlayoutV = headV.findViewById(R.id.headlayout);
		headlayoutV.setOnClickListener(this);
		mListView.addHeaderView(headV);
		mListView.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				getData();
			}
		});
		mJsonAdapter = new NetJSONAdapter(API.CWBaseurl + "/activity/"
				+ activityId + "/comment?userId=" + user.getUserId()
				+ "&token=" + user.getToken(), this, R.layout.listitem_comment);
		mJsonAdapter.addField(new FieldMap("comment", R.id.tv_comment_content) {

			@Override
			public Object fix(View itemV, Integer position, Object o, Object jo) {

				View layout_sexV = itemV.findViewById(R.id.layout_sex);
				JSONObject jo1 = (JSONObject) jo;
				if (JSONUtil.getString(jo1, "gender").equals("男")) {
					layout_sexV.setBackgroundResource(R.drawable.man);
				} else {
					layout_sexV.setBackgroundResource(R.drawable.woman);
				}

				RoundImageView headI = (RoundImageView) itemV
						.findViewById(R.id.imgView_avatar);
				ViewUtil.bindNetImage(headI, JSONUtil.getString(jo1, "photo"),
						"head");
				headI.setTag(JSONUtil.getString(jo1, "userId"));
				return o;
			}
		});
		mJsonAdapter.addField("nickname", R.id.tv_nickname);
		mJsonAdapter.addField("publishTime", R.id.tv_publish_time, "neartime");
		mJsonAdapter.addField("age", R.id.age);
		mJsonAdapter.fromWhat("data");
		mListView.setAdapter(mJsonAdapter);
		mJsonAdapter.showNextInDialog();

		getData();
	}

	public void getData() {
		DhNet net = new DhNet(API.CWBaseurl + "/activity/" + activityId
				+ "/info");
		net.doGetInDialog(new NetTask(self) {

			@Override
			public void doInUI(Response response, Integer transfer) {
				if (response.isSuccess()) {
					JSONObject headJo = response.jSONFromData();
					bindHeadView(headJo);
				}
			}
		});
	}

	private void bindHeadView(final JSONObject headJo) {
		JSONObject createrJo = JSONUtil.getJSONObject(headJo, "organizer");
		joinT = (TextView) headV.findViewById(R.id.join);
		joinT.setOnClickListener(this);
		activeRelative(headJo);
		ViewUtil.bindView(headV.findViewById(R.id.name),
				JSONUtil.getString(createrJo, "nickname"));
		ViewUtil.bindView(headV.findViewById(R.id.content),
				JSONUtil.getString(headJo, "introduction"));
		ViewUtil.bindView(headV.findViewById(R.id.des),
				JSONUtil.getString(headJo, "introduction"));

		CarPlayUtil.bindDriveAge(createrJo,
				(ImageView) headV.findViewById(R.id.car_logo),
				(TextView) headV.findViewById(R.id.drive_age));
		RoundImageView headI = (RoundImageView) headV.findViewById(R.id.head);
		ViewUtil.bindNetImage(headI, JSONUtil.getString(createrJo, "photo"),
				"head");
		headI.setTag(JSONUtil.getString(createrJo, "userId"));
		ViewUtil.bindView(headV.findViewById(R.id.publish_time),
				JSONUtil.getLong(headJo, "publishTime"), "neartime");

		ViewUtil.bindView(headV.findViewById(R.id.address),
				JSONUtil.getString(headJo, "location"));

		if (JSONUtil.getLong(headJo, "start") == 0) {
			ViewUtil.bindView(headV.findViewById(R.id.start_time), "不确定");
		} else {
			ViewUtil.bindView(headV.findViewById(R.id.start_time),
					JSONUtil.getLong(headJo, "start"), "time");
		}

		startTime = JSONUtil.getLong(headJo, "start");

		if (JSONUtil.getLong(headJo, "end") == 0) {
			ViewUtil.bindView(headV.findViewById(R.id.end_time), "不确定");

		} else {
			ViewUtil.bindView(headV.findViewById(R.id.end_time),
					JSONUtil.getLong(headJo, "end"), "time");
		}

		ViewUtil.bindView(headV.findViewById(R.id.pay),
				JSONUtil.getString(headJo, "pay"));

		View layoutSex = headV.findViewById(R.id.layout_sex);
		if (JSONUtil.getString(createrJo, "gender").equals("男")) {
			layoutSex.setBackgroundResource(R.drawable.man);
		} else {
			layoutSex.setBackgroundResource(R.drawable.woman);
		}
		ViewUtil.bindView(headV.findViewById(R.id.age),
				JSONUtil.getString(createrJo, "age"));

		if (JSONUtil.getString(createrJo, "userId").equals(user.getUserId())) {
			rightTitleT.setText("编辑活动");

		} else {
			int isSubscribed = JSONUtil.getInt(headJo, "isSubscribed");
			rightTitleT.setText(isSubscribed == 0 ? "关注" : "取消关注");
		}

		rightTitleT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (user.isLogin()) {
					if (rightTitleT.getText().toString().equals("编辑活动")) {
						Intent it = new Intent(ActiveDetailsActivity.this,
								EditActiveActivity.class);
						it.putExtra("json", headJo.toString());
						startActivity(it);
					} else if (rightTitleT.getText().toString().equals("关注")) {
						attention();
					} else {
						cancleattention();
					}
				} else {
					UserInfoManage.getInstance().checkLogin(self,
							new LoginCallBack() {

								@Override
								public void onisLogin() {
									getData();
								}

								@Override
								public void onLoginFail() {

								}
							});
				}
			}
		});

		rightTitleT.setVisibility(View.VISIBLE);

		JSONArray picJsa = JSONUtil.getJSONArray(headJo, "cover");
		LinearLayout pivlayout = (LinearLayout) headV
				.findViewById(R.id.pic_layout);
		pivlayout.removeAllViews();
		// holder.piclayoutV.removeAllViews();
		PicLayoutUtil util = new PicLayoutUtil(self, picJsa, 5, pivlayout,
				piclayoutWidth);
		util.addMoreChild();
		// holder.headlayoutV.removeAllViews();
		JSONArray headJsa = JSONUtil.getJSONArray(headJo, "members");

		LinearLayout headlayout = (LinearLayout) headV
				.findViewById(R.id.headlayout);
		headlayout.removeAllViews();
		PicLayoutUtil headUtil = new PicLayoutUtil(self, headJsa, 5,
				headlayout, headlayoutWidth);
		headUtil.setHeadMaxCount(6);
		headUtil.AddChild();
	}

	// 活动与登陆者的关系
	private void activeRelative(JSONObject jo) {
		int isOrganizer = JSONUtil.getInt(jo, "isOrganizer");
		int isMember = JSONUtil.getInt(jo, "isMember");
		if (isOrganizer == 1) {
			joinT.setText("管理");
			isJoin = true;
		} else {
			if (isMember == 1) {
				joinT.setText("查看");
				isJoin = true;
			} else {
				joinT.setText("我也要玩");
				isJoin = false;
			}
		}
		joinT.setVisibility(View.VISIBLE);
	}

	public void comment() {
		String commentContent = comment_contentE.getText().toString();
		User user = User.getInstance();
		DhNet net = new DhNet(API.CWBaseurl + "/activity/" + activityId
				+ "/comment?userId=" + user.getUserId() + "&token="
				+ user.getToken());
		net.addParam("replyUserId", "");
		net.addParam("comment", commentContent);
		net.doPostInDialog("发布评论中...", new NetTask(self) {

			@Override
			public void doInUI(Response response, Integer transfer) {
				if (response.isSuccess()) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					comment_contentE.setText("");
					showToast("评论发布成功");
					mJsonAdapter.refresh();
				}
			}
		});
	}

	/** 关注活动 */
	private void attention() {
		DhNet net = new DhNet(API.CWBaseurl + "/activity/" + activityId
				+ "/subscribe?userId=" + user.getUserId() + "&token="
				+ user.getToken());
		net.doPostInDialog(new NetTask(self) {

			@Override
			public void doInUI(Response response, Integer transfer) {
				if (response.isSuccess()) {
					showToast("关注成功!");
					rightTitleT.setText("取消关注");
				}
			}
		});
	}

	/** 关注活动 */
	private void cancleattention() {
		DhNet net = new DhNet(API.CWBaseurl + "/activity/" + activityId
				+ "/unsubscribe?userId=" + user.getUserId() + "&token="
				+ user.getToken());
		net.doPostInDialog(new NetTask(self) {

			@Override
			public void doInUI(Response response, Integer transfer) {
				if (response.isSuccess()) {
					showToast("取消关注成功!");
					rightTitleT.setText("关注");
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		Intent it;
		switch (v.getId()) {

		case R.id.release:
			String commentContent = comment_contentE.getText().toString();
			if (TextUtils.isEmpty(commentContent)) {
				showToast("请输入评论内容!");
				return;
			}
			UserInfoManage.getInstance().checkLogin((Activity) self,
					new LoginCallBack() {

						@Override
						public void onisLogin() {
							comment();
						}

						@Override
						public void onLoginFail() {

						}
					});
			break;

		case R.id.headlayout:
			if (User.getInstance().isLogin()) {
				if (joinT.getText().equals("管理")) {
					it = new Intent(self, MyActiveMembersManageActivity.class);
					it.putExtra("activityId", activityId);
					it.putExtra("isJoin", isJoin);
					startActivity(it);
				} else {
					it = new Intent(self, ActiveMembersActivity.class);
					it.putExtra("activityId", activityId);
					it.putExtra("startTime", startTime);
					it.putExtra("isJoin", isJoin);
					startActivity(it);
				}
			} else {
				UserInfoManage.getInstance().checkLogin((Activity) self,
						new LoginCallBack() {

							@Override
							public void onisLogin() {
								getData();
							}

							@Override
							public void onLoginFail() {

							}
						});
			}

			break;

		case R.id.join:

			if (User.getInstance().isLogin()) {

				if (joinT.getText().equals("管理")) {
					it = new Intent(self, MyActiveMembersManageActivity.class);
					it.putExtra("activityId", activityId);
					it.putExtra("isJoin", isJoin);
					startActivity(it);
				} else if (joinT.getText().toString().equals("已加入")) {
					it = new Intent(self, ActiveMembersActivity.class);
					it.putExtra("startTime", startTime);
					it.putExtra("activityId", activityId);
					it.putExtra("isJoin", isJoin);
					startActivity(it);
				} else {
					CarSeatSelectDialog dialog = new CarSeatSelectDialog(self);
					dialog.setOnSelectResultListener(new OnSelectResultListener() {

						@Override
						public void click(int seatCount) {
							joinActive(seatCount);
						}
					});
					dialog.show();
				}
			} else {
				UserInfoManage.getInstance().checkLogin((Activity) self,
						new LoginCallBack() {

							@Override
							public void onisLogin() {
								getData();
							}

							@Override
							public void onLoginFail() {

							}
						});
			}
			break;

		default:
			break;
		}
	}

	/**
	 * 加入活动
	 */
	private void joinActive(int seatCount) {
		DhNet net = new DhNet(API.CWBaseurl + "/activity/" + activityId
				+ "/join?userId=" + user.getUserId() + "&token="
				+ user.getToken());
		net.addParam("seat", seatCount);
		net.doPost(new NetTask(self) {

			@Override
			public void doInUI(Response response, Integer transfer) {
				if (response.isSuccess()) {
					findViewById(R.id.bottom_bar).setVisibility(View.GONE);
					showToast("已提交加入活动申请,等待管理员审核!");
				}
			}
		});
	}
}