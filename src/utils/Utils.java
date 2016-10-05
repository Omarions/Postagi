/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

/**
 *
 * @author Omar
 */
public class Utils {
    /**
     * Display error dialog.
     *
     * @param header of the dialog
     * @param msg of the dialog
     */
    public static void showErrorDialog(String header, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR,
                msg, ButtonType.OK);
        alert.setHeaderText(header);
        alert.showAndWait();
    }
    
    /**
     * Display Exception error Dialog
     *
     * @param header the title
     * @param msg the content message
     * @param exception the exception that happened
     */
    public static void showExceptionDialog(String header, String msg, String exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.getDialogPane().setContent(new TextArea(exception));
    }
}
