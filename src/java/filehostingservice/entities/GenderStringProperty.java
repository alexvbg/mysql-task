package filehostingservice.entities;

import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;

/**
 * Created by Администратор on 03.02.2017.
 */
public class GenderStringProperty extends SimpleStringProperty {

    public void set(Boolean gender) {
        if (gender == true) {
            this.set(Gender.MALE.name());
        } else {
            this.set(Gender.FEMALE.name());
        }
    }

    @Override
    public String getValue() {
        String gender = super.getValue();
        String b = new String();
        try {
            if (gender.equals(Gender.MALE.name())) {
                b = "1";
            } else if (gender.equals(Gender.FEMALE.name())) {
                b = "0";
            } else {
                throw new IOException("Type cast exeption");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

}
