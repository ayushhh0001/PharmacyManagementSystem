package dao;

import model.Sale;
import java.sql.*;
import java.util.*;

public class SalesDAO {
    public static void addSale(Sale sale) throws SQLException {
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn
                .prepareStatement("INSERT INTO sales (medicine_id, quantity_sold, sale_date) VALUES (?, ?, ?)");
        stmt.setInt(1, sale.medicineId);
        stmt.setInt(2, sale.quantitySold);
        stmt.setString(3, sale.saleDate);
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    public static List<Sale> getSalesByDate(String from, String to) throws SQLException {
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM sales WHERE sale_date BETWEEN ? AND ?");
        stmt.setString(1, from);
        stmt.setString(2, to);
        ResultSet rs = stmt.executeQuery();
        List<Sale> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new Sale(rs.getInt("medicine_id"), rs.getInt("quantity_sold"), rs.getString("sale_date")));
        }
        rs.close();
        stmt.close();
        conn.close();
        return list;
    }
}
