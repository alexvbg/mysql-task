package filehostingservice.presentation;

import filehostingservice.entities.Gender;
import filehostingservice.entities.Person;
import filehostingservice.persistence.PersonDAO;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class Controller implements Initializable {

    private final PersonDAO personDAO = new PersonDAO();

    public TableColumn<Person, String> birthDateColumn;
    public TableColumn<Person, String> firstNameColumn;
    public TableColumn<Person, String> genderColumn;
    public TableColumn<Person, String> patronymicColumn;
    public TableColumn<Person, String> secondNameColumn;

    public TableView<Person> personTable;
    public ToggleButton indexToggleButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        firstNameColumn.setCellValueFactory(param -> param.getValue().firstName);
        secondNameColumn.setCellValueFactory(param -> param.getValue().secondName);
        patronymicColumn.setCellValueFactory(param -> param.getValue().patronymic);
        birthDateColumn.setCellValueFactory(param -> param.getValue().birthDate);
        genderColumn.setCellValueFactory(param -> param.getValue().gender);

        //дропаем таблицу и создаем заново
        prepareDataBase();
//        onShowAllPersonsRequest();
    }

    public void prepareDataBase() {
        personDAO.createDataBase();
        onShowAllPersonsRequest();
    }

    public void onShowAllPersonsRequest() {
        personTable
                .getItems()
                .clear();
        personTable
                .getItems()
                .addAll(personDAO.getAllPersons());
    }

    public void onExecuteQueryRequest_1() {
        personTable
                .getItems()
                .clear();
        personTable
                .getItems()
                .addAll(personDAO.executeQuery_1());
}

    public void onExecuteQueryRequest_2() {
        personTable
                .getItems()
                .clear();

        long start = System.currentTimeMillis();
        personTable
                .getItems()
                .addAll(personDAO.executeQuery_2());
        long end = System.currentTimeMillis();

        Alert alert = new Alert(
                Alert.AlertType.NONE,
                "Execution time - " + String.valueOf(TimeUnit.MILLISECONDS.toMillis(end - start)) + " millis",
                ButtonType.YES);
        alert.showAndWait();
    }

    public void onExecuteQueryRequest_3() {
        personDAO.generateOneMillionPersons();
        onShowAllPersonsRequest();
    }

    public void createIndex(){
        String isCreated;
        if (indexToggleButton.isSelected()) {
            personDAO.createIndex();
            isCreated = "Index on column gender created";
        } else {
            personDAO.deleteIndex();
            isCreated = "Index on column gender deleted";
        }
        Alert alert = new Alert(
                Alert.AlertType.WARNING,
                isCreated,
                ButtonType.YES);
        alert.showAndWait();
    }

    public void onAddNewPersonRequest() {
        // Create the custom dialog
        Dialog<Boolean> dialog = new Dialog<>();

        dialog.setTitle("New person");

        ((Stage) dialog
                .getDialogPane()
                .getScene()
                .getWindow())
                .getIcons()
                .add(new Image(getClass().getResource("/addPerson.png").toString()));

        dialog.setHeaderText("Fill the form below to add new person");

        ButtonType createAccountButtonType = new ButtonType("Add new person", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(createAccountButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 45, 10, 10));

        TextField firstNameTextField = new TextField();
        firstNameTextField.setPromptText("first name");

        TextField secondNameTextField = new TextField();
        secondNameTextField.setPromptText("second name");

        TextField patronymicTextField = new TextField();
        patronymicTextField.setPromptText("patronymic");

        ComboBox<Gender> genderComboBox = new ComboBox<>();
        genderComboBox.setItems(FXCollections.observableArrayList(Gender.MALE, Gender.FEMALE));
        genderComboBox.getSelectionModel().select(0);

        DatePicker birthDatePicker = new DatePicker();
        birthDatePicker.setEditable(false);
        birthDatePicker.setValue(LocalDate.now());

        // x, y
        grid.add(firstNameTextField, 1, 0);
        grid.add(new Label("First name: "), 0, 0);

        grid.add(secondNameTextField, 1, 1);
        grid.add(new Label("Second name: "), 0, 1);

        grid.add(patronymicTextField, 1, 2);
        grid.add(new Label("Patronymic: "), 0, 2);

        grid.add(genderComboBox, 1, 3);
        grid.add(new Label("Gender: "), 0, 3);

        grid.add(birthDatePicker, 1, 4);
        grid.add(new Label("Choose birth date: "), 0, 4);

        Node createPersonButton = dialog
                .getDialogPane()
                .lookupButton(createAccountButtonType);

        createPersonButton.setDisable(true);

        ChangeListener<String> createAccountButtonAvailabilityChecker = (observable, oldValue, newValue) -> createPersonButton.setDisable(
                firstNameTextField.getText().trim().isEmpty()
                        || secondNameTextField.getText().trim().isEmpty()
                        || patronymicTextField.getText().trim().isEmpty());

        firstNameTextField.textProperty().addListener(createAccountButtonAvailabilityChecker);
        secondNameTextField.textProperty().addListener(createAccountButtonAvailabilityChecker);
        patronymicTextField.textProperty().addListener(createAccountButtonAvailabilityChecker);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> dialogButton == createAccountButtonType);

        dialog.showAndWait().ifPresent(isDone -> {
            if (isDone) {

                Person person = new Person();

                person.firstName.set(firstNameTextField.getText());
                person.secondName.set(secondNameTextField.getText());
                person.patronymic.set(patronymicTextField.getText());
                person.birthDate.set(birthDatePicker.getValue().toString());
                person.gender.set(genderComboBox.getSelectionModel().getSelectedItem().name());

                if (personDAO.savePerson(person)) personTable.getItems().add(person);
            }
        });
    }

}

