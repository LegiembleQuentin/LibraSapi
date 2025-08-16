package com.libra_s.libraS.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookStatistics {
    private int totalUsers;
    private double averageVolume;
    private int usersInProgress;
    private int usersCompleted;
    private int usersNotStarted;
    private double averageProgress;
    private double completionRate;
    
    private int activeUsersLast7Days;
    private int activeUsersLast30Days;
    private double engagementTrend;
    private int activeUsersThisMonth;
    private int activeUsersLastMonth;
    private int newReadersThisMonth;
}
