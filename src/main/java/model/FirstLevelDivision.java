package model;

/**
 This class defines FirstLevelDivision objects.
 @author Sean
 */
public class FirstLevelDivision {

    private int divId;
    private String division; // i.e. state, province, or territory name
    private int countryId;

    public FirstLevelDivision(int divId, String division, int countryId) {
        this.divId = divId;
        this.division = division;
        this.countryId = countryId;
    }

    public int getDivId() {
        return divId;
    }

    public void setDivId(int divId) {
        this.divId = divId;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    @Override
    public String toString() {
        return division;
    }

}

