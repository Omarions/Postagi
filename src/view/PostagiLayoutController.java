/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.cClient;
import controller.cContact;
import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalTime;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
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
    private VBox vbox;
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
    private Label lblSkip;
    @FXML
    private Label lblCancelAll;
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
    @FXML
    private MenuItem miExit;
    @FXML
    private MenuItem miRefresh;
    @FXML
    private Menu muClients;
    @FXML
    private MenuItem miAddClient;
    @FXML
    private MenuItem miEditClient;
    @FXML
    private MenuItem miDeleteClient;
    @FXML
    private Menu muContacts;
    @FXML
    private MenuItem miAddContact;
    @FXML
    private MenuItem miEditContact;
    @FXML
    private MenuItem miDeleteContact;
    @FXML
    private MenuItem miSettings;
    @FXML
    private MenuItem miAbout;

    private final CheckBoxTreeItem<String> rootNode = new CheckBoxTreeItem<>("Customers", Constants.CUSTOMER_ICON);
    private final List<TreeItem<String>> cbTreeItems = new ArrayList<>();

    private final ObservableList<Client> clientsList = FXCollections.observableArrayList();
    private final ObservableList<Contact> contactsList = FXCollections.observableArrayList();
    private final List<File> attachments = new ArrayList<>();
    private Properties settingProps;
    private BodyPart msgBodyPart = new MimeBodyPart();
    private cClient cclient;
    private cContact ccontact;
    private int currentTimerSpin = 0;
    static int counter = 0;

    //variables for dragging the window
    private double startMoveX = -1, startMoveY = -1;
    private Boolean dragging = false;
    private Rectangle moveTrackingRect;
    private Popup moveTrackingPopup;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cclient = new cClient();
        ccontact = new cContact();

        //init the menus icons
        miExit.setGraphic(new ImageView(Constants.Exit_MENU_ICON));

        miRefresh.setGraphic(new ImageView(Constants.REFRESH_MENU_ITEM_ICON));

        muClients.setGraphic(new ImageView(Constants.CLIENT_ICON));
        muContacts.setGraphic(new ImageView(Constants.CONTACT_ICON));

        miAddClient.setGraphic(new ImageView(Constants.ADD_MENU_ITEM_ICON));
        miEditClient.setGraphic(new ImageView(Constants.EDIT_MENU_ITEM_ICON));
        miDeleteClient.setGraphic(new ImageView(Constants.DELETE_MENU_ITEM_ICON));

        miAddContact.setGraphic(new ImageView(Constants.ADD_MENU_ITEM_ICON));
        miEditContact.setGraphic(new ImageView(Constants.EDIT_MENU_ITEM_ICON));
        miDeleteContact.setGraphic(new ImageView(Constants.DELETE_MENU_ITEM_ICON));

        miSettings.setGraphic(new ImageView(Constants.SETTING_MENU_ICON));

        miAbout.setGraphic(new ImageView(Constants.ABOUT_MENU_ICON));

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

                //get the handler for events of menus
                EventHandler<ActionEvent> handler = (ActionEvent event) -> {
                    menusEventHandler(event);
                };
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

        });
    }

    /**
     * Event handler for close label.
     *
     * @param event of the mouse click
     */
    @FXML
    public void closeHandler(MouseEvent event) {
        Platform.exit();
    }

    /**
     * Event handler for close button and close menu item.
     *
     * @param event of the mouse click
     */
    @FXML
    public void closeMenuHandler(ActionEvent event) {
        Platform.exit();
    }

    /**
     * Event handler of minimize button.
     *
     * @param event of the mouse click
     */
    @FXML
    public void minimizeHandler(MouseEvent event) {
        Stage stage = (Stage) ((Label) event.getSource()).getScene().getWindow();

        stage.setIconified(true);
    }

    /**
     * Event handler for menu items
     *
     * @param event
     * @return EventHandler object
     */
    @FXML
    public void menusEventHandler(ActionEvent event) {
        String selectedMenuItem = ((MenuItem) event.getSource()).getText();
        switch (selectedMenuItem) {
            case Constants.REFRESH_MENU_ITEM:
                fillClientsList();
                populateTreeView(clientsList);
                break;
            case Constants.ADD_CLIENT_MENU_ITEM:
                if (showDialog("/view/ClientLayout.fxml", DialogType.CLIENT, null, null)) {
                    fillClientsList();
                    populateTreeView(clientsList);
                }
                break;
            case Constants.ADD_CONTACT_MENU_ITEM:
                if (showDialog("/view/ContactLayout.fxml", DialogType.CONTACT, null, null)) {
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
                    if (showDialog("/view/ClientLayout.fxml", DialogType.CLIENT, client, null)) {
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
                    if (showDialog("view/ContactLayout.fxml", DialogType.CONTACT, null, contact)) {
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
            case Constants.SETTINGS_MENU_ITEM:
                Properties props = getProperties();
                String strCurrentSettings = "Not Set...";

                if (props != null) {
                    strCurrentSettings = props.getProperty(Constants.HOST_KEY);
                    currentTimerSpin = Integer.valueOf(
                            props.getProperty(Constants.SPIN_KEY));
                }
                Optional<Map<String, String>> settingResult
                        = Utils.showSettingsDialog(strCurrentSettings, currentTimerSpin);
                settingResult.ifPresent((result) -> {
                    if (settingResult.get().isEmpty()) {
                        setProperties(Constants.HOST_DEFAULT_VALUE, currentTimerSpin);
                    } else {
                        setProperties(result.get(Constants.HOST_KEY),
                                Integer.valueOf(result.get(Constants.SPIN_KEY)));
                    }
                });
                break;
        }
    }

    /**
     * Event handler for start dragging event.
     *
     * @param evt the mouse event of dragging detected
     */
    @FXML
    public void startMoveWindow(MouseEvent evt) {
        startMoveX = evt.getScreenX();
        startMoveY = evt.getScreenY();
        dragging = true;

        moveTrackingRect = new Rectangle();
        moveTrackingRect.setWidth(vbox.getWidth());
        moveTrackingRect.setHeight(vbox.getHeight());
        moveTrackingRect.getStyleClass().add("tracking-rect");

        moveTrackingPopup = new Popup();
        moveTrackingPopup.getContent().add(moveTrackingRect);
        moveTrackingPopup.show(vbox.getScene().getWindow());
        moveTrackingPopup.setOnHidden((e) -> resetMoveOperations());
    }

    /**
     * Event handler of dragging window.
     *
     * @param evt the mouse drag event
     */
    @FXML
    public void moveWindow(MouseEvent evt) {
        if (dragging) {
            double endMoveX = evt.getScreenX();
            double endMoveY = evt.getScreenY();

            Window w = vbox.getScene().getWindow();

            double stageX = w.getX();
            double stageY = w.getY();

            moveTrackingPopup.setX(stageX + (endMoveX - startMoveX));
            moveTrackingPopup.setY(stageY + (endMoveY - startMoveY));
        }
    }

    /**
     * Event handler for end of dragging
     *
     * @param evt the mouse click release event
     */
    @FXML
    public void endMoveWindow(MouseEvent evt) {
        if (dragging) {
            double endMoveX = evt.getScreenX();
            double endMoveY = evt.getScreenY();

            Window w = vbox.getScene().getWindow();

            double stageX = w.getX();
            double stageY = w.getY();

            w.setX(stageX + (endMoveX - startMoveX));
            w.setY(stageY + (endMoveY - startMoveY));

            if (moveTrackingPopup != null) {
                moveTrackingPopup.hide();
                moveTrackingPopup = null;
            }
        }
        resetMoveOperations();
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
        if (getSelectedMails().isEmpty()) {
            Utils.showErrorDialog("Empty Mail List", "No mails selected!");
        } else {
            Queue<String> queue = new ConcurrentLinkedQueue<>(getSelectedMails());
            SendMailsService service = new SendMailsService();
            //reset the counter.
            counter = 0;
            //get the timer spin from settings file, it's in minutes, if not set
            //take the default which is 0.
            int spin = Integer.valueOf(
                    getProperties().getProperty(Constants.SPIN_KEY));
            //using package javafx.util.Duration, it's in msec
            Duration duration = new Duration(spin * 60 * 1000);

            //the period between start of sending mail and start of sending the next one.
            service.setPeriod(duration);

            //set the delay of starting or restarting the service.
            //it's zero so it starts/restarts immediately.
            //service.setDelay(Duration.ZERO);
            //on failure restart the service
            service.setRestartOnFailure(true);
            //set max try on fail
            service.setMaximumFailureCount(1);

            service.setQueue(queue);

            hbStatusBar.setVisible(true);

            service.setOnReady((WorkerStateEvent readyEvent) -> {
                System.out.println(LocalTime.now());
                System.out.println(readyEvent.getEventType().getName());

                if (service.isAllDone) {
                    //hide the status bar when finish sending all selected mails
                    service.cancel();
                }
            });
            //update the message of status bar on scheduled state of service
            //which is waiting to the period to be expired.
            service.setOnScheduled((WorkerStateEvent scheduledEvent) -> {
                System.out.println(LocalTime.now());
                System.out.println(scheduledEvent.getEventType().getName());
                lblStatus.textProperty().unbind();
                lblStatus.setText("Waiting to send next mail...");

            });
            service.setOnRunning((WorkerStateEvent runningEvent) -> {
                System.out.println(LocalTime.now());
                System.out.println(runningEvent.getEventType().getName());
                lblStatus.textProperty().unbind();
                lblStatus.textProperty().bind(service.messageProperty());
            });
            service.setOnSucceeded((WorkerStateEvent succeededEvent) -> {
                System.out.println(LocalTime.now());
                System.out.println(succeededEvent.getEventType().getName());
            });
            service.setOnFailed((WorkerStateEvent failedEvent) -> {
                System.out.println(LocalTime.now());
                System.out.println(failedEvent.getEventType().getName());
                System.out.println(failedEvent.getSource().getMessage());
                lblStatus.textProperty().unbind();
                lblStatus.textProperty().bind(service.messageProperty());
            });

            service.setOnCancelled((WorkerStateEvent cancelledEvent) -> {
                System.out.println("GET in CANCELLED state...");
                
                hbStatusBar.setVisible(false);
            });

            lblCancelAll.setOnMouseClicked((MouseEvent cancelClicked) -> {
                System.out.println("cancel pressed");
                service.cancel();
            });

            lblSkip.setOnMouseClicked((MouseEvent skipClicked) -> {
                String skipMail = service.queue.poll();
                if (skipMail != null) {
                    service.queue.offer(skipMail);
                    service.restart();
                    hbStatusBar.setVisible(true);
                } else {
                    service.cancel();
                }
            });

            //bind the text and progress of status bar with message and progress of service
            lblStatus.textProperty().unbind();
            lblStatus.textProperty().bind(service.messageProperty());
            progStatus.progressProperty().unbind();
            progStatus.progressProperty().bind(service.progressProperty());

            service.start();
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
                if (getSession(FROM, PASSWORD) != null) {
                    //create the message object to be sent
                    MimeMessage message = new MimeMessage(getSession(FROM, PASSWORD));
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
                                Utils.showExceptionDialog("Message Error...", "Exception happen while creating the attachment!", ex);
                            }
                        });

                    }

                    message.setContent(multiPart);
                    try {
                        //send the message
                        Transport.send(message);
                        return true;
                    } catch (Exception ex) {
                        Logger.getLogger(PostagiLayoutController.class.getName()).log(Level.SEVERE, null, ex);
                        Utils.showExceptionDialog("Message Failure...", "Exception happen while sending message!", ex);
                        return false;
                    }
                } else {
                    Utils.showErrorDialog("Session Error...", "Cannot open session to send mails!");
                    return false;
                }
            }
        } catch (MessagingException ex) {
            Logger.getLogger(PostagiLayoutController.class.getName()).log(Level.SEVERE, null, ex);
            Utils.showExceptionDialog("Message Failure...", "Exception happen while sending message!", ex);
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

    /**
     * Create new session to send message.
     *
     * @param user the sender mail
     * @param password of sender mail
     * @return new session with properties which have the SMTP server host, and
     * authentication of user and password.
     */
    private Session getSession(String user, String password) {
        if (getProperties() != null) {
            try{
            Session session = Session.getDefaultInstance(getProperties(), new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, password);
                }
            });
            return session;
            }catch(Exception ex){
                Utils.showExceptionDialog("Session Error", "Exception Details: ", ex);
                return null;
            }
        }
        return null;
    }

    /**
     * Create the properties object for session, it loads the properties from
     * the settings file first if exists, otherwise it displays the error dialog
     *
     * @return properties object with values from settings file.
     */
    private Properties getProperties() {
        Properties props = new Properties();
        File propFile = new File(Constants.PROP_FILE_URL);
        if (propFile.exists()) {
            try {
                props.loadFromXML(new BufferedInputStream(new FileInputStream(propFile)));
                return props;
            } catch (IOException ex) {
                Logger.getLogger(PostagiLayoutController.class.getName()).log(Level.SEVERE, null, ex);
                Utils.showExceptionDialog("IO Exception", "Error in loading file!", ex);
                return null;
            }
        } else {
            Utils.showErrorDialog("File Error...", "The properties file is not exist!");
            return null;
        }
    }

    /**
     * set the values of host to the properties object and write it to the
     * settings file
     *
     * @param host the smtp server
     */
    private void setProperties(String host, int timerSpin) {
        Properties props = new Properties();
        File propFile = new File(Constants.PROP_FILE_URL);
        if (propFile.exists()) {
            try {
                props.setProperty(Constants.HOST_KEY, host);
                props.setProperty(Constants.AUTH_KEY, Constants.AUTH_VALUE);
                props.setProperty(Constants.SPIN_KEY, String.valueOf(timerSpin));

                props.storeToXML(new BufferedOutputStream(new FileOutputStream(propFile)), host);
            } catch (IOException ex) {
                Logger.getLogger(PostagiLayoutController.class.getName()).log(Level.SEVERE, null, ex);
                Utils.showExceptionDialog("IO Exception", "Error in loading file!", ex);
            }
        } else {
            Utils.showErrorDialog("File Error...", "The properties file is not exist!");
        }
    }

    /**
     * Reset the variables of moving the window.
     */
    private void resetMoveOperations() {
        startMoveX = 0;
        startMoveY = 0;
        dragging = false;
        moveTrackingRect = null;
    }

    /**
     * Show the dialog according to the type (client or contact)
     *
     * @param url the resource of fxml file that should be loaded
     * @param flag the type of dialog (dialog for client or for contact)
     * @return true when either button of save (save and quit or save and add
     * new ) is clicked, otherwise returns false.
     */
    private boolean showDialog(String url, DialogType flag, Client updateClient, Contact updateContact) {
        FXMLLoader loader = new FXMLLoader();
        Stage dialogeStage = new Stage();

        try {
            loader.setLocation(Postagi.class.getResource(url));
            AnchorPane page = (AnchorPane) loader.load();

            Scene scene = new Scene(page);
            scene.setFill(null);

            dialogeStage.initModality(Modality.WINDOW_MODAL);
            dialogeStage.initOwner(Postagi.mainStage);
            dialogeStage.initStyle(StageStyle.TRANSPARENT);
            dialogeStage.setY(Postagi.mainStage.getY() + 48);
            dialogeStage.setX(Postagi.mainStage.getX() + Postagi.mainStage.getWidth() - 5);
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

    /**
     * Scheduled Service class to send mails automatically with some conditions,
     * like period, delay. All conditions are specified later after creating the
     * object of this class.
     */
    class SendMailsService extends ScheduledService<Void> {

        int totalMails = getSelectedMails().size();
        private String mail;
        Queue<String> queue;
        private boolean isAllDone;

        private String getMail() {
            return (queue.isEmpty()) ? null : queue.peek();
        }

        public void setQueue(Queue<String> queue) {
            this.queue = queue;
        }

        public boolean isDone() {
            return isAllDone;
        }

        @Override
        protected void ready() {
            super.ready(); //To change body of generated methods, choose Tools | Templates.
            this.mail = getMail();
        }

        @Override
        protected void failed() {
            super.failed(); //To change body of generated methods, choose Tools | Templates.
            String fMail = queue.poll();
            queue.offer(fMail);
        }

        @Override
        public boolean cancel() {
            queue.clear();
            return super.cancel(); //To change body of generated methods, choose Tools | Templates.
        }
        
        

        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {

                    //double workDone = workDoneProperty().doubleValue();
                    //double totalWork = totalWorkProperty().doubleValue();
                    //update the progress of progress of sending mail
                    //update the message of process.
                    String mMail = getMail();
                    if (mMail != null) {
                        updateMessage("Sending to <" + mMail + ">");

                        //send the mail, if succeded update the message and progress bar of UI
                        if (sendMail(mMail)) {
                            //create the message
                            StringBuilder statusMsg = new StringBuilder();
                            statusMsg.append("Sent ")
                                    .append(counter + 1)
                                    .append("/")
                                    .append(totalMails)
                                    .append(" mails...");
                            //update the UI message
                            updateMessage(statusMsg.toString());
                            ++counter;
                            queue.poll();
                            isAllDone = queue.isEmpty();
                        } else {
                            updateMessage("Fail to send mail <" + mMail + ">");
                        }
                    }
                    return null;
                }
            };
        }

    }
}
