package org.controller;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.model.Data;
import org.model.ToDoItem;

import java.time.LocalDate;

public class NewItemController
{
    // layout
    @FXML
    private TextField shortDescription_TextField;
    @FXML
    private TextArea details_TextArea;
    @FXML
    private DatePicker deadline_DatePicker;

    // methods
    public ToDoItem processResult()
    {
        String shortDescription = shortDescription_TextField.getText().trim();
        String details = details_TextArea.getText().trim();
        LocalDate deadline = deadline_DatePicker.getValue();

        ToDoItem temp = new ToDoItem(shortDescription, details, deadline);
        Data.getInstance().getToDoItems().add(temp);
        return temp;
    }
}
