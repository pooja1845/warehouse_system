package com.wms.controllers;

import com.wms.dao.ReportDAO;
import com.wms.views.ReportsView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ReportsController {

    private ReportsView view;
    private ReportDAO dao;

    public ReportsController(ReportsView view) {
        this.view = view;
        this.dao = new ReportDAO();

        initController();
        // Load KPI cards and default report on startup
        loadKPIs();
        generateReport();
    }

    private void initController() {
        view.getBtnGenerate().addActionListener(e -> generateReport());

        view.getBtnExportCSV().addActionListener(e -> exportToCSV());

        // Auto-generate when dropdown changes
        view.getCmbReportType().addActionListener(e -> generateReport());
    }

    private void loadKPIs() {
        Map<String, Integer> kpiData = dao.getKPISummary();
        view.updateKPICards(kpiData);
    }

    private void generateReport() {
        String reportType = (String) view.getCmbReportType().getSelectedItem();
        if (reportType == null) return;

        switch (reportType) {
            case ReportsView.REPORT_INVENTORY: {
                String[] cols = {"SKU", "Name", "Category", "Location", "Quantity", "Min Stock", "Status"};
                List<Object[]> data = dao.getInventorySummary();
                view.setReportData(cols, data, "Status");
                break;
            }
            case ReportsView.REPORT_ORDERS: {
                String[] cols = {"Order ID", "Status", "Order Date", "Line Items", "Total Qty"};
                List<Object[]> data = dao.getOrdersSummary();
                view.setReportData(cols, data, "Status");
                break;
            }
            case ReportsView.REPORT_SHIPMENTS: {
                String[] cols = {"Shipment ID", "Order ID", "Status", "Tracking Number", "Dispatch Date"};
                List<Object[]> data = dao.getShipmentsSummary();
                view.setReportData(cols, data, "Status");
                break;
            }
            case ReportsView.REPORT_LOW_STOCK: {
                String[] cols = {"SKU", "Name", "Category", "Current Stock", "Min Stock", "Shortage Units"};
                List<Object[]> data = dao.getLowStockReport();
                view.setReportData(cols, data, null);
                break;
            }
        }
    }

    private void exportToCSV() {
        DefaultTableModel model = view.getTableModel();
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(view, "No data to export. Please generate a report first.", "Export Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report as CSV");
        String reportName = (String) view.getCmbReportType().getSelectedItem();
        fileChooser.setSelectedFile(new java.io.File((reportName != null ? reportName.replace(" ", "_") : "report") + ".csv"));

        int result = fileChooser.showSaveDialog(view);
        if (result != JFileChooser.APPROVE_OPTION) return;

        java.io.File file = fileChooser.getSelectedFile();
        if (!file.getName().endsWith(".csv")) {
            file = new java.io.File(file.getAbsolutePath() + ".csv");
        }

        try (FileWriter writer = new FileWriter(file)) {
            // Write header
            StringBuilder header = new StringBuilder();
            for (int col = 0; col < model.getColumnCount(); col++) {
                if (col > 0) header.append(",");
                header.append("\"").append(model.getColumnName(col)).append("\"");
            }
            writer.write(header.toString());
            writer.write("\n");

            // Write rows
            for (int row = 0; row < model.getRowCount(); row++) {
                StringBuilder line = new StringBuilder();
                for (int col = 0; col < model.getColumnCount(); col++) {
                    if (col > 0) line.append(",");
                    Object val = model.getValueAt(row, col);
                    line.append("\"").append(val != null ? val.toString().replace("\"", "\"\"") : "").append("\"");
                }
                writer.write(line.toString());
                writer.write("\n");
            }

            JOptionPane.showMessageDialog(view, "Report exported successfully to:\n" + file.getAbsolutePath(), "Export Successful", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Failed to export report:\n" + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
