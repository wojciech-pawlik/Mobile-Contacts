package sample;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import sample.datamodel.Contact;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static sample.datamodel.ContactData.*;


public class ContactController {
    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextField notesField;

    @FXML
    public Contact getNewContact(Statement statement) throws SQLException {
        int id = firstFreeId(statement);
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String phoneNumber = phoneNumberField.getText();
        String notes = notesField.getText();

        Contact newContact = new Contact(id, firstName, lastName, phoneNumber, notes);
        return newContact;
    }

    public void editContact(Contact contact) {
        firstNameField.setText(contact.getFirstName());
        lastNameField.setText(contact.getLastName());
        phoneNumberField.setText(contact.getPhoneNumber());
        notesField.setText(contact.getNotes());
    }

    public void updateContact(Statement statement, Contact contact) throws SQLException {
        contact.setFirstName(firstNameField.getText());
        contact.setLastName(lastNameField.getText());
        contact.setPhoneNumber(phoneNumberField.getText());
        contact.setNotes(notesField.getText());

        statement.execute("UPDATE " + TABLE_CONTACTS + " SET " +
                FIRST_NAME + " = '" + firstNameField.getText() + "', " +
                LAST_NAME + " = '" + firstNameField.getText() + "', " +
                PHONE_NUMBER + " = '" + firstNameField.getText() + "', " +
                NOTES + " = '" + firstNameField.getText() +
                "' WHERE " + _ID + " = " + contact.getId() + ";"
                );
    }

    public int firstFreeId(Statement statement) throws SQLException {
        int index = 0;
        ResultSet results = statement.executeQuery("SELECT " + _ID + " FROM " + TABLE_CONTACTS);
        while(results.next()) {
            if(results.getInt(_ID) == index)
                index++;
            else
                return index;
        }
        return ++index;
    }

    public int firstBusyId(Statement statement) throws SQLException {
        ResultSet results = statement.executeQuery("SELECT " + _ID + " FROM " + TABLE_CONTACTS);
        return results.getInt(_ID);
    }
}
