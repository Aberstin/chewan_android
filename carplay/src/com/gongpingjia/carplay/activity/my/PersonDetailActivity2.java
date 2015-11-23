package com.gongpingjia.carplay.activity.my;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gongpingjia.carplay.CarPlayValueFix;
import com.gongpingjia.carplay.R;
import com.gongpingjia.carplay.activity.CarPlayBaseActivity;
import com.gongpingjia.carplay.activity.main.PhotoSelectorActivity;
import com.gongpingjia.carplay.adapter.PersonDetailAdapter;
import com.gongpingjia.carplay.api.API2;
import com.gongpingjia.carplay.api.Constant;
import com.gongpingjia.carplay.bean.User;
import com.gongpingjia.carplay.photo.model.PhotoModel;
import com.gongpingjia.carplay.view.ImageGallery;
import com.gongpingjia.carplay.view.RoundImageView;
import com.gongpingjia.carplay.view.pop.ReportPop;
import com.gongpingjia.carplay.view.pop.SelectPicturePop;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import net.duohuo.dhroid.net.upload.FileInfo;
import net.duohuo.dhroid.util.PhotoUtil;
import net.duohuo.dhroid.util.ViewUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import jp.wasabeef.blurry.Blurry;


/**
 * TA 的详情
 */
public class PersonDetailActivity2 extends CarPlayBaseActivity implements View.OnClickListener {

    User user;

    private RoundImageView headI,carLogo;
    private ImageView sexI, photo_bgI;
    private TextView nameT, ageT, attentionT, carName,right_text;
    private RelativeLayout sexbgR;
    private LinearLayout myactiveL;
    private Button uploadBtn, perfectBtn;

    private RecyclerView recyclerView;
    PersonDetailAdapter mAdapter;

    List<JSONObject> album;

    // 图片缓存根目录
    private File mCacheDir;
    private String mPhotoPath;

    //上传图片总数
    private int uploadPhotoCount = 0;

