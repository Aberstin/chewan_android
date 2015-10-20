package com.gongpingjia.carplay.api;

/**
 * Created by Administrator on 2015/10/13.
 */
public class API2 {

    //测试版API前缀
    public static final String CWBaseurl = "http://cwapi.gongpingjia.com:8080/v2/";

    //获取验证码
    public static final String getVerification = CWBaseurl + "phone/";
    //官方活动详情
    public static final String ActiveDetails = CWBaseurl + "official/activity/";

    public static final String verifyCode = CWBaseurl + "phone/";
    //手机号登陆
    public static final String login = CWBaseurl + "user/login";
    //三方登陆
    public static final String snsLogin = CWBaseurl + "sns/login";
    //注册
    public static final String register = CWBaseurl + "user/register";
    //头像上传
    public static final String uploadAvatar = CWBaseurl + "avatar/upload";

    //忘记密码
    public static final String forgetPassword = CWBaseurl + "user/password";

    //获取我关注的信息
    public static String getSubscribe(String userId, String token) {
        return String.format(CWBaseurl + "user/%s/subscribe?token=%s", userId, token);
    }

    public static final String recommendList = CWBaseurl + "/official/activity/list";

    public static String getPlaces(String id) {
        return "http://cwapi.gongpingjia.com:8080/v2/area/list?parentId=" + id;
    }

}
