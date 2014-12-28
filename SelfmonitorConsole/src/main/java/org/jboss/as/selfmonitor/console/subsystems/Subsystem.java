package org.jboss.as.selfmonitor.console.subsystems;

import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
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
    private final List<Map.Entry<String, String>> entries = null;

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
            MetricItem item = new MetricItem(client);
            item.setName(metric);
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

}