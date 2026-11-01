package com.hopebridge.model.service;

import com.hopebridge.model.dao.StatsDao;
import com.hopebridge.model.dto.ImpactStats;

/**
 * The organization's headline impact numbers.
 */
public class ImpactService {

    private final StatsDao statsDao;

    public ImpactService(StatsDao statsDao) {
        this.statsDao = statsDao;
    }

    public ImpactStats snapshot() {
        return statsDao.impact();
    }
}
