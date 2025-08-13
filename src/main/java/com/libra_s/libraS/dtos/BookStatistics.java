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
}
