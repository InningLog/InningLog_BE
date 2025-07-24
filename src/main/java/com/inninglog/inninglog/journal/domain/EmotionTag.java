package com.inninglog.inninglog.journal.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EmotionTag {
    TOUCHED("감동"),
    EXCITED("짜릿함"),
    FRUSTRATED("답답함"),
    REGRETFUL("아쉬움"),
    ANGRY("분노"),
    SATISFIED("흡족");

    private final String description;

    EmotionTag(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static EmotionTag from(String input) {
        for (EmotionTag tag : EmotionTag.values()) {
            if (tag.description.equals(input)) {
                return tag;
            }
        }
        throw new IllegalArgumentException("Unknown emotion tag: " + input);
    }
}