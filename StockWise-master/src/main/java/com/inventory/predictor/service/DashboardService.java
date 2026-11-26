package com.inventory.predictor.service;

import com.inventory.predictor.dao.DashboardDAO;
import com.inventory.predictor.model.DashboardStats;

import java.sql.SQLException;

public class DashboardService {

    private final DashboardDAO dashboardDAO;

    public DashboardService() {
        this.dashboardDAO = new DashboardDAO();
    }

    // =====================================
    // GET DASHBOARD STATISTICS
    // =====================================

    public DashboardStats getDashboardStats()
            throws SQLException {

        return dashboardDAO.getDashboardStats();
    }
}