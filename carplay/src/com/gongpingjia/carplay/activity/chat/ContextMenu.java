/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gongpingjia.carplay.activity.chat;

import net.duohuo.dhroid.activity.ActivityTack;
import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.ioc.IocContainer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

import com.easemob.chat.EMMessage;
import com.gongpingjia.carplay.R;
import com.gongpingjia.carplay.activity.CarPlayBaseActivity;
import com.gongpingjia.carplay.activity.main.MainActivity.ExitRunnable;
import com.gongpingjia.carplay.service.MsgService;

public class ContextMenu extends CarPlayBaseActivity {

	private int position;

	Button deleteB, copyB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_chat_cd);
		getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		deleteB = (Button) findViewById(R.id.delete);
		copyB = (Button) findViewById(R.id.copy);
		int txtValue = EMMessage.Type.TXT.ordinal();
		int type = getIntent().getIntExtra("type", -1);
		// if (type == EMMessage.Type.TXT.ordinal()) {
		// setContentView(R.layout.context_menu_for_text);
		// } else if (type == EMMessage.Type.LOCATION.ordinal()) {
		// setContentView(R.layout.context_menu_for_location);
		// } else if (type == EMMessage.Type.IMAGE.ordinal()) {
		// setContentView(R.layout.context_menu_for_image);
		// } else if (type == EMMessage.Type.VOICE.ordinal()) {
		// setContentView(R.layout.context_menu_for_voice);
		// } else if (type == EMMessage.Type.VIDEO.ordinal()) {
		// // setContentView(R.layout.context_menu_for_video);
		// }
		copyB.setVisibility(type == EMMessage.Type.TXT.ordinal() ? View.VISIBLE
				: View.GONE);
		/*
		 * switch (getIntent().getIntExtra("type", -1)) { case txtValue:
		 * setContentView(R.layout.context_menu_for_text); break; case
		 * EMMessage.Type.LOCATION.ordinal():
		 * setContentView(R.layout.context_menu_for_location); break; case
		 * EMMessage.Type.IMAGE.ordinal():
		 * setContentView(R.layout.context_menu_for_image); break; case
		 * EMMessage.Type.VOICE.ordinal():
		 * setContentView(R.layout.context_menu_for_voice); break; //need to
		 * support netdisk and send netsdk? case Message.TYPE_NETDISK:
		 * setContentView(R.layout.context_menu_for_netdisk); break; case
		 * Message.TYPE_SENT_NETDISK:
		 * setContentView(R.layout.context_menu_for_sent_netdisk); break;
		 * default: break; }
		 */
		position = getIntent().getIntExtra("position", -1);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finishAnim();
		return true;
	}

	public void copy(View view) {
		setResult(ChatActivity.RESULT_CODE_COPY,
				new Intent().putExtra("position", position));
		finishAnim();
	}

	public void delete(View view) {
		setResult(ChatActivity.RESULT_CODE_DELETE,
				new Intent().putExtra("position", position));
		finishAnim();
	}

	public void forward(View view) {
		setResult(ChatActivity.RESULT_CODE_FORWARD,
				new Intent().putExtra("position", position));
		finishAnim();
	}

	public void open(View v) {
		setResult(ChatActivity.RESULT_CODE_OPEN,
				new Intent().putExtra("position", position));
		finishAnim();
	}

	public void download(View v) {
		setResult(ChatActivity.RESULT_CODE_DWONLOAD,
				new Intent().putExtra("position", position));
		finishAnim();
	}

	public void toCloud(View v) {
		setResult(ChatActivity.RESULT_CODE_TO_CLOUD,
				new Intent().putExtra("position", position));
		finishAnim();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finishAnim();
			return false;
		}
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

}
