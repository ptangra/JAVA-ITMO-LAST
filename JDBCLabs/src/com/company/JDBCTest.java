package com.company;

import java.sql.*;
import java.util.ArrayList;

public class JDBCTest {

    static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/items";
    static final String DB_USER = "GUEST";
    static final String DB_PASSWORD = "postgres";

    public static void main(String[] args) throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
            return;
        }

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ItemDAO itemDAO = new ItemDAO(conn);

            MusicItem mi1 = itemDAO.searchById(1);
            MusicItem mi2 = itemDAO.searchById(100);

            // Data taken successfully:
            System.out.println("Item ID_1: " + mi1.toString());

            System.out.println();

            // ID doesn't exist:
            System.out.println("Item ID_100: " + mi2.toString());

            // 1-4
            ArrayList<MusicItem> list1 = itemDAO.searchByKeyword("%of%");
            ArrayList<MusicItem> list2 = itemDAO.searchByKeyword("%Gay%");

            // Output

            if (list1.isEmpty()) {
                System.out.println("List 1 is empty.");
            } else {
                System.out.println("List 1:");
            }
            for (MusicItem mi : list1) {
                System.out.println(mi.toString());
                System.out.println();
            }

            if (list2.isEmpty()) {
                System.out.println("List 2 is empty.");
            } else {
                System.out.println("List 2:");
            }
            for (MusicItem mi : list2) {
                System.out.println(mi.toString());
                System.out.println();
            }

            // 1-5
            MusicItem mi = new MusicItem();
            mi.setTitle("new");
            mi.setArtist("new");
            mi.setReleaseDate(new Date(1999, 9, 9));
            mi.setPrice(99.99f);
            mi.setListPrice(99.99f);

            itemDAO.create(mi);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            conn.close();
        }
    }

    public static class ItemDAO {
        // Fields
        private Connection conn;

        // Constructor
        public ItemDAO(Connection conn) {
            this.conn = conn;
        }

        // Methods
        public MusicItem searchById(int ID) {
            MusicItem mi = new MusicItem();
            try {
                String sql = "SELECT * FROM item WHERE item_id = ?";
                PreparedStatement pstmt = this.conn.prepareStatement(sql);
                pstmt.setInt(1, ID);

                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    mi.setItemID(rs.getInt(1));
                    mi.setTitle(rs.getString(2));
                    mi.setArtist(rs.getString(3));
                    mi.setReleaseDate(rs.getDate(4));
                    mi.setListPrice(rs.getFloat(5));
                    mi.setPrice(rs.getFloat(6));
                    mi.setVersion(rs.getInt(7));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return mi;
        }

        public ArrayList<MusicItem> searchByKeyword(String pattern) {
            ArrayList<MusicItem> musicItemArrayList = new ArrayList<>();
            try {
                String sql = "SELECT * FROM item WHERE title LIKE ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, pattern);

                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    MusicItem mi = new MusicItem();
                    mi.setItemID(rs.getInt(1));
                    mi.setTitle(rs.getString(2));
                    mi.setArtist(rs.getString(3));
                    mi.setReleaseDate(rs.getDate(4));
                    mi.setListPrice(rs.getFloat(5));
                    mi.setPrice(rs.getFloat(6));
                    mi.setVersion(rs.getInt(7));

                    musicItemArrayList.add(mi);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return musicItemArrayList;
        }

        public void create(MusicItem mi) {
            String sql = "INSERT INTO item (title, " +
                    "artist, releasedate, listprice, " +
                    "price, version) VALUES (?, ?, ?, ?, ?, ?)";
            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, mi.getTitle());
                pstmt.setString(2, mi.getArtist());
                pstmt.setDate(3, mi.getReleaseDate());
                pstmt.setFloat(4, mi.getListPrice());
                pstmt.setFloat(5, mi.getPrice());
                pstmt.setInt(6, 1);

                System.out.println(pstmt.executeUpdate());

            } catch (Exception e) { }
        }
        public void close() {

        }
    }

    public static class MusicItem {
        private int itemID;
        private String title;
        private String artist;
        private Date releaseDate;
        private float listPrice;
        private float price;
        private int version;

        @Override
        public String toString() {
            return "ID: " + this.itemID +
                    "\nTitle: " + this.title +
                    "\nArtist: " + this.artist +
                    "\nRelease Date: " + this.releaseDate +
                    "\nList Price: " + this.listPrice +
                    "\nPrice: " + this.price +
                    "\nVersion: " + this.version;
        }

        // Setters & Getters
        public void setItemID(int itemID) {
            this.itemID = itemID;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public void setReleaseDate(Date releaseDate) {
            this.releaseDate = releaseDate;
        }

        public void setListPrice(float listPrice) {
            this.listPrice = listPrice;
        }

        public void setPrice(float price) {
            this.price = price;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public int getItemID() {
            return itemID;
        }

        public String getTitle() {
            return title;
        }

        public String getArtist() {
            return artist;
        }

        public Date getReleaseDate() {
            return releaseDate;
        }

        public float getListPrice() {
            return listPrice;
        }

        public float getPrice() {
            return price;
        }

        public int getVersion() {
            return version;
        }
    }
}
