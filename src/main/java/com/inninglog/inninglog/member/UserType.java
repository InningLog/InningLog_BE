package com.inninglog.inninglog.member;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserType {
    NEWBIE("뉴비"),
    VETERAN("고인물");

    private final String description;

    UserType(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static UserType from(String input) {
        for (UserType type : UserType.values()) {
            if (type.description.equals(input)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown user type: " + input);
    }
}