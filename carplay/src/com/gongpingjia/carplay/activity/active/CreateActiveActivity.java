package com.gongpingjia.carplay.activity.active;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import net.duohuo.dhroid.net.upload.FileInfo;
import net.duohuo.dhroid.util.DhUtil;
import net.duohuo.dhroid.util.PhotoUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.gongpingjia.carplay.R;
import com.gongpingjia.carplay.activity.CarPlayBaseActivity;
import com.gongpingjia.carplay.adapter.ImageAdapter;
import com.gongpingjia.carplay.api.API;
import com.gongpingjia.carplay.api.Constant;
import com.gongpingjia.carplay.bean.PhotoState;
import com.gongpingjia.carplay.bean.User;
import com.gongpingjia.carplay.util.Utils;
import com.gongpingjia.carplay.view.NestedGridView;
import com.gongpingjia.carplay.view.dialog.CommonDialog;
import com.gongpingjia.carplay.view.dialog.DateDialog;
import com.gongpingjia.carplay.view.dialog.DateDialog.OnDateResultListener;

/***
 * 
 * 创建活动
 * 
 * @author Administrator
 * 
 */
public class CreateActiveActivity extends CarPlayBaseActivity implements OnClickListener
{
    
    private static final int REQUEST_DESCRIPTION = 1;
    
    private static final int REQUEST_DESTINATION = 2;
    
    private Button mFinishBtn, mFinishInviteBtn;
    
    private View mTypeLayout, mDescriptionLayout, mDestimationLayout, mStartTimeLayout, mEndTimeLayout, mFeeLayout,
        mSeatLayout;
    
    private TextView mTypeText, mDescriptionText, mStartTimeText, mEndTimeText, mFeeText, mSeatText, mDestimationText,
        mSeatHintText;
    
    private NestedGridView mPhotoGridView;
    
    private ImageAdapter mImageAdapter;
    
    private List<PhotoState> mPhotoStates;
    
    // 最后一张图片的状态
    private PhotoState mLastPhoto;
    
    // 上传图片返回的id
    private List<String> mPicIds;
    
    private DhNet mDhNet;
    
    // 图片缓存根目录
    private File mCacheDir;
    
    // 当前选择图片的路径
    private String mCurPath;
    
    private List<String> mFeeOptions;
    
    private List<String> mTypeOptions;
    
    private List<String> mSeatOptions;
    
    private String mCity;
    
    private String mLocation;
    
    // 开始时间默认为当前的时间戳
    private long mStartTimeStamp = System.currentTimeMillis();
    
    private long mEndTimeStamp = 0;
    
    private User mUser = User.getInstance();
    
