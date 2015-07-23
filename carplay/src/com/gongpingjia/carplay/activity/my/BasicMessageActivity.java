package com.gongpingjia.carplay.activity.my;

import java.io.File;

import org.json.JSONObject;

import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import net.duohuo.dhroid.net.upload.FileInfo;
import net.duohuo.dhroid.util.ImageUtil;
import net.duohuo.dhroid.util.PhotoUtil;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gongpingjia.carplay.R;
import com.gongpingjia.carplay.activity.CarPlayBaseActivity;
import com.gongpingjia.carplay.api.API;
import com.gongpingjia.carplay.api.Constant;
import com.gongpingjia.carplay.bean.User;
import com.gongpingjia.carplay.view.dialog.CityPickDialog;
import com.gongpingjia.carplay.view.dialog.CityPickDialog.OnPickResultListener;
import com.gongpingjia.carplay.view.dialog.DateDialog;
import com.gongpingjia.carplay.view.dialog.DateDialog.OnDateResultListener;
import com.gongpingjia.carplay.view.dialog.PhotoSelectDialog;
import com.gongpingjia.carplay.view.dialog.PhotoSelectDialog.OnStateChangeListener;

/**
 * 车玩基本信息
 * 
 * @author Administrator
 * 
 */
public class BasicMessageActivity extends CarPlayBaseActivity implements OnClickListener
{
    
    /** 头像 */
    private ImageView headI = null;
    
    /** 昵称 */
    private EditText nicknameT = null;
    
    /** 性别 */
    private RadioGroup sexR = null;
    
    /** 选择年龄 */
    private TextView ageT = null;
    
    /** 选择城市 */
    private TextView cityT = null;
    
    /** 下一步 */
    private Button nextBtn = null;
    
    private String mPhotoPath;
    
    PhotoSelectDialog photoDialog;
    
    DateDialog dateDialog;
    
    CityPickDialog cityDialog;
    
    int mYear, mMonth, mDay;
    
    String mProvice, mCity, mDistrict;
    
    String photoUid;
    
