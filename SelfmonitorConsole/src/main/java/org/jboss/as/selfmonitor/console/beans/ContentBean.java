package org.jboss.as.selfmonitor.console.beans;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.swing.tree.TreeNode;
import org.apache.commons.lang3.time.DateUtils;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.selfmonitor.console.model.MetricValuesHandler;
import org.jboss.as.selfmonitor.console.subsystems.MetricItem;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;


/**
 *
 * @author Vojtech Schlemmer
 */
@ManagedBean(name = "contentBean")
@ViewScoped
public class ContentBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private TreeNode currentSelection;
    private ModelControllerClient client;
    private Date dateFrom = null;
    private Date dateTo = null;
    private String timeFrom = null;
    private String timeTo = null;
    private Map<String, String> data;
    private String minVal = "";
    private String minDate = "";
    private String maxVal = "";
    private String maxDate = "";
    private String avgVal = "";
    private String median = "";
    private boolean numericValues = true;
    private CartesianChartModel lineModel;

    public CartesianChartModel getLineModel() {
        createLineModel();
        return lineModel;
    }

    public void setLineModel(CartesianChartModel lineModel) {
        this.lineModel = lineModel;
    }

    public String getMinVal() {
        return minVal;
    }

    public void setMinVal(String minVal) {
        this.minVal = minVal;
    }

    public String getMaxVal() {
        return maxVal;
    }

    public void setMaxVal(String maxVal) {
        this.maxVal = maxVal;
    }

    public String getAvgVal() {
        return avgVal;
    }

    public void setAvgVal(String avgVal) {
        this.avgVal = avgVal;
    }

    public String getMedian() {
        return median;
    }

    public void setMedian(String median) {
        this.median = median;
    }

    public boolean isNumericValues() {
        return numericValues;
    }

    public void setNumericValues(boolean numericValues) {
        this.numericValues = numericValues;
    }
    
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
            data = valuesHandler.getValuesBetweenDates(currsel.getMetricId(), 
                    dateFromWithTime, dateToWithTime);
            if(!data.isEmpty()){
                setStatistics();
            }
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
            data = valuesHandler.getAllValues(currsel.getMetricId());
            if(!data.isEmpty()){
                setStatistics();
            }
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
    
    private void setStatistics(){
        long acc = 0;
        long min = 0;
        long max = 0;
        int counter = 0;
        Map<String, Long> metricSortedValues;
        metricSortedValues = new TreeMap();
        for (Map.Entry<String, String> entry : data.entrySet()){
            long value = getNumericValue(entry.getValue());
            metricSortedValues.put(entry.getKey(), value);
            if(counter == 0){
                min = value;
                max = value;
                minDate = entry.getKey();
            }
            else{
                if (value < min) {
                    min = value;
                }
                if (value > max) {
                    max = value;
                }
                if(counter == data.size()-1){
                    maxDate = entry.getKey();
                }
            }
            acc += value;
            counter++;
        }
        minVal = String.valueOf(min);
        maxVal = String.valueOf(max);
        if (data.size() > 0){
            avgVal = String.valueOf(acc/data.size());
        }
        median = getMedianValue(metricSortedValues);
    }
    
    private String getMedianValue(Map<String, Long> metricSortedValues){
        List<Long> values = new ArrayList(metricSortedValues.values());
        Long medianValue = null;
        if(metricSortedValues.size()%2 == 0){
            int firstPosition = (metricSortedValues.size()+1)/2;
            int secondPosition = (metricSortedValues.size()-1)/2;
            Long firstValue = values.get(firstPosition);
            Long secondValue = values.get(secondPosition);
            medianValue = (firstValue+secondValue)/2;
        }
        else{
            int medianPosition = (metricSortedValues.size()-1)/2;
            medianValue = values.get(medianPosition);
        }
        return String.valueOf(medianValue);
    }
    
    private void createLineModel(){
        MetricItem currsel = (MetricItem) currentSelection;
        lineModel = initLinearModel(currsel.getName());
        lineModel.setTitle(currsel.getName());
        lineModel.setLegendPosition("e");
        Axis yAxis = lineModel.getAxis(AxisType.Y);
        yAxis.setMin(getNumericValue(minVal));
        yAxis.setMax(getNumericValue(maxVal));
        DateAxis xAxis = new DateAxis("Dates");
        xAxis.setTickAngle(-50);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd.hh:mm:ss");
        Date resultMinDate = null;
        try {  
            resultMinDate = df.parse(minDate);
        } catch (ParseException ex) {
            Logger.getLogger(ContentBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        Date resultMaxDate = null;
        try {  
            resultMaxDate = df.parse(maxDate);
        } catch (ParseException ex) {
            Logger.getLogger(ContentBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(resultMinDate != null){
            xAxis.setMin(resultMinDate.getTime());
        }
        if(resultMaxDate != null){
            xAxis.setMax(resultMaxDate.getTime());
        }
        xAxis.setTickFormat("%Y-%m-%d.%H:%M:%S");
        lineModel.getAxes().put(AxisType.X, xAxis);
    }
    
    private LineChartModel initLinearModel(String name) {
        LineChartModel model = new LineChartModel();
        ChartSeries line = new ChartSeries();
        line.setLabel(name);
        for (Map.Entry<String, String> entry : data.entrySet()){
            long numValue = getNumericValue(entry.getValue());
            String label = entry.getKey();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd.hh:mm:ss");
            Date result = null;
            try {  
                result =  df.parse(label);
            } catch (ParseException ex) {
                Logger.getLogger(ContentBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(result != null){
                line.set(result.getTime(), numValue);
            }
            else{
                line.set("null", numValue);
            }
        }
        model.addSeries(line);
        return model;
    }
    
    private long getNumericValue(String value){
        if(value.length() > 0 && value.charAt(value.length()-1)=='L') {
            value = value.substring(0, value.length()-1);
        }
        long numericValue = 0;
        try{
            numericValue = Long.parseLong(value, 10);
            numericValues = true;
        }catch(NumberFormatException ex){
            String message = "Cannot calculate statistic of non-numeric value: " 
                    + value;
            Logger.getLogger(ContentBean.class.getName()).log(
                Level.SEVERE, message);
            numericValues = false;
        }
        return numericValue;
    }
    
}
