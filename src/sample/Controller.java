package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import sample.datamodel.Contact;
import sample.datamodel.ContactData;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static sample.datamodel.ContactData.CONNECTION_STRING;

public class Controller {
    @FXML
    private BorderPane mainPanel;

    private ContactData data;

    @FXML
    private TableView<Contact> contactsTable;

    @FXML
    public void initialize() {
        data = new ContactData();
        data.loadContacts();
        contactsTable.setItems(data.getContacts());
        contactsTable.getSelectionModel().selectFirst();
    }

    @FXML
    public void showAddContactDialog() throws SQLException {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPanel.getScene().getWindow());
        dialog.setTitle("Add new contact");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("contactdialog.fxml"));
        loadDialog(dialog, fxmlLoader);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            Connection connection = DriverManager.getConnection(CONNECTION_STRING);
            Statement statement = connection.createStatement();

            ContactController contactController = fxmlLoader.getController();
            Contact newContact = contactController.getNewContact(statement);
            data.addContact(statement, newContact);
            contactsTable.getSelectionModel().select(newContact);
            contactsTable.scrollTo(newContact);
            statement.close();
            connection.close();
        }
    }

    @FXML
    public void showEditContactDialog() throws SQLException {
        Contact selectedContact = contactsTable.getSelectionModel().getSelectedItem();
        if(selectedContact == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No contact selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select the contact you want to edit.");
            alert.showAndWait();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPanel.getScene().getWindow());
        dialog.setTitle("Edit contact");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("contactdialog.fxml"));
        loadDialog(dialog, fxmlLoader);
        ContactController contactController = fxmlLoader.getController();
        contactController.editContact(selectedContact);
        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            Connection connection = DriverManager.getConnection(CONNECTION_STRING);
            Statement statement = connection.createStatement();
            data.updateContact(statement, contactController, selectedContact);
            contactsTable.getSelectionModel().select(selectedContact);
            statement.close();
            connection.close();
        }
    }

    @FXML
    private void loadDialog(Dialog<ButtonType> dialog, FXMLLoader fxmlLoader) {
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog.");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
    }

    @FXML
    public void deleteContact() throws SQLException {
        Contact selectedContact = contactsTable.getSelectionModel().getSelectedItem();
        if(selectedContact == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No contact selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select the contact you want to delete.");
            alert.showAndWait();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete contact");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure to delete contact: " + selectedContact.getFirstName() + " " +
                selectedContact.getLastName() + "?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            Connection connection = DriverManager.getConnection(CONNECTION_STRING);
            Statement statement = connection.createStatement();
            data.deleteContact(statement, selectedContact);
            statement.close();
            connection.close();
        }

    }

    @FXML
    public void handleKeyPressed(KeyEvent keyEvent) throws SQLException {
        Contact selectedContact = contactsTable.getSelectionModel().getSelectedItem();
        if(selectedContact != null)
            if(keyEvent.getCode().equals(KeyCode.DELETE))
                deleteContact();
    }
}
