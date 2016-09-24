/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.Optional;
import model.Client;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Omar
 */
public class cClientTest {
    
    public cClientTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getByID method, of class cClient.
     */
    @Test
    public void existClientGetByID() {
        System.out.println("getByID");
        int id = 1;
        cClient instance = new cClient();
        Optional<Client> expResult = Optional.of(
                new Client(1,"client1","address1","website1",
                        "mail1@client1.com;mail2@client1.com","N/A","N/A","tag1; tag2"));
        Optional<Client> result = instance.getByID(id);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
    /**
     * Test of getByID method, of class cClient.
     */
    @Test
    public void existClientGetByName() {
        System.out.println("getByName");
        String name = "client1";
        cClient instance = new cClient();
        Optional<Client> expResult = Optional.of(
                new Client(1,"client1","address1","website1",
                        "mail1@client1.com;mail2@client1.com","N/A","N/A","tag1; tag2"));
        Optional<Client> result = instance.getByName(name);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
}
