package com.wangwangyz.LiteChatClient.Model.service;

import com.wangwangyz.LiteChatClient.Controller.Login;
import com.wangwangyz.LiteChatClient.Controller.Tips;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

public class validate {
    Login login = null;
    private boolean intact = true;
    Tips tips = null;
    String backInfo = "登录成功！";
    public validate(Login login){
        this.login = login;
        validateInput();
        tips = new Tips(backInfo);
        tips.show();

    }
    /**
     * @Func
     * @Description 验证登录界面的输入
     * @Pram
     * @Return 返回消息backInfo和完整性intact
    */
    public void validateInput(){
        if (((TextField)login.$("username")).getText().equals("")){
            backInfo = "您还未输入用户名哦";
            intact = false;
            return;
        }
        if(((PasswordField)login.$("password")).getText().equals("")){
            backInfo = "您还未输入密码哦";
            intact = false;
            return;
        }
        if(((PasswordField)login.$("password")).getText().matches("[0-9a-zA-Z./]{6,18}")){
            backInfo = "密码应该为由数字、字母或特殊符号./之外的6至18个字符";
            intact = false;
            ((PasswordField)login.$("password")).setText("");
            return;
        }
    }
    /**
     * @Func
     * @Description 将用户名和密码进行验证
     * @Pram
     * @Return  用户名是否存在及用户密码验证结果
    */
       public void validatePsw(){

       }
}
