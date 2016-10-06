/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.cClient;
import controller.cContact;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.ws.Service;
import model.Client;
import model.Contact;
import org.controlsfx.dialog.ProgressDialog;
import postagi.Postagi;
import utils.Constants;
import utils.DialogType;
import utils.Utils;

/**
 *
 * @author Omar
 */
public class PostagiLayoutController implements Initializable {

    @FXML
    private TextField tfFrom;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private TextField tfCC;
    @FXML
    private TextField tfTitle;
    @FXML
    private TextField tfAttach;
    @FXML
    private TextArea taContents;
    @FXML
    private HBox hbAttachments;

    @FXML
    private TreeView tvTo;
    @FXML
    private TextField tfTagFilter;
    @FXML
    private HBox hbStatusBar;
    @FXML
    private Label lblStatus;
    @FXML
    private ProgressBar progStatus;

    private final CheckBoxTreeItem<String> rootNode = new CheckBoxTreeItem<>("Customers", Constants.CUSTOMER_ICON);
    private final List<TreeItem<String>> cbTreeItems = new ArrayList<>();

    private final ObservableList<Client> clientsList = FXCollections.observableArrayList();
    private final ObservableList<Contact> contactsList = FXCollections.observableArrayList();
    private final List<File> attachments = new ArrayList<>();
    private BodyPart msgBodyPart = new MimeBodyPart();
    private cClient cclient;
    private cContact ccontact;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cclient = new cClient();
        ccontact = new cContact();
        
        fillClientsList();

