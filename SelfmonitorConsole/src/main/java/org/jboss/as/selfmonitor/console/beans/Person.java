package org.jboss.as.selfmonitor.console.beans;

import java.util.Enumeration;
import javax.swing.tree.TreeNode;

/**
 *
 * @author vojtech
 */
public class Person implements TreeNode {

    private String name;
    private int age;
    private TreeBean simpleTreeBean;

    public TreeBean getSimpleTreeBean() {
        return simpleTreeBean;
    }

    public void setSimpleTreeBean(TreeBean simpleTreeBean) {
        this.simpleTreeBean = simpleTreeBean;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public TreeNode getChildAt(int i) {
        return null;
    }

    public int getChildCount() {
        return 0;
    }

    public TreeNode getParent() {
        return null;
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
