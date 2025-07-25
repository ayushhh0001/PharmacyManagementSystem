package dao;

import model.Medicine;
import java.sql.*;
import java.util.*;

public class MedicineDAO {

    public static void addMedicine(Medicine med) throws SQLException {
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO medicines (name, company, quantity, price, expiry_date) VALUES (?, ?, ?, ?, ?)");
        stmt.setString(1, med.name);
        stmt.setString(2, med.company);
        stmt.setInt(3, med.quantity);
        stmt.setDouble(4, med.price);
        stmt.setString(5, med.expiryDate);
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    public static List<Medicine> getAllMedicines() throws SQLException {
        List<Medicine> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM medicines");
        while (rs.next()) {
            list.add(new Medicine(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("company"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getString("expiry_date")));
        }
        rs.close();
        stmt.close();
        conn.close();
        return list;
    }

    public static Medicine getMedicineByName(String name) throws SQLException {
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM medicines WHERE name = ?");
        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            Medicine med = new Medicine(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("company"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getString("expiry_date"));
            rs.close();
            stmt.close();
            conn.close();
            return med;
        }
        rs.close();
        stmt.close();
        conn.close();
        return null;
    }

    public static void deleteMedicine(int id) throws SQLException {
        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false); // Start transaction

        try {
            // Step 1: Delete related sales
            PreparedStatement deleteSales = conn.prepareStatement("DELETE FROM sales WHERE medicine_id = ?");
            deleteSales.setInt(1, id);
            deleteSales.executeUpdate();
            deleteSales.close();

            // Step 2: Delete the medicine
            PreparedStatement deleteMedicine = conn.prepareStatement("DELETE FROM medicines WHERE id = ?");
            deleteMedicine.setInt(1, id);
            int rowsAffected = deleteMedicine.executeUpdate();
            deleteMedicine.close();

            if (rowsAffected == 0) {
                conn.rollback();
                throw new SQLException("No medicine deleted. ID may not exist.");
            }

            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            conn.rollback(); // Roll back on error
            throw e;
        } finally {
            conn.setAutoCommit(true); // Restore default
            conn.close();
        }
    }

    public static void updateMedicine(Medicine med) throws SQLException {
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(
                "UPDATE medicines SET name=?, company=?, quantity=?, price=?, expiry_date=? WHERE id=?");
        stmt.setString(1, med.name);
        stmt.setString(2, med.company);
        stmt.setInt(3, med.quantity);
        stmt.setDouble(4, med.price);
        stmt.setString(5, med.expiryDate);
        stmt.setInt(6, med.id);
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    public static void updateMedicineStockAndInsertSale(int id, int quantitySold) throws SQLException {
        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false);

        try {
            String updateStockQuery = "UPDATE medicines SET quantity = quantity - ? WHERE id = ?";
            PreparedStatement updateStockStmt = conn.prepareStatement(updateStockQuery);
            updateStockStmt.setInt(1, quantitySold);
            updateStockStmt.setInt(2, id);
            int rowsUpdated = updateStockStmt.executeUpdate();
            if (rowsUpdated == 0) {
                conn.rollback();
                updateStockStmt.close();
                throw new SQLException("No medicine updated, possible insufficient stock");
            }

            String insertSaleQuery = "INSERT INTO sales (medicine_id, quantity_sold, sale_date) VALUES (?, ?, CURDATE())";
            PreparedStatement insertSaleStmt = conn.prepareStatement(insertSaleQuery);
            insertSaleStmt.setInt(1, id);
            insertSaleStmt.setInt(2, quantitySold);
            insertSaleStmt.executeUpdate();

            conn.commit();

            insertSaleStmt.close();
            updateStockStmt.close();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
}
