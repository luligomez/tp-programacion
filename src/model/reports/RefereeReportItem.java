package model.reports;

public class RefereeReportItem{
    private String name;
    private String country;
    private int matchesOfficiated;
    private int yearsAsReferee;

    public RefereeReportItem(String name, String country, int matchesOfficiated, int yearsAsReferee) {
        this.name = name;
        this.country = country;
        this.matchesOfficiated = matchesOfficiated;
        this.yearsAsReferee = yearsAsReferee;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public int getMatchesOfficiated() {
        return matchesOfficiated;
    }

    public int getYearsAsReferee() {
        return yearsAsReferee;
    }
}