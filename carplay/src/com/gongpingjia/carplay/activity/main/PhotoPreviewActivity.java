package com.gongpingjia.carplay.activity.main;

/**
 * 
 * @author Aizaz AZ
 *
 */
import java.util.List;

import android.os.Bundle;

import com.gongpingjia.carplay.activity.main.PhotoSelectorActivity.OnLocalReccentListener;
import com.gongpingjia.carplay.photo.domain.PhotoSelectorDomain;
import com.gongpingjia.carplay.photo.model.PhotoModel;
import com.gongpingjia.carplay.photo.ui.BasePhotoPreviewActivity;
import com.gongpingjia.carplay.util.CommonUtils;

public class PhotoPreviewActivity extends BasePhotoPreviewActivity implements OnLocalReccentListener {

    private PhotoSelectorDomain photoSelectorDomain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        photoSelectorDomain = new PhotoSelectorDomain(getApplicationContext());

        init(getIntent().getExtras());
    }

    @SuppressWarnings("unchecked")
    protected void init(Bundle extras) {
        if (extras == null)
            return;

        if (extras.containsKey("photos")) { // 预览图片
            photos = (List<PhotoModel>) extras.getSerializable("photos");
            current = extras.getInt("position", 0);
            updatePercent();
            bindData();
        } else if (extras.containsKey("album")) { // 点击图片查看
            String albumName = extras.getString("album"); // 相册
            this.current = extras.getInt("position");
            if (!CommonUtils.isNull(albumName) && albumName.equals(PhotoSelectorActivity.RECCENT_PHOTO)) {
                photoSelectorDomain.getReccent(this);
            } else {
                photoSelectorDomain.getAlbum(albumName, this);
            }
        }
    }

    @Override
    public void onPhotoLoaded(List<PhotoModel> photos) {
        this.photos = photos;
        updatePercent();
        bindData(); // 更新界面
    }

}
