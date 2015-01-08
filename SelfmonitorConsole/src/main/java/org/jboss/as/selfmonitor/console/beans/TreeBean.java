package org.jboss.as.selfmonitor.console.beans;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.swing.tree.TreeNode;
import org.jboss.as.selfmonitor.console.model.MetricsHandler;
import org.jboss.as.selfmonitor.console.subsystems.Subsystem;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.selfmonitor.console.model.ModelHandler;
import org.richfaces.component.AbstractTree;
import org.richfaces.event.TreeSelectionChangeEvent;
import org.jboss.as.selfmonitor.console.util.MetricUtil;

/**
 *
 * @author Vojtech Schlemmer
 */
@ManagedBean(name = "treeBean")
@ViewScoped
public class TreeBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private final String HOST = "localhost";
    private final int PORT = 9990;
    @ManagedProperty(value="#{contentBean}")
    private ContentBean contentBean;
    private ModelControllerClient client;
    private List<TreeNode> subsystemNodes;
    private List<String> metrics;
    private MetricsHandler metricsHandler;
    private TreeNode currentSelection = null;
    private String serverName;

    @PostConstruct
    public void init() {
        try {
            client = ModelControllerClient.Factory.create(
                    InetAddress.getByName(HOST), PORT);
        } catch (UnknownHostException ex) {
            Logger.getLogger(TreeBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        metricsHandler = new MetricsHandler(client);
        subsystemNodes = new ArrayList<TreeNode>();
        metrics = metricsHandler.getAllMetrics();
        contentBean.setClient(client);
        setServerName(ModelHandler.getServerName(client));
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public ContentBean getContentBean() {
        return contentBean;
    }

    public void setContentBean(ContentBean contentBean) {
        this.contentBean = contentBean;
    }
    
    public ModelControllerClient getClient() {
        return client;
    }
    
    public void selectionChanged(TreeSelectionChangeEvent selectionChangeEvent) {
        List<Object> selection = new ArrayList<Object>(selectionChangeEvent.getNewSelection());
        Object currentSelectionKey = selection.get(0);
        AbstractTree tree = (AbstractTree) selectionChangeEvent.getSource();
        Object storedKey = tree.getRowKey();
        tree.setRowKey(currentSelectionKey);
        currentSelection = (TreeNode) tree.getRowData();
        tree.setRowKey(storedKey);
        contentBean.setCurrentSelection(currentSelection);
        contentBean.setData();
    }
    
    public List<TreeNode> getSubsystemNodes(){
        for(String metric : metrics){
            String metricSubsystemName = MetricUtil.getSubsystemName(metric);
            Subsystem subsystem = new Subsystem(client);
            if(!metricSubsystemName.equals("")){
                subsystem.setName(metricSubsystemName);
                subsystem.setMetricItems();
            }
            else{
                subsystem.setName(metric);
            }
            if(!subsystemNodes.contains(subsystem)){
                subsystemNodes.add(subsystem);
            }
        }
        return subsystemNodes;
    }
    
    public TreeNode getCurrentSelection() {
        return currentSelection;
    }
 
    public void setCurrentSelection(TreeNode currentSelection) {
        this.currentSelection = currentSelection;
        contentBean.setCurrentSelection(currentSelection);
    }
            
}