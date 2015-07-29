package com.gongpingjia.carplay.util;

import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.util.DhUtil;
import net.duohuo.dhroid.util.ViewUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.gongpingjia.carplay.CarPlayValueFix;
import com.gongpingjia.carplay.R;
import com.gongpingjia.carplay.R.id;
import com.gongpingjia.carplay.activity.my.PersonDetailActivity;
import com.gongpingjia.carplay.manage.UserInfoManage;
import com.gongpingjia.carplay.manage.UserInfoManage.LoginCallBack;
import com.gongpingjia.carplay.view.RoundImageView;
import com.google.gson.JsonArray;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class PicLayoutUtil
{
    LayoutParams params;
    
    JSONArray data;
    
    Context mContext;
    
    int count;
    
    int padding;
    
    LinearLayout layout;
    
    OnChildClickListener onChildClickListener;
    
    int width;
    
    int horizontalMax = 3;
    
    int headMax = 5;
    
    public int MorePic = 1;
    
    public int ThreePic = 2;
    
    /** type为1时表示直接加载,适用于详情页面,type==2表示只添加空的ImageView,不加载网络图片,用于adapter中 */
    int type;
    
    /** 直接加载网络图片 */
    public PicLayoutUtil(Context context, JSONArray jsa, int padding, LinearLayout layout, int width)
    {
        this.data = jsa;
        this.count = jsa.length();
        this.padding = DhUtil.dip2px(context, padding);
        this.layout = layout;
        this.mContext = context;
        this.width = width;
        this.type = 1;
    }
    
    /** 只添加Image,不加载图片 */
    public PicLayoutUtil(Context context, int count, int padding, LinearLayout layout, int width)
    {
        this.count = count;
        this.padding = DhUtil.dip2px(context, padding);
        this.layout = layout;
        this.mContext = context;
        this.width = width;
        this.type = 2;
    }
    
    /** 设置最多头像数量 */
    public void setHeadMaxCount(int count)
    {
        this.headMax = count;
    }
    
    public PicLayoutUtil(Context context)
    {
        this.mContext = context;
    }
    
    /**
     * 添加多个用户头像
     * 
     */
    public void AddChild()
    {
        int drawCount;
        int childWidth = (width - (headMax - 1) * padding) / headMax;
        params = new LayoutParams(childWidth, childWidth);
        params.rightMargin = padding;
        if (count >= headMax)
        {
            drawCount = headMax;
        }
        else
        {
            drawCount = count + 1;
        }
        
        if (drawCount == headMax)
        {
            for (int i = 0; i < drawCount; i++)
            {
                try
                {
                    if (i == drawCount - 1)
                    {
                        layout.addView(createCountTextView(count), params);
                    }
                    else
                    {
                        layout.addView(createHeadImageView(data.getJSONObject(i)), params);
                    }
                    
                }
                catch (JSONException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        else
        {
            for (int i = 0; i < drawCount; i++)
            {
                try
                {
                    layout.addView(createHeadImageView(data.getJSONObject(i)), params);
                }
                catch (JSONException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
    }
    
    /**
     * 添加5个用户头像(不加载图片)
     * 
     */
    public void AddAllHead()
    {
        int childWidth = (width - (headMax - 1) * padding) / headMax;
        params = new LayoutParams(childWidth, childWidth);
        params.rightMargin = padding;
        for (int i = 0; i < headMax; i++)
        {
            if (i == headMax - 1)
            {
                layout.addView(createCountTextView(), params);
            }
            else
            {
                layout.addView(createHeadImageView(null), params);
            }
        }
    }
    
    private TextView createCountTextView(int count)
    {
        TextView text = new TextView(mContext);
        text.setBackgroundResource(R.drawable.head2);
        text.setGravity(Gravity.CENTER);
        text.setTextColor(mContext.getResources().getColor(R.color.white));
        text.setText(count + "");
        return text;
    }
    
    private TextView createCountTextView()
    {
        TextView text = new TextView(mContext);
        text.setBackgroundResource(R.drawable.head2);
        text.setGravity(Gravity.CENTER);
        text.setTextColor(mContext.getResources().getColor(R.color.white));
        text.setVisibility(View.GONE);
        return text;
    }
    
    /** 加载网络头像 */
    public void BindHeadImage(LinearLayout layout, JSONArray jsa)
    {
        int drawcount;
        
        if (jsa.length() >= headMax)
        {
            drawcount = headMax;
        }
        else
        {
            drawcount = jsa.length() + 1;
        }
        
        if (drawcount == headMax)
        {
            for (int i = 0; i < drawcount; i++)
            {
                try
                {
                    if (i == drawcount - 1)
                    {
                        TextView text = (TextView)layout.getChildAt(headMax - 1);
                        text.setText(jsa.length() + "");
                        text.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        JSONObject jo = jsa.getJSONObject(i);
                        RoundImageView img = (RoundImageView)layout.getChildAt(i);
                        ViewUtil.bindNetImage(img, JSONUtil.getString(jo, "photo"), "optionsDefault");
                        img.setTag(JSONUtil.getString(jo, "userId"));
                        img.setVisibility(View.VISIBLE);
                    }
                }
                catch (JSONException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        else
        {
            for (int i = 0; i < drawcount; i++)
            {
                try
                {
                    JSONObject jo = jsa.getJSONObject(i);
                    RoundImageView img = (RoundImageView)layout.getChildAt(i);
                    ViewUtil.bindNetImage(img, JSONUtil.getString(jo, "photo"), "optionsDefault");
                    img.setTag(JSONUtil.getString(jo, "userId"));
                    img.setVisibility(View.VISIBLE);
                }
                catch (JSONException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
    }
    
    /** 加载网络图片 */
    public void BindImageView(LinearLayout layout, JSONArray jsa)
    {
        
        for (int i = 0; i < layout.getChildCount(); i++)
        {
            try
            {
                JSONObject jo = jsa.getJSONObject(i);
                LinearLayout lc = (LinearLayout)layout.getChildAt(i);
                for (int j = 0; j < lc.getChildCount(); j++)
                {
                    ImageView img = (ImageView)lc.getChildAt(j);
                    ViewUtil.bindNetImage(img, JSONUtil.getString(jo, "thumbnail_pic"), "optionsDefault");
                    
                }
            }
            catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 添加活动的多个图片
     * 
     * 
     */
    public void addMoreChild()
    {
        if (count == 1)
        {
            addPicOneChild();
        }
        else if (count == 4 || count == 2)
        {
            
            int childWidth = (width - (horizontalMax - 1) * padding) / horizontalMax;
            params = new LayoutParams(childWidth, childWidth);
            params.rightMargin = padding;
            for (int i = 0; i < count; i++)
            {
                try
                {
                    if (type == 1)
                    {
                        addMorePic(data.getJSONObject(i), 1);
                    }
                    else
                    {
                        addMorePic(null, 1);
                    }
                }
                catch (JSONException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        else
        {
            
            int childWidth = (width - (horizontalMax - 1) * padding) / horizontalMax;
            params = new LayoutParams(childWidth, childWidth);
            params.rightMargin = padding;
            
            for (int i = 0; i < count; i++)
            {
                try
                {
                    if (type == 1)
                    {
                        addMorePic(data.getJSONObject(i), 2);
                    }
                    else
                    {
                        addMorePic(null, 2);
                    }
                }
                catch (JSONException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
    }
    
    // private void AddPicThreeChild()
    // {
    // LinearLayout child = addChild(layout);
    // for (int i = 0; i < count; i++)
    // {
    // try
    // {
    // if (type == 1)
    // {
    // child.addView(createPicImageView(data.getJSONObject(i)), params);
    // }
    // else
    // {
    // child.addView(createPicImageView(null), params);
    // }
    // }
    // catch (JSONException e)
    // {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
    // }
    
    private ImageView createHeadImageView(JSONObject jo)
    {
        RoundImageView img = new RoundImageView(mContext);
        img.setLayoutParams(params);
        if (type == 1)
        {
            ViewUtil.bindNetImage(img, JSONUtil.getString(jo, "photo"), "optionsDefault");
            img.setTag(JSONUtil.getString(jo, "userId"));
            // img.setOnClickListener(new OnClickListener()
            // {
            //
            // @Override
            // public void onClick(final View v)
            // {
            // UserInfoManage manager = UserInfoManage.getInstance();
            // manager.checkLogin((Activity)mContext, new LoginCallBack()
            // {
            //
            // @Override
            // public void onisLogin()
            // {
            // ImageView i = (ImageView)v;
            // Intent it = new Intent(mContext, PersonDetailActivity.class);
            // it.putExtra("userId", i.getTag().toString());
            // mContext.startActivity(it);
            // }
            //
            // @Override
            // public void onLoginFail()
            // {
            // // TODO Auto-generated method stub
            //
            // }
            // });
            // }
            // });
            img.setVisibility(View.VISIBLE);
        }
        else
        {
            img.setVisibility(View.GONE);
        }
        
        return img;
    }
    
    private ImageView createPicImageView(JSONObject jo)
    {
        ImageView img = new ImageView(mContext);
        img.setLayoutParams(params);
        if (type == 1)
        {
            ViewUtil.bindNetImage(img, JSONUtil.getString(jo, "thumbnail_pic"), "optionsDefault");
        }
        img.setScaleType(ScaleType.FIT_XY);
        img.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                if (onChildClickListener != null)
                {
                    onChildClickListener.onclick("userid");
                }
            }
        });
        
        return img;
    }
    
    private void addPicOneChild()
    {
        LinearLayout child = addChild(layout);
        try
        {
            params = new LayoutParams(DhUtil.dip2px(mContext, 150), DhUtil.dip2px(mContext, 200));
            ImageView img = new ImageView(mContext);
            img.setLayoutParams(params);
            if (type == 1)
            {
                JSONObject jo = data.getJSONObject(0);
                ViewUtil.bindNetImage(img, JSONUtil.getString(jo, "thumbnail_pic"), "optionsDefault");
            }
            child.addView(img, params);
            img.setScaleType(ScaleType.FIT_XY);
            img.setOnClickListener(new OnClickListener()
            {
                
                @Override
                public void onClick(View v)
                {
                    if (onChildClickListener != null)
                    {
                        onChildClickListener.onclick("userid");
                    }
                }
            });
        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /** 添加多张图片 一行2个 */
    private void addMorePic(JSONObject jo, int horizontalMax)
    {
        LinearLayout child;
        if (layout.getChildCount() == 0)
        {
            child = addChild(layout);
        }
        else
        {
            child = (LinearLayout)layout.getChildAt(layout.getChildCount() - 1);
            if (child.getChildCount() > horizontalMax)
            {
                child = addChild(layout);
            }
        }
        
        child.addView(createPicImageView(jo), params);
    }
    
    private LinearLayout addChild(LinearLayout piclayout)
    {
        LinearLayout child = new LinearLayout(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.bottomMargin = padding;
        piclayout.addView(child, params);
        return child;
    }
    
    public interface OnChildClickListener
    {
        void onclick(String userid);
    }
}
