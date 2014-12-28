package org.jboss.as.selfmonitor.console.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.as.selfmonitor.console.beans.TreeBean;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.ClientConstants;
import static org.jboss.as.controller.client.helpers.ClientConstants.OP_ADDR;
import static org.jboss.as.controller.client.helpers.ClientConstants.SUBSYSTEM;
import org.jboss.dmr.ModelNode;

/**
 *
 * @author vojtech
 */
public class MetricsHandler {

    private final ModelControllerClient client;
    
    public MetricsHandler(ModelControllerClient client){
        this.client = client;
    }
    
    public List<String> getAllMetrics(){
        List<String> enabledMetrics = getMetrics(true);
        List<String> disabledMetrics = getMetrics(false);
        List<String> allMetrics = enabledMetrics;
        allMetrics.addAll(disabledMetrics);
        return allMetrics;
    }
    
    public List<String> getMetrics(boolean enabled){
        List<String> enabledMetrics = new ArrayList<String>();
        ModelNode op = new ModelNode();
        op.get(OP_ADDR).set(PathAddress.pathAddress(PathElement.pathElement(
            SUBSYSTEM, "selfmonitor")).toModelNode());
        op.get(ClientConstants.OP).set("read-metrics");
        op.get("show-enabled").set(enabled ? "true" : "false");
        ModelNode result = null;
		try {
			result = client.execute(op).get(ClientConstants.RESULT);
		} catch (IOException e) {
            Logger.getLogger(TreeBean.class.getName()).log(Level.SEVERE, null, e);
		}
		if(result != null){
			for(ModelNode resultItem : result.asList()){
                enabledMetrics.add(resultItem.get("id").asString());
            }
		}
        return enabledMetrics;
    }
    
    public List<String> getMetricsOfSubsystem(String subsystem){
        List<String> resultMetrics = new ArrayList<String>();
        String[] subsystemParts = subsystem.split("_");
        List<String> metrics = getAllMetrics();
        for(String metric : metrics){
            String[] metricParts = metric.split("_");
            if(metricParts[0].equals(subsystemParts[0]) &&
               metricParts[1].equals(subsystemParts[1])){
                resultMetrics.add(metric);
            }
        }
        return resultMetrics;
    }
}
