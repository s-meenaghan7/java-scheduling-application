package model;

/**
 *
 * @author Sean
 */
public class ProjectCustomer extends Customer {

    private String projectName;

    // ID required for updating
    public ProjectCustomer(int id, String name, String address, String zipCode, String phoneNum, Country country, FirstLevelDivision division, String projectName) {
        super(id, name, address, zipCode, phoneNum, country, division);
        this.projectName = projectName;
    }

    // no ID for creating new
    public ProjectCustomer(String name, String address, String zipCode, String phoneNum, Country country, FirstLevelDivision division, String projectName) {
        super(name, address, zipCode, phoneNum, country, division);
        this.projectName = projectName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

}
