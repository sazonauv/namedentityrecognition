package io.dlminer.ner.poc;

/**
 * Created by slava on 31/08/17.
 */
public enum NamedEntityType {
    UNNAMED("unnamed"),
    PERSON("person"),
    ORGANIZATION("organization"),
    LOCATION("location"),
    TIME("time"),
    MONEY("money"),
    PERCENTAGE("percentage");


    private String type;

    NamedEntityType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static NamedEntityType findIn(String name) {
        for (NamedEntityType type : NamedEntityType.values()) {
            if (name.contains(type.getType())) {
                return type;
            }
        }
        return null;
    }
}
