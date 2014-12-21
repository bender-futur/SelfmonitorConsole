package org.jboss.as.SelfmonitorConsole;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.swing.tree.TreeNode;
import model.MetricsHandler;
import org.jboss.as.SelfmonitorConsole.subsystems.Subsystem;
import org.jboss.as.controller.client.ModelControllerClient;
import org.richfaces.component.AbstractTree;
import org.richfaces.event.TreeSelectionChangeEvent;
import util.MetricUtil;

@ManagedBean(name = "treeBean")
@SessionScoped
public class TreeBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private final String HOST = "localhost";
    private final int PORT = 9990;
    private ModelControllerClient client;
    private List<TreeNode> subsystemNodes;
    private List<TreeNode> rootNodes;
    private List<String> metrics;
    private MetricsHandler metricsHandler;
    private TreeNode currentSelection = null;
    
    @PostConstruct
    public void init() {
        try {
            client = ModelControllerClient.Factory.create(
                    InetAddress.getByName(HOST), PORT);
        } catch (UnknownHostException ex) {
            Logger.getLogger(TreeBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        metricsHandler = new MetricsHandler();
        subsystemNodes = new ArrayList<TreeNode>();
        rootNodes = new ArrayList<TreeNode>();
        metrics = metricsHandler.getAllMetrics();
    }
    
    public void selectionChanged(TreeSelectionChangeEvent selectionChangeEvent) {
        // considering only single selection
        List<Object> selection = new ArrayList<Object>(selectionChangeEvent.getNewSelection());
        Object currentSelectionKey = selection.get(0);
        AbstractTree tree = (AbstractTree) selectionChangeEvent.getSource();
 
        Object storedKey = tree.getRowKey();
        tree.setRowKey(currentSelectionKey);
        currentSelection = (TreeNode) tree.getRowData();
        tree.setRowKey(storedKey);
    }
    
    public List<TreeNode> getSubsystemNodes(){
        for(String metric : metrics){
            String metricSubsystemName = MetricUtil.getSubsystemName(metric);
            if(!metricSubsystemName.equals("")){
                Subsystem subsystem = new Subsystem();
                subsystem.setName(metricSubsystemName);
                if(!subsystemNodes.contains(subsystem)){
                    subsystemNodes.add(subsystem);
                }
            }
        }
        return subsystemNodes;
    }
    
    public List<TreeNode> getRootNodes(){
        for(String metric : metrics){
            String metricSubsystemName = MetricUtil.getSubsystemName(metric);
            if(metricSubsystemName.equals("")){
                Subsystem subsystem = new Subsystem();
                subsystem.setName(metric);
                if(!rootNodes.contains(subsystem)){
                    rootNodes.add(subsystem);
                }
            }
        }
        return rootNodes;
    }
    
    public TreeNode getCurrentSelection() {
        return currentSelection;
    }
 
    public void setCurrentSelection(TreeNode currentSelection) {
        this.currentSelection = currentSelection;
    }
    
            
}