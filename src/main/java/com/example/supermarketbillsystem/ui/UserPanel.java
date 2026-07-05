package com.example.supermarketbillsystem.ui;

import com.example.supermarketbillsystem.dao.UserDao;
import com.example.supermarketbillsystem.entity.User;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * 用户管理面板
 * 用于展示用户列表，提供查询、添加、修改、删除用户的功能
 */
public class UserPanel extends JPanel {
    /** 用户数据表格，用于展示查询结果 */
    private JTable table;
    /** 表格数据模型，负责填充和刷新表格内容 */
    private DefaultTableModel tableModel;
    /** 用户名称输入框，用于查询条件 */
    private JTextField txtUserName;
    /** 角色下拉选择框，用于查询条件（请选择/普通用户/部门经理） */
    private JComboBox<String> cmbRole;
    /** 用户数据访问对象，用于执行数据库增删改查操作 */
    private UserDao userDao = new UserDao();

    /**
     * 构造方法：初始化用户管理面板
     * 包含三部分：顶部查询区域、中间表格区域、底部操作按钮区域
     */
    public UserPanel(){
        // 设置整体布局为边界布局（上北下南左西右东）
        setLayout(new BorderLayout());

        // ===== 顶部查询区域 =====
        JPanel searchPanel = new JPanel();
        
        // 用户名查询条件
        searchPanel.add(new JLabel("用户名称："));
        txtUserName = new JTextField(15);  // 文本框宽度为15个字符
        searchPanel.add(txtUserName);

        // 角色查询条件
        searchPanel.add(new JLabel("角色："));
        cmbRole = new JComboBox<>(new String[]{"请选择","普通用户","部门经理"});
        searchPanel.add(cmbRole);

        // 查询和重置按钮
        JButton btnSearch = new JButton("查询");
        JButton btnReset = new JButton("重置");
        searchPanel.add(btnSearch);
        searchPanel.add(btnReset);

        // 将查询面板添加到顶部（北方）
        add(searchPanel, BorderLayout.NORTH);

        // ===== 中间表格区域 =====
        // 定义表格列名：编号、姓名、性别、年龄、电话、地址、权限
        String[] columns = {"编号","姓名","性别","年龄","电话","地址","权限"};
        
        // 创建表格模型，设置为不可编辑（所有增删改只能通过底部按钮操作）
        tableModel = new DefaultTableModel(columns, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;  // 返回false表示单元格不可编辑
            }
        };
        
        // 创建表格，绑定数据模型
        table = new JTable(tableModel);
        