    //已上传的图片
    private int uploadedCount = 0;
    String  name,logo,brand;
    JSONObject jo;
    String userId;
    boolean issubscribe;        //是否关注
    String licenseAuthStatus;
    String photoAuthStatus;
    String gender;
    String age;
    String emchatName;
    String idel;
    String type ;
    private String headimg;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail2);

    }

    @Override
    public void initView() {
        user = User.getInstance();
        setTitle("TA的详情");
        mCacheDir = new File(getExternalCacheDir(), "CarPlay");
        mCacheDir.mkdirs();
        album = new ArrayList<JSONObject>();
        bundle = getIntent().getExtras();
        type = bundle.getString("type");

        headI = (RoundImageView) findViewById(R.id.head);
        nameT = (TextView) findViewById(R.id.name);
        sexbgR = (RelativeLayout) findViewById(R.id.layout_sex_and_age);
        sexI = (ImageView) findViewById(R.id.iv_sex);
        ageT = (TextView) findViewById(R.id.tv_age);
        photo_bgI = (ImageView) findViewById(R.id.photo_bg);
        attentionT = (TextView) findViewById(R.id.attention);
        myactiveL = (LinearLayout) findViewById(R.id.myactive);
        uploadBtn = (Button) findViewById(R.id.upload);
        carLogo = (RoundImageView) findViewById(R.id.carlogo);
        carName = (TextView) findViewById(R.id.carname);
        perfectBtn = (Button) findViewById(R.id.perfect);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        right_text = (TextView) findViewById(R.id.right_text);
        if ("activity".equals(type)){
            right_text.setVisibility(View.VISIBLE);
            right_text.setText("举报");

        }else{
            right_text.setVisibility(View.GONE);
        }

        headI.setOnClickListener(this);
        myactiveL.setOnClickListener(this);
        uploadBtn.setOnClickListener(this);
        perfectBtn.setOnClickListener(this);
        right_text.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(self);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mAdapter = new PersonDetailAdapter(self);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

//      if (user.isLogin()) {
        getDetails();
//      }
    }

    public void getDetails() {
        userId = getIntent().getStringExtra("userId");
        DhNet verifyNet = new DhNet(API2.CWBaseurl + "/user/" + userId
                + "/info?viewUser=" + user.getUserId() + "&token=" + user.getToken());
        verifyNet.doGetInDialog(new NetTask(self) {

            @Override
            public void doInUI(Response response, Integer transfer) {
//                System.out.println(user.getUserId()+"---------"+user.getToken());
                if (response.isSuccess()) {
                    jo = response.jSONFromData();
                     idel = JSONUtil.getString(jo, "idle");
//                    System.out.println("他的详情"+JSONUtil.getString(jo, "idle"));
                    JSONObject carjo = JSONUtil.getJSONObject(jo, "car");
                      name = JSONUtil.getString(jo, "nickname");
                    logo = JSONUtil.getString(carjo, "logo");
                    brand = JSONUtil.getString(carjo, "brand");

                     emchatName = JSONUtil.getString(jo,"emchatName");
                    //昵称,性别,年龄
                    ViewUtil.bindView(nameT, JSONUtil.getString(jo, "nickname"));
                     gender = JSONUtil.getString(jo, "gender");
                     age = JSONUtil.getString(jo, "age");
                    if (gender.equals("男")) {
                        sexbgR.setBackgroundResource(R.drawable.radio_sex_man_normal);
                        sexI.setBackgroundResource(R.drawable.icon_man3x);
                    } else {
                        sexbgR.setBackgroundResource(R.drawable.radion_sex_woman_normal);
                        sexI.setBackgroundResource(R.drawable.icon_woman3x);
                    }
                    ViewUtil.bindView(ageT, JSONUtil.getInt(jo, "age"));

                    //头像
                     headimg = JSONUtil.getString(jo, "avatar");
                    ViewUtil.bindNetImage(headI, headimg, "head");
                    //相册
                    JSONArray albumJsa = JSONUtil.getJSONArray(jo, "album");
                    getAlbum(albumJsa);
//                    try {
//                        if (albumJsa != null) {
//                            ViewUtil.bindNetImage(photo_bgI, albumJsa.getJSONObject(0).getString("url"), "default");
//                            getAlbum(albumJsa);
//                        } else {
//                            ViewUtil.bindNetImage(photo_bgI, headimg, "head");
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                    //模糊效果
                    ImageLoader.getInstance().displayImage(headimg, photo_bgI, CarPlayValueFix.optionsDefault, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                            final ImageView img = (ImageView) view;
                            img.setImageBitmap(bitmap);
                            Blurry.with(self)
                                    .radius(10)
                                    .sampling(Constant.BLUR_VALUE)
                                    .async()
                                    .capture(img)
                                    .into(img);
                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {

                        }
                    });
                    //是否可查看Ta的照片
                    if (user.isHasAlbum()) {
                        ((RelativeLayout) findViewById(R.id.uploadlayout)).setVisibility(View.GONE);
                        ((LinearLayout) findViewById(R.id.photolayout)).setVisibility(View.VISIBLE);

                    } else {
                        ((RelativeLayout) findViewById(R.id.uploadlayout)).setVisibility(View.VISIBLE);
                        ((LinearLayout) findViewById(R.id.photolayout)).setVisibility(View.GONE);
                    }
                    issubscribe = JSONUtil.getBoolean(jo, "subscribeFlag");
                    perfectBtn.setBackgroundResource(issubscribe ? R.drawable.btn_grey_fillet : R.drawable.btn_red_fillet);
                    //头像认证
                     photoAuthStatus = JSONUtil.getString(jo, "photoAuthStatus");
                    if (photoAuthStatus.equals("认证通过")) {
                        attentionT.setBackgroundResource(R.drawable.btn_yellow_fillet);
                        attentionT.setText("已认证");
                    } else {
                        attentionT.setBackgroundResource(R.drawable.radio_sex_man_focused);
                        attentionT.setText("未认证");
                    }
                    //车主认证
                     licenseAuthStatus = JSONUtil.getString(jo, "licenseAuthStatus");
                    if (licenseAuthStatus.equals("认证通过")) {
                        findViewById(R.id.carlayout).setVisibility(View.VISIBLE);
                        ViewUtil.bindNetImage(carLogo, JSONUtil.getString(carjo, "logo"), "head");

                        ViewUtil.bindView(carName, JSONUtil.getString(carjo, "brand"));
                    } else {
                        findViewById(R.id.carlayout).setVisibility(View.GONE);
                    }


                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        Intent it;
        switch (v.getId()) {
            //关注
            case R.id.perfect:
                if (!issubscribe) {
                    attentionorCancle();
                }
                break;
            //TA的活动
            case R.id.myactive:
                it = new Intent(self,HisDynamicActivity.class);
                it.putExtra("userId",userId);
                it.putExtra("name",name);
                it.putExtra("photoAuthStatus",photoAuthStatus);
                it.putExtra("licenseAuthStatus",licenseAuthStatus);
                it.putExtra("gender",gender);
                it.putExtra("age",age);
                it.putExtra("brand",brand);
                it.putExtra("logo",logo);
                it.putExtra("emchatName",emchatName);
                it.putExtra("idel",idel);
                startActivity(it);
                break;
            //上传照片
            case R.id.upload:
                mPhotoPath = new File(mCacheDir, System.currentTimeMillis() + ".jpg").getAbsolutePath();
                final File tempFile = new File(mPhotoPath);
//                final CharSequence[] items = {"相册", "拍照"};
//                AlertDialog dlg = new AlertDialog.Builder(self).setTitle("选择图片")
//                        .setItems(items, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int item) {
//                                if (item == 1) {
//                                    Intent getImageByCamera = new Intent(
//                                            "android.media.action.IMAGE_CAPTURE");
//                                    getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT,
//                                            Uri.fromFile(tempFile));
//                                    startActivityForResult(getImageByCamera,
//                                            Constant.TAKE_PHOTO);
//                                } else {
//                                    Intent intent = new Intent(self,
//                                            PhotoSelectorActivity.class);
//                                    intent.putExtra(PhotoSelectorActivity.KEY_MAX,
//                                            9);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                                    startActivityForResult(intent, Constant.PICK_PHOTO);
//                                }
//                            }
//                        }).create();
//                Window window = dlg.getWindow();
//                window.setWindowAnimations(R.style.mystyle);
//                dlg.show();

                final SelectPicturePop pop = SelectPicturePop.getInstance(self, -1);
                pop.setPhotoGraphListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent getImageByCamera = new Intent(
                                "android.media.action.IMAGE_CAPTURE");
                        getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(tempFile));
                        startActivityForResult(getImageByCamera,
                                Constant.TAKE_PHOTO);
                        pop.dismiss();
                    }
                });
                pop.setAlbumListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(self,
                                PhotoSelectorActivity.class);
                        intent.putExtra(PhotoSelectorActivity.KEY_MAX,
                                9);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, Constant.PICK_PHOTO);
                        pop.dismiss();
                    }
                });
                pop.show();

                break;
            case R.id.head:
                it = new Intent(self,ImageGallery.class);
                String[] photos ={headimg};
                it.putExtra("imgurls", photos);
