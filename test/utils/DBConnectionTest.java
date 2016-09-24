/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author Omar
 */
public class DBConnectionTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    public DBConnectionTest() {
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
     * It passed if the method doesn't throw SQLException by running 
     * the method getConnection in the DBConnection class, if it throws 
     * the exception, the test method fails.
     */
    @Test
    public void noExpectedSQLExceptionThrown(){
        try {
            Connection result = DBConnection.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnectionTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * It passed if the method throws SQLException by running 
     * the method getConnection in the DBConnection class, if doesn't throw 
     * the exception, the test method fails.
     */
    @Test
    public void expectedSQLExceptionThrown(){
        try {
            thrown.expect(SQLException.class);
            Connection result = DBConnection.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnectionTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  
}
