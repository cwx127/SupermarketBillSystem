package com.example.supermarketbillsystem.dao;

import com.example.supermarketbillsystem.entity.Supplier;
import com.example.supermarketbillsystem.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierDao {

    public  List<Supplier> findAll(){//存放所有查询到的供应商实体
        List<Supplier> list = new ArrayList <> ();
        String sql = "SELECT * FROM supplier ORDER BY id";

        try(Connection conn = DBUtil.getConnection ();
        PreparedStatement ps = conn.prepareStatement ( sql );
        ResultSet rs = ps.executeQuery ()){
            while (rs.next ()){
                Supplier s = new Supplier ();
                s.setSupplier_id ( rs.getInt ( "id" ) );
                s.setName ( rs.getString ( "name" ) );
                s.setContact ( rs.getString ( "contact" ) );
                s.setPhone ( rs.getString ( "phone" ) );
                s.setAddress (rs.getString ( "address" ));
                s.setDescription (rs.getString ( "description" ));
                list.add ( s );
            }
        }catch (SQLException e){
            e.printStackTrace ();
        }
        return list;
    }

    public Supplier findById(int id){
        String sql = "SELECT * FROM supplier WHERE id = ?";
        try (Connection conn = DBUtil.getConnection ();
        PreparedStatement ps = conn.prepareStatement ( sql )) {
            ps.setInt ( 1,id );
            ResultSet rs = ps.executeQuery ();
            if (rs.next () ){
                Supplier s = new Supplier();
                s.setSupplier_id (rs.getInt("id"));
                s.setName(rs.getString("name"));
                s.setContact(rs.getString("contact"));
                s.setPhone(rs.getString("phone"));
                s.setAddress(rs.getString("address"));
                s.setDescription(rs.getString("description"));
                return s;
            }
        }catch (SQLException e){
            e.printStackTrace ();
        }
        return null;
    }

    public boolean add(Supplier supplier) {
        String sql = "INSERT INTO supplier (name, contact, phone, address, description) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getContact());
            ps.setString(3, supplier.getPhone());
            ps.setString(4, supplier.getAddress());
            ps.setString(5, supplier.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Supplier supplier) {
        String sql = "UPDATE supplier SET name=?, contact=?, phone=?, address=?, description=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getContact());
            ps.setString(3, supplier.getPhone());
            ps.setString(4, supplier.getAddress());
            ps.setString(5, supplier.getDescription());
            ps.setInt(6, supplier.getSupplier_id());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int[] ids) {
        String sql = "DELETE FROM supplier WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (int id : ids) {
                ps.setInt(1, id);
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
