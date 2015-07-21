package com.gongpingjia.carplay.util;

import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import net.duohuo.dhroid.net.cache.CachePolicy;
import net.duohuo.dhroid.util.Perference;

import org.json.JSONObject;

import android.content.Context;

/**
 * 
 * 用于存Preferences的获取与存储
 * 
 * @author duohuo
 * 
 */
public class CarPlayPerference extends Perference
{
    
    public String uid;
    
    // 用户的密码
    public String pswd;
    //手机号
    public String phone;
    //验证码
    public String code;
    //性别
    public String gender;
    //出生年
    public Integer birthYear;
    //出生月
    public Integer birthMonth;
   //出生日	
    public Integer birthday;
    //省份
    public String  province;
   //城市
    public String  city;
    //区域
    public String  district;
   //图片
    public String  photo;
    //昵称
    public String nickname;
    
    public String headUrl;
    
    public String getUid()
    {
        return uid;
    }
    
    public void setUid(String uid)
    {
        this.uid = uid;
    }
    
    
    public String settingbg;
    
    // 网络更新用户信息
    public void refreshUserInfo(Context context)
    {
        // DhNet net = new DhNet(API.persondetail);
        // net.useCache(CachePolicy.POLICY_NOCACHE);
        // net.doGet(new NetTask(context)
        // {
        // @Override
        // public void doInUI(Response response, Integer transfer)
        // {
        // if (response.isSuccess())
        // {
        // JSONObject jo = response.jSONFromData();
        // String faceurl = JSONUtil.getString(jo, "faceurl");
        // if (faceurl != null)
        // {
        // faceurl = faceurl.replace("_s.jpg", "_m.jpg");
        // }
        // // face = API.ImageBase + faceurl;
        // uid = JSONUtil.getString(jo, "id");
        // username = JSONUtil.getString(jo, "name");
        // commit();
        // notifyDataSetChanged();
        // }
        // }
        // });
    }
    
    public String getPswd()
    {
        return pswd;
    }
    
    public void setPswd(String pswd)
    {
        this.pswd = pswd;
    }

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Integer getBirthYear() {
		return birthYear;
	}

	public void setBirthYear(Integer birthYear) {
		this.birthYear = birthYear;
	}

	public Integer getBirthMonth() {
		return birthMonth;
	}

	public void setBirthMonth(Integer birthMonth) {
		this.birthMonth = birthMonth;
	}

	public Integer getBirthday() {
		return birthday;
	}

	public void setBirthday(Integer birthday) {
		this.birthday = birthday;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getHeadUrl() {
		return headUrl;
	}

	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}

	public String getSettingbg() {
		return settingbg;
	}

	public void setSettingbg(String settingbg) {
		this.settingbg = settingbg;
	}
    
    
}
