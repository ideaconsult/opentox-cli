package net.idea.opentox.cli.group;

import net.idea.opentox.cli.AbstractURLResource;

public class Group extends AbstractURLResource {

    /**
     * 
     */
    private static final long serialVersionUID = 160259088707955882L;
    protected String identifier;
    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    protected String title;
    protected String groupName;
}
