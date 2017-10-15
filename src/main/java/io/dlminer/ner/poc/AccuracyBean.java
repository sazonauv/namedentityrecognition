package io.dlminer.ner.poc;

/**
 * Created by slava on 27/08/17.
 */
public class AccuracyBean {

    private Double overallPrecision;

    private Double overallRecall;

    private Double personPrecision;

    private Double personRecall;

    private Double orgPrecision;

    private Double orgRecall;

    private Double locationPrecision;

    private Double locationRecall;

    private Double timePrecision;

    private Double timeRecall;

    private Double moneyPrecision;

    private Double moneyRecall;

    private Double percentPrecision;

    private Double percentRecall;


    public Double getOverallPrecision() {
        return overallPrecision;
    }

    public void setOverallPrecision(Double overallPrecision) {
        this.overallPrecision = overallPrecision;
    }

    public Double getOverallRecall() {
        return overallRecall;
    }

    public void setOverallRecall(Double overallRecall) {
        this.overallRecall = overallRecall;
    }

    public Double getPersonPrecision() {
        return personPrecision;
    }

    public void setPersonPrecision(Double personPrecision) {
        this.personPrecision = personPrecision;
    }

    public Double getPersonRecall() {
        return personRecall;
    }

    public void setPersonRecall(Double personRecall) {
        this.personRecall = personRecall;
    }

    public Double getOrgPrecision() {
        return orgPrecision;
    }

    public void setOrgPrecision(Double orgPrecision) {
        this.orgPrecision = orgPrecision;
    }

    public Double getOrgRecall() {
        return orgRecall;
    }

    public void setOrgRecall(Double orgRecall) {
        this.orgRecall = orgRecall;
    }

    public Double getLocationPrecision() {
        return locationPrecision;
    }

    public void setLocationPrecision(Double locationPrecision) {
        this.locationPrecision = locationPrecision;
    }

    public Double getLocationRecall() {
        return locationRecall;
    }

    public void setLocationRecall(Double locationRecall) {
        this.locationRecall = locationRecall;
    }

    public Double getTimePrecision() {
        return timePrecision;
    }

    public void setTimePrecision(Double timePrecision) {
        this.timePrecision = timePrecision;
    }

    public Double getTimeRecall() {
        return timeRecall;
    }

    public void setTimeRecall(Double timeRecall) {
        this.timeRecall = timeRecall;
    }

    public Double getMoneyPrecision() {
        return moneyPrecision;
    }

    public void setMoneyPrecision(Double moneyPrecision) {
        this.moneyPrecision = moneyPrecision;
    }

    public Double getMoneyRecall() {
        return moneyRecall;
    }

    public void setMoneyRecall(Double moneyRecall) {
        this.moneyRecall = moneyRecall;
    }

    public Double getPercentPrecision() {
        return percentPrecision;
    }

    public void setPercentPrecision(Double percentPrecision) {
        this.percentPrecision = percentPrecision;
    }

    public Double getPercentRecall() {
        return percentRecall;
    }

    public void setPercentRecall(Double percentRecall) {
        this.percentRecall = percentRecall;
    }

    @Override
    public String toString() {
        return "AccuracyBean{" +
                "\noverallPrecision=" + overallPrecision +
                ", \noverallRecall=" + overallRecall +
                ", \npersonPrecision=" + personPrecision +
                ", \npersonRecall=" + personRecall +
                ", \norgPrecision=" + orgPrecision +
                ", \norgRecall=" + orgRecall +
                ", \nlocationPrecision=" + locationPrecision +
                ", \nlocationRecall=" + locationRecall +
                ", \ntimePrecision=" + timePrecision +
                ", \ntimeRecall=" + timeRecall +
                ", \nmoneyPrecision=" + moneyPrecision +
                ", \nmoneyRecall=" + moneyRecall +
                ", \npercentPrecision=" + percentPrecision +
                ", \npercentRecall=" + percentRecall +
                "\n}";
    }
}
