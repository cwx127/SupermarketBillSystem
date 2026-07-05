package com.example.supermarketbillsystem.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.example.supermarketbillsystem.dao.BillDao;
import com.example.supermarketbillsystem.dao.SupplierDao;
import com.example.supermarketbillsystem.entity.Bill;
import com.example.supermarketbillsystem.entity.Supplier;

public class ReportPanel extends JPanel {
    private JLabel lblTotalAmount;
    private JLabel lblTotalCount;
    private JLabel lblPaidAmount;
    private JLabel lblUnpaidAmount;
    private JTable supplierTable;
    private DefaultTableModel supplierTableModel;
    private BillDao billDao = new BillDao();
    private SupplierDao supplierDao = new SupplierDao();

    public ReportPanel() {
        setLayout(new BorderLayout());

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(2, 2, 20, 20));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JPanel p1 = createStatPanel("总交易金额", "0.00");
        lblTotalAmount = (JLabel) p1.getComponent(1);
        statsPanel.add(p1);

        JPanel p2 = createStatPanel("总交易笔数", "0");
        lblTotalCount = (JLabel) p2.getComponent(1);
        statsPanel.add(p2);

        JPanel p3 = createStatPanel("已付款金额", "0.00");
        lblPaidAmount = (JLabel) p3.getComponent(1);
        statsPanel.add(p3);

        JPanel p4 = createStatPanel("未付款金额", "0.00");
        lblUnpaidAmount = (JLabel) p4.getComponent(1);
        statsPanel.add(p4);

        add(statsPanel, BorderLayout.NORTH);

        String[] columns = {"供应商名称", "交易金额", "交易笔数"};
        supplierTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        supplierTable = new JTable(supplierTableModel);
        supplierTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));
        add(new JScrollPane(supplierTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton btnRefresh = new JButton("刷新统计");
        btnRefresh.addActionListener(e -> loadReportData());
        btnPanel.add(btnRefresh);
        add(btnPanel, BorderLayout.SOUTH);

        loadReportData();
    }

    private JPanel createStatPanel(String title, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        panel.add(lblTitle, BorderLayout.NORTH);
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("微软雅黑", Font.BOLD, 24));
        lblValue.setForeground(new Color(52, 152, 219));
        panel.add(lblValue, BorderLayout.CENTER);
        return panel;
    }

    private void loadReportData() {
        List<Bill> bills = billDao.findAll();

        double totalAmount = 0;
        double paidAmount = 0;
        double unpaidAmount = 0;
        Map<Integer, Double> supplierAmounts = new HashMap<>();
        Map<Integer, Integer> supplierCounts = new HashMap<>();

        for (Bill bill : bills) {
            totalAmount += bill.getAmount();
            if (bill.getIsPaid() == 1) {
                paidAmount += bill.getAmount();
            } else {
                unpaidAmount += bill.getAmount();
            }

            int supplierId = bill.getSupplierId();
            supplierAmounts.put(supplierId, supplierAmounts.getOrDefault(supplierId, 0.0) + bill.getAmount());
            supplierCounts.put(supplierId, supplierCounts.getOrDefault(supplierId, 0) + 1);
        }

        lblTotalAmount.setText(String.format("%.2f", totalAmount));
        lblTotalCount.setText(String.valueOf(bills.size()));
        lblPaidAmount.setText(String.format("%.2f", paidAmount));
        lblUnpaidAmount.setText(String.format("%.2f", unpaidAmount));

        supplierTableModel.setRowCount(0);
        for (Map.Entry<Integer, Double> entry : supplierAmounts.entrySet()) {
            int supplierId = entry.getKey();
            Supplier supplier = supplierDao.findById(supplierId);
            String name = supplier != null ? supplier.getName() : "未知供应商";
            supplierTableModel.addRow(new Object[]{
                    name,
                    String.format("%.2f", entry.getValue()),
                    supplierCounts.get(supplierId)
            });
        }
    }
}