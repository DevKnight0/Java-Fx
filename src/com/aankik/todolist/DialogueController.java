package com.aankik.todolist;
import com.aankik.todolist.Datamodel.TodoData;
import com.aankik.todolist.Datamodel.TodoItem;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Callback;


import java.time.LocalDate;

public class DialogueController {

    @FXML
    public TextField ShortDescriptionField;
    @FXML
    private TextArea detailsArea;
    @FXML
    private DatePicker deadlinePicker;
    private Object Callback;

    public TodoItem processResult(){

        String shortDescription =ShortDescriptionField.getText().trim();
        String details= detailsArea.getText().trim();
        LocalDate deadlineValue= deadlinePicker.getValue();

        TodoItem newItem = new TodoItem(shortDescription,details,deadlineValue);

        TodoData.getInstance().addTodoItem(newItem);
        return newItem;


        //adding new item to singleton instance
    }

    public boolean Empty(){
        System.out.println("Empty called");
        String shortDescription =ShortDescriptionField.getText().trim();
        String details= detailsArea.getText().trim();
        LocalDate deadlineValue= deadlinePicker.getValue();
        if(shortDescription.isEmpty()  || details.isEmpty() || deadlineValue ==null)
        {
            // System.out.println("True Sent");
            return true;}

        return false;
    }

    public boolean dateCheck(){LocalDate deadlineValue= deadlinePicker.getValue();
        //System.out.println(deadlineValue);
        if(deadlineValue ==null) return false;
        if(deadlineValue.isBefore(LocalDate.now()) )
            return false;

        return true;
    }

    public void displayResult(TodoItem item){
        ShortDescriptionField.setText(item.getShortDescription());
        detailsArea.setText(item.getDetails());
        deadlinePicker.setValue(item.getDeadline());

    }
}
