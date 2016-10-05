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
public class cClient {

    public Optional<Client> getByID(int id) {
        final String GET_BY_ID_SQL = "SELECT * FROM client WHERE id=?";
        Optional<Client> client = Optional.empty();
        try (PreparedStatement prep
                = DBConnection.getConnection().prepareStatement(GET_BY_ID_SQL)) {
            prep.setInt(1, id);
            ResultSet rs = prep.executeQuery();

            if (rs.first()) {
                Client cl = new Client();
                rs.beforeFirst();
                while (rs.next()) {
                    cl.setId(id);
                    cl.setName(rs.getString("name"));
                    cl.setAddress(rs.getString("address"));
                    cl.setFax(rs.getString("fax"));
                    cl.setTel(rs.getString("tel"));
                    cl.setEmails(rs.getString("mails"));
                    cl.setWebsite(rs.getString("website"));
                    cl.setTags(rs.getString("tags"));
                }

                client = Optional.ofNullable(cl);
                rs.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(cClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        return client;
    }

    public Optional<Client> getByName(String name) {
        final String GET_BY_Name_SQL = "SELECT * FROM client WHERE name=?";
        Optional<Client> client = Optional.empty();
        try (PreparedStatement prep
                = DBConnection.getConnection().prepareStatement(GET_BY_Name_SQL)) {
            prep.setString(1, name);
            ResultSet rs = prep.executeQuery();

            if (rs.first()) {
                Client cl = new Client();
                rs.beforeFirst();
                while (rs.next()) {
                    cl.setId(rs.getInt("id"));
                    cl.setName(rs.getString("name"));
                    cl.setAddress(rs.getString("address"));
                    cl.setFax(rs.getString("fax"));
                    cl.setTel(rs.getString("tel"));
                    cl.setEmails(rs.getString("mails"));
                    cl.setWebsite(rs.getString("website"));
                    cl.setTags(rs.getString("tags"));
                }

                client = Optional.ofNullable(cl);
                rs.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(cClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        return client;
    }

    public List<Client> getAll() throws SQLException{
        final String GET_ALL_SQL = "SELECT * FROM client";
        List<Client> clients = new ArrayList<>();

        try (PreparedStatement prep
                = DBConnection.getConnection().prepareStatement(GET_ALL_SQL)) {

            ResultSet rs = prep.executeQuery();

            if (rs.first()) {

                rs.beforeFirst();
                while (rs.next()) {
                    Client cl = new Client();
                    cl.setId(rs.getInt("id"));
                    cl.setName(rs.getString("name"));
                    cl.setAddress(rs.getString("address"));
                    cl.setWebsite(rs.getString("website"));
                    cl.setEmails(rs.getString("mails"));
                    cl.setTel(rs.getString("tel"));
                    cl.setFax(rs.getString("fax"));
                    cl.setTags(rs.getString("tags"));

                    clients.add(cl);
                }

                rs.close();
                DBConnection.close();
            }

        } catch (SQLException ex) {
            throw ex;
        }

        return clients;
    }
    
    public List<Contact> getContacts(int clientID) {
        final String GET_ALL_SQL = "SELECT cont.*, cl.* FROM contact AS cont "
                + "INNER JOIN client AS cl ON(cont.client_id = cl.id) "
                + "WHERE cont.client_id = ?";
        List<Contact> contacts = new ArrayList<>();

        try (PreparedStatement prep
                = DBConnection.getConnection().prepareStatement(GET_ALL_SQL)) {

            prep.setInt(1, clientID);
            
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
            Logger.getLogger(cClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        return contacts;
    }

    public int insert(Client client) {
        final String INSERT_SQL = "INSERT INTO client(name, address, website, "
                + "mails, tel, fax, tags) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement prep
                = DBConnection.getConnection().prepareStatement(INSERT_SQL)) {

            prep.setString(1, client.getName());
            prep.setString(2, client.getAddress());
            prep.setString(3, client.getWebsite());
            prep.setString(4, client.getEmails());
            prep.setString(5, client.getTel());
            prep.setString(6, client.getFax());
            prep.setString(7, client.getTags());

            return prep.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(cClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;
    }

    public int update(Client client) {
        final String INSERT_SQL = "UPDATE client SET name = ?, address = ?, "
                + "website = ?, mails = ?, tel = ?, fax = ?, tags = ? "
                + "WHERE id = ?";

        try (PreparedStatement prep
                = DBConnection.getConnection().prepareStatement(INSERT_SQL)) {

            prep.setString(1, client.getName());
            prep.setString(2, client.getAddress());
            prep.setString(3, client.getWebsite());
            prep.setString(4, client.getEmails());
            prep.setString(5, client.getTel());
            prep.setString(6, client.getFax());
            prep.setString(7, client.getTags());
            prep.setInt(8, client.getId());

            return prep.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(cClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;
    }

    public int delete(int id) {
        final String INSERT_SQL = "DELETE FROM client WHERE id = ?";

        try (PreparedStatement prep
                = DBConnection.getConnection().prepareStatement(INSERT_SQL)) {

            prep.setInt(1, id);

            return prep.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(cClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;
    }
}
