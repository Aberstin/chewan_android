package com.gongpingjia.carplay.activity.my;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import net.duohuo.dhroid.net.upload.FileInfo;
import net.duohuo.dhroid.util.ImageUtil;
import net.duohuo.dhroid.util.PhotoUtil;
import net.duohuo.dhroid.util.ViewUtil;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gongpingjia.carplay.CarPlayValueFix;
import com.gongpingjia.carplay.R;
import com.gongpingjia.carplay.activity.CarPlayBaseActivity;
import com.gongpingjia.carplay.api.API;
import com.gongpingjia.carplay.api.Constant;
import com.gongpingjia.carplay.bean.User;
import com.gongpingjia.carplay.view.RoundImageView;
import com.gongpingjia.carplay.view.dialog.CityPickDialog;
import com.gongpingjia.carplay.view.dialog.CityPickDialog.OnPickResultListener;

/**
 * 
 * @Description 编辑资料界面
 * @author wang
 * @date 2015-7-16 上午9:21:59
 */
public class EditPersonalInfoActivity extends CarPlayBaseActivity implements
		OnClickListener {

	/** 头像 */
	private RoundImageView headI = null;

	/** 昵称 */
	private EditText nicknameT = null;
	private String nickname;
	/** 性别 */
	private RadioGroup sexR = null;
	private String gender;
	
	/** 选择城市 */
	private TextView cityT = null;
	private String mProvice, mCity, mDistrict;

	/** 车龄 */
	private EditText carageT = null;
	private String drivingExperience;

	/** 图片缓存根目录 */
	private File mCacheDir;

	private CityPickDialog cityDialog;

	private String mPhotoPath;

	private String photoUid;

	private User mUser = User.getInstance();
	
	private Map<String,Boolean> map=new HashMap<String, Boolean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_data);
	}

	@Override
	public void initView() {
		setTitle("编辑资料");
		View backV = findViewById(R.id.backLayout);
		if (backV != null) {
			backV.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					//如果没有改动 直接关闭本页
					if (isModify()) {
						modification();
					}else {
						finish();
					}
					
				}
			});
		}
		map.put("flag", false);
		mCacheDir = new File(getExternalCacheDir(), "CarPlay");
		mCacheDir.mkdirs();

		cityDialog = new CityPickDialog(self);
		cityDialog.setOnPickResultListener(new OnPickResultListener() {

			@Override
			public void onResult(String provice, String city, String district) {
				cityT.setText(provice + city + district);
				mProvice = provice;
				mCity = city;
				mDistrict = district;
				map.put("flag", true);
			}
		});

		headI = (RoundImageView) findViewById(R.id.head);
		sexR = (RadioGroup) findViewById(R.id.tab);
		cityT = (TextView) findViewById(R.id.city);
		nicknameT = (EditText) findViewById(R.id.nickname);
		carageT = (EditText) findViewById(R.id.carage);
		headI.setOnClickListener(this);
		cityT.setOnClickListener(this);
		getMyDetails();

	}
	
	/** 判断资料是否有改动 */
	private boolean isModify(){
		String sex = sexR.getCheckedRadioButtonId() == R.id.tab_left ? "男"
				: "女";
		String name=nicknameT.getText().toString();
		String carage=carageT.getText().toString();
		if (carage.contains("年")) {
			carage=carage.replace("年", "");
		}
		if (map.get("flag")||!name.equals(nickname)||!carage.equals(drivingExperience)||!sex.equals(gender)) {
			return true;
		}
		return false;
	}
	
	/** 获取个人资料 */
	private void getMyDetails() {
		DhNet net = new DhNet(API.CWBaseurl + "/user/" + mUser.getUserId()
				+ "/info?userId=" + mUser.getUserId() + "&token="
				+ mUser.getToken());
		net.doGetInDialog(new NetTask(this) {

			@Override
			public void doInUI(Response response, Integer transfer) {
				JSONObject jo = response.jSONFromData();

				nickname = JSONUtil.getString(jo, "nickname");
				drivingExperience = JSONUtil.getString(jo,
						"drivingExperience");
				String photo = JSONUtil.getString(jo, "photo");
				gender = JSONUtil.getString(jo, "gender");
				mProvice= JSONUtil.getString(jo, "province");
				mCity= JSONUtil.getString(jo, "city");
				mDistrict= JSONUtil.getString(jo, "district");
				 cityT.setText(mProvice + mCity + mDistrict);
				nicknameT.setText(nickname);
				RadioButton rb=(RadioButton) sexR.findViewById(gender.equals("男") ? R.id.tab_left : R.id.tab_right);
				rb.setChecked(true);
				carageT.setText(drivingExperience+"年");
				ViewUtil.bindNetImage(headI, photo,
						CarPlayValueFix.optionsDefault.toString());
			}
		});

		
		
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.head:
			mPhotoPath = new File(mCacheDir, System.currentTimeMillis()
					+ ".jpg").getAbsolutePath();
			PhotoUtil.getPhoto(self, Constant.TAKE_PHOTO, Constant.PICK_PHOTO,
					new File(mPhotoPath));
			break;
		case R.id.city:
			cityDialog.show();
			break;
		default:
			break;
		}
	}

	private void modification() {
		String nickname = nicknameT.getText().toString();
		String carage = carageT.getText().toString().trim();
		int drivingExperience;
		if (TextUtils.isEmpty(nickname)) {
			showToast("用户名不能为空!");
			return;
		}
		if (carage.contains("年")) {
			carage=carage.replace("年", "");
		}
		try {
			drivingExperience = Integer.parseInt(carage);
		} catch (Exception e) {
			showToast("请输入0~20数字");
			return;
		}
		if (drivingExperience < 0 || drivingExperience > 20) {
			showToast("请输入0~20数字");
			return;
		}

		String gender = sexR.getCheckedRadioButtonId() == R.id.tab_left ? "男"
				: "女";
		DhNet net = new DhNet(API.availableSeat + mUser.getUserId()
				+ "/info?token=" + mUser.getToken());
		net.addParam("nickname", nickname);
		net.addParam("gender", gender);
		net.addParam("drivingExperience", drivingExperience);
		net.addParam("province", mProvice);
		net.addParam("city", mCity);
		net.addParam("district", mDistrict);
		net.doPostInDialog(new NetTask(self) {

			@Override
			public void doInUI(Response response, Integer transfer) {
				if (response.isSuccess()) {
					showToast("修改成功");
					finish();
				} else {
					showToast("修改失败");
				}
			}
		});
	}

	private void uploadHead(String path) {
		DhNet net = new DhNet(API.availableSeat + mUser.getUserId()
				+ "/avatar?token=" + mUser.getToken());
		net.upload(new FileInfo("attach", new File(path)), new NetTask(self) {

			@Override
			public void doInUI(Response response, Integer transfer) {
				if (response.isSuccess()) {
					JSONObject jo = response.jSONFromData();
					photoUid = JSONUtil.getString(jo, "photoId");
				} 
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case Constant.TAKE_PHOTO:
				String newPath = new File(mCacheDir, System.currentTimeMillis()
						+ ".jpg").getAbsolutePath();
				String path = PhotoUtil.onPhotoFromCamera(self,
						Constant.ZOOM_PIC, mPhotoPath, 1, 1, 1000, newPath);
				mPhotoPath = path;
				break;
			case Constant.PICK_PHOTO:
				PhotoUtil.onPhotoFromPick(self, Constant.ZOOM_PIC, mPhotoPath,
						data, 1, 1, 1000);
				break;
			case Constant.ZOOM_PIC:
				Bitmap bmp = PhotoUtil.getLocalImage(new File(mPhotoPath));
				headI.setImageBitmap(ImageUtil.toRoundCorner(bmp, 1000));
				uploadHead(mPhotoPath);
				break;

			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		 if (keyCode == KeyEvent.KEYCODE_BACK
                 && event.getRepeatCount() == 0) {
			 if (isModify()) {
					modification();
				}else {
					finish();
				}
			 return true;
         }
		return super.onKeyDown(keyCode, event);
	}
	

}
