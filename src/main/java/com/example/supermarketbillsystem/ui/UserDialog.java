package com.example.supermarketbillsystem.ui;

import com.example.supermarketbillsystem.dao.UserDao;
import com.example.supermarketbillsystem.entity.User;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 用户添加/修改对话框
 * 支持两种模式：新增模式（不传userId）和修改模式（传入userId）
 */
public class UserDialog extends JDialog {
    /** 保存状态标记，用于通知父窗口是否成功保存 */
    private boolean saved = false;
    /** 用户数据访问对象，用于执行数据库操作 */
    private UserDao userDao = new UserDao();

    /** 用户名输入框 */
    private JTextField txtUsername;
    /** 密码输入框（使用JPasswordField提高安全性） */
    private JPasswordField txtPassword;
    /** 性别下拉框（男/女） */
    private JComboBox<String> cmbSex;
    /** 年龄输入框 */
    private JTextField txtAge;
    /** 电话输入框 */
    private JTextField txtPhone;
    /** 地址输入框 */
    private JTextField txtAddress;
    /** 角色下拉框（普通用户/部门经理） */
    private JComboBox<String> cmbRole;

    /** 用户ID，新增时为null，修改时为具体值 */
    private Integer userId;

    /**
     * 构造方法：创建用户对话框
     * @param parent 父窗口（用于弹窗居中显示）
     * @param userId 用户ID，null表示新增模式，非null表示修改模式
     */
    public UserDialog(JFrame parent, Integer userId) {
        // 设置弹窗标题：新增时显示"添加用户"，修改时显示"修改用户"
        super(parent, userId == null ? "添加用户" : "修改用户", true);
        this.userId = userId;

        // 设置弹窗大小和位置
        setSize(400, 400);
        setLocationRelativeTo(parent);  // 相对于父窗口居中
        setResizable(false);  // 禁止调整大小

        // 初始化界面组件
        initComponents();

        // 如果是修改模式，加载用户已有数据
        if (userId != null) {
            loadUserData(userId);
        }
    }

