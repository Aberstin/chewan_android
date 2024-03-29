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

import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.util.ImageUtils;
import com.gongpingjia.carplay.R;
import com.gongpingjia.carplay.activity.CarPlayBaseActivity;
import com.gongpingjia.carplay.chat.task.DownloadImageTask;
import com.gongpingjia.carplay.chat.util.ImageCache;

public class AlertDialog extends CarPlayBaseActivity {
	private TextView mTextView;
	private Button mButton;
	private int position;
	private ImageView imageView;
	private EditText editText;
	private boolean isEditextShow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_dialog);
		// getWindow().setLayout(LayoutParams.MATCH_PARENT,
		// LayoutParams.MATCH_PARENT);
		mTextView = (TextView) findViewById(R.id.title);
		mButton = (Button) findViewById(R.id.btn_cancel);
		imageView = (ImageView) findViewById(R.id.image);
		editText = (EditText) findViewById(R.id.edit);
		// 提示内容
		String msg = getIntent().getStringExtra("msg");
		// 提示标题
		String title = getIntent().getStringExtra("title");
		position = getIntent().getIntExtra("position", -1);
		// 是否显示取消标题
		boolean isCanceTitle = getIntent().getBooleanExtra("titleIsCancel",
				false);
		// 是否显示取消按钮
		boolean isCanceShow = getIntent().getBooleanExtra("cancel", false);
		// 是否显示文本编辑框
		isEditextShow = getIntent().getBooleanExtra("editTextShow", false);
		// 转发复制的图片的path
		String path = getIntent().getStringExtra("forwardImage");
		//
		String edit_text = getIntent().getStringExtra("edit_text");

		if (msg != null)
			((TextView) findViewById(R.id.alert_message)).setText(msg);
		if (title != null)
			mTextView.setText(title);
		if (isCanceTitle) {
			mTextView.setVisibility(View.GONE);
		}
		if (isCanceShow)
			mButton.setVisibility(View.VISIBLE);
		if (path != null) {
			// 优先拿大图，没有去取缩略图
			if (!new File(path).exists())
				path = DownloadImageTask.getThumbnailImagePath(path);
			imageView.setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.alert_message))
					.setVisibility(View.GONE);
			if (ImageCache.getInstance().get(path) != null) {
				imageView.setImageBitmap(ImageCache.getInstance().get(path));
			} else {
				Bitmap bm = ImageUtils.decodeScaleImage(path, 150, 150);
				imageView.setImageBitmap(bm);
				ImageCache.getInstance().put(path, bm);
			}

		}
		if (isEditextShow) {
			editText.setVisibility(View.VISIBLE);
			editText.setText(edit_text);
		}
	}

	public void ok(View view) {
		setResult(RESULT_OK, new Intent().putExtra("position", position)
				.putExtra("edittext", editText.getText().toString())
		/* .putExtra("voicePath", voicePath) */);
		if (position != -1)
			ChatActivity.resendPos = position;
		finishAnim();

	}

	public void cancel(View view) {
		finishAnim();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finishAnim();
		return true;
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
