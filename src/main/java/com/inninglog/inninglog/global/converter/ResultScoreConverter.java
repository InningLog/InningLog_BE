package com.inninglog.inninglog.global.converter;

import com.inninglog.inninglog.domain.journal.domain.ResultScore;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ResultScoreConverter implements Converter<String, ResultScore> {
    @Override
    public ResultScore convert(String source) {
        return ResultScore.from(source);
    }
}