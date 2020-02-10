package com.aankik.todolist;
import com.aankik.todolist.Datamodel.TodoData;
import com.aankik.todolist.Datamodel.TodoItem;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;


import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller  {
    private List<TodoItem> todoItem;
    @FXML
    private ListView<TodoItem> todoListView;
    @FXML
    private TextArea itemDetails;
    @FXML
    private Label deadline;
    @FXML
    private BorderPane mainBorderPane;


    @FXML
    private ContextMenu listContextMenu;
    @FXML
    private ContextMenu list1;


    @FXML
    private ToggleButton filterToggleButton;

    private FilteredList<TodoItem> filteredList;

    private Predicate<TodoItem> wantAllItems;
    private Predicate<TodoItem> wantTodaysItems;
    @FXML
    private Button Speak;


    public void initialize(){
//        TodoItem item1= new TodoItem("Mail Bday Card ","Buy a 30th Bday card",
//                LocalDate.of(2020, Month.APRIL,25));
//
//        TodoItem item2= new TodoItem("Doctors Appointment","See Dr. Pathak at Ruchi life",
//                LocalDate.of(2020, Month.MAY,22));
//
//        TodoItem item3= new TodoItem("Finish Design proposal  for client ","I would email Ashok by 22 April",
//                LocalDate.of(2020, Month.APRIL,22));
//
//        TodoItem item4= new TodoItem("Pickup Bhavesh at main station ","Bhavesh's arriving on 23 March 9:00 am",
//                LocalDate.of(2020, Month.MARCH,23));
//
//        TodoItem item5= new TodoItem("Buy Grocery ","Take Id card and and grocery List",
//                LocalDate.of(2020, Month.APRIL,20));
//
//        todoItem =new ArrayList<>();
//        todoItem.add(item1);
//        todoItem.add(item2);
//        todoItem.add(item3);
//        todoItem.add(item4);
//        todoItem.add(item5);

//        TodoData.getInstance().setTodoItems(todoItem);


        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        list1 =new ContextMenu();
        MenuItem editMenuItem= new MenuItem("Edit");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TodoItem item = todoListView.getSelectionModel().getSelectedItem();
                deleteItem(item);
            }
        });

        editMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TodoItem item =todoListView.getSelectionModel().getSelectedItem();
                editItem(item);
            }
        });
        listContextMenu.getItems().addAll(deleteMenuItem);
        listContextMenu.getItems().addAll(editMenuItem);


        //        to already select first list
        todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoItem>() {
            @Override
//            event listener
            public void changed(ObservableValue<? extends TodoItem> observableValue, TodoItem todoItem, TodoItem t1) {
                if(t1 !=null){
                    TodoItem item= todoListView.getSelectionModel().getSelectedItem();
                    itemDetails.setText(item.getDetails());
                    DateTimeFormatter df= DateTimeFormatter.ofPattern("MMMM d,yyyy");
                    deadline.setText(df.format(item.getDeadline()));
                }
                else{
                    itemDetails.setText("");
                    deadline.setText("");
                }

            }
        });

        //to pass all test to display all items
        wantAllItems = new Predicate<TodoItem>() {
            @Override
            public boolean test(TodoItem todoItem) {
                return true;
            }
        };

        //to shows items of today's deadline
        wantTodaysItems = new Predicate<TodoItem>() {
            @Override
            public boolean test(TodoItem todoItem) {
                return (todoItem.getDeadline().equals(LocalDate.now()));
            }
        };


        filteredList = new FilteredList<TodoItem>(TodoData.getInstance().getTodoItems(), wantAllItems);

        SortedList<TodoItem> sortedList = new SortedList<TodoItem>(filteredList,
                new Comparator<TodoItem>() {
                    @Override
                    public int compare(TodoItem o1, TodoItem o2) {
                        return o1.getDeadline().compareTo(o2.getDeadline());
                    }
                });

//        todoListView.setItems(TodoData.getInstance().getTodoItems());
        todoListView.setItems(sortedList);
        //bind listview in obserbable list in Tododata class
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
//     calling toString();
        todoListView.getSelectionModel().selectFirst();
