package net.idea.opentox.cli.user;

import java.util.List;

import net.idea.opentox.cli.AbstractURLResource;
import net.idea.opentox.cli.group.Group;

public class User extends AbstractURLResource {

    /**
     * 
     */
    private static final long serialVersionUID = 160259088707955882L;

    protected String identifier;
    protected String firstName;
    protected String homepage;
    protected List<Group> organisations;
    public List<Group> getOrganisations() {
        return organisations;
    }
    public void setOrganisations(List<Group> organisations) {
        this.organisations = organisations;
    }

    protected List<Group> projects;
    public List<Group> getProjects() {
        return projects;
    }
    public void setProjects(List<Group> projects) {
        this.projects = projects;
    }
    public String getHomepage() {
        return homepage;
    }
    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    protected String lastName;
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

    protected String title;
    protected String userName;
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
