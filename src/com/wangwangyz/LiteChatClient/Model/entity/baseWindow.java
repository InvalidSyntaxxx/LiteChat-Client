package com.wangwangyz.LiteChatClient.Model.entity;

import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
// TODO:优化windo父类，子类直接继承父类最小化、最大化、背景框透明、不可resize等
public  class baseWindow extends Stage {
    protected final Log log = LogFactory.getLog(getClass());
    Parent root;
    private double xOffset;
    private double yOffset;
    private String path;
    /**
     * @Func
     * @Description 设置窗口图标
     * @Pram
     * @Return
     */
    public void setIcon() {
        getIcons().add(new Image(getClass().getResourceAsStream("../View/resource/icons/AppIcon.png")));
    }
    /**
     * @Func
     * @Description 设置窗口移动
     */
    public void move() {
        root.setOnMousePressed(event -> {
            xOffset = getX() - event.getScreenX();
            yOffset = getY() - event.getScreenY();
            getRoot().setCursor(Cursor.CLOSED_HAND);
        });
        root.setOnMouseDragged(event -> {
            setX(event.getScreenX() + xOffset);
            setY(event.getScreenY() + yOffset);
        });
        root.setOnMouseReleased(event -> {
            root.setCursor(Cursor.DEFAULT);
        });
    }

    /**
     * @Func
     * @Description 窗口退出、最小化方法
     * @Pram
     * @Return
     */
    public void quit(){
        Button quitBtn = (Button) root.lookup("#quit" );
        quitBtn.setTooltip(new Tooltip("退出"));
        quitBtn.setOnAction( event ->{
            close();
        });
    };
    public void minimiser(){
        Button minimiserBtn = (Button) root.lookup("#minimiser");
        minimiserBtn.setOnAction( event -> {
            setIconified(true);
        });
    };

    /**
     * @Func
     * @Description 获取root
     * @Pram
     * @Return  root
     */
    public Parent getRoot() {
        return root;
    }
    public  void setRoot(Parent root){
        this.root = root;
    }
    /**
     * @Func
     * @Description 获取界面元素
     * @Pram String id
     * @Return 返回指定id的元素
     */
    public Object $(String id) {
        return (Object) root.lookup("#" + id);
    }
}

