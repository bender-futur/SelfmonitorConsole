package org.jboss.as.selfmonitor.console.model;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.ClientConstants;
import static org.jboss.as.controller.client.helpers.ClientConstants.OP_ADDR;
import static org.jboss.as.controller.client.helpers.ClientConstants.SUBSYSTEM;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

/**
 *
 * @author Vojtech Schlemmer
 */
public class MetricValuesHandler {
    
    private final ModelControllerClient client;
    private final PathElement SUBSYSTEM_PATH = PathElement.pathElement(
            SUBSYSTEM, "selfmonitor");
    
    public MetricValuesHandler(ModelControllerClient client){
        this.client = client;
    }
    
    public Map<String,String> getAllValues(String metricId){
        Map<String,String> valuesMap = new TreeMap<String, String>();
        PathAddress addr = PathAddress.pathAddress(SUBSYSTEM_PATH, PathElement.pathElement("metric", metricId));
        ModelNode op = new ModelNode();
        op.get(OP_ADDR).set(addr.toModelNode());
        op.get(ClientConstants.OP).set("read-all-values");
//        op.get("show-enabled").set(enabled ? "true" : "false");
        ModelNode result = null;
        if(client != null){
            try {
                result = client.execute(op).get(ClientConstants.RESULT);
            } catch (IOException e) {
                Logger.getLogger(MetricValuesHandler.class.getName()).log(Level.SEVERE, null, e);
            }
            if(result != null && result.getType().equals(ModelType.LIST)){
                for(ModelNode resultItem : result.asList()){
                    valuesMap.put(resultItem.get("time").asString(), resultItem.get("value").asString());
                }
            }
        }
        else{
            Logger.getLogger(MetricValuesHandler.class.getName()).log(Level.SEVERE, "CLI client is null");
        }
        return valuesMap;
    }
}
