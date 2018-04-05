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
   * 获取动态
   * @param id
   */
  public static String GET_TRENDS_BY_ID = BASE_PATH + "/gettrendsbyid";

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
   */
  public static String CHANGE_BIRTHDAY = BASE_PATH + "/changebirthday";
}
