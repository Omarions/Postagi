/*
 * Created at Tue Aug 30, 2016
 * Last modified: 
 */
package model;

import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Omar
 * 
 * Represent the model of Contact table in DB.
 */
public class Contact {
    private final IntegerProperty idProperty;
    private final ObjectProperty<Client> clientProperty;
    private final StringProperty nameProperty;
    private final StringProperty titleProperty;
    private final StringProperty mailsProperty;
    private final StringProperty mobilesProperty;
    private final StringProperty whatsappProperty;
    private final StringProperty skypeProperty;
    private final StringProperty othersProperty;

    /**
     *
     */
    public Contact() {
        this.idProperty = new SimpleIntegerProperty();
        this.clientProperty = new SimpleObjectProperty<>();
        this.nameProperty = new SimpleStringProperty();
        this.titleProperty = new SimpleStringProperty();
        this.mailsProperty = new SimpleStringProperty();
        this.mobilesProperty = new SimpleStringProperty();
        this.whatsappProperty = new SimpleStringProperty();
        this.skypeProperty = new SimpleStringProperty();
        this.othersProperty = new SimpleStringProperty();            
    }

    /**
     *
     * @param id
     * @param client
     * @param name
     * @param title
     * @param mails
     * @param mobiles
     * @param whatsapp
     * @param skype
     * @param others
     */
    public Contact(int id, Client client, String name, String title, 
            String mails, String mobiles, String whatsapp, String skype, 
            String others) {
        
        this.idProperty = new SimpleIntegerProperty(id);
        this.clientProperty = new SimpleObjectProperty<>(client);
        this.nameProperty = new SimpleStringProperty(name);
        this.titleProperty = new SimpleStringProperty(title);
        this.mailsProperty = new SimpleStringProperty(mails);
        this.mobilesProperty = new SimpleStringProperty(mobiles);
        this.whatsappProperty = new SimpleStringProperty(whatsapp);
        this.skypeProperty = new SimpleStringProperty(skype);
        this.othersProperty = new SimpleStringProperty(others);
    }

    /**
     * @return the idProperty
     */
    public IntegerProperty idProperty() {
        return idProperty;
    }

    /**
     *
     * @return id integer value
     */
    public int getId() {
        return idProperty.get();
    }
    
    /**
     * @param id
     */
    public void setId(int id) {
        this.idProperty.set(id);
    }

    /**
     * @return the clientProperty
     */
    public ObjectProperty<Client> clientProperty() {
        return clientProperty;
    }

    /**
     *
     * @return client object value
     */
    public Client getClient() {
        return clientProperty.get();
    }
    
    /**
     * @param client
     */
    public void setClient(Client client) {
        this.clientProperty.set(client);
    }

    /**
     * @return the nameProperty
     */
    public StringProperty nameProperty() {
        return nameProperty;
    }

    /**
     *
     * @return name string value
     */
    public String getName() {
        return nameProperty.get();
    }
    
    /**
     * @param name
     */
    public void setName(String name) {
        this.nameProperty.set(name);
    }

    /**
     * @return the titleProperty
     */
    public StringProperty titleProperty() {
        return titleProperty;
    }

    /**
     *
     * @return title string value
     */
    public String getTitle() {
        return titleProperty.get();
    }
    
    /**
     * @param title
     */
    public void setTitle(String title) {
        this.titleProperty.set(title);
    }

    /**
     * @return the mailsProperty
     */
    public StringProperty getMailsProperty() {
        return mailsProperty;
    }

    /**
     *
     * @return mails string value
     */
    public String getMails() {
        return mailsProperty.get();
    }
    
    /**
     * @param mails
     */
    public void setMails(String mails) {
        this.mailsProperty.set(mails);
    }

    /**
     * @return the mobilesProperty
     */
    public StringProperty mobilesProperty() {
        return mobilesProperty;
    }

    /**
     *
     * @return mobiles string value
     */
    public String getMobiles() {
        return mobilesProperty.get();
    }
    
    /**
     * @param mobiles
     */
    public void setMobiles(String mobiles) {
        this.mobilesProperty.set(mobiles);
    }

    /**
     * @return the whatsappProperty
     */
    public StringProperty whatsappProperty() {
        return whatsappProperty;
    }

    /**
     *
     * @return whatsapp string value
     */
    public String getWhatsapp() {
        return whatsappProperty.get();
    }
    
    /**
     * @param whatsapp
     */
    public void setWhatsapp(String whatsapp) {
        this.whatsappProperty.set(whatsapp);
    }

    /**
     * @return the skypeProperty
     */
    public StringProperty skypeProperty() {
        return skypeProperty;
    }

    /**
     *
     * @return skype string value
     */
    public String getSkype() {
        return skypeProperty.get();
    }
    
    /**
     * @param skype
     */
    public void setSkype(String skype) {
        this.skypeProperty.set(skype);
    }

    /**
     * @return the othersProperty
     */
    public StringProperty othersProperty() {
        return othersProperty;
    }

    /**
     *
     * @return others value
     */
    public String getOthers() {
        return othersProperty.get();
    }
    
    /**
     * @param others
     */
    public void setOthers(String others) {
        this.othersProperty.set(others);
    }

    @Override
    public String toString() {
        return getName() + "(" + getMails() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Contact){ 
           return (((Contact)obj).getId() == this.getId());
        }else{
            throw new ClassCastException("It's not object of Contact class");
        }      
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.idProperty);
        hash = 89 * hash + Objects.hashCode(this.nameProperty);
        return hash;
    }

}