        // 设置表格选择模式为：支持按住Ctrl多选、按住Shift区间连续多选（用于批量删除）
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        // 设置表头字体为微软雅黑加粗12号
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));

        // 创建滚动面板包裹表格，支持垂直/水平滚动
        JScrollPane scrollPane = new JScrollPane(table);
        
        // 将滚动面板添加到中间（中央）
        add(scrollPane, BorderLayout.CENTER);

        // ===== 底部操作按钮区域 =====
        JPanel btnPanel = new JPanel();
        
        // 操作按钮：添加、修改、删除
        JButton btnAdd = new JButton("添加数据");
        JButton btnEdit = new JButton("修改数据");
        JButton btnDelete = new JButton("删除数据");

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);

        // 将按钮面板添加到底部（南方）
        add(btnPanel, BorderLayout.SOUTH);

        // ===== 事件绑定 =====
        btnSearch.addActionListener(e -> loadData());  // 查询按钮：加载数据
        
        // 重置按钮：清空输入框，恢复下拉框默认值，重新加载数据
        btnReset.addActionListener(e -> {
            txtUserName.setText("");
            cmbRole.setSelectedIndex(0);
            loadData();
        });
        
        btnAdd.addActionListener(e -> showAddDialog());    // 添加按钮：打开添加弹窗
        btnEdit.addActionListener(e -> showEditDialog());  // 修改按钮：打开修改弹窗
        btnDelete.addActionListener(e -> deleteUsers());   // 删除按钮：执行删除操作

        // 初始化时加载所有用户数据
        loadData();
    }

    /**
     * 加载用户数据到表格
     * 根据查询条件（用户名、角色）从数据库查询用户列表，然后填充到表格中
     */
    private void loadData(){
        // 获取用户输入的查询条件
        String username = txtUserName.getText().trim();  // 去除首尾空格
        
        // 将下拉框索引转换为角色代码：0=普通用户，1=部门经理
        Integer role = null;
        if (cmbRole.getSelectedIndex() == 1) role = 0;   // 选中"普通用户"
        if (cmbRole.getSelectedIndex() == 2) role = 1;   // 选中"部门经理"

        // 调用UserDao的search方法执行多条件查询
        List<User> users = userDao.search(username, role);

        // 清空表格现有数据
        tableModel.setRowCount(0);
        
        // 遍历查询结果，逐行添加到表格
        for (User user : users){
            // 将数字类型转换为中文显示：性别（1=男，0=女），角色（0=普通用户，1=部门经理）
            String sexStr = user.getSex() == 1 ? "男" : "女";
            String roleStr = user.getRole() == 0 ? "普通用户" : "部门经理";
            
            // 向表格模型添加一行数据
            tableModel.addRow(new Object[]{
                    user.getId(),           // 编号
                    user.getUsername(),     // 姓名
                    sexStr,                 // 性别
                    user.getAge(),          // 年龄
                    user.getPhonenumber(),  // 电话
                    user.getAddress(),      // 地址
                    roleStr                 // 权限
            });
        }
    }

    /**
     * 显示添加用户对话框
     * 创建一个不带用户ID的UserDialog（表示新增模式）
     */
    private void showAddDialog(){
        // 创建弹窗，传入null表示新建（不传userId）
        UserDialog dialog = new UserDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);  // 显示弹窗（阻塞等待用户操作）

        // 如果用户点击了保存（dialog返回true），刷新表格数据
        if (dialog.isSaved()) {
            loadData();
        }
    }

    /**
     * 显示修改用户对话框
     * 先校验用户选择，再创建带用户ID的UserDialog（表示修改模式）
     */
    private void showEditDialog(){
        // 获取用户选中的行索引
        int row = table.getSelectedRow();
        
        // 校验：未选中任何行
        if (row == -1){
            JOptionPane.showMessageDialog(this, "请选择一行用户");
            return;
        }
        
        // 校验：选中了多行（修改只能针对单行）
        if (table.getSelectedRows().length > 1) {
            JOptionPane.showMessageDialog(this, "只能选择一行进行修改！");
            return;
        }

        // 从表格模型中获取选中行的用户ID（第一列）
        int userId = (int) tableModel.getValueAt(row, 0);
        
        // 创建弹窗，传入userId表示修改模式
        UserDialog dialog = new UserDialog((JFrame) SwingUtilities.getWindowAncestor(this), userId);
        dialog.setVisible(true);  // 显示弹窗
        
        // 如果用户点击了保存，刷新表格数据
        if (dialog.isSaved()){
            loadData();
        }
    }

    /**
     * 删除选中的用户（支持批量删除）
     * 先校验选择，再弹出确认对话框，最后执行删除操作
     */
    private void deleteUsers(){
        // 获取用户选中的所有行索引
        int[] rows = table.getSelectedRows();
        
        // 校验：未选中任何数据
        if (rows.length == 0){
            JOptionPane.showMessageDialog(this, "请选择要删除的用户");
            return;
        }

        // 弹出确认对话框，提示用户确认删除操作
        int confirm = JOptionPane.showConfirmDialog(this, 
                "确定要删除选中的" + rows.length + "个用户吗", 
                "确认删除", 
                JOptionPane.YES_NO_OPTION);
        
        // 如果用户选择"否"，直接退出方法
        if (confirm != JOptionPane.YES_OPTION) return;

        // 收集选中行的用户ID到数组
        int[] ids = new int[rows.length];
        for (int i = 0; i < rows.length; i++){
            ids[i] = (int) tableModel.getValueAt(rows[i], 0);  // 获取每行第一列的ID
        }

        // 调用UserDao的delete方法执行批量删除
        if (userDao.delete(ids)){
            JOptionPane.showMessageDialog(this, "删除成功");
            loadData();  // 删除成功后刷新表格
        } else {
            JOptionPane.showMessageDialog(this, "删除失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}