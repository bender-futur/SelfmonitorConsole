package org.jboss.as.SelfmonitorConsole.subsystems;

import java.util.Enumeration;
import javax.swing.tree.TreeNode;

/**
 *
 * @author vojtech
 */
public class MetricItem implements TreeNode {

    private Subsystem subsystem;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Subsystem getSubsystem() {
        return subsystem;
    }
    
    public TreeNode getChildAt(int i) {
        return null;
    }

    public int getChildCount() {
        return 0;
    }

    public TreeNode getParent() {
        return subsystem;
    }
    
    public void setParent(Subsystem subsystem) {
        this.subsystem = subsystem;
    }

    public int getIndex(TreeNode tn) {
        return 0;
    }

    public boolean getAllowsChildren() {
        return false;
    }

    public boolean isLeaf() {
        return true;
    }

    public Enumeration children() {
        return null;
    }
    
}
