package com.gongpingjia.carplay.activity.my;

import com.gongpingjia.carplay.R;
import com.gongpingjia.carplay.R.layout;
import com.gongpingjia.carplay.activity.CarPlayBaseActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
/**
 * 
 * @Description 认证车主
 * @author wang
 * @date 2015-7-17 上午10:14:30
 */
public class AuthenticateOwnersActivity extends CarPlayBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate_owners);
    }

    @Override
    public void initView() {
    	setTitle("车主认证");
        // TODO Auto-generated method stub
        
    }

}
