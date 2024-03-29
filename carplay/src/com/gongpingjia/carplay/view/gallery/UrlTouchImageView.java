/*
 Copyright (c) 2012 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.gongpingjia.carplay.view.gallery;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.ioc.IocContainer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.gongpingjia.carplay.CarPlayValueFix;
import com.gongpingjia.carplay.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class UrlTouchImageView extends RelativeLayout {
	// protected ProgressBar mProgressBar;

	protected TouchImageView mImageView;

	protected Context mContext;

	Dialog progressdialog;

	ProgressBar mProgressBar;

	public UrlTouchImageView(Context ctx) {
		super(ctx);
		mContext = ctx;
		init();
	}

	public UrlTouchImageView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		mContext = ctx;
		init();
	}

	public TouchImageView getImageView() {
		return mImageView;
	}

	@SuppressWarnings("deprecation")
	protected void init() {
		mImageView = new TouchImageView(mContext);
		mImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Activity activity = (Activity) mContext;
				activity.finish();
			}
		});
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		mImageView.setLayoutParams(params);
		this.addView(mImageView);
		mImageView.setVisibility(GONE);

		mProgressBar = new ProgressBar(mContext);
		params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		params.setMargins(30, 0, 30, 0);
		mProgressBar.setLayoutParams(params);
		mProgressBar.setIndeterminate(false);
		this.addView(mProgressBar);
	}

	public void setUrl(String imageUrl) {
		if (!imageUrl.startsWith("http")) {
			imageUrl = "file://" + imageUrl;
		}

		ImageLoader.getInstance().loadImage(imageUrl,
				CarPlayValueFix.optionsDefault,
				new SimpleImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						super.onLoadingStarted(imageUri, view);
						mProgressBar.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap bitmap) {
						if (bitmap == null) {
							mImageView.setScaleType(ScaleType.CENTER);
							bitmap = BitmapFactory.decodeResource(
									getResources(), R.drawable.no_photo);
							mImageView.setImageBitmap(bitmap);
						} else {
							mImageView.setScaleType(ScaleType.MATRIX);
							mImageView.setImageBitmap(bitmap);
						}
						mImageView.setVisibility(VISIBLE);
						mProgressBar.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
					}
				});

	}

	// No caching load
	public class ImageLoadTask extends AsyncTask<String, Integer, Bitmap> {
		@Override
		protected Bitmap doInBackground(String... strings) {
			String url = strings[0];
			Bitmap bm = null;
			try {
				URL aURL = new URL(url);
				URLConnection conn = aURL.openConnection();
				conn.connect();
				InputStream is = conn.getInputStream();
				int totalLen = conn.getContentLength();
				InputStreamWrapper bis = new InputStreamWrapper(is, 8192,
						totalLen);
				bis.setProgressListener(new InputStreamWrapper.InputStreamProgressListener() {
					@Override
					public void onProgress(float progressValue,
							long bytesLoaded, long bytesTotal) {
						publishProgress((int) (progressValue * 100));
					}
				});
				bm = BitmapFactory.decodeStream(bis);
				bis.close();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return bm;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (bitmap == null) {
				mImageView.setScaleType(ScaleType.CENTER);
				bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.no_photo);
				mImageView.setImageBitmap(bitmap);
			} else {
				mImageView.setScaleType(ScaleType.MATRIX);
				mImageView.setImageBitmap(bitmap);
			}
			mImageView.setVisibility(VISIBLE);
			// mProgressBar.setVisibility(GONE);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// mProgressBar.setProgress(values[0]);
		}
	}
}
