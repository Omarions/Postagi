/*
 * Created at Tue Aug 30, 2016
 * Last modified: 
 */
package model;

import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Omar
 * 
 * Represent the model of Client table in DB
 */
public class Client extends Customer{
    private IntegerProperty idProperty;
    private StringProperty nameProperty;
    private StringProperty addressProperty;
    private StringProperty websiteProperty;
    private StringProperty emailsProperty;
    private StringProperty telProperty;
    private StringProperty faxProperty;
    private StringProperty tagsProperty;

    /**
     *
     */
    public Client() {
        this.idProperty = new SimpleIntegerProperty();
        this.nameProperty = new SimpleStringProperty();
        this.addressProperty = new SimpleStringProperty();
        this.websiteProperty = new SimpleStringProperty();
        this.emailsProperty = new SimpleStringProperty();
        this.telProperty = new SimpleStringProperty();
        this.faxProperty = new SimpleStringProperty();
        this.tagsProperty = new SimpleStringProperty();
    }

    /**
     *
     * @param id
     * @param name
     * @param address
     * @param website
     * @param emails
     * @param tel
     * @param fax
     * @param tags
     */
    public Client(int id, String name, String address, String website, 
            String emails, String tel, String fax, String tags) {
        
        this.idProperty = new SimpleIntegerProperty(id);
        this.nameProperty = new SimpleStringProperty(name);
        this.addressProperty = new SimpleStringProperty(address);
        this.websiteProperty = new SimpleStringProperty(website);
        this.emailsProperty = new SimpleStringProperty(emails);
        this.telProperty = new SimpleStringProperty(tel);
        this.faxProperty = new SimpleStringProperty(fax);
        this.tagsProperty = new SimpleStringProperty(tags);
    }

    /**
     * @return the idProperty
     */
    public IntegerProperty idProperty() {
        return idProperty;
    }

    /**
     *
     * @return id value
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
     *
     * @return name value
     */
    public StringProperty nameProperty() {
        return nameProperty;
    }
    
    /**
     * @return the nameProperty
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
     * @return the addressProperty
     */
    public StringProperty addressProperty() {
        return addressProperty;
    }

    /**
     *
     * @return address value
     */
    public String getAddress() {
        return addressProperty.get();
    }
    
    /**
     * @param address
     */
    public void setAddress(String address) {
        this.addressProperty.set(address);
    }

    /**
     * @return the websiteProperty
     */
    public StringProperty websiteProperty() {
        return websiteProperty;
    }

    /**
     *
     * @return the website value
     */
    public String getWebsite() {
        return websiteProperty.get();
    }
    
    /**
     * @param website
     */
    public void setWebsite(String website) {
        this.websiteProperty.set(website);
    }

    /**
     * @return the emailsProperty
     */
    public StringProperty emailsProperty() {
        return emailsProperty;
    }

    /**
     *
     * @return the emails value
     */
    public String getEmails() {
        return emailsProperty.get();
    }
    
    /**
     * @param emails
     */
    public void setEmails(String emails) {
        this.emailsProperty.set(emails);
    }

    /**
     * @return the telProperty
     */
    public StringProperty telProperty() {
        return telProperty;
    }

    /**
     *
     * @return tel value
     */
    public String getTel() {
        return telProperty.get();
    }
    
    /**
     * @param tel
     */
    public void setTel(String tel) {
        this.telProperty.set(tel);
    }

    /**
     * @return the faxProperty
     */
    public StringProperty faxProperty() {
        return faxProperty;
    }

    /**
     *
     * @return fax value
     */
    public String getFax() {
        return faxProperty.get();
    }
    
    /**
     * @param fax
     */
    public void setFax(String fax) {
        this.faxProperty.set(fax);
    }

    /**
     * @return the tagsProperty
     */
    public StringProperty tagsProperty() {
        return tagsProperty;
    }

    /**
     *
     * @return tags value
     */
    public String getTags() {
        return tagsProperty.get();
    }
    
    /**
     * @param tags
     */
    public void setTags(String tags) {
        this.tagsProperty.set(tags);
    }
    
    @Override
    public String toString(){
        return getId() + ": " + getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Client other = (Client) obj;
        if (!Objects.equals(this.idProperty, other.idProperty)) {
            return false;
        }
        if (!Objects.equals(this.nameProperty, other.nameProperty)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.idProperty);
        hash = 89 * hash + Objects.hashCode(this.nameProperty);
        return hash;
    }
  
}
