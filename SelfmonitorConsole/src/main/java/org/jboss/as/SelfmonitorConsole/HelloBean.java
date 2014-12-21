package org.jboss.as.SelfmonitorConsole;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.jboss.as.controller.client.ModelControllerClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jboss.dmr.ModelNode;
import org.jboss.as.controller.client.helpers.ClientConstants;

@ManagedBean(name="helloBean")
@SessionScoped
public class HelloBean implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final String HOST = "localhost";
    public static final int PORT = 9990;
	private String serverName;
	
	public String getServerName(){
		getServerInfo();
		return serverName;
	}
	
	public void setServerName(String serverName){
		this.serverName = serverName;
	}
	
	private void getServerInfo(){
		ModelControllerClient client = null;
        try {  
            client = ModelControllerClient.Factory.create(
                    InetAddress.getByName(HOST), PORT);
        } catch (UnknownHostException ex) {
            
        }
        
        ModelNode op = new ModelNode();
        op.get(ClientConstants.OP).set(ClientConstants.READ_ATTRIBUTE_OPERATION);
        op.get(ClientConstants.NAME).set("name");
        ModelNode result = null;
		try {
			result = client.execute(op).get(ClientConstants.RESULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(result != null){
			setServerName(result.asString());
		}
        
	}

}
