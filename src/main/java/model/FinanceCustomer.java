package model;

/**
 *
 * @author Sean
 */
public class FinanceCustomer extends Customer {

    private boolean includesCrypto;

    // id required for updates
    public FinanceCustomer(int id, String name, String address, String zipCode, String phoneNum, Country country, FirstLevelDivision division, boolean includesCrypto) {
        super(id, name, address, zipCode, phoneNum, country, division);
        this.includesCrypto = includesCrypto;
    }

    // for creating new (no ID)
    public FinanceCustomer(String name, String address, String zipCode, String phoneNum, Country country, FirstLevelDivision division, boolean includesCrypto) {
        super(name, address, zipCode, phoneNum, country, division);
        this.includesCrypto = includesCrypto;
    }

    public boolean includesCrypto() {
        return includesCrypto;
    }

    public void setIncludesCrypto(boolean includesCrypto) {
        this.includesCrypto = includesCrypto;
    }

}

