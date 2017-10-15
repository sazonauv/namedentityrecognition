package io.dlminer.ner.poc;


import static io.dlminer.ner.poc.NamedEntityType.*;

/**
 * Created by slava on 27/08/17.
 */
public class TagConfig {

    private String notNamedTag;
    private String personTag;
    private String orgTag;
    private String locationTag;
    private String timeTag;
    private String moneyTag;
    private String percentTag;


    public String getNotNamedTag() {
        return notNamedTag;
    }

    public void setNotNamedTag(String notNamedTag) {
        this.notNamedTag = notNamedTag;
    }

    public String getPersonTag() {
        return personTag;
    }

    public void setPersonTag(String personTag) {
        this.personTag = personTag;
    }

    public String getOrgTag() {
        return orgTag;
    }

    public void setOrgTag(String orgTag) {
        this.orgTag = orgTag;
    }

    public String getLocationTag() {
        return locationTag;
    }

    public void setLocationTag(String locationTag) {
        this.locationTag = locationTag;
    }

    public String getTimeTag() {
        return timeTag;
    }

    public void setTimeTag(String timeTag) {
        this.timeTag = timeTag;
    }

    public String getMoneyTag() {
        return moneyTag;
    }

    public void setMoneyTag(String moneyTag) {
        this.moneyTag = moneyTag;
    }

    public String getPercentTag() {
        return percentTag;
    }

    public void setPercentTag(String percentTag) {
        this.percentTag = percentTag;
    }


    public String get(NamedEntityType type) {
        if (type.equals(PERSON)) {
            return personTag;
        } else if (type.equals(ORGANIZATION)) {
            return orgTag;
        } else if (type.equals(LOCATION)) {
            return locationTag;
        } else if (type.equals(TIME)) {
            return timeTag;
        } else if (type.equals(MONEY)) {
            return moneyTag;
        } else if (type.equals(PERCENTAGE)) {
            return percentTag;
        } else if (type.equals(UNNAMED)) {
            return notNamedTag;
        } else {
            return null;
        }
    }


    public String match(String tag) {
        if (notNamedTag != null && tag.equals(notNamedTag)) {
            return notNamedTag;
        } else if (personTag != null && tag.contains(personTag)) {
            return personTag;
        } else if (orgTag != null && tag.contains(orgTag)) {
            return orgTag;
        } else if (locationTag != null && tag.contains(locationTag)) {
            return locationTag;
        } else if (timeTag != null && tag.contains(timeTag)) {
            return timeTag;
        } else if (moneyTag != null && tag.contains(moneyTag)) {
            return moneyTag;
        } else if (percentTag != null && tag.contains(percentTag)) {
            return percentTag;
        } else {
            return notNamedTag;
        }
    }


    public NamedEntityType getTypeOf(String tag) {
        if (tag.equals(notNamedTag)) {
            return UNNAMED;
        } else if (tag.equals(personTag)) {
            return PERSON;
        } else if (tag.equals(orgTag)) {
            return ORGANIZATION;
        } else if (tag.equals(locationTag)) {
            return LOCATION;
        } else if (tag.equals(timeTag)) {
            return TIME;
        } else if (tag.equals(moneyTag)) {
            return MONEY;
        } else if (tag.equals(percentTag)) {
            return PERCENTAGE;
        } else {
            return null;
        }
    }


    public boolean isUnnamed(String tag) {
        return notNamedTag.equals(match(tag));
    }


    public static String getStartTag(NamedEntityType type) {
        return "<START:" + type.getType() + ">";
    }

    public static String getEndTag() {
        return "<END>";
    }


}
