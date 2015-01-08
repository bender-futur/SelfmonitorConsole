package org.jboss.as.selfmonitor.console.subsystems;

import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.TreeNode;
import org.jboss.as.selfmonitor.console.model.MetricsHandler;
import org.jboss.as.controller.client.ModelControllerClient;

/**
 *
 * @author Vojtech Schlemmer
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
        boolean firstIter = true;
        for(String metric : metrics){
            MetricItem item = new MetricItem();
            item.setMetricId(metric);
            String itemShortName = createShortName(metric);
            item.setName(itemShortName);
            item.setShortName(itemShortName);
            item.setParent(this);
            metricItems.add(item);
            firstIter = false;
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
            return tmp.toString();
        }
        else{
            return longName;
        }
    }

}
