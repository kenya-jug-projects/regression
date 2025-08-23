package com.kenyajug.regression.services;
import java.util.List;
public record LogChartData(
        List<HourlyLogStats> hourlyLogStatsList
) {
}
