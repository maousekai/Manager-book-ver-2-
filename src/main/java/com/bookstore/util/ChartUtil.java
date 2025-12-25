package com.bookstore.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class ChartUtil {
    public static JFreeChart createRevenueLineChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        return ChartFactory.createLineChart("Monthly Revenue", "Month", "Revenue", dataset);
    }

}