package com.auto.table.enums;

/**
 * @author ly
 */

public enum SqlserverEnum {
    INT("int", ""),
    BIGINT("bigint", ""),
    DATETIME("date", ""),
    BIT("bit", ""),
    VARCHAR("varchar", "256");
    private String type;
    private String length;


    SqlserverEnum(String type, String length) {
        this.type = type;
        this.length = length;
    }

    public String getType() {
        return type;
    }

    public String getLength() {
        return length;
    }
}
