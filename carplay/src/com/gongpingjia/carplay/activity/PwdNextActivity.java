package com.gongpingjia.carplay.activity;

import com.gongpingjia.carplay.R;
import com.gongpingjia.carplay.R.id;
import com.gongpingjia.carplay.R.layout;
import com.gongpingjia.carplay.R.menu;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
/**
 * 找回密码(设置新密码)页面
 * hqh
 * @author Administrator
 *
 */
public class PwdNextActivity extends CarPlayBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pwd_next);
		
		setTitle("找回密码");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pwd_next, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
