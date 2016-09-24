/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Client;
import model.Contact;
import utils.DBConnection;

/**
 *
 * @author Omar
 */
public class cContact {
    
    public Optional<Contact> getByID(int id) {
        final String GET_BY_ID_SQL = "SELECT cont.*, cl.* FROM contact AS cont "
                + "INNER JOIN client AS cl ON(cont.client_id = cl.id) WHERE id=?";
        Optional<Contact> contact = Optional.empty();
        try (PreparedStatement prep
                = DBConnection.getConnection().prepareStatement(GET_BY_ID_SQL)) {
            prep.setInt(1, id);
            ResultSet rs = prep.executeQuery();

            if (rs.first()) {
                Contact cont = new Contact();
                rs.beforeFirst();
                while (rs.next()) {
                    cont.setId(id);
                    cont.setName(rs.getString("name"));
                    cont.setTitle(rs.getString("title"));
                    cont.setMails(rs.getString("mails"));
                    cont.setMobiles(rs.getString("mobiles"));
                    cont.setWhatsapp(rs.getString("whatsapp"));
                    cont.setSkype(rs.getString("skype"));
                    cont.setOthers(rs.getString("others"));
                    
                    Client cl = new Client();
                    cl.setId(rs.getInt("client_id"));
                    cl.setName(rs.getString("name"));
                    cl.setAddress(rs.getString("address"));
                    cl.setWebsite(rs.getString("website"));
                    cl.setEmails(rs.getString("cl.mails"));
                    cl.setTel(rs.getString("tel"));
                    cl.setFax(rs.getString("fax"));
                    cl.setTags(rs.getString("tags"));
                    
                    cont.setClient(cl);
                }

                contact = Optional.ofNullable(cont);
                rs.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(cContact.class.getName()).log(Level.SEVERE, null, ex);
        }

        return contact;
    }

    public Optional<Contact> getByName(String name) {
        final String GET_BY_Name_SQL = "SELECT cont.*, cl.* FROM contact AS cont "
                + "INNER JOIN client AS cl ON(cont.client_id = cl.id) WHERE name=?";
        Optional<Contact> contact = Optional.empty();
        try (PreparedStatement prep
                = DBConnection.getConnection().prepareStatement(GET_BY_Name_SQL)) {
            prep.setString(1, name);
            ResultSet rs = prep.executeQuery();

            if (rs.first()) {
                Contact cont = new Contact();
                rs.beforeFirst();
                while (rs.next()) {
                    cont.setId(rs.getInt("id"));
                    cont.setName(rs.getString("name"));
                    cont.setTitle(rs.getString("title"));
                    cont.setMails(rs.getString("mails"));
                    cont.setMobiles(rs.getString("mobiles"));
                    cont.setWhatsapp(rs.getString("whatsapp"));
                    cont.setSkype(rs.getString("skype"));
                    cont.setOthers(rs.getString("others"));
                    
                    Client cl = new Client();
                    cl.setId(rs.getInt("client_id"));
                    cl.setName(rs.getString("name"));
                    cl.setAddress(rs.getString("address"));
                    cl.setWebsite(rs.getString("website"));
                    cl.setEmails(rs.getString("cl.mails"));
                    cl.setTel(rs.getString("tel"));
                    cl.setFax(rs.getString("fax"));
                    cl.setTags(rs.getString("tags"));
                    
                    cont.setClient(cl);
                }

                contact = Optional.ofNullable(cont);
                rs.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(cContact.class.getName()).log(Level.SEVERE, null, ex);
        }

        return contact;
    }

    public List<Contact> getAll() {
        final String GET_ALL_SQL = "SELECT cont.*, cl.* FROM contact AS cont "
                + "INNER JOIN client AS cl ON(cont.client_id = cl.id) ";
        List<Contact> contacts = new ArrayList<>();

        try (PreparedStatement prep
                = DBConnection.getConnection().prepareStatement(GET_ALL_SQL)) {

            ResultSet rs = prep.executeQuery();

            if (rs.first()) {

                rs.beforeFirst();
                while (rs.next()) {
                    Contact cont = new Contact();
                    cont.setId(rs.getInt("id"));
                    cont.setName(rs.getString("name"));
                    cont.setTitle(rs.getString("title"));
                    cont.setMails(rs.getString("mails"));
                    cont.setMobiles(rs.getString("mobiles"));
                    cont.setWhatsapp(rs.getString("whatsapp"));
                    cont.setSkype(rs.getString("skype"));
                    cont.setOthers(rs.getString("others"));
                    
                    Client cl = new Client();
                    cl.setId(rs.getInt("client_id"));
                    cl.setName(rs.getString("name"));
                    cl.setAddress(rs.getString("address"));
                    cl.setWebsite(rs.getString("website"));
                    cl.setEmails(rs.getString("cl.mails"));
                    cl.setTel(rs.getString("tel"));
                    cl.setFax(rs.getString("fax"));
                    cl.setTags(rs.getString("tags"));
                    
                    cont.setClient(cl);

                    contacts.add(cont);
                }

                rs.close();
                DBConnection.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(cContact.class.getName()).log(Level.SEVERE, null, ex);
        }

        return contacts;
    }

    public int insert(Contact contact) {
        final String INSERT_SQL = "INSERT INTO contact(client_id, name, title,"
                + " mails, mobiles, whatsapp, skype, others) VALUES(?, ?, ?, ?,"
                + " ?, ?, ?, ?)";
        try (PreparedStatement prep
                = DBConnection.getConnection().prepareStatement(INSERT_SQL)) {

            prep.setInt(1, contact.getClient().getId());
            prep.setString(2, contact.getName());
            prep.setString(3, contact.getTitle());
            prep.setString(4, contact.getMails());
            prep.setString(5, contact.getMobiles());
            prep.setString(6, contact.getWhatsapp());
            prep.setString(7, contact.getSkype());
            prep.setString(8, contact.getOthers());

            return prep.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(cContact.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;
    }

    public int update(Contact contact, int id) {
        final String INSERT_SQL = "UPDATE contact SET client_id=?, name = ?,"
                + "title = ?, mails = ?, mobiles = ?, whatsapp = ?, skype = ?, "
                + "others = ? WHERE id = ?";

        try (PreparedStatement prep
                = DBConnection.getConnection().prepareStatement(INSERT_SQL)) {

            prep.setInt(1, contact.getClient().getId());
            prep.setString(2, contact.getName());
            prep.setString(3, contact.getTitle());
            prep.setString(4, contact.getMails());
            prep.setString(5, contact.getMobiles());
            prep.setString(6, contact.getWhatsapp());
            prep.setString(7, contact.getSkype());
            prep.setString(8, contact.getOthers());
            prep.setInt(9, id);

            return prep.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(cContact.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;
    }

    public int delete(int id) {
        final String INSERT_SQL = "DELETE FROM contact WHERE id = ?";

        try (PreparedStatement prep
                = DBConnection.getConnection().prepareStatement(INSERT_SQL)) {

            prep.setInt(1, id);

            return prep.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(cContact.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;
    }
}
