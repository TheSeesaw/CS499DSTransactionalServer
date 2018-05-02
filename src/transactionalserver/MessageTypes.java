/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transactionalserver;

/**
 *
 * @author Krasis
 */
public interface MessageTypes {
    
    public static int OPEN_TRANSACTION = 1;
    public static int CLOSE_TRANSACTION = 2;
    
    public static int READ_REQUEST = 3;
    public static int WRITE_REQUEST = 4;
}
