package com.hopebridge.model.dao;

import com.hopebridge.model.db.SqlExecutor;
import com.hopebridge.model.dto.ImpactStats;
import com.hopebridge.model.entity.DonationStatus;

/**
 * The organization-wide numbers shown on the landing page and the staff dashboard,
 * gathered in a single round trip.
 */
public class StatsDao {

    private final SqlExecutor sql;

    public StatsDao(SqlExecutor sql) {
        this.sql = sql;
    }

    public ImpactStats impact() {
        return sql.queryOne("""
                SELECT (SELECT ISNULL(SUM(total_amount), 0) FROM donations)                  AS total_raised,
                       (SELECT COUNT(DISTINCT donor_id) FROM donations)                      AS donor_count,
                       (SELECT ISNULL(SUM(hours), 0) FROM volunteer_hours)                   AS volunteer_hours,
                       (SELECT COUNT(*) FROM events WHERE starts_at >= SYSDATETIME())        AS upcoming_events,
                       (SELECT COUNT(*) FROM campaigns WHERE is_active = 1)                  AS active_campaigns,
                       (SELECT COUNT(*) FROM donations WHERE status <> ?)                    AS open_donations""",
                rs -> new ImpactStats(rs.getBigDecimal("total_raised"), rs.getInt("donor_count"),
                        rs.getBigDecimal("volunteer_hours"), rs.getInt("upcoming_events"),
                        rs.getInt("active_campaigns"), rs.getInt("open_donations")),
                DonationStatus.CONFIRMED).orElseThrow();
    }
}