        populateTreeView(clientsList);
        //set context menu (according to its type - client or contact -) for the cells.
        tvTo.setCellFactory(
                new Callback<TreeView<String>, CheckBoxTreeCell<String>>() {

            @Override
            public CheckBoxTreeCell<String> call(TreeView<String> param) {
                CheckBoxTreeCell<String> cell = (CheckBoxTreeCell<String>) CheckBoxTreeCell.<String>forTreeView().call(param);
                //create the menu and sub-menus objects
                ContextMenu addMenu = new ContextMenu();
                Menu clientMenu = new Menu("Clients", new ImageView(Constants.CLIENT_ICON));
                Menu contactMenu = new Menu("Contacts", new ImageView(Constants.CONTACT_ICON));
                //create menu items objects
                MenuItem refreshMenuItem = new MenuItem(Constants.REFRESH_MENU_ITEM);
                MenuItem addClientMenuItem = new MenuItem(Constants.ADD_CLIENT_MENU_ITEM);
                MenuItem addContactMenuItem = new MenuItem(Constants.ADD_CONTACT_MENU_ITEM);
                MenuItem editClientMenuItem = new MenuItem(Constants.EDIT_CLIENT_MENU_ITEM);
                MenuItem editContactMenuItem = new MenuItem(Constants.EDIT_CONTACT_MENU_ITEM);
                MenuItem deleteClientMenuItem = new MenuItem(Constants.DELETE_CLIENT_MENU_ITEM);
                MenuItem deleteContactMenuItem = new MenuItem(Constants.DELETE_CONTACT_MENU_ITEM);
                SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();

                //Add icons to the menu items
                refreshMenuItem.setGraphic(new ImageView(Constants.REFRESH_MENU_ITEM_ICON));
                addClientMenuItem.setGraphic(new ImageView(Constants.ADD_MENU_ITEM_ICON));
                addContactMenuItem.setGraphic(new ImageView(Constants.ADD_MENU_ITEM_ICON));
                editClientMenuItem.setGraphic(new ImageView(Constants.EDIT_MENU_ITEM_ICON));
                editContactMenuItem.setGraphic(new ImageView(Constants.EDIT_MENU_ITEM_ICON));
                deleteClientMenuItem.setGraphic(new ImageView(Constants.DELETE_MENU_ITEM_ICON));
                deleteContactMenuItem.setGraphic(new ImageView(Constants.DELETE_MENU_ITEM_ICON));

                EventHandler<ActionEvent> handler = (ActionEvent event) -> {
                    String selectedMenuItem = ((MenuItem) event.getSource()).getText();
                    switch (selectedMenuItem) {
                        case Constants.REFRESH_MENU_ITEM:
                            fillClientsList();
                            populateTreeView(clientsList);
                            break;
                        case Constants.ADD_CLIENT_MENU_ITEM:
                            if (showDialog("ClientLayout.fxml", DialogType.CLIENT, null, null)) {
                                fillClientsList();
                                populateTreeView(clientsList);
                            }
                            break;
                        case Constants.ADD_CONTACT_MENU_ITEM:
                            if (showDialog("ContactLayout.fxml", DialogType.CONTACT, null, null)) {
                                fillClientsList();
                                populateTreeView(clientsList);
                            }
                            break;
                        case Constants.EDIT_CLIENT_MENU_ITEM:
                            fillClientsList();
                            ChoiceDialog<Client> editClientDialog = new ChoiceDialog<>(clientsList.get(0), clientsList);
                            editClientDialog.setTitle("Client Dialog");
                            editClientDialog.setHeaderText("Client to update...");
                            editClientDialog.setContentText("Choose the client to update.");
                            Optional<Client> editClientResult = editClientDialog.showAndWait();
                            editClientResult.ifPresent((client) -> {
                                if (showDialog("ClientLayout.fxml", DialogType.CLIENT, client, null)) {
                                    fillClientsList();
                                    populateTreeView(clientsList);
                                }
                            });
                            break;
                        case Constants.EDIT_CONTACT_MENU_ITEM:
                            fillContactsList();
                            ChoiceDialog<Contact> editContactDialog = new ChoiceDialog<>(contactsList.get(0), contactsList);
                            editContactDialog.setTitle("Contact Dialog");
                            editContactDialog.setHeaderText("Contact to update...");
                            editContactDialog.setContentText("Choose the contact to update.");
                            Optional<Contact> editContactResult = editContactDialog.showAndWait();
                            editContactResult.ifPresent((contact) -> {
                                if (showDialog("ContactLayout.fxml", DialogType.CONTACT, null, contact)) {
                                    fillClientsList();
                                    populateTreeView(clientsList);
                                }
                            });
                            break;
                        case Constants.DELETE_CLIENT_MENU_ITEM:
                            fillClientsList();
                            ChoiceDialog<Client> deleteClientDialog = new ChoiceDialog<>(clientsList.get(0), clientsList);
                            deleteClientDialog.setTitle("Client Dialog");
                            deleteClientDialog.setHeaderText("Client to update...");
                            deleteClientDialog.setContentText("Choose the client to remove.");
                            Optional<Client> deleteClientResult = deleteClientDialog.showAndWait();
                            deleteClientResult.ifPresent((client) -> {
                                if (cclient.delete(client.getId()) == 1) {
                                    fillClientsList();
                                    populateTreeView(clientsList);
                                } else {
                                    Utils.showErrorDialog("Remove Client Failure...", "There is no client with that ID in database...");
                                }
                            });
                            break;
                        case Constants.DELETE_CONTACT_MENU_ITEM:
                            fillContactsList();
                            ChoiceDialog<Contact> deleteContactDialog = new ChoiceDialog<>(contactsList.get(0), contactsList);
                            deleteContactDialog.setTitle("Contact Dialog");
                            deleteContactDialog.setHeaderText("Contact to update...");
                            deleteContactDialog.setContentText("Choose the contact to update.");
                            Optional<Contact> deleteContactResult = deleteContactDialog.showAndWait();
                            deleteContactResult.ifPresent((contact) -> {
                                if (ccontact.delete(contact.getId()) == 1) {
                                    fillClientsList();
                                    populateTreeView(clientsList);
                                } else {
                                    Utils.showErrorDialog("Remove Contact Failure...", "There is no contact with that ID in database...");
                                }
                            });
                            break;
                    }

                };

                //add menu items to the menu
                addMenu.getItems().add(refreshMenuItem);
                addMenu.getItems().add(separatorMenuItem);
                addMenu.getItems().add(clientMenu);
                addMenu.getItems().add(contactMenu);
                
                //add menu items of client to client sub-menu.
                clientMenu.getItems().add(addClientMenuItem);
                clientMenu.getItems().add(editClientMenuItem);
                clientMenu.getItems().add(deleteClientMenuItem);
                
                //add menu items of contact to contact sub-menu.
                contactMenu.getItems().add(addContactMenuItem);
                contactMenu.getItems().add(editContactMenuItem);
                contactMenu.getItems().add(deleteContactMenuItem);
                
                //assign the event handler to the menu items
                refreshMenuItem.setOnAction(handler);
                addClientMenuItem.setOnAction(handler);
                addContactMenuItem.setOnAction(handler);
                editClientMenuItem.setOnAction(handler);
                editContactMenuItem.setOnAction(handler);
                deleteClientMenuItem.setOnAction(handler);
                deleteContactMenuItem.setOnAction(handler);
                
                //add the context menu to the cell
                cell.setContextMenu(addMenu);

                return cell;
            }

            /**
             * Show the dialog according to the type (client or contact)
             *
             * @param url the resource of fxml file that should be loaded
             * @param flag the type of dialog (dialog for client or for contact)
             * @return true when either button of save (save and quit or save
             * and add new ) is clicked, otherwise returns false.
             */
            private boolean showDialog(String url, DialogType flag, Client updateClient, Contact updateContact) {
                FXMLLoader loader = new FXMLLoader();
                Stage dialogeStage = new Stage();

                try {
                    loader.setLocation(Postagi.class.getResource(url));
                    AnchorPane page = (AnchorPane) loader.load();

                    dialogeStage.initModality(Modality.WINDOW_MODAL);
                    dialogeStage.initOwner(Postagi.mainStage);
                    dialogeStage.initStyle(StageStyle.UNDECORATED);
                    dialogeStage.setY(Postagi.mainStage.getY() + 40);
                    dialogeStage.setX(Postagi.mainStage.getX() + Postagi.mainStage.getWidth());

                    Scene scene = new Scene(page);
                    dialogeStage.setScene(scene);

                    switch (flag) {
                        case CLIENT:
                            // create the controller of client layout controller
                            ClientLayoutController clientController = loader.getController();
                            clientController.setDialogStage(dialogeStage);
                            if (updateClient != null) {
                                clientController.showEditableClient(updateClient);
                            }

                            // Show the dialog and wait until the user closes it
                            dialogeStage.showAndWait();

                            return clientController.saveClicked;
                        case CONTACT:
                            // create the controller of contact layout controller
                            ContactLayoutController contactController = loader.getController();
                            contactController.setDialogStage(dialogeStage);
                            if (updateContact != null) {
                                contactController.showEditableContact(updateContact);
                            }

                            // Show the dialog and wait until the user closes it
                            dialogeStage.showAndWait();

                            return contactController.isSaveClicked;
                    }

                } catch (IOException ex) {
                    Logger.getLogger(PostagiLayoutController.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                }
                return false;
            }
        });
    }

