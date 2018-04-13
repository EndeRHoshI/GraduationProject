package com.hoshi.graduationproject.utils;

/**
 * Created by HoShI on 2018/3/15.
 */

public class ServerPath {

  //public static String BASE_PATH = "http://localhost:8080/hello";
  public static String BASE_PATH = "http://hoshi.fun:8080/GraduationJFinal/hello";

  /**
   * 用户注册
   * @param phone
   * @param nickname
   * @param password
   */
  public static String REGISTER = BASE_PATH + "/register";

  /**
   * 用户登录
   * @param phone
   * @param password
   */
  public static String LOGIN = BASE_PATH + "/login";

  /**
   * 用户注销
   * @param phone
   * @param password
   */
  public static String LOGOUT = BASE_PATH + "/logout";

  /**
   * 用户发动态
   * @param author_id
   * @param content
   */
  public static String TRENDS = BASE_PATH + "/trends";

  /**
   * 登录检查
   * @param id
   */
  public static String CHECK_LOGIN = BASE_PATH + "/checklogin";

  /**
   * 我的音乐登录检查
   * @param id
   */
  public static String CHECK_LOGIN_MY_MUSIC = BASE_PATH + "/checkloginmymusic";

  /**
   * 获取某个id的动态
   * @param id
   */
  public static String GET_TRENDS_BY_ID = BASE_PATH + "/gettrendsbyid";

  /**
   * 获取某个id的所有朋友的动态
   * @param id
   */
  public static String GET_FRIENDS_TRENDS_BY_ID = BASE_PATH + "/getfriendstrendsbyid";

  /**
   * 修改昵称
   * @param id
   */
  public static String CHANGE_NICKNAME = BASE_PATH + "/changenickname";

  /**
   * 修改性别
   * @param id
   */
  public static String CHANGE_SEX = BASE_PATH + "/changesex";

  /**
   * 修改生日
   * @param id
   * @param birthday
   */
  public static String CHANGE_BIRTHDAY = BASE_PATH + "/changebirthday";

  /**
   * 修改个人简介
   * @param id
   * @param profile
   */
  public static String CHANGE_PROFILE = BASE_PATH + "/changeprofile";

  /**
   * 查找好友
   * @param id
   */
  public static String SEARCH_FRIENDS = BASE_PATH + "/searchfriends";

  /**
   * 添加关注
   * @param id
   */
  public static String FOLLOW = BASE_PATH + "/follow";

  /**
   * 取消关注
   * @param id
   */
  public static String CANCEL = BASE_PATH + "/cancel";

  /**
   * 获得全部关注列表
   */
  public static String GET_FOLLOWS = BASE_PATH + "/getfollows";

  /**
   * 获得关注列表
   * @param follower_id
   */
  public static String GET_FOLLOWS_BY_ID = BASE_PATH + "/getfollowsbyid";

  /**
   * 获得粉丝列表
   * @param follows_id
   */
  public static String GET_FANS_BY_ID = BASE_PATH + "/getfansbyid";

  /**
   * 获得朋友的关注、粉丝和互相关注状态
   * @param id
   * @param friend_id
   * @param page_type
   */
  public static String GET_FRIENDS_DETAIL = BASE_PATH + "/getfriendsdetail";

  /**
   * 添加个人歌单
   * @param id
   * @param list_name
   */
  public static String ADD_SONG_LIST = BASE_PATH + "/addsonglist";

  /**
   * 获得个人歌单
   * @param id
   */
  public static String GET_P_SONG_LIST = BASE_PATH + "/getpsonglist";
}
