package com.inninglog.inninglog.journal.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ResultScore {
    WIN("승"),
    DRAW("무승부"),
    LOSE("패");

    private final String label;

    ResultScore(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static ResultScore from(String input) {
        for (ResultScore rs : ResultScore.values()) {
            if (rs.label.equals(input) || rs.name().equalsIgnoreCase(input)) {
                return rs;
            }
        }
        throw new IllegalArgumentException("Unknown result: " + input);
    }

    public static ResultScore of(int ourScore, int theirScore) {
        if (ourScore > theirScore) {
            return WIN;
        } else if (ourScore < theirScore) {
            return LOSE;
        } else {
            return DRAW;
        }
    }
}