    public static final int AuthenticateOwners = 1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_message);
        
    }
    
    @Override
    public void initView()
    {
        setTitle("注册");
        photoDialog = new PhotoSelectDialog(self);
        photoDialog.setOnStateChangeListener(new OnStateChangeListener()
        {
            
            @Override
            public void close(String photoPath)
            {
                mPhotoPath = photoPath;
            }
        });
        
        dateDialog = new DateDialog();
        dateDialog.setOnDateResultListener(new OnDateResultListener()
        {
            
            @Override
            public void result(String date, long datetime, int year, int month, int day)
            {
                ageT.setText(date);
                mYear = year;
                mMonth = month;
                mDay = day;
            }
        });
        
        cityDialog = new CityPickDialog(self);
        cityDialog.setOnPickResultListener(new OnPickResultListener()
        {
            
            @Override
            public void onResult(String provice, String city, String district)
            {
                cityT.setText(provice + city + district);
                mProvice = provice;
                mCity = city;
                mDistrict = district;
            }
        });
        sexR = (RadioGroup)findViewById(R.id.tab);
        
        headI = (ImageView)findViewById(R.id.head);
        nicknameT = (EditText)findViewById(R.id.nickname);
        ageT = (TextView)findViewById(R.id.age);
        cityT = (TextView)findViewById(R.id.city);
        nextBtn = (Button)findViewById(R.id.next);
        
        headI.setOnClickListener(this);
        ageT.setOnClickListener(this);
        cityT.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        
    }
    
    private void uploadHead(String path)
    {
        DhNet net = new DhNet(API.uploadHead);
        net.upload(new FileInfo("attach", new File(path)), new NetTask(self)
        {
            
            @Override
            public void doInUI(Response response, Integer transfer)
            {
                if (response.isSuccess())
                {
                    JSONObject jo = response.jSONFromData();
                    photoUid = JSONUtil.getString(jo, "photoId");
                }
            }
        });
    }
    
    @Override
    public void onClick(View arg0)
    {
        switch (arg0.getId())
        {
            case R.id.head:
                photoDialog.show();
                break;
            case R.id.age:
                dateDialog.show(self);
                break;
            case R.id.city:
                cityDialog.show();
                break;
            case R.id.next:
                
                nextStep();
                
                break;
            
            default:
                break;
        }
    }
    
    private void nextStep()
    {
        
        final String strnickname = nicknameT.getText().toString().trim();
        if (TextUtils.isEmpty(strnickname))
        {
            showToast("昵称不能为空");
            return;
        }
        final String strage = ageT.getText().toString();
        if (TextUtils.isEmpty(strage))
        {
            showToast("请设置您的年龄");
            return;
        }
        final String strcity = cityT.getText().toString();
        if (TextUtils.isEmpty(strcity))
        {
            showToast("请设置您所在的城市");
            return;
        }
        
        register();
        
        // Bundle bundle = new Bundle();
        // Bundle b = getIntent().getExtras();
        // if (b != null)
        // {
        // bundle.putString("phone", b.getString("phone"));
        // bundle.putString("code", b.getString("code"));
        // bundle.putString("pswd", b.getString("pswd"));
        // }
        // bundle.putString("nickname", strnickname);
        // bundle.putString("gender", sex);
        // bundle.putInt("birthYear", birthYear);
        // bundle.putInt("birthMonth", birthMonth);
        // bundle.putInt("birthday", birthday);
        // bundle.putString("province", province);
        // bundle.putString("city", city);
        // bundle.putString("district", district);
        // bundle.putString("photo", photo);
        //
        // Intent it = new Intent(self, AuthenticateOwnersActivity.class);
        // it.putExtra("data", bundle);
        // startActivity(it);
    }
    
    private void register()
    {
        Intent it = getIntent();
        String gender = sexR.getCheckedRadioButtonId() == R.id.tab_left ? "男" : "女";
        DhNet net = new DhNet(API.register);
        net.addParam("phone", it.getStringExtra("phone"));
        net.addParam("code", it.getStringExtra("code"));
        net.addParam("password", it.getStringExtra("pswd"));
        net.addParam("nickname", nicknameT.getText().toString());
        net.addParam("gender", gender);
        net.addParam("birthYear", mYear);
        net.addParam("birthMonth", mMonth);
        net.addParam("birthDay", mDay);
        net.addParam("province", mProvice);
        net.addParam("city", mCity);
        
        net.addParam("district", mDistrict);
        net.addParam("photo", photoUid);
        net.doPostInDialog(new NetTask(self)
        {
            
            @Override
            public void doInUI(Response response, Integer transfer)
            {
                if (response.isSuccess())
                {
                    showToast("注册成功!");
                    JSONObject jo = response.jSONFromData();
                    
                    User user = User.getInstance();
                    user.setToken(JSONUtil.getString(jo, "token"));
                    user.setUserId(JSONUtil.getString(jo, "userId"));
                    
                    Intent it = new Intent(self, AuthenticateOwnersActivity.class);
                    startActivityForResult(it, AuthenticateOwners);
                }
            }
        });
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case Constant.TAKE_PHOTO:
                    PhotoUtil.onPhotoFromCamera(self, Constant.ZOOM_PIC, mPhotoPath, 1, 1, 1000);
                    break;
                case Constant.PICK_PHOTO:
                    PhotoUtil.onPhotoFromPick(self, Constant.ZOOM_PIC, mPhotoPath, data, 1, 1, 1000);
                    break;
                case Constant.ZOOM_PIC:
                    Bitmap bmp = PhotoUtil.getLocalImage(new File(mPhotoPath));
                    headI.setImageBitmap(ImageUtil.toRoundCorner(bmp, 1000));
                    uploadHead(mPhotoPath);
                    break;
                
                case AuthenticateOwners:
                    Intent it = getIntent();
                    setResult(Activity.RESULT_OK, it);
                    finish();
                    break;
            }
        }
    }
    
}
