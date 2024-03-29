package com.gongpingjia.carplay.activity.main;

import java.io.File;
import java.util.List;
import java.util.Stack;
import java.util.Timer;

import net.duohuo.dhroid.activity.ActivityTack;
import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.ioc.IocContainer;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import net.duohuo.dhroid.net.upload.FileInfo;
import net.duohuo.dhroid.util.PhotoUtil;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMGroupChangeListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.gongpingjia.carplay.R;
import com.gongpingjia.carplay.activity.active.ActiveListFragment;
import com.gongpingjia.carplay.activity.active.CreateActiveActivity;
import com.gongpingjia.carplay.activity.msg.MsgFragment;
import com.gongpingjia.carplay.activity.my.LoginActivity;
import com.gongpingjia.carplay.activity.my.ManageAlbumActivity;
import com.gongpingjia.carplay.activity.my.MyFragment;
import com.gongpingjia.carplay.activity.my.SettingActivity;
import com.gongpingjia.carplay.api.API;
import com.gongpingjia.carplay.api.Constant;
import com.gongpingjia.carplay.bean.TabEB;
import com.gongpingjia.carplay.bean.User;
import com.gongpingjia.carplay.chat.DemoHXSDKHelper;
import com.gongpingjia.carplay.chat.bean.GroupEB;
import com.gongpingjia.carplay.chat.controller.HXSDKHelper;
import com.gongpingjia.carplay.manage.UserInfoManage;
import com.gongpingjia.carplay.manage.UserInfoManage.LoginCallBack;
import com.gongpingjia.carplay.receiver.NetReceiver;
import com.gongpingjia.carplay.service.MsgService;
import com.gongpingjia.carplay.util.CarPlayPerference;
import com.gongpingjia.carplay.view.pop.ActiveFilterPop;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.event.EventBus;

