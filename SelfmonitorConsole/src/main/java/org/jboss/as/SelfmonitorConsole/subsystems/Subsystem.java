package org.jboss.as.SelfmonitorConsole.subsystems;

import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.TreeNode;
import model.MetricsHandler;

/**
 *
 * @author vojtech
 */
public class Subsystem implements TreeNode {
    
    private String name;
    private final List<MetricItem> metricItems = new ArrayList<MetricItem>();

    public List<MetricItem> getMetricItems() {
        MetricsHandler metricsHandler = new MetricsHandler();
        List<String> metrics = metricsHandler.getMetricsOfSubsystem(name);
        for(String metric : metrics){
            MetricItem item = new MetricItem();
            item.setName(metric);
            item.setParent(this);
            metricItems.add(item);
        }
        return metricItems;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Subsystem other = (Subsystem) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }
    
}
