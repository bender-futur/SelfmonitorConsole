package org.jboss.as.selfmonitor.console.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.swing.tree.TreeNode;
import org.apache.commons.lang3.time.DateUtils;
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
    private Date dateFrom = null;
    private Date dateTo = null;
    private String timeFrom = null;
    private String timeTo = null;
    private Map<String, String> data;
    
    public void setClient(ModelControllerClient client) {
        this.client = client;
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(String timeFrom) {
        this.timeFrom = timeFrom;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(String timeTo) {
        this.timeTo = timeTo;
    }
    
    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }
    
    public void filterDateListener(){
        data = null;
        if(currentSelection != null && currentSelection instanceof MetricItem){
            MetricValuesHandler valuesHandler = new MetricValuesHandler(client);
            MetricItem currsel = (MetricItem) currentSelection;
            Date dateFromWithTime = addTimeToDate(dateFrom, timeFrom);
            Date dateToWithTime = addTimeToDate(dateTo, timeTo);
            data = valuesHandler.getValuesBetweenDates(currsel.getName(), 
                    dateFromWithTime, dateToWithTime);
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
    
    private Date addTimeToDate(Date date, String time){
        String[] parts = time.split(":");
        Date newDate = new Date();
        if(parts.length == 3){
            newDate = DateUtils.addHours(date, Integer.parseInt(parts[0]));
            newDate = DateUtils.addMinutes(newDate, Integer.parseInt(parts[1]));
            newDate = DateUtils.addSeconds(newDate, Integer.parseInt(parts[2]));
        }
        return newDate;
    }
}