    /**
     * Display a file chooser to select attachments
     *
     * @param event
     */
    @FXML
    public void browse(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Attachments...");
        File selectedFile = fileChooser.showOpenDialog(Postagi.mainStage);
        if (selectedFile != null) {
            attachments.add(selectedFile);
            createAttachment(selectedFile);
        }

    }

    /**
     * Send message event handler of Send button
     *
     * @param event
     */
    @FXML
    public void sendHandler(ActionEvent event) {
        //create service to send mails in background and show progress dialog for the user
        javafx.concurrent.Service<Void> service = new javafx.concurrent.Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    int mailsSize = getSelectedMails().size();

                    @Override
                    protected Void call() throws Exception {
                        //update the progress dialog message
                        updateMessage("Sending mails . . .");
                        //update the progress of progress dialog from 0 to size of mails
                        updateProgress(0, mailsSize);
                        //loop on mails and send them one by one and update the progress dialog.
                        //to show how many mail is sent.
                        for (int i = 0; i < mailsSize; i++) {
                            if (sendMail(getSelectedMails().get(i))) {
                                updateProgress(i + 1, mailsSize);
                                updateMessage("send " + (i + 1) + "/" + mailsSize + " mails!");
                            }
                        }
                        updateMessage("Send all.");
                        Thread.sleep(2000);
                        return null;
                    }
                };
            }
        };
        //close the service on succeed
        service.setOnSucceeded(v -> {
            //hide the statusbar when done.
            hbStatusBar.setVisible(false);
        });
        //show the status bar when sending mails.
        hbStatusBar.setVisible(true);
        
        //bind the text and progress of status bar with message and progress of service
        lblStatus.textProperty().unbind();
        lblStatus.textProperty().bind(service.messageProperty());
        progStatus.progressProperty().unbind();
        progStatus.progressProperty().bind(service.progressProperty());
        
        //start the service.
        service.start();

    }

    /**
     * Filter the collection of clients and its contacts with the tag of client.
     * So, only clients with specified tag written in tag text field is
     * displayed in the tree view with the helper method populateTVTO().
     *
     * @param event
     */
    @FXML
    public void filter(ActionEvent event) {
        ObservableList<Client> tagedClients = FXCollections.observableArrayList();

        String filterQuery = (tfTagFilter.getText().isEmpty())
                ? "none"
                : tfTagFilter.getText().toLowerCase();

        clientsList.stream().filter((client) -> {
            return (filterQuery.equalsIgnoreCase("none"))
                    ? true
                    : client.getTags().toLowerCase().contains(filterQuery);

        }).forEach((client) -> tagedClients.add(client));

        populateTreeView(tagedClients);
    }

    /**
     * Populate the TreeView with the data from DB
     */
    private void populateTreeView(ObservableList<Client> clients) {
        rootNode.getChildren().clear();
        rootNode.setExpanded(true);

        clients.stream().forEach((client) -> {
            CheckBoxTreeItem<String> clientNode
                    = new CheckBoxTreeItem<>(client.getName(), new ImageView(Constants.CLIENT_ICON));

            rootNode.getChildren().add(clientNode);

            List<String> clientMails = extractMails(client.getEmails());
            clientMails.stream().map((mail)
                    -> new CheckBoxTreeItem<>(mail, new ImageView(Constants.MAIL_ICON)))
                    .forEach((mailLeaf) -> {
                        clientNode.getChildren().add(mailLeaf);
                        cbTreeItems.add(mailLeaf);
                    });

            contactsList.clear();
            contactsList.addAll(cclient.getContacts(client.getId()));

            contactsList.stream().forEach((contact) -> {
                List<String> contactMails = extractMails(contact.getMails());
                CheckBoxTreeItem<String> contactNode
                        = new CheckBoxTreeItem<>(contact.getName(), new ImageView(Constants.CONTACT_ICON));

                clientNode.getChildren().add(contactNode);
                contactMails.stream().map((mail)
                        -> new CheckBoxTreeItem<>(mail, new ImageView(Constants.MAIL_ICON)))
                        .forEach((mailLeaf) -> {
                            contactNode.getChildren().add(mailLeaf);
                            cbTreeItems.add(mailLeaf);
                        });
            });
        });

        tvTo.setRoot(rootNode);
    }

    /**
     * Extract mails from the field Mails in DB (as they are separated with ";")
     *
     * @param eMails the string of mails
     * @return list of mails exist in the mails string
     */
    private List<String> extractMails(String eMails) {
        List<String> mails = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(eMails, ";");
        while (tokenizer.hasMoreTokens()) {
            mails.add(tokenizer.nextToken().trim());
        }
        return mails;
    }

    /**
     * Get the selected mails in the TreeView control
     *
     * @return list of the selected mails
     */
    private List<String> getSelectedMails() {
        List<String> mails = new ArrayList<>();
        if (!cbTreeItems.isEmpty()) {
            cbTreeItems.stream().filter((cbTreeItem)
                    -> (((CheckBoxTreeItem<String>) cbTreeItem)
                    .selectedProperty().get()))
                    .forEach((cbTreeItem) -> {

                        mails.add(cbTreeItem.getValue());

                    });
        }

        return mails;
    }

    /**
     * Create the mail parts from controls after validating them. The value is
     * list because "To" and "CC" parts could be group of mails.
     *
     * We use Optional class, to avoid the null or empty map.
     *
     * @return Optional Object of map with key as the part name of message, and
     * the value is from the input control.
     */
    private Optional<Map<String, List<String>>> createMail() {
        Map<String, List<String>> mailParts = null;
        if (validate()) {
            mailParts = new HashMap<>();
            mailParts.put("from", Arrays.asList(tfFrom.getText()));
            mailParts.put("password", Arrays.asList(pfPassword.getText()));
            mailParts.put("To", getSelectedMails());
            mailParts.put("cc", (tfCC.getText().isEmpty())
                    ? Arrays.asList()
                    : Arrays.asList(tfCC.getText()));
            mailParts.put("subject", Arrays.asList(tfTitle.getText()));
            mailParts.put("content", Arrays.asList(taContents.getText()));
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Please, Fill all fields!", ButtonType.OK);
            alert.setHeaderText("Required Fields...");
            alert.showAndWait();
        }
        return Optional.ofNullable(mailParts);
    }

    /**
     * Validate the input before using them, as they are shouldn't be empty.
     * While the content and the attachments parts could be empty, they don't be
     * validated here.
     *
     * @return
     */
    private boolean validate() {
        return !(tfFrom.getText().isEmpty() || pfPassword.getText().isEmpty()
                || tfTitle.getText().isEmpty() || getSelectedMails().isEmpty());
    }

    /**
     * Create a standalone control for new attachment. With functionality of
     * Opening the file with associated application for the extension in the
     * platform (windows, Linux,...), also, can remove the object from the
     * window and from attachments list.
     *
     * @param selectedFile to be attached
     */
    private void createAttachment(File selectedFile) {
        HBox container = new HBox();
        container.getStyleClass().add("flat_button");
        container.setSpacing(3);

        ImageView deleteIcon = new ImageView(Constants.INACTIVE_DELETE_ICON);
        deleteIcon.setId("deleteIcon");

        deleteIcon.setOnMouseClicked((event) -> {
            attachments.remove(selectedFile);
            hbAttachments.getChildren().remove(container);
        });

        Button btnAttachment = new Button(selectedFile.getName());
        btnAttachment.getStyleClass().add("attachment_button");

        btnAttachment.setOnAction((ActionEvent event) -> {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop desktop = Desktop.getDesktop();
                    desktop.open(selectedFile);
                } catch (IOException ex) {
                    Logger.getLogger(PostagiLayoutController.class.getName()).log(Level.SEVERE, null, ex);
                    Utils.showErrorDialog("Open File Error...", "There is no supported application to open the file!");
                }
            }
        });

        Tooltip tooltip = new Tooltip();
        container.setOnMouseEntered((event) -> {
            Node source = (Node) event.getSource();
            tooltip.setText(btnAttachment.getText());
            Tooltip.install(source, tooltip);
        });
        container.setOnMouseExited((event) -> {
            tooltip.hide();
        });

        container.getChildren().addAll(btnAttachment, deleteIcon);

        hbAttachments.getChildren().add(container);
    }

    /**
     * Prepare Internet addresses array of mails (CC or TO mails)
     *
     * @param toList the list of mails
     * @return array of InternetAddress
     */
    private InternetAddress[] prepareMailsList(List<String> mailStringList) {
        InternetAddress[] addresses = new InternetAddress[mailStringList.size()];
        if (!mailStringList.isEmpty()) {
            for (int i = 0; i < mailStringList.size(); i++) {
                try {
                    addresses[i] = new InternetAddress(mailStringList.get(i));
                } catch (AddressException ex) {
                    Logger.getLogger(PostagiLayoutController.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
                }
            }
        }
        return addresses;
    }

    /**
     * Send mail code using JavaMail API
     */
    private boolean sendMail(String to) {
        Properties props = new Properties();
        props.put("mail.smtp.host", Constants.HOST);
        props.put("mail.smtp.auth", true);

        Optional<Map<String, List<String>>> parts = createMail();
        try {
            if (parts.isPresent()) {
                //Extract parts from the map.
                final String FROM = parts.get().get("from").get(0);
                final String PASSWORD = parts.get().get("password").get(0);
                final String SUBJECT = parts.get().get("subject").get(0);
                final String CONTENT = parts.get().get("content").get(0);

                //create the Address array for the reciepents CC addresses, and fill it
                InternetAddress[] addressesCC = prepareMailsList(parts.get().get("cc"));

                //create the Address array for the reciepents addresses, and fill it
                //InternetAddress[] addressesTo = prepareMailsList(getSelectedMails());
                //check for null addresses array.
                if (addressesCC == null) {
                    Utils.showErrorDialog("Message Error...", "Error in creating CC address)!");
                    return false;
                }

                //Create the session for sending the message.
                Session session = Session.getDefaultInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        //authenticate the sender account with user and password
                        return new PasswordAuthentication(FROM, PASSWORD);
                    }
                });
                //create the message object to be sent
                MimeMessage message = new MimeMessage(session);
                //set the parts of the message
                message.setFrom(new InternetAddress(FROM));
                message.addRecipients(Message.RecipientType.TO, to);
                message.addRecipients(Message.RecipientType.CC, addressesCC);
                message.setSubject(SUBJECT);
                //Set the body of the message

                msgBodyPart.setText(CONTENT);

                Multipart multiPart = new MimeMultipart();
                multiPart.addBodyPart(msgBodyPart);
                //Add the attachments
                if (!attachments.isEmpty()) {
                    attachments.stream().filter(File::exists).forEach((file) -> {
                        msgBodyPart = new MimeBodyPart();
                        try {
                            DataSource dataSource = new FileDataSource(file);
                            msgBodyPart.setDataHandler(new DataHandler(dataSource));
                            msgBodyPart.setFileName(file.getName());
                            multiPart.addBodyPart(msgBodyPart);
                        } catch (MessagingException ex) {
                            Logger.getLogger(PostagiLayoutController.class.getName()).log(Level.SEVERE, null, ex);
                            Utils.showExceptionDialog("Message Error...", "Exception happen while creating the attachment!", ex.toString());
                        }
                    });

                }

                message.setContent(multiPart);
                //send the message
                Transport.send(message);
                return true;
            }
        } catch (MessagingException ex) {
            Logger.getLogger(PostagiLayoutController.class.getName()).log(Level.SEVERE, null, ex);
            Utils.showExceptionDialog("Message Failure...", "Exception happen while send message!", ex.toString());
            return false;
        }
        return false;
    }

    

    /**
     * Fill the list of clients and show error dialog if happens while
     * retrieving data from DB
     */
    private void fillClientsList() {
        try {
            clientsList.setAll(cclient.getAll());
        } catch (SQLException ex) {
            Utils.showErrorDialog("Database Error...", ex.toString());
        }
    }
    
    /**
     * Fill the list of clients and show error dialog if happens while
     * retrieving data from DB
     */
    private void fillContactsList() {
        try {
            contactsList.setAll(ccontact.getAll());
        } catch (SQLException ex) {
            Utils.showErrorDialog("Database Error...", ex.toString());
        }
    }

    
}
