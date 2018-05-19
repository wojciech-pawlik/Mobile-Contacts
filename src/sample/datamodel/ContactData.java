package sample.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.ContactController;

import java.sql.*;

public class ContactData {
    private static final String DB_NAME = "contacts.db";
    public static final String CONNECTION_STRING = "jdbc:sqlite:C:\\Users\\naalb\\Desktop\\Programowanie\\JAVA\\Contacts\\" + DB_NAME;

    public static final String TABLE_CONTACTS = "contacts";

    public static final String _ID = "_id";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String NOTES = "notes";

    private ObservableList<Contact> contacts;

    public ContactData() {
        this.contacts = FXCollections.observableArrayList();
    }

    public ObservableList<Contact> getContacts() {
        return contacts;
    }

    public void addContact(Statement statement, Contact contact) throws SQLException {
        this.contacts.add(contact);
        insertContact(statement, contact);
    }

    public void loadContacts() {
        contacts = FXCollections.observableArrayList();
        try {
            Connection connection = DriverManager.getConnection(CONNECTION_STRING);
            Statement statement = connection.createStatement();

            statement.execute("CREATE TABLE IF NOT EXISTS " + TABLE_CONTACTS + " (" +
                    _ID + " INT NOT NULL, " +
                    FIRST_NAME + " TEXT, " +
                    LAST_NAME + " TEXT, " +
                    PHONE_NUMBER + " TEXT, " +
                    NOTES + " TEXT)");

            ResultSet results = statement.executeQuery("SELECT * FROM " + TABLE_CONTACTS + " ORDER BY " + _ID);
            while(results.next()) {
                Contact contact = new Contact(results.getInt(_ID), results.getString(FIRST_NAME), results.getString(LAST_NAME),
                        results.getString(PHONE_NUMBER), results.getString(NOTES));
                contacts.add(contact);
            }

            statement.close();
            connection.close();
        } catch(SQLException e) {
            System.out.println("Something went wrong. " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void insertContact(Statement statement, Contact contact) throws SQLException {
        insertContact(statement, contact.getId(), contact.getFirstName(), contact.getLastName(), contact.getPhoneNumber(), contact.getNotes());
    }

    private void insertContact(Statement statement, int id, String firstName, String lastName, String phoneNumber, String notes)
            throws SQLException {
        statement.execute("INSERT INTO " + TABLE_CONTACTS + " (" +
                _ID + ", " +
                FIRST_NAME + ", " +
                LAST_NAME + ", " +
                PHONE_NUMBER + ", " +
                NOTES + ") " +
                "VALUES (" + id + ", '" + firstName + "', '" + lastName + "', '" + phoneNumber + "', '" + notes + "')");
    }

    public void updateContact(Statement statement, ContactController contactController, Contact contact) throws SQLException {
        contact = contactController.updateContact(statement, contact);

        statement.execute("UPDATE " + TABLE_CONTACTS + " SET " +
                FIRST_NAME + " = '" + contact.getFirstName() + "', " +
                LAST_NAME + " = '" + contact.getLastName() + "', " +
                PHONE_NUMBER + " = '" + contact.getPhoneNumber() + "', " +
                NOTES + " = '" + contact.getNotes() +
                "' WHERE " + _ID + " = " + contact.getId() + ";"
        );
        contacts.remove(contact.getId());
        contacts.set(contact.getId(), contact);
    }

    public void deleteContact(Statement statement, Contact contact) throws SQLException {
        contacts.remove(contact);
        statement.execute("DELETE FROM " + TABLE_CONTACTS + " WHERE " + _ID + " = " + contact.getId() + ";");
    }
}
