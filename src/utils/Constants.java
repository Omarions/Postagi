/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Omar
 */
public class Constants {
    public static final String HOST = "mail.ngc-eg.com";
    public static final String REFRESH_MENU_ITEM = "Refresh";
    public static final String ADD_CLIENT_MENU_ITEM = "Add Client";
    public static final String ADD_CONTACT_MENU_ITEM = "Add Contact";
    public static final String EDIT_CLIENT_MENU_ITEM = "Edit Client";
    public static final String EDIT_CONTACT_MENU_ITEM = "Edit Contact";
    public static final String DELETE_CLIENT_MENU_ITEM = "Delete Client";
    public static final String DELETE_CONTACT_MENU_ITEM = "Delete Contact";
    
    public static final Node CUSTOMER_ICON = new ImageView(
            new Image(Constants.class.getResourceAsStream("/images/customers_32.png")));
    public static final Image MAIL_ICON
            = new Image(Constants.class.getResourceAsStream("/images/mails.png"));
    public static final Image CONTACT_ICON
            = new Image(Constants.class.getResourceAsStream("/images/contacts.png"));
    public static final Image CLIENT_ICON
            = new Image(Constants.class.getResourceAsStream("/images/clients.png"));
    public static final Image ACTIVE_DELETE_ICON
            = new Image(Constants.class.getResourceAsStream("/images/Delete_active.png"));
    public static final Image INACTIVE_DELETE_ICON
            = new Image(Constants.class.getResourceAsStream("/images/Delete_inactive.png"));
    public static final Image EDIT_MENU_ITEM_ICON
            = new Image(Constants.class.getResourceAsStream("/images/edit_icon.png"));
    public static final Image ADD_MENU_ITEM_ICON
            = new Image(Constants.class.getResourceAsStream("/images/add_icon.png"));
    public static final Image DELETE_MENU_ITEM_ICON
            = new Image(Constants.class.getResourceAsStream("/images/delete_icon.png"));
    public static final Image REFRESH_MENU_ITEM_ICON
            = new Image(Constants.class.getResourceAsStream("/images/arrows_refresh.png"));
}