//                it.putExtra("imgids", "123456");
                it.putExtra("type", "his");
                startActivity(it);
                break;
            case R.id.right_text:
                System.out.println("举报id"+bundle.getString("activityId"));
                ReportPop.getInstance(self,bundle.getString("activityId"),userId).show();
                break;
            default:
                break;
        }
    }

    //他的相册
    private void getAlbum(JSONArray albumJsa) {
        album.clear();
        if (albumJsa != null) {
            for (int i = 0; i < albumJsa.length(); i++) {
                try {
                    album.add(albumJsa.getJSONObject(i));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mAdapter.setData(album);
        }
    }

    //关注
    private void attentionorCancle() {
        DhNet net = new DhNet(API2.CWBaseurl + "/user/" + user.getUserId() + "/listen?token=" + user.getToken());
        net.addParam("targetUserId", userId);
        net.doPostInDialog(new NetTask(self) {
            @Override
            public void doInUI(Response response, Integer transfer) {
                if (response.isSuccess()) {
                    perfectBtn.setBackgroundResource(R.drawable.radio_sex_man_focused);
//                    JSONObject userjo = JSONUtil.getJSONObject(jo, "organizer");
//                    try {
//                        userjo.put("subscribeFlag", !attention);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//
//                    }
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.PICK_PHOTO:
                    showProgressDialog("图片上传中...");
                    if (data != null && data.getExtras() != null) {
                        @SuppressWarnings("unchecked")
                        List<PhotoModel> photos = (List<PhotoModel>) data.getExtras().getSerializable("photos");
                        if (photos == null || photos.isEmpty()) {
                            showToast("没有选择图片!");
                        } else {
                            uploadPhotoCount = photos.size();
                            for (int i = 0; i < photos.size(); i++) {
                                String newPhotoPath = new File(mCacheDir, System.currentTimeMillis() + ".jpg")
                                        .getAbsolutePath();
                                Bitmap btp = PhotoUtil.getLocalImage(new File(photos.get(i).getOriginalPath()));
                                PhotoUtil.saveLocalImageSquare(btp, new File(newPhotoPath));
                                uploadHead(newPhotoPath);
                            }
                        }
                    }
                    break;
                case Constant.TAKE_PHOTO:
                    Bitmap btp1 = PhotoUtil.getLocalImage(new File(mPhotoPath));
                    String newPath = new File(mCacheDir, System.currentTimeMillis()
                            + ".jpg").getAbsolutePath();
                    int degree = PhotoUtil.getBitmapDegree(mPhotoPath);
                    PhotoUtil.saveLocalImageSquare(btp1, new File(newPath), degree);
                    btp1.recycle();
                    showProgressDialog("图片上传中...");
                    uploadPhotoCount = 1;
                    uploadHead(newPath);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadHead(String path) {

        Bitmap bmp = PhotoUtil.getLocalImage(new File(path));
//        addPhoto.setImageBitmap(bmp);
        DhNet net = new DhNet(API2.CWBaseurl + "user/" + user.getUserId() + "/album/upload?token=" + user.getToken());
        net.upload(new FileInfo("attach", new File(path)), new NetTask(self) {

            @Override
            public void doInUI(Response response, Integer transfer) {
                hidenProgressDialog();
                if (response.isSuccess()) {
//                    user.setHasAlbum(true);         //设置相册状态
                    uploadedCount = uploadedCount + 1;
                    JSONObject jo = response.jSONFromData();
                    showToast("上传成功");
                    String success = "上传成功";
                    if (uploadPhotoCount == uploadedCount) {
                        EventBus.getDefault().post(success);
                        uploadedCount = 0;
                        setHasAlbm();

                        DhNet net = new DhNet(API2.CWBaseurl + "user/" + user.getUserId() + "/photoCount?token=" + user.getToken());
                        net.addParam("count", uploadPhotoCount);
                        net.doPost(new NetTask(self) {
                            @Override
                            public void doInUI(Response response, Integer transfer) {

                            }
                        });
                    }
                }
            }
        });
    }

    private void setHasAlbm(){
        DhNet net = new DhNet(API2.CWBaseurl + "/user/" + user.getUserId()
                + "/info?viewUser=" + user.getUserId() + "&token=" + user.getToken());
        net.doGet(new NetTask(self) {
            @Override
            public void doInUI(Response response, Integer transfer) {
                JSONObject jo = response.jSONFromData();
                JSONArray albumJsa = JSONUtil.getJSONArray(jo, "album");
                if (albumJsa.length() > 1) {
                    user.setHasAlbum(true);         //设置相册状态
                    ((LinearLayout) findViewById(R.id.photolayout)).setVisibility(View.VISIBLE);        //可以查看
                    ((RelativeLayout) findViewById(R.id.uploadlayout)).setVisibility(View.GONE);
                    //控制附近列表刷新
                    EventBus.getDefault().post(new String("刷新附近列表"));
                } else {
                    ((LinearLayout) findViewById(R.id.photolayout)).setVisibility(View.GONE);        //可以查看
                    ((RelativeLayout) findViewById(R.id.uploadlayout)).setVisibility(View.VISIBLE);
                }

            }
        });
    }
}
