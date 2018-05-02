/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transactionalserver;


import java.io.Serializable;
/**
 *
 * @author Krasis
 */
public class Message implements MessageTypes, Serializable {
    
    int type;
    Object content;
    
    public Message(int mType, Object mContents) {
        this.type = mType;
        this.content = mContents;
    }
    
    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public Object getContent() {
        return this.content;
    }
    
}
