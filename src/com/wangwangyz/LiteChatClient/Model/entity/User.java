package com.wangwangyz.LiteChatClient.Model.entity;

public class User {
    private String id;
    private String email;
    private String nickname;
    private String avatar;
    private String phone;
    private String introduce;
    private String status;
    private String password;
    public User( String id, String nickname, String password, String email,String introduce){
        this.id = id;
        this.nickname = nickname;
       this.password = password;
       this.email = email;
       this.introduce = introduce;
    }
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


}
