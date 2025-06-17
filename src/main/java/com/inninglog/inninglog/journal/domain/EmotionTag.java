package com.inninglog.inninglog.journal.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.inninglog.inninglog.global.entity.BaseTimeEntity;

public enum EmotionTag  {
    HAPPY("기쁨"),        // 😆
    SAD("슬픔"),          // 😢
    FRUSTRATED("짜증"),   // 😤
    EXCITED("흥분"),      // 🤩
    PROUD("자랑스러움"), // 😎
    TOUCHED("감동"),      // 🥹
    SHOCKED("충격"),      // 🤯
    BORED("지루함"),      // 😐
    PEACEFUL("평온함");   // 😇

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