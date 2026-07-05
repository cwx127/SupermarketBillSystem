package com.example.supermarketbillsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.supermarketbillsystem.entity.Bill;
import com.example.supermarketbillsystem.util.DBUtil;

public class BillDao {

    //查询所有账单
    public List<Bill> findAll(){
        List<Bill> list = new ArrayList <> ();
        Connection conn = null;//数据库连接通道
        PreparedStatement ps = null;//预编译SQL
        ResultSet rs = null;//查询结果集

        try {
            conn = DBUtil.getConnection ();//获取数据库对象
            String sql = "SELECT * FROM bill ORDER BY id DESC";
            ps = conn.prepareStatement ( sql );//预编译对象
            rs = ps.executeQuery ();//select查询只返回结果集

            //结果集数据封装
            while (rs.next()) {
                Bill bill = new Bill ();
                bill.setBillId ( rs.getInt("id") );
                bill.setProduct_name ( rs.getString ( "product_name" ) );
                bill.setQuantity ( rs.getInt ( "quantity" ) );
                bill.setUnit ( rs.getString ( "unit" ) );
                bill.setAmount ( rs.getDouble ( "amount" ) );
                bill.setSupplierId ( rs.getInt ( "supplier_id" ) );
                bill.setIsPaid ( rs.getInt ( "is_paid" ) );
                bill.setDescription ( rs.getString ( "description" ) );
                bill.setCreateTime ( rs.getTimestamp ( "create_time" ) );
                list.add ( bill );
            }
        }catch (SQLException e){
            e.printStackTrace ();
        }
        finally {
            DBUtil.close ( conn,ps,rs );//强制释放资源
        }
        return list;
    }

    //根据条件查询
    public List<Bill> search(String productName,Integer isPaid){
        //初始化变量
        List<Bill> list = new ArrayList <> ();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{
            conn = DBUtil.getConnection ();
            StringBuilder sql = new StringBuilder ("SELECT * FROM bill WHERE 1=1");//1=1占位符;字符串可变容器
            List<Object> params = new ArrayList <> ();//?占位符单独传值

            //判断商品条件
            if (productName != null && !productName.isEmpty ()){
                sql.append ( " AND product_name LIKE ?" );//?占位符传%%防止SQL注入漏洞
                params.add("%"+productName+"%");
            }
            //判断付款条件
            if(isPaid != null){
                sql.append ( " AND is_paid = ?" );
                params.add ( isPaid );
            }
            sql.append ( " ORDER BY id DESC" );//按主键倒序，新帐单靠前

            //预编译 SQL + 批量给占位符赋值
            ps = conn.prepareStatement ( sql.toString () );
            for (int i = 0;i < params.size ();i++){
                ps.setObject ( i+1,params.get ( i ) );
            }

            //执行查询 + 封装实体
            rs = ps.executeQuery ();
            while (rs.next ()){
                Bill bill = new Bill ();
                bill.setBillId ( rs.getInt("id") );
                bill.setProduct_name ( rs.getString ( "product_name" ) );
                bill.setQuantity ( rs.getInt ( "quantity" ) );
                bill.setUnit ( rs.getString ( "unit" ) );
                bill.setAmount ( rs.getDouble ( "amount" ) );
                bill.setSupplierId ( rs.getInt ( "supplier_id" ) );
                bill.setIsPaid ( rs.getInt ( "is_paid" ) );
                bill.setDescription ( rs.getString ( "description" ) );
                bill.setCreateTime ( rs.getTimestamp ( "create_time" ) );
                list.add ( bill );
            }
        }catch (SQLException e){
            e.printStackTrace ();
        }finally {
            DBUtil.close ( conn,ps,rs );
        }

        return list;
    }

    //添加账单
    public boolean add(Bill bill){
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection ();
            String sql = "INSERT INTO bill (product_name,quantity,unit,amount,supplier_id,is_paid,description) VALUES(?,?,?,?,?,?,?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, bill.getProduct_name());
            ps.setInt(2, bill.getQuantity());
            ps.setString(3, bill.getUnit());
            ps.setDouble(4, bill.getAmount());
            ps.setInt(5, bill.getSupplierId());
            ps.setInt(6, bill.getIsPaid());
            ps.setString(7, bill.getDescription());

            return ps.executeUpdate () > 0;
        }catch (SQLException e){
            e.printStackTrace ();
        }finally {
            DBUtil.close ( conn,ps,null );//三参数方法
        }
        return false;
    }

    //修改账单
    public boolean update(Bill bill){
        Connection conn = null;
        PreparedStatement ps = null;

        try{
            conn = DBUtil.getConnection ();
            String sql = "UPDATE bill SET product_name=?, quantity=?, amount=?, unit=?, supplier_id=?, is_paid=? ,description=? WHERE id=?";
            ps = conn.prepareStatement ( sql );
            ps.setString(1, bill.getProduct_name());
            ps.setInt(2, bill.getQuantity());
            ps.setDouble(3, bill.getAmount());
            ps.setString(4, bill.getUnit());
            ps.setInt(5, bill.getSupplierId());
            ps.setInt(6, bill.getIsPaid());
            ps.setString(7, bill.getDescription());
            ps.setInt(8, bill.getBillId());  // WHERE 条件的 id

            return ps.executeUpdate () > 0;
        }catch (SQLException e){
            e.printStackTrace ();
        }finally {
            DBUtil.close ( conn,ps,null );
        }
        return false;
    }
    //删除账单
    public boolean delete(int[] ids){//勾选多条账单得到的 id 数组
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection ();
            conn.setAutoCommit ( false );//开启事务,关闭自动提交，实现批量删除的原子性，中间出错可以全部撤销

            String sql = "DELETE FROM bill WHERE id = ?";
            ps = conn.prepareStatement (sql);

            for (int id : ids){
                ps.setInt ( 1,id );
                ps.addBatch ();//批量添加,不立即执行 SQL，把本次删除操作存入批处理队列缓存
            }
            ps.executeBatch ();//把队列里所有 DELETE 语句一次性发送给数据库批量执行
            conn.commit();//提交事务
            return true;
        }catch (SQLException e){
            e.printStackTrace ();
        }finally {
            DBUtil.close ( conn,ps,null );
        }
        return false;
    }
}
