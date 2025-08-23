package com.kenyajug.regression.services;

import java.util.List;

public record HourlyLogStats(
        String severity,
        List<Long> hourlyCounts
) {
}
