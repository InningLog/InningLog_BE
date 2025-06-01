package com.inninglog.inninglog.member.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MemberType {
    NEWBIE("뉴비"),
    VETERAN("고인물");

    private final String description;

    MemberType(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static MemberType from(String input) {
        for (MemberType type : MemberType.values()) {
            if (type.description.equals(input)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown user type: " + input);
    }
}