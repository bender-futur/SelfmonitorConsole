package org.jboss.as.selfmonitor.console.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.swing.tree.TreeNode;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.selfmonitor.console.model.MetricValuesHandler;
import org.jboss.as.selfmonitor.console.subsystems.MetricItem;

/**
 *
 * @author vojtech
 */
@ManagedBean(name = "contentBean")
@SessionScoped
public class ContentBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private TreeNode currentSelection;
    private ModelControllerClient client;
    private boolean filterDate = false;
    private Date dateFrom;
    private Map<String, String> data;

    public void setClient(ModelControllerClient client) {
        this.client = client;
    }
    
    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }
    
    public boolean isFilterDate() {
        return filterDate;
    }

    public void setFilterDate(boolean filterDate) {
        this.filterDate = filterDate;
    }
    
    public void filterDateListener(){
        if(filterDate){
            Logger.getLogger(ContentBean.class.getName()).log(Level.INFO, "Debug: setting to false");
            setFilterDate(false);
        }
        else{
            Logger.getLogger(ContentBean.class.getName()).log(Level.INFO, "Debug: setting to true");
            setFilterDate(true);
        }
    }
    
    public TreeNode getCurrentSelection() {
        return currentSelection;
    }
 
    public void setCurrentSelection(TreeNode currentSelection) {
        this.currentSelection = currentSelection;
    }
    
    public Map<String, String> getData() {
        return data;
    }
    
    public void setData() {
        data = null;
        if(currentSelection != null && currentSelection instanceof MetricItem){
            MetricValuesHandler valuesHandler = new MetricValuesHandler(client);
            MetricItem currsel = (MetricItem) currentSelection;
            data = valuesHandler.getAllValues(currsel.getName());
        }
    }
    
}
