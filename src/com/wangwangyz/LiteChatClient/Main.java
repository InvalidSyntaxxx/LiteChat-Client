package com.wangwangyz.LiteChatClient;

import com.wangwangyz.LiteChatClient.Controller.Login;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Login login = new Login();
        login.show();

    }
}
