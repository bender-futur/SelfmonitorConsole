package org.jboss.as.selfmonitor.console.subsystems;

import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.TreeNode;
import org.jboss.as.selfmonitor.console.model.MetricsHandler;
import org.jboss.as.controller.client.ModelControllerClient;

/**
 *
 * @author vojtech
 */
public class Subsystem extends NamedNode implements TreeNode {
    
    private final List<MetricItem> metricItems = new ArrayList<MetricItem>();
    private final ModelControllerClient client;

    public Subsystem(ModelControllerClient client) {
        this.setType("subsystem");
        this.client = client;
    }
    
    public List<MetricItem> getMetricItems() {
        return metricItems;
    }
    
    public void setMetricItems() {
        MetricsHandler metricsHandler = new MetricsHandler(client);
        List<String> metrics = metricsHandler.getMetricsOfSubsystem(name);
        for(String metric : metrics){
            MetricItem item = new MetricItem();
            item.setName(metric);
            item.setShortName(createShortName(metric));
            item.setParent(this);
            metricItems.add(item);
        }
    }

    public TreeNode getChildAt(int i) {
        return metricItems.get(i);
    }

    public int getChildCount() {
        return metricItems.size();
    }

    public TreeNode getParent() {
        return null;
    }

    public int getIndex(TreeNode tn) {
        return metricItems.indexOf(tn);
    }

    public boolean getAllowsChildren() {
        return true;
    }

    public boolean isLeaf() {
        return metricItems.isEmpty();
    }

    public Enumeration children() {
        return Iterators.asEnumeration(metricItems.iterator());
    }   
    
    private String createShortName(String longName){
        String[] parts = longName.split("_");
        StringBuilder tmp = new StringBuilder();
        if(parts.length > 1){
            for(int i = 2; i < parts.length; i++){
                tmp.append(parts[i]);
                if(i != parts.length-1){
                    tmp.append("_");
                }
            }
            
            //debug
//            String message = "Created name " + tmp.toString() + " from " + longName;
//            Logger.getLogger(Subsystem.class.getName()).log(Level.INFO, message);
            
            return tmp.toString();
        }
        else{
            return longName;
        }
    }

}
