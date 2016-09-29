/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package postagi;

import controller.cClient;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
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
import model.Client;
import model.Contact;

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

    final String HOST = "mail.ngc-eg.com";

    private final Node customersIcon = new ImageView(
            new Image(getClass().getResourceAsStream("/images/customers_32.png")));
    private final Image mailsIcon
            = new Image(getClass().getResourceAsStream("/images/mails.png"));
    private final Image contactsIcon
            = new Image(getClass().getResourceAsStream("/images/contacts.png"));
    private final Image clientsIcon
            = new Image(getClass().getResourceAsStream("/images/clients.png"));
    private final Image activeDeleteIcon
            = new Image(getClass().getResourceAsStream("/images/Delete_active.png"));
    private final Image inactiveDeleteIcon
            = new Image(getClass().getResourceAsStream("/images/Delete_inactive.png"));

    private final CheckBoxTreeItem<String> rootNode = new CheckBoxTreeItem<>("Customers", customersIcon);

    private final ObservableList<Client> clientsList = FXCollections.observableArrayList();
    private final ObservableList<Contact> contacts = FXCollections.observableArrayList();
    private final List<TreeItem<String>> cbTreeItems = new ArrayList<>();
    private final List<File> attachments = new ArrayList<>();

    private cClient cclient;
    boolean clientClicked = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cclient = new cClient();
        clientsList.addAll(cclient.getAll());

        populateTreeView(clientsList);
        //set context menu (according to its type - client or contact -) for the cells.
        tvTo.setCellFactory(
                new Callback<TreeView<String>, CheckBoxTreeCell<String>>() {

            @Override
            public CheckBoxTreeCell<String> call(TreeView<String> param) {
                CheckBoxTreeCell<String> cell = (CheckBoxTreeCell<String>) CheckBoxTreeCell.<String>forTreeView().call(param);
                ContextMenu addMenu = new ContextMenu();
                MenuItem refresh = new MenuItem("Refresh");
                MenuItem addClient = new MenuItem("Add Client");
                MenuItem addContact = new MenuItem("Add Contact");

                EventHandler<ActionEvent> handler = (ActionEvent event) -> {
                    String selectedMenuItem = ((MenuItem) event.getSource()).getText();
                    if (selectedMenuItem.equalsIgnoreCase("refresh")) {
                        clientsList.clear();
                        clientsList.addAll(cclient.getAll());
                        populateTreeView(clientsList);
                    } else if (selectedMenuItem.equalsIgnoreCase("add client")) {
                        if (showDialog("ClientLayout.fxml")) {
                            clientsList.clear();
                            clientsList.addAll(cclient.getAll());
                            populateTreeView(clientsList);
                        }
                    } else if (selectedMenuItem.equalsIgnoreCase("add contact")) {
                        //showDialog("ContactLayout.fxml");
                    }

                };

                if (cell.getTreeItem() != null) {
                    if (!cell.getTreeItem().isLeaf()
                            && cell.getTreeItem().getParent().equals(rootNode)) {
                        clientClicked = true;
                    } else if (!cell.getTreeItem().isLeaf()
                            && !(cell.getTreeItem().getParent().getValue()
                            .equalsIgnoreCase("customers"))) {
                        clientClicked = false;
                    }

                }

                addMenu.getItems().add(refresh);
                addMenu.getItems().add(addClient);
                addMenu.getItems().add(addContact);

                refresh.setOnAction(handler);
                addClient.setOnAction(handler);
                addContact.setOnAction(handler);

                cell.setContextMenu(addMenu);
                return cell;
            }

            private boolean showDialog(String url) {
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(Postagi.class.getResource(url));
                    AnchorPane page = (AnchorPane) loader.load();

                    Stage dialogeStage = new Stage();
                    dialogeStage.initModality(Modality.WINDOW_MODAL);
                    dialogeStage.initOwner(Postagi.mainStage);
                    dialogeStage.initStyle(StageStyle.UNDECORATED);
                    dialogeStage.setY(Postagi.mainStage.getY() + 40);
                    dialogeStage.setX(Postagi.mainStage.getX() + Postagi.mainStage.getWidth());

                    Scene scene = new Scene(page);
                    dialogeStage.setScene(scene);
                    // Set the person into the controller.
                    ClientLayoutController controller = loader.getController();
                    controller.setDialogStage(dialogeStage);

                    // Show the dialog and wait until the user closes it
                    dialogeStage.showAndWait();

                    return controller.saveClicked;

                } catch (IOException ex) {
                    Logger.getLogger(PostagiLayoutController.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                }
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
    public void send(ActionEvent event) {

        Properties props = new Properties();
        props.put("mail.smtp.host", HOST);
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
                List<String> ccList = parts.get().get("cc");
                InternetAddress[] addressesCC = new InternetAddress[ccList.size()];
                if (!ccList.isEmpty()) {
                    for (int i = 0; i < ccList.size(); i++) {
                        try {
                            addressesCC[i] = new InternetAddress(ccList.get(i));
                        } catch (AddressException ex) {
                            Logger.getLogger(PostagiLayoutController.class.getName()).log(Level.SEVERE, null, ex);

                            Alert alert = new Alert(Alert.AlertType.ERROR,
                                    ex.toString(), ButtonType.OK);
                            alert.setHeaderText("Message Failure...");
                            alert.showAndWait();
                            return;
                        }
                    }
                }
                //create the Address array for the reciepents addresses, and fill it
                InternetAddress[] addressesTo = new InternetAddress[getSelectedMails().size()];
                for (int i = 0; i < getSelectedMails().size(); i++) {
                    try {
                        addressesTo[i] = new InternetAddress(getSelectedMails().get(i));
                    } catch (AddressException ex) {
                        Logger.getLogger(PostagiLayoutController.class.getName()).log(Level.SEVERE, null, ex);

                        Alert alert = new Alert(Alert.AlertType.ERROR,
                                ex.toString(), ButtonType.OK);
                        alert.setHeaderText("Message Failure...");
                        alert.showAndWait();
                        return;
                    }
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
                message.addRecipients(Message.RecipientType.TO, addressesTo);
                message.addRecipients(Message.RecipientType.CC, addressesCC);
                message.setSubject(SUBJECT);
                //Set the body of the message
                BodyPart msgBodyPart = new MimeBodyPart();
                msgBodyPart.setText(CONTENT);
                Multipart multiPart = new MimeMultipart();
                multiPart.addBodyPart(msgBodyPart);
                //Add the attachments

                if (!attachments.isEmpty()) {
                    attachments.stream().filter(File::exists).forEach((file) -> {
                        BodyPart attBodyPart = new MimeBodyPart();
                        try {
                            DataSource dataSource = new FileDataSource(file);
                            msgBodyPart.setDataHandler(new DataHandler(dataSource));
                            msgBodyPart.setFileName(file.getName());
                            multiPart.addBodyPart(attBodyPart);
                        } catch (MessagingException ex) {
                            Logger.getLogger(PostagiLayoutController.class.getName()).log(Level.SEVERE, null, ex);
                            Alert alert = new Alert(Alert.AlertType.ERROR,
                                    ex.toString(), ButtonType.OK);
                            alert.setHeaderText("Message Failure...");
                            alert.showAndWait();
                        }
                    });

                }
                //send the message
                Transport.send(message);
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "Message Sent Successfully!", ButtonType.OK);
                alert.setHeaderText("Message Sent...");
                alert.showAndWait();
            }
        } catch (MessagingException ex) {
            Logger.getLogger(PostagiLayoutController.class.getName()).log(Level.SEVERE, null, ex);
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    ex.toString(), ButtonType.OK);
            alert.setHeaderText("Message Failure...");
            alert.showAndWait();
        }

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
                    = new CheckBoxTreeItem<>(client.getName(), new ImageView(clientsIcon));

            rootNode.getChildren().add(clientNode);

            List<String> clientMails = extractMails(client.getEmails());
            clientMails.stream().map((mail)
                    -> new CheckBoxTreeItem<>(mail, new ImageView(mailsIcon)))
                    .forEach((mailLeaf) -> {
                        clientNode.getChildren().add(mailLeaf);
                        cbTreeItems.add(mailLeaf);
                    });

            contacts.clear();
            contacts.addAll(cclient.getContacts(client.getId()));

            contacts.stream().forEach((contact) -> {
                List<String> contactMails = extractMails(contact.getMails());
                CheckBoxTreeItem<String> contactNode
                        = new CheckBoxTreeItem<>(contact.getName(), new ImageView(contactsIcon));

                clientNode.getChildren().add(contactNode);
                contactMails.stream().map((mail)
                        -> new CheckBoxTreeItem<>(mail, new ImageView(mailsIcon)))
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

        ImageView deleteIcon = new ImageView(inactiveDeleteIcon);
        deleteIcon.setId("deleteIcon");
        System.out.println(deleteIcon.getId());

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
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            "You have no supported application to open the file!",
                            ButtonType.OK);
                    alert.setHeaderText("Open File Error...");
                    alert.showAndWait();
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

}