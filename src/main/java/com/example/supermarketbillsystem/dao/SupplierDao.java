package com.example.supermarketbillsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.supermarketbillsystem.entity.Supplier;
import com.example.supermarketbillsystem.util.DBUtil;

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

    public List<Supplier> search(String name){
        List<Supplier> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM supplier WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if(name != null && !name.isEmpty()){
            sql.append(" AND name LIKE ?");
            params.add("%" + name + "%");
        }
        sql.append(" ORDER BY id");

        try(Connection conn = DBUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql.toString())){
            for(int i = 0; i < params.size(); i++){
                ps.setObject(i+1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Supplier s = new Supplier();
                s.setSupplier_id(rs.getInt("id"));
                s.setName(rs.getString("name"));
                s.setContact(rs.getString("contact"));
                s.setPhone(rs.getString("phone"));
                s.setAddress(rs.getString("address"));
                s.setDescription(rs.getString("description"));
                list.add(s);
            }
        }catch(SQLException e){
            e.printStackTrace();
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
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT MAX(id) FROM supplier";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            int nextId = 1;
            if (rs.next()) {
                nextId = rs.getInt(1) + 1;
            }

            sql = "INSERT INTO supplier (id, name, contact, phone, address, description) VALUES(?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, nextId);
            ps.setString(2, supplier.getName());
            ps.setString(3, supplier.getContact());
            ps.setString(4, supplier.getPhone());
            ps.setString(5, supplier.getAddress());
            ps.setString(6, supplier.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
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
