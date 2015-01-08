package org.jboss.as.selfmonitor.console.model;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.ClientConstants;
import org.jboss.dmr.ModelNode;

/**
 *
 * @author Vojtech Schlemmer
 */
public class ModelHandler {

    public static String getServerName(ModelControllerClient client){
        ModelNode op = new ModelNode();
        op.get(ClientConstants.OP).set(ClientConstants.READ_ATTRIBUTE_OPERATION);
        op.get(ClientConstants.NAME).set("name");
        ModelNode result = null;
		try {
			result = client.execute(op).get(ClientConstants.RESULT);
		} catch (IOException e) {
			Logger.getLogger(ModelHandler.class.getName()).log(
                        Level.SEVERE, null, e);
		}
		if(result != null){
			return result.asString();
		}
        return "not found";
    }
    
}
