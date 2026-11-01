package com.hopebridge.model.dao;

import com.hopebridge.model.db.SqlExecutor;
import com.hopebridge.model.entity.Donation;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Reads and writes the donations table.
 */
public class DonationDao {

    private static final String SELECT = """
            SELECT d.id, d.donor_id, d.campaign_id, d.category_id, d.method, d.base_amount, d.total_amount,
                   d.tax_deductible_amount, d.description, d.add_ons, d.item_description, d.check_number,
                   d.status, d.reference, d.receipt_number, d.created_at,
                   u.full_name AS donor_name, u.email AS donor_email,
                   c.title AS campaign_title, dc.name AS category_name
            FROM donations d
            JOIN users u ON u.id = d.donor_id
            JOIN donation_categories dc ON dc.id = d.category_id
            LEFT JOIN campaigns c ON c.id = d.campaign_id""";

    private final SqlExecutor sql;

    public DonationDao(SqlExecutor sql) {
        this.sql = sql;
    }

    public void insert(Donation donation) {
        int id = sql.insert("""
                        INSERT INTO donations (donor_id, campaign_id, category_id, method, base_amount, total_amount,
                                               tax_deductible_amount, description, add_ons, item_description,
                                               check_number, status, reference)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
                donation.getDonorId(), donation.getCampaignId(), donation.getCategoryId(), donation.getMethod(),
                donation.getBaseAmount(), donation.getTotalAmount(), donation.getTaxDeductibleAmount(),
                donation.getDescription(), donation.getAddOns(), donation.getItemDescription(),
                donation.getCheckNumber(), donation.getStatus(), donation.getReference());
        donation.setId(id);
        donation.setCreatedAt(LocalDateTime.now());
    }

    public void updateReceiptNumber(int donationId, String receiptNumber) {
        sql.update("UPDATE donations SET receipt_number = ? WHERE id = ?", receiptNumber, donationId);
    }

    public Optional<Donation> findById(int id) {
        return sql.queryOne(SELECT + " WHERE d.id = ?", DonationDao::map, id);
    }

    public List<Donation> findByDonor(int donorId) {
        return sql.query(SELECT + " WHERE d.donor_id = ? ORDER BY d.created_at DESC", DonationDao::map, donorId);
    }

    public List<Donation> findRecent(int limit) {
        return sql.query(SELECT + " ORDER BY d.created_at DESC OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY", DonationDao::map, limit);
    }

    // What the donor personally gave over the years - this is what their tier is based on.
    public BigDecimal lifetimeGiving(int donorId) {
        return sql.scalar("SELECT ISNULL(SUM(tax_deductible_amount), 0) FROM donations WHERE donor_id = ?",
                BigDecimal.class, donorId);
    }

    public int countByDonor(int donorId) {
        return sql.scalar("SELECT COUNT(*) FROM donations WHERE donor_id = ?", Integer.class, donorId);
    }

    private static Donation map(ResultSet rs) throws SQLException {
        Donation donation = new Donation();
        donation.setId(rs.getInt("id"));
        donation.setDonorId(rs.getInt("donor_id"));
        donation.setCampaignId(rs.getObject("campaign_id", Integer.class));
        donation.setCategoryId(rs.getInt("category_id"));
        donation.setMethod(rs.getString("method"));
        donation.setBaseAmount(rs.getBigDecimal("base_amount"));
        donation.setTotalAmount(rs.getBigDecimal("total_amount"));
        donation.setTaxDeductibleAmount(rs.getBigDecimal("tax_deductible_amount"));
        donation.setDescription(rs.getString("description"));
        donation.setAddOns(rs.getString("add_ons"));
        donation.setItemDescription(rs.getString("item_description"));
        donation.setCheckNumber(rs.getString("check_number"));
        donation.setStatus(rs.getString("status"));
        donation.setReference(rs.getString("reference"));
        donation.setReceiptNumber(rs.getString("receipt_number"));
        donation.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        donation.setDonorName(rs.getString("donor_name"));
        donation.setDonorEmail(rs.getString("donor_email"));
        donation.setCampaignTitle(rs.getString("campaign_title"));
        donation.setCategoryName(rs.getString("category_name"));
        return donation;
    }
}
