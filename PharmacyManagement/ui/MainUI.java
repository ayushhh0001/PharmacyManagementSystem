package ui;

import dao.MedicineDAO;
import model.Medicine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainUI extends JFrame {
    JTable table;
    DefaultTableModel model;

    public MainUI() {
        setTitle("Pharmacy Dashboard");
        setSize(900, 600);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[] { "ID", "Name", "Company", "Qty", "Price", "Expiry" }, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton delBtn = new JButton("Delete");
        JButton updBtn = new JButton("Update");
        JButton refreshBtn = new JButton("Refresh");
        JButton billingBtn = new JButton("Billing");
        JButton salesBtn = new JButton("View Sales");

        panel.add(addBtn);
        panel.add(delBtn);
        panel.add(updBtn);
        panel.add(refreshBtn);
        panel.add(billingBtn);
        panel.add(salesBtn);

        add(panel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> addMedicine());
        delBtn.addActionListener(e -> deleteMedicine());
        updBtn.addActionListener(e -> updateMedicine());
        refreshBtn.addActionListener(e -> loadData());
        billingBtn.addActionListener(e -> new BillingUI());
        salesBtn.addActionListener(e -> new SalesReportUI());

        loadData();
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void loadData() {
        try {
            model.setRowCount(0); // Clear the existing data in the table
            List<Medicine> list = MedicineDAO.getAllMedicines();
            StringBuilder alerts = new StringBuilder();
            String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            for (Medicine m : list) {
                model.addRow(new Object[] { m.id, m.name, m.company, m.quantity, m.price, m.expiryDate });

                if (m.quantity <= 5) {
                    alerts.append("⚠️ Low stock: ").append(m.name).append("\n");
                }
                if (m.expiryDate.compareTo(today) < 0) {
                    alerts.append("❌ Expired: ").append(m.name).append("\n");
                }
            }

            if (!alerts.isEmpty()) {
                JOptionPane.showMessageDialog(this, alerts.toString(), "Alerts", JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load data");
        }
    }

    private void addMedicine() {
        JTextField nameField = new JTextField(10);
        JTextField companyField = new JTextField(10);
        JTextField quantityField = new JTextField(5);
        JTextField priceField = new JTextField(5);
        JTextField expiryField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Company:"));
        panel.add(companyField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Expiry (YYYY-MM-DD):"));
        panel.add(expiryField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Medicine", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Medicine m = new Medicine(0,
                        nameField.getText(),
                        companyField.getText(),
                        Integer.parseInt(quantityField.getText()),
                        Double.parseDouble(priceField.getText()),
                        expiryField.getText());
                MedicineDAO.addMedicine(m);
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        }
    }

    // Updated deleteMedicine method
    private void deleteMedicine() {
        int row = table.getSelectedRow(); // Get the selected row from the table
        if (row == -1) { // If no row is selected
            JOptionPane.showMessageDialog(this, "Select a medicine to delete.");
            return;
        }

        // Get the ID of the selected medicine
        int id = (int) model.getValueAt(row, 0);

        // Confirm delete action
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this medicine?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Delete medicine by ID
                MedicineDAO.deleteMedicine(id);
                loadData(); // Reload the data to reflect the changes in the table
                JOptionPane.showMessageDialog(this, "Medicine deleted successfully.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to delete medicine.");
            }
        }
    }

    private void updateMedicine() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a medicine to update.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        JTextField nameField = new JTextField(model.getValueAt(row, 1).toString());
        JTextField companyField = new JTextField(model.getValueAt(row, 2).toString());
        JTextField quantityField = new JTextField(model.getValueAt(row, 3).toString());
        JTextField priceField = new JTextField(model.getValueAt(row, 4).toString());
        JTextField expiryField = new JTextField(model.getValueAt(row, 5).toString());

        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Company:"));
        panel.add(companyField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Expiry (YYYY-MM-DD):"));
        panel.add(expiryField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Medicine", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Medicine m = new Medicine(id,
                        nameField.getText(),
                        companyField.getText(),
                        Integer.parseInt(quantityField.getText()),
                        Double.parseDouble(priceField.getText()),
                        expiryField.getText());
                MedicineDAO.updateMedicine(m);
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        }
    }
}
