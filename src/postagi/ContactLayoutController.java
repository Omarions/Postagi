/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package postagi;

import controller.cClient;
import controller.cContact;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Client;
import model.Contact;

/**
 * FXML Controller class
 *
 * @author Omar
 */
public class ContactLayoutController implements Initializable {

    public boolean isSaveClicked = false;
    
    @FXML
    private TextField tfName;
    @FXML
    private TextField tfTitle;
    @FXML
    private TextField tfMails;
    @FXML
    private TextField tfMobiles;
    @FXML
    private TextField tfWhatsapp;
    @FXML
    private TextField tfSkype;
    @FXML
    private TextField tfOthers;
    @FXML
    private ComboBox<Client> cbClient;
    
    private Stage stage;
    private cContact mcContact;
    private cClient mcClient;
    private Client selectedClient;
    private final ObservableList<Client> clients = FXCollections.observableArrayList();
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //create the objects of controllers to communicate with DB
        mcContact = new cContact();
        mcClient = new cClient();
        
        clients.clear();
        clients.addAll(mcClient.getAll());
        
        cbClient.getItems().addAll(clients);
        //cbClient.getSelectionModel().select(clients.get(0));
        //System.out.println("cbClients items = " + cbClient.getItems().size());
        System.out.println("");
    }  
    
    /**
     * Set the stage of the current window, 
     * so we can close it or hide and show again for various operation in the window.
     * @param stage the stage of current window
     */
    public void setDialogStage(Stage stage){
        this.stage = stage;
    }
    
    /**
     * Event Handler of buttons for save (save and quit) and (save and add new)
     * according to the button clicked, the operation will be performed
     * close the window or clear for new input data.
     * @param event 
     */
    @FXML
    public void saveHandler(ActionEvent event){
        
        Button btnSave = (Button) event.getSource();
        if(btnSave.getText().equalsIgnoreCase("save & quit")){
            saveContact();
            stage.close();
            isSaveClicked = true;
        }else if (btnSave.getText().equalsIgnoreCase("save & add new")){
            saveContact();
            clearInputs();
            stage.hide();
            isSaveClicked = true;
            stage.show();
        }
        
    }
    
    /**
     * Event Handler of Cancel button
     * @param event 
     */
    @FXML
    public void cancelHandler(ActionEvent event){
        isSaveClicked = false;
        stage.close();
    }
    
    @FXML
    public void cbClientsChanged(ActionEvent event){
        //System.out.println("Changed");
        //System.out.println(cbClient.getValue());
    }
    
    /**
     * Build the client object from the input controls. It returns optional object
     * of type client with the client object created from input data 
     * or empty optional object in case of missing inputs.
     * @return Optional object of type client.
     */
    private Optional<Contact> buildContact(){
        String title = (tfTitle.getText().isEmpty())? "N/A" : tfTitle.getText();
        String mobiles = (tfMobiles.getText().isEmpty())? "N/A" : tfMobiles.getText();
        String whatsapp = (tfWhatsapp.getText().isEmpty())? "N/A" : tfWhatsapp.getText();
        String skype = (tfSkype.getText().isEmpty())? "N/A" : tfSkype.getText();
        String others = (tfOthers.getText().isEmpty())? "N/A" : tfOthers.getText();
        
        if(validate()){
            Contact mContact = new Contact();
            mContact.setId(0);
            mContact.setName(tfName.getText());
            mContact.setTitle(title);
            mContact.setMobiles(mobiles);
            mContact.setMails(tfMails.getText());
            mContact.setWhatsapp(whatsapp);
            mContact.setSkype(skype);
            mContact.setOthers(others);
            mContact.setClient(cbClient.getValue());
            
            return Optional.of(mContact);
        }else{
            return Optional.empty();
        }
        
    }
    
    /**
     * Save the client to the DB 
     * and alert user with success or missing required fields
     */
    private void saveContact(){
        //create the optional object of client from the method
        Optional<Contact> contact = buildContact();
        //check if the client is present, save to db and exit,
        //otherwise (the optional is empty) show alert message for the required fields.
        if(contact.isPresent()){
            mcContact.insert(contact.get());
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "The contact saved successfully!", ButtonType.OK);
            alert.setHeaderText("Save Contact...");
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR, "Name and Emails are required fields!", ButtonType.OK);
            alert.setHeaderText("Required Fields Error...");
            alert.showAndWait();
        }
    }
    
    /**
     * Validate the inputs for required fields
     * @return 
     */
    private boolean validate(){
        return !(tfName.getText().isEmpty() || tfMails.getText().isEmpty() 
                );
    }
    
    /**
     * Clear all input controls to add new client data
     */
    private void clearInputs(){
        tfName.setText("");
        tfTitle.setText("");
        tfMobiles.setText("");
        tfMails.setText("");
        tfWhatsapp.setText("");
        tfSkype.setText("");
        tfSkype.setText("");
        cbClient.getSelectionModel().clearSelection();
    }
}