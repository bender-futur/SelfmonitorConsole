package org.jboss.as.selfmonitor.console.model;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    protected final String DATE_FORMAT = "yyyy-MM-dd.HH:mm:ss";
    
    public MetricValuesHandler(ModelControllerClient client){
        this.client = client;
    }
    
    public Map<String,String> getAllValues(String metricId){
        Map<String,String> valuesMap = new TreeMap<String, String>();
        PathAddress addr = PathAddress.pathAddress(SUBSYSTEM_PATH, 
                PathElement.pathElement("metric", metricId));
        ModelNode op = new ModelNode();
        op.get(OP_ADDR).set(addr.toModelNode());
        op.get(ClientConstants.OP).set("read-all-values");
        ModelNode result = null;
        if(client != null){
            try {
                result = client.execute(op).get(ClientConstants.RESULT);
            } catch (IOException e) {
                Logger.getLogger(MetricValuesHandler.class.getName()).log(
                        Level.SEVERE, null, e);
            }
            if(result != null && result.getType().equals(ModelType.LIST)){
                for(ModelNode resultItem : result.asList()){
                    valuesMap.put(resultItem.get("time").asString(), 
                            resultItem.get("value").asString());
                }
            }
        }
        else{
            Logger.getLogger(MetricValuesHandler.class.getName()).log(
                    Level.SEVERE, "CLI client is null");
        }
        return valuesMap;
    }
    
    public Map<String,String> getValuesBetweenDates(String metricId, 
            Date dateFrom, Date dateTo){
        Map<String,String> allValues = getAllValues(metricId);
        Map<String,String> resultMap = new TreeMap<String, String>();
        Date actualDate;
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        boolean lowerBoundCond;
        boolean higherBoundCond;
        for(Map.Entry<String, String> entry : allValues.entrySet()){
            lowerBoundCond = true;
            higherBoundCond = true;
            actualDate = null;
            try {
                actualDate = dateFormat.parse(entry.getKey());
            } catch (ParseException ex) {
                Logger.getLogger(MetricValuesHandler.class.getName()).log(Level.SEVERE, 
                        "Could not parse date, expecting format " + DATE_FORMAT);
            }
            if(actualDate != null){
                if(dateFrom != null &&
                   dateFrom.getTime() > actualDate.getTime()){
                    lowerBoundCond = false;
                }
                if(dateTo != null &&
                   dateTo.getTime() < actualDate.getTime()){
                    higherBoundCond = false;
                }
            }
            if(lowerBoundCond && higherBoundCond){
                resultMap.put(entry.getKey(), entry.getValue());
            }
        }
        return resultMap;
    }
    
}
