package ui;

import dao.MedicineDAO;
import model.Medicine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.Vector;

public class BillingUI extends JFrame {
    private JComboBox<String> medicineDropdown;
    private JTextField quantityField;
    private JTable billingTable;
    private DefaultTableModel billingModel;
    private JButton addToBillButton, finalizeBillButton;
    private double totalAmount = 0.0;

    public BillingUI() {
        setTitle("Billing");
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Top panel
        JPanel topPanel = new JPanel();
        medicineDropdown = new JComboBox<>();
        quantityField = new JTextField(5);
        addToBillButton = new JButton("Add to Bill");

        topPanel.add(new JLabel("Medicine:"));
        topPanel.add(medicineDropdown);
        topPanel.add(new JLabel("Quantity:"));
        topPanel.add(quantityField);
        topPanel.add(addToBillButton);

        add(topPanel, BorderLayout.NORTH);

        // Table for billing
        billingModel = new DefaultTableModel(new String[] { "Name", "Qty", "Price", "Total" }, 0);
        billingTable = new JTable(billingModel);
        add(new JScrollPane(billingTable), BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel();
        finalizeBillButton = new JButton("Finalize Bill");
        bottomPanel.add(finalizeBillButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load medicines into dropdown
        loadMedicines();

        // Event handlers
        addToBillButton.addActionListener(e -> addMedicineToBill());
        finalizeBillButton.addActionListener(e -> finalizeBill());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadMedicines() {
        try {
            medicineDropdown.removeAllItems();
            for (Medicine m : MedicineDAO.getAllMedicines()) {
                medicineDropdown.addItem(m.name);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load medicines.");
        }
    }

    private void addMedicineToBill() {
        String name = (String) medicineDropdown.getSelectedItem();
        String qtyStr = quantityField.getText().trim();

        if (name == null || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a medicine and enter quantity.");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(qtyStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity.");
            return;
        }

        try {
            Medicine med = MedicineDAO.getMedicineByName(name);
            if (med == null) {
                JOptionPane.showMessageDialog(this, "Medicine not found.");
                return;
            }

            if (med.quantity < qty) {
                JOptionPane.showMessageDialog(this, "Not enough stock.");
                return;
            }

            double lineTotal = med.price * qty;
            totalAmount += lineTotal;

            billingModel.addRow(new Object[] { med.name, qty, med.price, lineTotal });

            // Update stock in DB and insert into sales
            MedicineDAO.updateMedicineStockAndInsertSale(med.id, qty);

            quantityField.setText("");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error processing billing.");
        }
    }

    private void finalizeBill() {
        JOptionPane.showMessageDialog(this, "Total Bill: ₹" + totalAmount);
        billingModel.setRowCount(0);
        totalAmount = 0.0;
        loadMedicines();
    }
}
