/*
    This is my singleton class. It stores some data.
 */

package org.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

public class Data
{
    // fields
    private static final Data instance = new Data();
    private static final String filename = "data.txt";
    private ObservableList<ToDoItem> toDoItems;

    // constructor - private
    private Data()
    {
        // empty and private
    }

    // methods
    public static Data getInstance()
    {
        return instance;
    }

    public void loadToDoItems() throws IOException
    {
        toDoItems = FXCollections.observableArrayList();

        Path filePath = Paths.get(filename);
        BufferedReader br = Files.newBufferedReader(filePath);

        String inputLine;

        try
        {
            while ((inputLine = br.readLine()) != null)
            {
                String[] itemPieces = inputLine.split("\t");
                String shortDescription = itemPieces[0];
                String details = itemPieces[1];
                String deadlineString = itemPieces[2];
                LocalDate deadlineLD = LocalDate.parse(deadlineString);

                ToDoItem item = new ToDoItem(shortDescription, details, deadlineLD);
                toDoItems.add(item);
            }
        }
        finally
        {
            if (br != null) br.close();
        }
    }

    public void storeToDoItems() throws IOException
    {
        Path filePath = Paths.get(filename);

        try(BufferedWriter bw = Files.newBufferedWriter(filePath))
        {
            for (ToDoItem item : toDoItems)
            {
                String shortDescription = item.getShortDescription();
                String details = item.getDetails();
                String deadline = item.getDeadline().toString();

                bw.write(String.format("%s\t%s\t%s", shortDescription, details, deadline));
                bw.newLine();
            }
        }
    }

    // methods - getters and setters
    public ObservableList<ToDoItem> getToDoItems()
    {
        return toDoItems;
    }
}
