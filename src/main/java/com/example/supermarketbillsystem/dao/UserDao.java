package com.example.supermarketbillsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.supermarketbillsystem.entity.User;
import com.example.supermarketbillsystem.util.DBUtil;

/**
 * 用户数据访问对象（DAO）
 * 负责用户表（sys_user）的数据库操作：增删改查
 */
public class UserDao {

    /**
     * 根据用户名查询用户
     * 用于登录验证、用户名唯一性检查等场景
     * @param username 用户名
     * @return User对象，如果未找到返回null
     */
    public User findByUsername(String username){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        User user = null;

        try{
            // 获取数据库连接
            conn = DBUtil.getConnection();
            
            // SQL语句：根据用户名查询用户信息
            String sql = "SELECT * FROM \"USER\" WHERE username = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);  // 设置参数：用户名
            
            // 执行查询
            rs = ps.executeQuery();

            // 如果查询到结果，封装为User对象
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getInt("role"));
                user.setSex(rs.getInt("sex"));
                user.setAge(rs.getInt("age"));
                user.setPhonenumber(rs.getString("phonenumber"));
                user.setAddress(rs.getString("address"));
                user.setCreateTime(rs.getTimestamp("create_time"));
            }
        }catch (SQLException e){
            e.printStackTrace();  // 打印异常信息（实际项目中应使用日志框架）
        }finally {
            // 关闭数据库连接资源
            DBUtil.close(conn, ps, rs);
        }
        return user;
    }

    /**
     * 查询所有用户
     * 返回按ID降序排列的用户列表（最新的用户在前）
     * @return 用户列表，如果没有数据返回空列表
     */
    public List<User> findAll(){
        List<User> list = new ArrayList<>();  // 创建空列表，避免返回null
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            
            // SQL语句：查询所有用户，按ID降序排列
            String sql = "SELECT * FROM \"USER\" ORDER BY id DESC";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            // 遍历结果集，封装为User对象并添加到列表
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getInt("role"));
                user.setSex(rs.getInt("sex"));
                user.setAge(rs.getInt("age"));
                user.setPhonenumber(rs.getString("phonenumber"));
                user.setAddress(rs.getString("address"));
                user.setCreateTime(rs.getTimestamp("create_time"));
                list.add(user);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /**
     * 多条件查询用户
     * 支持用户名模糊查询和角色精确查询
     * @param username 用户名（支持模糊匹配，可以为null或空字符串）
     * @param role 角色（0=普通用户，1=部门经理，可以为null表示不限制）
     * @return 符合条件的用户列表
     */
    public List<User> search(String username, Integer role){
        List<User> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{
            conn = DBUtil.getConnection();
            
            // 使用StringBuilder动态拼接SQL语句
            StringBuilder sql = new StringBuilder("SELECT * FROM \"USER\" WHERE 1=1");
            List<Object> params = new ArrayList<>();  // 存储参数值

            // 如果用户名不为空，添加模糊查询条件
            if (username != null && !username.isEmpty()){
                sql.append(" AND username LIKE ?");
                params.add("%" + username + "%");  // 前后加%实现模糊匹配
            }
            
            // 如果角色不为null，添加精确查询条件
            if (role != null){
                sql.append(" AND role = ?");
                params.add(role);
            }
            
            // 添加排序：按ID降序
            sql.append(" ORDER BY id DESC");

            // 准备SQL语句并设置参数
            ps = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++){
                ps.setObject(i + 1, params.get(i));  // 参数索引从1开始
            }

            // 执行查询并封装结果
            rs = ps.executeQuery();
            while (rs.next()){
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getInt("role"));
                user.setSex(rs.getInt("sex"));
                user.setAge(rs.getInt("age"));
                user.setPhonenumber(rs.getString("phonenumber"));
                user.setAddress(rs.getString("address"));
                user.setCreateTime(rs.getTimestamp("create_time"));
                list.add(user);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /**
     * 添加新用户
     * @param user 用户对象（id字段会被数据库自增生成，不需要设置）
     * @return true表示添加成功，false表示失败
     */
    public boolean add(User user){
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            
            // SQL语句：插入用户记录
            // 注意：create_time字段由数据库自动生成（DEFAULT CURRENT_TIMESTAMP），不需要手动插入
            String sql = "INSERT INTO \"USER\" (username, password, role, sex, age, phonenumber, address) VALUES(?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            
            // 设置参数
            ps.setString(1, user.getUsername());   // 用户名
            ps.setString(2, user.getPassword());   // 密码
            ps.setInt(3, user.getRole());          // 角色（0=普通用户，1=部门经理）
            ps.setInt(4, user.getSex());           // 性别（0=女，1=男）
            ps.setInt(5, user.getAge());           // 年龄
            ps.setString(6, user.getPhonenumber());// 电话
            ps.setString(7, user.getAddress());    // 地址

            // 执行插入，返回影响的行数
            // 如果影响行数大于0，说明插入成功
            return ps.executeUpdate() > 0;
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            DBUtil.close(conn, ps, null);  // 插入操作没有ResultSet
        }
        return false;  // 发生异常时返回false
    }

    /**
     * 修改用户信息
     * @param user 用户对象（必须包含id字段）
     * @return true表示修改成功，false表示失败
     */
    public boolean update(User user){
        Connection conn = null;
        PreparedStatement ps = null;

        try{
            conn = DBUtil.getConnection();
            
            // SQL语句：更新用户记录（根据ID）
            String sql = "UPDATE \"USER\" SET username=?, password=?, role=?, sex=?, age=?, phonenumber=?, address=? WHERE id=?";
            ps = conn.prepareStatement(sql);
            
            // 设置参数（注意顺序与SQL中的占位符对应）
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setInt(3, user.getRole());
            ps.setInt(4, user.getSex());
            ps.setInt(5, user.getAge());
            ps.setString(6, user.getPhonenumber());
            ps.setString(7, user.getAddress());
            ps.setInt(8, user.getId());  // 条件：根据ID更新

            // 执行更新，返回影响的行数
            return ps.executeUpdate() > 0;
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            DBUtil.close(conn, ps, null);
        }
        return false;
    }

    /**
     * 批量删除用户
     * 使用事务保证删除的原子性（要么全部成功，要么全部失败）
     * @param ids 用户ID数组
     * @return true表示删除成功，false表示失败
     */
    public boolean delete(int[] ids){
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);  // 关闭自动提交，开启事务

            // SQL语句：根据ID删除用户
            String sql = "DELETE FROM \"USER\" WHERE id = ?";
            ps = conn.prepareStatement(sql);

            // 使用批量操作：添加多个删除语句到批处理队列
            for (int id : ids){
                ps.setInt(1, id);
                ps.addBatch();  // 添加到批处理
            }
            
            // 执行批处理
            ps.executeBatch();
            
            // 提交事务
            conn.commit();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            // 发生异常时回滚事务
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }finally {
            DBUtil.close(conn, ps, null);
        }
        return false;
    }
}