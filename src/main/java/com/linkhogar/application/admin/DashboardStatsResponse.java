package com.linkhogar.application.admin;

public record DashboardStatsResponse(
        long totalUsers,
        long pendingHouses,
        long publishedHouses
) {}
