package org.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.model.Data;
import org.model.ToDoItem;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Predicate;

public class MainController
{
    // fields
    private FilteredList<ToDoItem> filteredList;
    private Predicate<ToDoItem> wantAllItems;
    private Predicate<ToDoItem> wantTodayItems;

    // fields - layout
    @FXML
    private ListView<ToDoItem> toDoItems_ListView;
    @FXML
    private TextArea details_TextArea;
    @FXML
    private Label deadline_Label;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu list_ContextMenu;
    @FXML
    private ToggleButton filter_ToggleButton;


    // methods - initialize
    public void initialize()
    {
        // initialize list_ContextMenu
        list_ContextMenu = new ContextMenu();
        MenuItem delete_MenuItem = new MenuItem("Delete");
        delete_MenuItem.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                ToDoItem item = toDoItems_ListView.getSelectionModel().getSelectedItem();
                Data.getInstance().getToDoItems().remove(item);
            }
        });
        list_ContextMenu.getItems().add(delete_MenuItem);

        // add change listener to my ListView
        toDoItems_ListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoItem>()
        {
            @Override
            public void changed(ObservableValue<? extends ToDoItem> observable, ToDoItem oldValue, ToDoItem newValue)
            {
                if (newValue != null)
                {
                    details_TextArea.setText(newValue.getDetails());
                    deadline_Label.setText(newValue.getDeadline().toString());
                }
            }
        });

        // filtered and sorted lists logic
        wantAllItems = (item) -> true;
        wantTodayItems = (item) -> item.getDeadline().equals(LocalDate.now());

        filteredList = new FilteredList<ToDoItem>(Data.getInstance().getToDoItems(), wantAllItems);
        SortedList<ToDoItem> sortedList = new SortedList<>(filteredList,
                (item1, item2) -> item1.getDeadline().compareTo(item2.getDeadline()));

        // move data to UI
        toDoItems_ListView.setItems(sortedList);

        // set selection model
        toDoItems_ListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // select the first item
        toDoItems_ListView.getSelectionModel().selectFirst();

        //
        toDoItems_ListView.setCellFactory(new Callback<ListView<ToDoItem>, ListCell<ToDoItem>>()
        {
            @Override
            public ListCell<ToDoItem> call(ListView<ToDoItem> param)
            {
                ListCell<ToDoItem> cell = new ListCell<>()
                {
                    @Override
                    protected void updateItem(ToDoItem item, boolean empty)
                    {
                        super.updateItem(item, empty);
                        if (empty)
                        {
                            setText(null);
                        }
                        else
                        {
                            setText(item.getShortDescription());
                            if (item.getDeadline().isBefore(LocalDate.now().plusDays(1)))
                            {
                                setTextFill(Color.RED);
                            }
                            else if (item.getDeadline().equals(LocalDate.now().plusDays(1)))
                            {
                                setTextFill(Color.ORANGE);
                            }
                        }
                    }
                };

                cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) ->
                        {
                            if (isNowEmpty) cell.setContextMenu(null);
                            else cell.setContextMenu(list_ContextMenu);
                        });

                return cell;
            }
        });
    }

    // methods - events
    @FXML
    public void showNewItemDialog()
    {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add new ToDo item");
        dialog.setHeaderText("Be productive");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("new_todo_item_layout.fxml"));

        try
        {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK)
        {
            NewItemController newItemController = fxmlLoader.getController();
            ToDoItem temp = newItemController.processResult();
            toDoItems_ListView.getSelectionModel().select(temp);
        }
    }

    @FXML
    public void handleKeyPressed(KeyEvent keyEvent)
    {
        ToDoItem selectedItem = toDoItems_ListView.getSelectionModel().getSelectedItem();

        if (selectedItem != null)
        {
            if (keyEvent.getCode().equals(KeyCode.DELETE))
            {
                Data.getInstance().getToDoItems().remove(selectedItem);
            }
        }
    }

    @FXML
    public void handleFilterButton()
    {
        ToDoItem selectedItem = toDoItems_ListView.getSelectionModel().getSelectedItem();

        // with Predicate<ToDoItem> functional interface
        if (filter_ToggleButton.isSelected()) // today's items
        {
            filteredList.setPredicate(wantTodayItems);
            if (filteredList.isEmpty())
            {
                details_TextArea.clear();
                deadline_Label.setText("");
            }
            else if (filteredList.contains(selectedItem))
            {
                toDoItems_ListView.getSelectionModel().select(selectedItem);
            }
            else
            {
                toDoItems_ListView.getSelectionModel().selectFirst();
            }
        }
        else // all items
        {
            filteredList.setPredicate(wantAllItems);
            toDoItems_ListView.getSelectionModel().select(selectedItem);
        }
    }

    @FXML
    public void handleExit()
    {
        Platform.exit();
    }
}