    /**
     * 初始化界面组件
     * 使用绝对布局（null布局），手动设置每个组件的位置和大小
     */
    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(null);  // 使用绝对布局
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));  // 设置内边距

        // ===== 用户名 =====
        JLabel lblUsername = new JLabel("用户名：");
        lblUsername.setBounds(50, 20, 80, 25);  // (x, y, 宽度, 高度)
        panel.add(lblUsername);
        txtUsername = new JTextField(20);
        txtUsername.setBounds(130, 20, 200, 25);
        panel.add(txtUsername);

        // ===== 密码 =====
        JLabel lblPassword = new JLabel("密码：");
        lblPassword.setBounds(50, 60, 80, 25);
        panel.add(lblPassword);
        txtPassword = new JPasswordField(20);
        txtPassword.setBounds(130, 60, 200, 25);
        panel.add(txtPassword);

        // ===== 性别 =====
        JLabel lblSex = new JLabel("性别：");
        lblSex.setBounds(50, 100, 80, 25);
        panel.add(lblSex);
        cmbSex = new JComboBox<>(new String[]{"男", "女"});
        cmbSex.setBounds(130, 100, 200, 25);
        panel.add(cmbSex);

        // ===== 年龄 =====
        JLabel lblAge = new JLabel("年龄：");
        lblAge.setBounds(50, 140, 80, 25);
        panel.add(lblAge);
        txtAge = new JTextField(20);
        txtAge.setBounds(130, 140, 200, 25);
        panel.add(txtAge);

        // ===== 电话 =====
        JLabel lblPhone = new JLabel("电话：");
        lblPhone.setBounds(50, 180, 80, 25);
        panel.add(lblPhone);
        txtPhone = new JTextField(20);
        txtPhone.setBounds(130, 180, 200, 25);
        panel.add(txtPhone);

        // ===== 地址 =====
        JLabel lblAddress = new JLabel("地址：");
        lblAddress.setBounds(50, 220, 80, 25);
        panel.add(lblAddress);
        txtAddress = new JTextField(20);
        txtAddress.setBounds(130, 220, 200, 25);
        panel.add(txtAddress);

        // ===== 角色 =====
        JLabel lblRole = new JLabel("角色：");
        lblRole.setBounds(50, 260, 80, 25);
        panel.add(lblRole);
        cmbRole = new JComboBox<>(new String[]{"普通用户", "部门经理"});
        cmbRole.setBounds(130, 260, 200, 25);
        panel.add(cmbRole);

        // ===== 保存按钮 =====
        JButton btnSave = new JButton("保存");
        btnSave.setBounds(100, 310, 80, 30);
        btnSave.addActionListener(e -> saveUser());  // 绑定保存事件
        panel.add(btnSave);

        // ===== 取消按钮 =====
        JButton btnCancel = new JButton("取消");
        btnCancel.setBounds(220, 310, 80, 30);
        btnCancel.addActionListener(e -> dispose());  // 绑定关闭事件
        panel.add(btnCancel);

        // 将面板添加到对话框
        add(panel);
    }

    /**
     * 加载用户数据到表单（修改模式使用）
     * 根据用户ID从数据库查询用户信息，然后填充到各个输入框中
     * @param id 用户ID
     */
    private void loadUserData(int id) {
        // 查询所有用户，找到匹配ID的用户
        List<User> users = userDao.findAll();
        User user = null;
        for (User u : users) {
            if (u.getId() == id) {
                user = u;
                break;
            }
        }

        // 如果找到用户，填充表单数据
        if (user != null) {
            txtUsername.setText(user.getUsername());
            txtPassword.setText(user.getPassword());
            cmbSex.setSelectedIndex(user.getSex() == 1 ? 0 : 1);  // 1=男(索引0)，0=女(索引1)
            txtAge.setText(String.valueOf(user.getAge()));
            txtPhone.setText(user.getPhonenumber());
            txtAddress.setText(user.getAddress());
            cmbRole.setSelectedIndex(user.getRole() == 0 ? 0 : 1);  // 0=普通用户(索引0)，1=部门经理(索引1)
        }
    }

    /**
     * 保存用户数据
     * 先进行输入校验，然后根据模式选择调用新增或修改方法
     */
    private void saveUser() {
        // 获取用户输入的表单数据（去除首尾空格）
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();  // JPasswordField需要特殊处理
        String ageStr = txtAge.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();

        // ===== 输入校验 =====
        // 用户名不能为空
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // 新增时密码不能为空，修改时可以为空（保持原密码）
        if (password.isEmpty() && userId == null) {
            JOptionPane.showMessageDialog(this, "密码不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // 年龄不能为空
        if (ageStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "年龄不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 年龄必须是数字
        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "年龄必须是数字！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ===== 封装用户对象 =====
        User user = new User();
        if (userId != null) {
            user.setId(userId);  // 修改模式需要设置ID
        }
        user.setUsername(username);
        
        // 处理密码：输入了新密码则使用新密码，否则保留原密码（仅修改模式）
        if (!password.isEmpty()) {
            user.setPassword(password);
        } else if (userId != null) {
            // 查询原用户获取密码（通过用户名查询，假设用户名唯一）
            User existing = userDao.findByUsername(username);
            if (existing != null) {
                user.setPassword(existing.getPassword());
            }
        }
        
        // 设置其他字段
        user.setSex(cmbSex.getSelectedIndex() == 0 ? 1 : 0);  // 索引0=男(1)，索引1=女(0)
        user.setAge(age);
        user.setPhonenumber(phone);
        user.setAddress(address);
        user.setRole(cmbRole.getSelectedIndex() == 0 ? 0 : 1);  // 索引0=普通用户(0)，索引1=部门经理(1)

        // ===== 执行保存操作 =====
        boolean success;
        if (userId == null) {
            // 新增模式：调用add方法
            success = userDao.add(user);
        } else {
            // 修改模式：调用update方法
            success = userDao.update(user);
        }

        // ===== 显示结果 =====
        if (success) {
            JOptionPane.showMessageDialog(this, userId == null ? "添加成功" : "修改成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            saved = true;  // 设置保存成功标记
            dispose();  // 关闭对话框
        } else {
            JOptionPane.showMessageDialog(this, userId == null ? "添加失败" : "修改失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 获取保存状态
     * @return true表示用户点击了保存并成功，false表示取消或保存失败
     */
    public boolean isSaved() {
        return saved;
    }
}