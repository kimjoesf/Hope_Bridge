package com.hopebridge.model.dao;

import com.hopebridge.model.db.SqlExecutor;
import com.hopebridge.model.entity.Campaign;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Reads and writes the campaigns table.
 */
public class CampaignDao {

    private static final String SELECT = """
            SELECT c.id, c.title, c.summary, c.goal_amount, c.raised_amount, c.category_id, c.ends_on, c.is_active,
                   dc.name AS category_name
            FROM campaigns c
            JOIN donation_categories dc ON dc.id = c.category_id""";

    private final SqlExecutor sql;

    public CampaignDao(SqlExecutor sql) {
        this.sql = sql;
    }

    public List<Campaign> findActive() {
        return sql.query(SELECT + " WHERE c.is_active = 1 ORDER BY c.ends_on", CampaignDao::map);
    }

    public Optional<Campaign> findById(int id) {
        return sql.queryOne(SELECT + " WHERE c.id = ?", CampaignDao::map, id);
    }

    // Done in SQL (raised = raised + x) so two gifts landing together never lose one another's amount.
    public void addToRaised(int campaignId, BigDecimal amount) {
        sql.update("UPDATE campaigns SET raised_amount = raised_amount + ? WHERE id = ?", amount, campaignId);
    }

    private static Campaign map(ResultSet rs) throws SQLException {
        Campaign campaign = new Campaign();
        campaign.setId(rs.getInt("id"));
        campaign.setTitle(rs.getString("title"));
        campaign.setSummary(rs.getString("summary"));
        campaign.setGoalAmount(rs.getBigDecimal("goal_amount"));
        campaign.setRaisedAmount(rs.getBigDecimal("raised_amount"));
        campaign.setCategoryId(rs.getInt("category_id"));
        campaign.setCategoryName(rs.getString("category_name"));
        campaign.setEndsOn(rs.getObject("ends_on", LocalDate.class));
        campaign.setActive(rs.getBoolean("is_active"));
        return campaign;
    }
}