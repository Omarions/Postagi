/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.cClient;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Client;

/**
 * FXML Controller class
 *
 * @author Omar
 */
public class ClientLayoutController implements Initializable {

    public boolean saveClicked = false;

    @FXML
    private TextField tfName;
    @FXML
    private TextField tfAddress;
    @FXML
    private TextField tfWebsite;
    @FXML
    private TextField tfMails;
    @FXML
    private TextField tfTel;
    @FXML
    private TextField tfFax;
    @FXML
    private TextField tfTags;

    private Stage stage;
    private cClient mCClient;
    private Client editableClient;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //create the object of cClient controller to communicate with DB
        mCClient = new cClient();
    }

    /**
     * Set the stage of the current window, so we can close it or hide and show
     * again for various operation in the window.
     *
     * @param stage the stage of current window
     */
    public void setDialogStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Show the client to be updated in controls
     * @param client to be updated
     */
    public void showEditableClient(Client client) {
        editableClient = client;
        populateWindow(client);
    }

    /**
     * Event Handler of buttons for save (save and quit) and (save and add new)
     * according to the button clicked, the operation will be performed close
     * the window or clear for new input data.
     *
     * @param event
     */
    @FXML
    public void saveHandler(ActionEvent event) {

        Button btnSave = (Button) event.getSource();
        if (btnSave.getText().equalsIgnoreCase("save & quit")) {
            saveClient();
            stage.close();
            saveClicked = true;
        } else if (btnSave.getText().equalsIgnoreCase("save & add new")) {
            saveClient();
            clearInputs();
            stage.hide();
            saveClicked = true;
            stage.show();
        }

    }

    /**
     * Event Handler of Cancel button
     *
     * @param event
     */
    @FXML
    public void cancelHandler(ActionEvent event) {
        saveClicked = false;
        stage.close();
    }

    /**
     * Build the client object from the input controls. It returns optional
     * object of type client with the client object created from input data or
     * empty optional object in case of missing inputs.
     *
     * @return Optional object of type client.
     */
    private Optional<Client> buildClient() {
        int id = (editableClient != null)? editableClient.getId() : 0;
        String address = (tfAddress.getText().isEmpty()) ? "N/A" : tfAddress.getText();
        String website = (tfWebsite.getText().isEmpty()) ? "N/A" : tfWebsite.getText();
        String mails = (tfMails.getText().isEmpty()) ? "N/A" : tfMails.getText();
        String tel = (tfTel.getText().isEmpty()) ? "N/A" : tfTel.getText();
        String fax = (tfFax.getText().isEmpty()) ? "N/A" : tfFax.getText();
        String tags = (tfTags.getText().isEmpty()) ? "N/A" : tfTags.getText();

        if (validate()) {
            Client mClient = new Client();
            mClient.setId(id);
            mClient.setName(tfName.getText());
            mClient.setAddress(address);
            mClient.setWebsite(website);
            mClient.setEmails(mails);
            mClient.setTel(tel);
            mClient.setFax(fax);
            mClient.setTags(tags);

            return Optional.of(mClient);
        } else {
            return Optional.empty();
        }

    }

    /**
     * Save the client to the DB and alert user with success or missing required
     * fields
     */
    private void saveClient() {
        //create the optional object of client from the method
        Optional<Client> client = buildClient();
        //check if the client is present, save to db and exit,
        //otherwise (the optional is empty) show alert message for the required fields.
        if(!client.isPresent()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Name is required field!", ButtonType.OK);
            alert.setHeaderText("Required Fields Error...");
            alert.showAndWait();
        }
        if (editableClient == null) {
            client.ifPresent((mClient) -> {
                mCClient.insert(mClient);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "The client saved successfully!", ButtonType.OK);
                alert.setHeaderText("Save Client...");
                alert.showAndWait();
            });
        } else {
            client.ifPresent((mClient) -> {
                mCClient.update(mClient);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "The client saved successfully!", ButtonType.OK);
                alert.setHeaderText("Save Client...");
                alert.showAndWait();
            });
        }

        
    }

    /**
     * Validate the inputs for required fields
     *
     * @return
     */
    private boolean validate() {
        if (tfName.getText().isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Clear all input controls to add new client data
     */
    private void clearInputs() {
        tfName.setText("");
        tfAddress.setText("");
        tfWebsite.setText("");
        tfMails.setText("");
        tfTel.setText("");
        tfFax.setText("");
        tfTags.setText("");
    }

    /**
     * Populate the window controls with client data
     * @param client to be shown
     */
    private void populateWindow(Client client) {
        tfName.setText(client.getName());
        tfAddress.setText(client.getAddress());
        tfMails.setText(client.getEmails());
        tfWebsite.setText(client.getWebsite());
        tfTel.setText(client.getTel());
        tfFax.setText(client.getFax());
        tfTags.setText(client.getTags());
        
    }
}