//        to already select the first item



        //to add color to past bookmark
        //passing annonymous class which implements callback interface
        //second argument method is used to return instance of cell
        todoListView.setCellFactory(new Callback<ListView<TodoItem>, ListCell<TodoItem>>() {
            @Override
            public ListCell<TodoItem> call(ListView<TodoItem> param) {
                ListCell<TodoItem> cell = new ListCell<TodoItem>() {

                    @Override
                    protected void updateItem(TodoItem item, boolean empty) {
                        super.updateItem(item, empty);
                        //if cell is empty
                        setTextFill(Color.BLACK);
//                       setStyle("-fx-background-color: white;");

                        if(empty) {
                            setText(null);
                        } else {
                            setText(item.getShortDescription());
                            if(item.getDeadline().isBefore(LocalDate.now().plusDays(0))) {
                                setTextFill(Color.DARKRED);
                            } else   if(item.getDeadline().equals(LocalDate.now().plusDays(0))) {
                                setTextFill(Color.RED);
                            }
                            else if(item.getDeadline().equals(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.BLUE);
                            }
                        }

                    }
                };


                //right click when activated
//delete menu

                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if(isNowEmpty) {//cell.setStyle("-fx-background-color: white;");
                                cell.setContextMenu(null);
                            } else {//cell.setStyle("-fx-background-color: white;");
                                cell.setContextMenu(listContextMenu);
                            }

                        });

                return cell;
            }
        });

    }

    @FXML
    public  void showNewItemDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add New Todo Item");
        //headertext
        dialog.setHeaderText("Use this dialog to create a new todo item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch(IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        Optional<ButtonType> result = dialog.showAndWait();


        DialogueController controller=fxmlLoader.getController();

        if(controller.Empty()==true && result.get()!=ButtonType.CLOSE){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("ALERT !");
            alert.setHeaderText(null);
            if(controller.dateCheck()==true ) {
                System.out.println("IF");
                alert.setContentText("Enter Details Again.........");
            }
            else {
                System.out.println("else");
                alert.setContentText("Enter Correct Date........");
            }
            alert.showAndWait();
            return;

        }
        if(controller.dateCheck()==false && result.get()!=ButtonType.CLOSE){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("ALERT !");
            alert.setHeaderText(null);
            alert.setContentText("Enter Correct Date........");
            alert.showAndWait();
            return;
        }

        if(result.isPresent() && result.get()==ButtonType.OK ){
            // DialogueController controller=fxmlLoader.getController();
            TodoItem newItem = controller.processResult();
            //  todoListView.getItems().setAll(TodoData.getInstance().getTodoItems());
            if(newItem !=null)
                todoListView.getSelectionModel().select(newItem);
            //loading new items



            System.out.println("Ok Pressed");
        }

        else System.out.println("Please Enter Something");



    }


    //to add code for DELETE Key
    @FXML
    public void handleKeyPressed(KeyEvent keyEvent) {
        TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
        if(selectedItem != null) {
            if(keyEvent.getCode().equals(KeyCode.DELETE)) {
                deleteItem(selectedItem);
            }
        }
    }


    @FXML
    public void handleClickedListView(){
        TodoItem item= todoListView.getSelectionModel().getSelectedItem();
        itemDetails.setText(item.getDetails());
        deadline.setText(item.getDeadline().toString());
        //    System.out.println("The selected item is : " +item);
//       StringBuilder sb=new StringBuilder(item.getDetails());
//       sb.append("\n\n\n\n");
//       sb.append("Due: ");
//       sb.append(item.getDeadline().toString());
//       itemDetails.setText(sb.toString());
        // itemDetails.setText(item.getDetails());


    }


    public void deleteItem(TodoItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Todo Item");
        alert.setHeaderText("Delete item: " + item.getShortDescription());
        alert.setContentText("Are you sure?  Press OK to confirm, or cancel to Back out.");
        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent() && (result.get() == ButtonType.OK)) {
            TodoData.getInstance().deleteTodoItem(item);
        }

    }
    @FXML
    public void deleteListItem()
    {   TodoItem item= todoListView.getSelectionModel().getSelectedItem();
       deleteItem(item);

    }

    public void editItem(TodoItem item)
    {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Edit Selected Item");
        //headertext
        dialog.setHeaderText("Use this dialog to edit selected todo item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch(IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        DialogueController controller=fxmlLoader.getController();
        controller.displayResult(item);


        Optional<ButtonType> result = dialog.showAndWait();


        if(controller.Empty()==true && result.get()!=ButtonType.CLOSE){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("ALERT !");
            alert.setHeaderText(null);
            if(controller.dateCheck()==true )
                alert.setContentText("Enter Details Again.........");
            else
                alert.setContentText("Enter Correct Date........");
            alert.showAndWait();
            return;

        }

        if(result.isPresent() && result.get()==ButtonType.OK ){
            TodoItem new1= controller.processResult();
            TodoData.getInstance().deleteTodoItem(item);
            todoListView.getSelectionModel().select(new1);
            //loading new items

            System.out.println("Ok Pressed");
        }

        else System.out.println("Please Enter Something");


    }

    @FXML
    void editListItem()
    {   TodoItem item= todoListView.getSelectionModel().getSelectedItem();
       editItem(item);
    }
    @FXML
    public void handleFilterButton() {
        TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
        if(filterToggleButton.isSelected()) {
            filteredList.setPredicate(wantTodaysItems);
            if(filteredList.isEmpty()) {
                itemDetails.clear();
                deadline.setText("");
            } else if(filteredList.contains(selectedItem)) {
                todoListView.getSelectionModel().select(selectedItem);
            } else {
                todoListView.getSelectionModel().selectFirst();
            }
        } else {
            filteredList.setPredicate(wantAllItems);
            todoListView.getSelectionModel().select(selectedItem);
        }
    }



    @FXML
    public void handleExit() {
        Platform.exit();

    }

//    public  void toSpeech(ActionEvent event){
//
//            Audio audio = Audio.getInstance();
//            InputStream sound = null;
//            try { ;
//                sound = audio.getAudio(itemDetails.getText(), Language.ENGLISH);
//            } catch (IOException ex) {
//                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            try {
//                audio.play(sound);
//            } catch (JavaLayerException ex) {
//                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
//            }
//    }
//



}
