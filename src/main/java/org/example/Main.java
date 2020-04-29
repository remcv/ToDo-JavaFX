package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.model.Data;

import java.io.IOException;

public class Main extends Application
{
    // main
    public static void main(String[] args)
    {
        launch(args);
    }

    // start
    @Override
    public void start(Stage primaryStage) throws IOException
    {
        // layout
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("main_layout.fxml"));

        // scene
        Scene myScene = new Scene(root, 1000, 700);

        // stage
        primaryStage.setScene(myScene);
        primaryStage.setTitle("Just do it");
        primaryStage.show();
    }

    // init
    @Override
    public void init() throws Exception
    {
        try
        {
            Data.getInstance().loadToDoItems();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // stop
    @Override
    public void stop() throws Exception
    {
        try
        {
            Data.getInstance().storeToDoItems();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
