package DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Item;

public class itemDao {

    private dao dao;

    public itemDao() {
        dao=new dao();
        dao.Dao();
    }

    public List<Item> getAllItems() throws SQLException {
    	dao=new dao();
        dao.Dao();
        List<Item> items = new ArrayList<>();
        String query = "SELECT * FROM addproduct";
        try (PreparedStatement pst = dao.con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Item item = new Item();
                item.setId(rs.getInt("ID"));
                item.setName(rs.getString("ProductName"));
                item.setQuantity(rs.getInt("Quantity"));
                item.setDate(rs.getString("Date"));
                items.add(item);
            }
        }
        return items;
    }

    public void addItem(Item item) throws SQLException {
    	dao=new dao();
        dao.Dao();
        String query = "INSERT INTO addproduct (ProductName, Quantity, Date) VALUES (?, ?, ?)";
        try (PreparedStatement pst = dao.con.prepareStatement(query)) {
            pst.setString(1, item.getName());
            pst.setInt(2, item.getQuantity());
            pst.setString(3, item.getDate());
            pst.executeUpdate();
        }
    }

    public void deleteItem(int id) throws SQLException {
    	dao=new dao();
        dao.Dao();
        String query = "DELETE FROM addproduct WHERE ID like ?";
        try (PreparedStatement pst = dao.con.prepareStatement(query)) {
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.print(id);
        }
    }

    public Item getItemById(int id) throws SQLException {
    	dao=new dao();
        dao.Dao();
        String query = "SELECT * FROM addproduct WHERE id = ?";
        try (PreparedStatement pst = dao.con.prepareStatement(query)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Item item = new Item();
                    item.setName(rs.getString("ProductName"));
                    item.setQuantity(rs.getInt("Quantity"));
                    item.setDate(rs.getString("Date"));
                    return item;
                }
            }
        }
        return null;
    }
}