public class MainActivity extends BaseFragmentActivity implements
		EMEventListener {
	LinearLayout layout;

	LinearLayout tabV;

	FragmentManager fm;

	// Fragment 的栈
	Stack<Fragment> slist;

	ActiveFilterPop activeFilterPop;

	View titleBar;

	CarPlayPerference per;

	String tempPath;

	File mCacheDir;

	ImageView msgT;

	ImageView chatPointI;

	Timer mTimer;

	// 消息数据
	JSONObject dataJo;

	private static boolean isExit = false;

	Handler mHandler;

	List<EMGroup> groupList;

	private MyConnectionListener connectionListener = null;

	public boolean isConflict = false;

	private MyGroupChangeListener groupChangeListener = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		EventBus.getDefault().register(this);

		initView();
		isAuthen();
		updateApp();

		IntentFilter cmdIntentFilter = new IntentFilter(EMChatManager
				.getInstance().getCmdMessageBroadcastAction());
		registerReceiver(cmdMessageReceiver, cmdIntentFilter);
		updateUnreadLabel();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Intent it = new Intent(this, MsgService.class);
		startService(it);
		EMChatManager.getInstance().registerEventListener(
				this,
				new EMNotifierEvent.Event[] {
						EMNotifierEvent.Event.EventNewMessage,
						EMNotifierEvent.Event.EventOfflineMessage,
						EMNotifierEvent.Event.EventConversationListChanged });

		// asyncFetchGroupsFromServer();
		// ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager()
		// .asyncGetCurrentUserInfo();
	}

	public void initView() {
		mHandler = new Handler();
		slist = new Stack<Fragment>();
		fm = getSupportFragmentManager();
		tabV = (LinearLayout) findViewById(R.id.tab);
		activeFilterPop = ActiveFilterPop.getInstance(self);
		titleBar = findViewById(R.id.titlebar);
		initTab();
		setTab(0);

		per = IocContainer.getShare().get(CarPlayPerference.class);
		per.load();
		if (per.isShowMainGuilde == 0) {
			findViewById(R.id.guide).setVisibility(View.VISIBLE);
		}

		findViewById(R.id.know).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				per.load();
				per.isShowMainGuilde = 1;
				per.commit();
				findViewById(R.id.guide).setVisibility(View.GONE);
			}
		});

		msgT = (ImageView) findViewById(R.id.msg_point);
		chatPointI = (ImageView) findViewById(R.id.chat_point);
		connectionListener = new MyConnectionListener();
		EMChatManager.getInstance().addConnectionListener(connectionListener);
		groupChangeListener = new MyGroupChangeListener();
		// 注册群聊相关的listener
		EMGroupManager.getInstance()
				.addGroupChangeListener(groupChangeListener);
	}

	private void initTab() {
		for (int i = 0; i < tabV.getChildCount(); i++) {
			final int index = i;
			View childV = tabV.getChildAt(i);
			childV.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					setTab(index);
				}
			});
		}
	}

	private void setTab(int index) {

		if (index == 1) {
			if (!User.getInstance().isLogin()) {
				UserInfoManage.getInstance().checkLogin(self,
						new LoginCallBack() {

							@Override
							public void onisLogin() {
								setTab(1);
							}

							@Override
							public void onLoginFail() {
							}
						});
			}
		}

		if (index == 1 && !User.getInstance().isLogin()) {
			return;
		}

		for (int i = 0; i < tabV.getChildCount(); i++) {
			View childV = tabV.getChildAt(i);
			View imgLayout = childV.findViewById(R.id.img_layout);
			final ImageView img = (ImageView) imgLayout.findViewById(R.id.img);
			TextView text = (TextView) childV.findViewById(R.id.text);
			if (index == i) {
				text.setTextColor(getResources().getColor(
						R.color.text_blue_light));
				switch (index) {
				case 0:
					setTitle("同城");
					img.setImageResource(R.drawable.city_f);
					switchContent(ActiveListFragment.getInstance());
					setRightAction("创建活动", -1, new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							UserInfoManage.getInstance().checkLogin(self,
									new LoginCallBack() {

										@Override
										public void onisLogin() {
											Intent it = new Intent(
													MainActivity.this,
													CreateActiveActivity.class);
											startActivity(it);
										}

										@Override
										public void onLoginFail() {
											// TODO Auto-generated method stub

										}
									});
						}
					});
					setLeftAction(R.drawable.filtrate, "筛选",
							new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									activeFilterPop.show(titleBar);
								}
							});
					break;

				case 1:
					setTitle("消息");
					setRightVISIBLEOrGone(View.GONE);
					switchContent(MsgFragment.getInstance(dataJo));

					img.setImageResource(R.drawable.msg_f);
					setLeftAction(-2, null, new OnClickListener() {

						@Override
						public void onClick(View arg0) {
						}
					});
					break;

				case 2:
					setTitle("我的");
					switchContent(MyFragment.getInstance());
					img.setImageResource(R.drawable.my_f);
					setLeftAction(R.drawable.icon_setting, null,
							new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									Intent it = new Intent(MainActivity.this,
											SettingActivity.class);
									startActivity(it);
								}
							});
					setRightAction(null, R.drawable.icon_camera,
							new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									UserInfoManage.getInstance().checkLogin(
											self, new LoginCallBack() {

												@Override
												public void onisLogin() {
													// TODO Auto-generated
													// method stub
													mCacheDir = new File(
															getExternalCacheDir(),
															"CarPlay");
													mCacheDir.mkdirs();
													tempPath = new File(

															mCacheDir,
															System.currentTimeMillis()
																	+ ".jpg")
															.getAbsolutePath();
													PhotoUtil
															.getPhoto(
																	self,
																	Constant.TAKE_PHOTO,
																	Constant.PICK_PHOTO,
																	new File(
																			tempPath));

												}

												@Override
												public void onLoginFail() {

												}
											});
								}
							});
					break;

				default:
					break;

				}
			} else {
				text.setTextColor(getResources().getColor(R.color.text_grey));
				switch (i) {
				case 0:
					img.setImageResource(R.drawable.city_n);
					break;

				case 1:
					img.setImageResource(R.drawable.msg_n);
					break;

				case 2:
					img.setImageResource(R.drawable.my_n);
					break;

				default:
					break;
				}
			}
		}
	}

	public void switchContent(Fragment fragment) {
		try {
			FragmentTransaction t = fm.beginTransaction();
			List<Fragment> flist = fm.getFragments();
			if (flist == null) {
				t.add(R.id.main_content, fragment);
			} else {
				if (!flist.contains(fragment)) {
					t.add(R.id.main_content, fragment);
				}
				t.hide(slist.get(slist.size() - 1));
				t.show(fragment);
			}

			if (slist.contains(fragment)) {
				slist.remove(fragment);
			}

			slist.add(fragment);

			t.commitAllowingStateLoss();

		} catch (Exception e) {
		}
	}

	private void isAuthen() {
		User user = User.getInstance();
		if (user.isLogin()) {
			DhNet mDhNet = new DhNet(API.availableSeat + user.getUserId()
					+ "/seats?token=" + user.getToken());
			mDhNet.doGet(new NetTask(self) {

				@Override
				public void doInUI(Response response, Integer transfer) {
					// TODO Auto-generated method stub
					if (response.isSuccess()) {
						JSONObject json = response.jSONFrom("data");
						try {
							User user = User.getInstance();
							user.setIsAuthenticated(json
									.getInt("isAuthenticated"));
							// 认证车主
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
	}

	public void updateApp() {
		final String mCurrentVersion = getAppVersion();
		DhNet net = new DhNet(API.update);
		net.doGet(new NetTask(self) {

			@Override
			public void doInUI(Response response, Integer transfer) {
				if (response.isSuccess()) {
					JSONObject jo = response.jSONFromData();
					String version = JSONUtil.getString(jo, "version");
					if (0 < version.compareTo(mCurrentVersion)) {
						showUpdateDialog(jo);
					}
				}
			}
		});
	}

	private String getAppVersion() {
		String versionName = null;
		try {
			String pkName = this.getPackageName();
			versionName = this.getPackageManager().getPackageInfo(pkName, 0).versionName;

		} catch (Exception e) {
			return null;
		}
		return versionName;
	}

	private void showUpdateDialog(final JSONObject jo) {
		Builder builder = new Builder(this);
		builder.setTitle("发现新版本 " + JSONUtil.getString(jo, "version"));
		builder.setMessage(JSONUtil.getString(jo, "remarks"));
		builder.setPositiveButton("立即更新",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent it = new Intent(Intent.ACTION_VIEW);
						Uri uri = Uri.parse(JSONUtil.getString(jo, "url"));
						it.setData(uri);
						startActivity(it);
					}

				});
		builder.setNegativeButton("以后再说",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if (JSONUtil.getInt(jo, "forceUpgrade") == 1) {
							finish();
						}
					}
				});
		builder.create().show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			// case Constant.TAKE_PHOTO:
			// String newPath = new File(mCacheDir, System.currentTimeMillis()
			// + ".jpg").getAbsolutePath();
			// String path = PhotoUtil.onPhotoFromCamera(self,
			// Constant.ZOOM_PIC, tempPath, 3, 2, 1000, newPath);
			// tempPath = path;
			// break;
			// case Constant.PICK_PHOTO:
			// PhotoUtil.onPhotoFromPick(self, Constant.ZOOM_PIC, tempPath,
			// data, 3, 2, 1000);
			// break;
			// case Constant.ZOOM_PIC:
			// upLoadPic(tempPath);
			// break;

			case Constant.PICK_PHOTO:
				Bitmap btp = PhotoUtil.checkImage(self, data);
				PhotoUtil.saveLocalImage(btp, new File(tempPath));
				btp.recycle();
				upLoadPic(tempPath);
				break;
			case Constant.TAKE_PHOTO:
				Bitmap btp1 = PhotoUtil.getLocalImage(new File(tempPath));
				String newPath = new File(mCacheDir, System.currentTimeMillis()
						+ ".jpg").getAbsolutePath();
				int degree = PhotoUtil.getBitmapDegree(tempPath);
				PhotoUtil.saveLocalImage(btp1, new File(newPath), degree);
				btp1.recycle();
				upLoadPic(newPath);
				break;
			}
		}
	}

	private void upLoadPic(String path) {
		User user = User.getInstance();
		DhNet net = new DhNet(API.uploadAlbum + user.getUserId()
				+ "/album/upload?token=" + user.getToken());
		Log.e("url", net.getUrl());
		net.upload(new FileInfo("attach", new File(path)), new NetTask(self) {

			@Override
			public void doInUI(Response response, Integer transfer) {
				if (response.isSuccess()) {
					IocContainer.getShare().get(IDialog.class)
							.showToastShort(self, "图片上传成功!");
					Intent it = new Intent(MainActivity.this,
							ManageAlbumActivity.class);
					it.putExtra("tempPath", tempPath);
					startActivity(it);
				}
			}
		});
	}

	public void onEventMainThread(JSONObject jo) {
		dataJo = jo;
		JSONObject commentJo = JSONUtil.getJSONObject(jo, "comment");
		int commentCount = JSONUtil.getInt(commentJo, "count");

		JSONObject applicationJo = JSONUtil.getJSONObject(jo, "application");
		int applicationCount = JSONUtil.getInt(applicationJo, "count");

		if (commentCount != 0 || applicationCount != 0) {
			// msgT.setText(commentCount + applicationCount + "");
			msgT.setVisibility(View.VISIBLE);
		} else {
			msgT.setVisibility(View.GONE);
		}
	}

	public void onEventMainThread(TabEB tab) {
		setTab(0);
	}

	public static void asyncFetchGroupsFromServer() {
		HXSDKHelper.getInstance().asyncFetchGroupsFromServer(new EMCallBack() {

			@Override
			public void onSuccess() {
				HXSDKHelper.getInstance().noitifyGroupSyncListeners(true);

				if (HXSDKHelper.getInstance().isContactsSyncedWithServer()) {
					HXSDKHelper.getInstance().notifyForRecevingEvents();
				}

			}

			@Override
			public void onError(int code, String message) {
				HXSDKHelper.getInstance().noitifyGroupSyncListeners(false);
			}

			@Override
			public void onProgress(int progress, String status) {

			}

		});
	}

	/**
	 * 连接监听listener
	 * 
	 */
	public class MyConnectionListener implements EMConnectionListener {

		@Override
		public void onConnected() {

		}

		@Override
		public void onDisconnected(final int error) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {

					if (User.getInstance().isLogin()) {
						if (error == EMError.USER_REMOVED) {
							// 显示帐号已经被移除
							showToast("账号被移除!");
							// showAccountRemovedDialog();
						} else if (error == EMError.CONNECTION_CONFLICT) {
							// 显示帐号在其他设备登陆dialog
							showToast("账号在另一地点登录!");
							isConflict = true;
							Intent it = new Intent(self, LoginActivity.class);
							it.putExtra("action", "logout");
							startActivity(it);
							finish();
							User.getInstance().setDisconnect(false);
							// showConflictDialog();
						} else {
							showToast("网络异常,请重新连接!");
							// chatHistoryFragment.errorItem
							// .setVisibility(View.VISIBLE);
							// if (NetUtils.hasNetwork(MainActivity.this))
							// chatHistoryFragment.errorText.setText(st1);
							// else
							// chatHistoryFragment.errorText.setText(st2);

						}
						User.getInstance().setLogin(false);
						User.getInstance().setDisconnect(true);
						DemoHXSDKHelper.getInstance().logout(true, null);
					}
				}

			});
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		if (connectionListener != null) {
			EMChatManager.getInstance().removeConnectionListener(
					connectionListener);
		}

		if (groupChangeListener != null) {
			EMGroupManager.getInstance().removeGroupChangeListener(
					groupChangeListener);
		}

		unregisterReceiver(cmdMessageReceiver);
	}

	@Override
	protected void onStop() {
		EMChatManager.getInstance().unregisterEventListener(this);
		DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper
				.getInstance();

		super.onStop();

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	static public class ExitRunnable implements Runnable {
		@Override
		public void run() {
			isExit = false;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!isExit) {
				isExit = true;
				IocContainer.getShare().get(IDialog.class)
						.showToastShort(self, "再按一次退出程序");
				mHandler.postDelayed(new ExitRunnable(), 2000);
			} else {
				Intent it = new Intent(self, MsgService.class);
				stopService(it);
				ActivityTack.getInstanse().exit(self);
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage: // 普通消息
		{

			EMMessage message = (EMMessage) event.getData();
			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
				}
			});
			// 提示新消息
			// HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
			EventBus.getDefault().post(message);
			break;
		}

		case EventOfflineMessage: {
			EMMessage message = (EMMessage) event.getData();
			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
				}
			});
			// 提示新消息
			// HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
			EventBus.getDefault().post(message);
			break;
		}

		case EventConversationListChanged: {
			break;
		}

		default:
			break;
		}
	}

	public void updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();
		chatPointI.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
	}

	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		int chatroomUnreadMsgCount = 0;
		int chatGroupCount = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		for (EMConversation conversation : EMChatManager.getInstance()
				.getAllConversations().values()) {
			if (conversation.getType() == EMConversationType.GroupChat)
				chatroomUnreadMsgCount = chatroomUnreadMsgCount
						+ conversation.getUnreadMsgCount();

			chatGroupCount = chatGroupCount + conversation.getUnreadMsgCount();
		}
		return chatGroupCount;
	}

	public class MyGroupChangeListener implements EMGroupChangeListener {

		@Override
		public void onInvitationReceived(String groupId, String groupName,
				String inviter, String reason) {

		}

		@Override
		public void onInvitationAccpted(String groupId, String inviter,
				String reason) {

		}

		@Override
		public void onInvitationDeclined(String groupId, String invitee,
				String reason) {

		}

		@Override
		public void onUserRemoved(String groupId, String groupName) {

			// 提示用户被T了，demo省略此步骤
			// 刷新ui
			runOnUiThread(new Runnable() {
				public void run() {
					try {
						updateUnreadLabel();
						GroupEB eb = new GroupEB();
						EventBus.getDefault().post(eb);

					} catch (Exception e) {
					}
				}
			});
		}

		@Override
		public void onGroupDestroy(String groupId, String groupName) {

		}

		@Override
		public void onApplicationReceived(String groupId, String groupName,
				String applyer, String reason) {

		}

		@Override
		public void onApplicationAccept(String groupId, String groupName,
				String accepter) {

		}

		@Override
		public void onApplicationDeclined(String groupId, String groupName,
				String decliner, String reason) {
			// 加群申请被拒绝，demo未实现
		}
	}

	private BroadcastReceiver cmdMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 获取cmd message对象
			String msgId = intent.getStringExtra("msgid");
			EMMessage message = intent.getParcelableExtra("message");
			// 获取消息body
			CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
			String aciton = cmdMsgBody.action;// 获取自定义action
			// 获取扩展属性
			try {
				String attr = message.getStringAttribute("photoUrl");
				ImageLoader.getInstance().getMemoryCache().remove(attr);
				ImageLoader.getInstance().getDiskCache().remove(attr);
				showToast("清除缓存");
				System.out.println("清除缓存");
			} catch (EaseMobException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
}
