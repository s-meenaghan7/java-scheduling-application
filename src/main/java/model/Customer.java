package model;

/**
 This class defines Customer objects.
 @author Sean
 */
public abstract class Customer {

    private int id;
    private String name;
    private String address;
    private String zipCode;
    private String phoneNum;
    private Country country;
    private FirstLevelDivision division;

    // with id, for updating
    public Customer(int id, String name, String address, String zipCode, String phoneNum, Country country, FirstLevelDivision division) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.zipCode = zipCode;
        this.phoneNum = phoneNum;
        this.country = country;
        this.division = division;
    }

    // No ID, for adding/creating Customers
    public Customer(String name, String address, String zipCode, String phoneNum, Country country, FirstLevelDivision division) {
        this.name = name;
        this.address = address;
        this.zipCode = zipCode;
        this.phoneNum = phoneNum;
        this.country = country;
        this.division = division;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setDivision(FirstLevelDivision division) {
        this.division = division;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return this.address;
    }

    public String getZipCode() {
        return this.zipCode;
    }

    public String getPhoneNum() {
        return this.phoneNum;
    }

    public FirstLevelDivision getDivision() {
        return this.division;
    }

    public Country getCountry() {
        return this.country;
    }

    @Override
    public String toString() {
        return name;
    }
}