    Integer degree;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_active);
    }
    
    @Override
    public void initView()
    {
        setTitle("创建活动");
        mPicIds = new ArrayList<String>();
        mFeeOptions = new ArrayList<String>();
        mTypeOptions = new ArrayList<String>();
        mSeatOptions = new ArrayList<String>();
        
        mFeeOptions.add("AA制");
        mFeeOptions.add("我请客");
        mFeeOptions.add("请我吧");
        
        mTypeOptions.add("看电影");
        mTypeOptions.add("吃饭");
        mTypeOptions.add("唱歌");
        mTypeOptions.add("旅行");
        mTypeOptions.add("运动");
        mTypeOptions.add("拼车");
        mTypeOptions.add("代驾");
        
        mTypeText = (TextView)findViewById(R.id.tv_active_type);
        mDescriptionText = (TextView)findViewById(R.id.tv_description);
        mStartTimeText = (TextView)findViewById(R.id.tv_start_time);
        mEndTimeText = (TextView)findViewById(R.id.tv_end_time);
        mFeeText = (TextView)findViewById(R.id.tv_fee);
        mSeatText = (TextView)findViewById(R.id.tv_seat);
        mDestimationText = (TextView)findViewById(R.id.tv_destination);
        mSeatHintText = (TextView)findViewById(R.id.tv_seat_hint);
        
        mTypeLayout = findViewById(R.id.layout_active_type);
        mDescriptionLayout = findViewById(R.id.layout_description);
        mDestimationLayout = findViewById(R.id.layout_destination);
        mStartTimeLayout = findViewById(R.id.layout_start_time);
        mEndTimeLayout = findViewById(R.id.layout_end_time);
        mFeeLayout = findViewById(R.id.layout_fee);
        mSeatLayout = findViewById(R.id.layout_seats);
        
        // 初始化开始时间,默认为当前的时间
        mStartTimeText.setText(Utils.getDate());
        
        mTypeLayout.setOnClickListener(this);
        mDescriptionLayout.setOnClickListener(this);
        mDestimationLayout.setOnClickListener(this);
        mStartTimeLayout.setOnClickListener(this);
        mEndTimeLayout.setOnClickListener(this);
        mSeatLayout.setOnClickListener(this);
        mFeeLayout.setOnClickListener(this);
        
        mCacheDir = new File(getExternalCacheDir(), "CarPlay");
        mCacheDir.mkdirs();
        
        mFinishBtn = (Button)findViewById(R.id.btn_finish);
        mFinishInviteBtn = (Button)findViewById(R.id.btn_finish_invite);
        mPhotoGridView = (NestedGridView)findViewById(R.id.gv_photo);
        
        // 获取可用座位数
        mDhNet = new DhNet(API.availableSeat + mUser.getUserId() + "/seats?token=" + mUser.getToken());
        mDhNet.doGet(new NetTask(self)
        {
            
            @Override
            public void doInUI(Response response, Integer transfer)
            {
                // TODO Auto-generated method stub
                if (response.isSuccess())
                {
                    JSONObject json = response.jSONFrom("data");
                    try
                    {
                        if (json.getInt("isAuthenticated") == 1)
                        {
                            // 认证车主
                            int minSeat = json.getInt("minValue");
                            int maxSeat = json.getInt("maxValue");
                            for (int i = minSeat; i <= maxSeat; i++)
                            {
                                mSeatOptions.add(String.valueOf(i));
                            }
                        }
                        else
                        {
                            // 未认证
                            mSeatHintText.setText("邀请人数");
                            mSeatOptions.add("1");
                            mSeatOptions.add("2");
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        
        mFinishBtn.setOnClickListener(this);
        mFinishInviteBtn.setOnClickListener(this);
        
        mPhotoStates = new ArrayList<PhotoState>();
        mLastPhoto = new PhotoState();
        mLastPhoto.setLast(true);
        mLastPhoto.setChecked(false);
        mPhotoStates.add(mLastPhoto);
        mImageAdapter = new ImageAdapter(this, mPhotoStates);
        
        mPhotoGridView.setAdapter(mImageAdapter);
        
        mPhotoGridView.setOnItemClickListener(new OnItemClickListener()
        {
            
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // TODO Auto-generated method stub
                if (mPhotoStates.get(position).isLast())
                {
                    mCurPath = new File(mCacheDir, System.currentTimeMillis() + ".jpg").getAbsolutePath();
                    PhotoUtil.getPhoto(self, Constant.TAKE_PHOTO, Constant.PICK_PHOTO, new File(mCurPath));
                }
                else
                {
                    mPhotoStates.remove(position);
                    if (mPhotoStates.size() == 8 && !mPhotoStates.get(7).isLast())
                    {
                        mPhotoStates.add(mLastPhoto);
                    }
                    mImageAdapter.notifyDataSetChanged();
                }
            }
        });
        
    }
    
    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        int id = v.getId();
        Intent it = null;
        CommonDialog dlg = null;
        DateDialog date = null;
        switch (id)
        {
        
            case R.id.layout_active_type:
                dlg = new CommonDialog(self, mTypeOptions, "请选择活动");
                dlg.setOnItemClickListener(new CommonDialog.OnItemClickListener()
                {
                    
                    @Override
                    public void onItemClickListener(int which)
                    {
                        // TODO Auto-generated method stub
                        mTypeText.setText(mTypeOptions.get(which));
                    }
                });
                dlg.show();
                break;
            
            case R.id.layout_description:
                it = new Intent(self, ActiveDescriptionActivity.class);
                startActivityForResult(it, REQUEST_DESCRIPTION);
                break;
            
            case R.id.layout_destination:
                it = new Intent(self, MapActivity.class);
                startActivityForResult(it, REQUEST_DESTINATION);
                break;
            
            case R.id.layout_start_time:
                date = new DateDialog();
                date.setOnDateResultListener(new OnDateResultListener()
                {
                    
                    @Override
                    public void result(String date, long datetime, int year, int month, int day)
                    {
                        // TODO Auto-generated method stub
                        mStartTimeText.setText(date);
                        mStartTimeStamp = datetime;
                    }
                });
                date.show(self);
                break;
            case R.id.layout_end_time:
                date = new DateDialog();
                date.setOnDateResultListener(new OnDateResultListener()
                {
                    
                    @Override
                    public void result(String date, long datetime, int year, int month, int day)
                    {
                        // TODO Auto-generated method stub
                        mEndTimeText.setText(date);
                        mEndTimeStamp = datetime;
                    }
                });
                date.show(self);
                break;
            case R.id.layout_fee:
                dlg = new CommonDialog(self, mFeeOptions, "请选择付费方式");
                dlg.setOnItemClickListener(new CommonDialog.OnItemClickListener()
                {
                    
                    @Override
                    public void onItemClickListener(int which)
                    {
                        // TODO Auto-generated method stub
                        mFeeText.setText(mFeeOptions.get(which));
                    }
                });
                dlg.show();
                break;
            case R.id.layout_seats:
                dlg = new CommonDialog(self, mSeatOptions, "请选择提供座位数");
                dlg.setOnItemClickListener(new CommonDialog.OnItemClickListener()
                {
                    
                    @Override
                    public void onItemClickListener(int which)
                    {
                        // TODO Auto-generated method stub
                        mSeatText.setText(mSeatOptions.get(which));
                    }
                });
                dlg.show();
                break;
            
            case R.id.btn_finish:
                if (mTypeText.getText().toString().equals(""))
                {
                    showToast("请选择活动");
                    return;
                }
                if (mPicIds.size() == 0)
                {
                    showToast("请至少选择一张图片");
                    return;
                }
                if (mDescriptionText.getText().equals(""))
                {
                    
                }
                if (mDestimationText.getText().toString().length() == 0)
                {
                    showToast("请选择目的地");
                    return;
                }
                if (mSeatText.getText().toString().length() == 0)
                {
                    showToast("请提供座位数");
                    return;
                }
                
                // 获取可用的座位数
                mDhNet = new DhNet(API.createActive + "userId=" + mUser.getUserId() + "&token=" + mUser.getToken());
                mDhNet.addParam("type", mTypeText.getText().toString());
                mDhNet.addParam("introduction", mDescriptionText.getText().toString());
                JSONArray array = new JSONArray(mPicIds);
                mDhNet.addParam("cover", array);
                mDhNet.addParam("location", mLocation);
                mDhNet.addParam("city", mCity);
                mDhNet.addParam("address", mDestimationText.getText().toString());
                mDhNet.addParam("start", mStartTimeStamp);
                mDhNet.addParam("pay", mFeeText.getText().toString());
                mDhNet.addParam("seat", mSeatText.getText().toString());
                if (mEndTimeStamp != 0)
                {
                    mDhNet.addParam("end", mEndTimeStamp);
                }
                
                Map<String, Object> params = mDhNet.getParams();
                for (String key : params.keySet())
                {
                    Log.e("tag", key + ": " + params.get(key));
                }
                mDhNet.doPostInDialog(new NetTask(this)
                {
                    
                    @Override
                    public void doInUI(Response response, Integer transfer)
                    {
                        // TODO Auto-generated method stub
                        if (response.isSuccess())
                        {
                            showToast("发布成功");
                        }
                        else
                        {
                            try
                            {
                                Log.e("err", response.jSON().getString("errmsg"));
                            }
                            catch (JSONException e)
                            {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                });
                break;
            case R.id.btn_finish_invite:
                
                break;
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            switch (requestCode)
            {
            
                case REQUEST_DESCRIPTION:
                    mDescriptionText.setText(data.getStringExtra("des"));
                    break;
                
                case REQUEST_DESTINATION:
                    mDestimationText.setText(data.getStringExtra("destination"));
                    mCity = data.getStringExtra("city");
                    mLocation = data.getStringExtra("location");
                    break;
                case Constant.TAKE_PHOTO:
                    Bitmap btp1 = PhotoUtil.getLocalImage(new File(mCurPath));
                    String newPath = new File(mCacheDir, System.currentTimeMillis() + ".jpg").getAbsolutePath();
                    int degree = PhotoUtil.getBitmapDegree(mCurPath);
                    PhotoUtil.saveLocalImage(btp1, new File(newPath),degree);
                    btp1.recycle();
                    upLoadPic(newPath);
                    // PhotoUtil.onPhotoFromCamera(self, Constant.ZOOM_PIC, mCurPath, 1, 1, 1000);
                    break;
                case Constant.PICK_PHOTO:
                    Bitmap btp = PhotoUtil.checkImage(self, data);
                    PhotoUtil.saveLocalImage(btp, new File(mCurPath));
                    btp.recycle();
                    upLoadPic(mCurPath);
                    // PhotoUtil.onPhotoFromPick(self, Constant.ZOOM_PIC, mCurPath, data, 1, 1, 1000);
                    break;
            }
        }
    }
    
    private void upLoadPic(String path)
    {
        mPhotoStates.remove(mPhotoStates.size() - 1);
        PhotoState state = new PhotoState();
        state.setChecked(true);
        state.setLast(false);
        state.setPath(path);
        mPhotoStates.add(state);
        if (mPhotoStates.size() != 9)
        {
            mPhotoStates.add(mLastPhoto);
        }
        mImageAdapter.notifyDataSetChanged();
        DhNet net = new DhNet(API.uploadPictures + "userId=" + mUser.getUserId() + "&token=" + mUser.getToken());
        net.upload(new FileInfo("attach", new File(mCurPath)), new NetTask(self)
        {
            
            @Override
            public void doInUI(Response response, Integer transfer)
            {
                // TODO Auto-generated method stub
                if (response.isSuccess())
                {
                    JSONObject jo = response.jSONFrom("data");
                    try
                    {
                        String picId = jo.getString("photoId");
                        mPicIds.add(picId);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    showToast("图片上传成功");
                }
                else
                {
                    
                    if (mPhotoStates.size() != 9)
                    {
                        mPhotoStates.remove(mPhotoStates.size() - 1);
                    }
                    else
                    {
                        if (mPhotoStates.get(mPhotoStates.size() - 1).isLast())
                        {
                            mPhotoStates.remove(mPhotoStates.size() - 2);
                        }
                        else
                        {
                            mPhotoStates.remove(mPhotoStates.size() - 1);
                        }
                    }
                    try
                    {
                        showToast(response.jSON().getString("errmsg") + " 图片上传失败,请重新选择上传");
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    
}
