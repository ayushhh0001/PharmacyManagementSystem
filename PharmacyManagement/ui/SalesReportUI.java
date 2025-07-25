package ui;

import dao.SalesDAO;
import dao.MedicineDAO;
import model.Sale;
import model.Medicine;

import javax.swing.*;
import java.awt.*;
import java.util.List; // ✅ Import this
import java.util.Optional;

public class SalesReportUI extends JFrame {
    JTextField fromField = new JTextField(10);
    JTextField toField = new JTextField(10);
    JTextArea reportArea = new JTextArea(20, 40);

    public SalesReportUI() {
        setTitle("Sales Report");
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("From (yyyy-MM-dd):"));
        topPanel.add(fromField);
        topPanel.add(new JLabel("To:"));
        topPanel.add(toField);
        JButton viewBtn = new JButton("View");
        topPanel.add(viewBtn);

        reportArea.setEditable(false);
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(reportArea), BorderLayout.CENTER);

        viewBtn.addActionListener(e -> viewReport());

        setSize(600, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void viewReport() {
        String from = fromField.getText().trim();
        String to = toField.getText().trim();
        try {
            List<Sale> sales = SalesDAO.getSalesByDate(from, to);
            reportArea.setText("Sales Report from " + from + " to " + to + "\n\n");

            List<Medicine> allMeds = MedicineDAO.getAllMedicines();

            for (Sale s : sales) {
                Optional<Medicine> optionalMed = allMeds.stream().filter(m -> m.id == s.medicineId).findFirst();
                if (optionalMed.isPresent()) {
                    Medicine m = optionalMed.get();
                    reportArea.append(m.name + " - " + s.quantitySold + " pcs on " + s.saleDate + "\n");
                }
            }

        } catch (Exception e) {
            reportArea.setText("❌ Failed to load report. " + e.getMessage());
        }
    }
}
