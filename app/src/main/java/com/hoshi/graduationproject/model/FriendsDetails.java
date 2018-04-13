package com.hoshi.graduationproject.model;

public class FriendsDetails {

  public String getFriend_avatar() {
    return friend_avatar;
  }

  public void setFriend_avatar(String friend_avatar) {
    this.friend_avatar = friend_avatar;
  }

  public String getFriend_name() {
    return friend_name;
  }

  public void setFriend_name(String friend_name) {
    this.friend_name = friend_name;
  }

  public String getFriend_presonal_profile() {
    return friend_presonal_profile;
  }

  public void setFriend_presonal_profile(String friend_presonal_profile) {
    this.friend_presonal_profile = friend_presonal_profile;
  }

  public int getFriend_sex() {
    return friend_sex;
  }

  public void setFriend_sex(int friend_sex) {
    this.friend_sex = friend_sex;
  }

  public int getFriend_id() {
    return friend_id;
  }

  public String getFriend_birthday() {
    return friend_birthday;
  }

  public void setFriend_birthday(String friend_birthday) {
    this.friend_birthday = friend_birthday;
  }

  public void setFriend_id(int friend_id) {
    this.friend_id = friend_id;
  }

  public int friend_id;
  public String friend_avatar;
  public String friend_name;
  public String friend_birthday;
  public String friend_presonal_profile;
  public int friend_sex;
}
