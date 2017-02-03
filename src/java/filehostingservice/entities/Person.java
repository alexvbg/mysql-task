package filehostingservice.entities;

import javafx.beans.property.SimpleStringProperty;

public class Person {

    private long id;

    public final SimpleStringProperty firstName = new SimpleStringProperty();
    public final SimpleStringProperty secondName = new SimpleStringProperty();
    public final SimpleStringProperty patronymic = new SimpleStringProperty();
//    public final SimpleStringProperty gender = new SimpleStringProperty();
    public final GenderStringProperty gender = new GenderStringProperty();
    public final SimpleStringProperty birthDate = new SimpleStringProperty();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}


