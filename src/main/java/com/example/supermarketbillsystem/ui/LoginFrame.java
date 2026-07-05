package com.example.supermarketbillsystem.ui;

import com.example.supermarketbillsystem.dao.UserDao;//数据库操作类
import com.example.supermarketbillsystem.entity.User;//封装数据库实体类
import javax.swing.*;//窗口布局
import java.awt.*;//窗口布局
import java.awt.event.*;//点击事件

//创建输入框
public class LoginFrame extends JFrame{
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private UserDao userDao = new UserDao ();//创建数据库操作对象

    //窗口标题、窗口大小、居中、退出窗口、禁止改变大小
    public LoginFrame(){
        setTitle ( "超市账单管理系统 - 登录" );
        setSize ( 400,300 );
        setLocationRelativeTo ( null );
        setDefaultCloseOperation ( JFrame.EXIT_ON_CLOSE );
        setResizable ( false );

        //创建面板
        JPanel panel = new JPanel ();
        panel.setLayout ( null );

        //用户名标签和输入框
        JLabel lblUser = new JLabel ("用户名：");
        lblUser.setBounds ( 50,40,80,25 );
        panel.add(lblUser);
        txtUsername = new JTextField(20);
        txtUsername.setBounds ( 140,40,200,25 );
        panel.add(txtUsername);

        //密码标签和输入框
        JLabel lbPwd = new JLabel ("密码：");
        lbPwd.setBounds ( 50,80,80,25 );
        panel.add(lbPwd);
        txtPassword = new JPasswordField(20);
        txtPassword.setBounds ( 140,80,200,25 );
        panel.add(txtPassword);

        //身份选择
        JLabel lbRole = new JLabel ("身份：");
        lbRole.setBounds ( 50,120,80,25 );
        panel.add ( lbRole );

        String[] roles = {"请选择","普通员工","部门经理"};
        cmbRole = new JComboBox <> (roles);
        cmbRole.setBounds ( 140,120,200,25 );
        panel.add ( cmbRole );

        //登录按钮
        JButton btnLogin = new JButton ("登录");
        btnLogin.setBounds ( 100,160,80,30 );
        panel.add ( btnLogin );

        //重置按钮
        JButton btnReset = new JButton ("重置");
        btnReset.setBounds ( 220,160,80,30 );
        panel.add ( btnReset );

        //登录按钮点击事件
        btnLogin.addActionListener ( new ActionListener () {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        } );

        //重置按钮点击事件
        btnReset.addActionListener ( new ActionListener () {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtUsername.setText ( "" );
                txtPassword.setText ( "" );
                cmbRole.setSelectedIndex ( 0 );
            }
        } );
        add(panel);
    }

    //登录验证方法
    private void login(){
        String username = txtUsername.getText ().trim ();
        String password = new String (txtPassword.getPassword ());
        int roleIndex = cmbRole.getSelectedIndex ();

        //检验是否为空
        if(username.isEmpty () || password.isEmpty () || roleIndex == 0){
            JOptionPane.showMessageDialog ( this,"用户名、密码和身份不能为空！","提示",JOptionPane.WARNING_MESSAGE );
            return;
        }
        //去数据库查询
        User user = userDao.findByUsername ( username );
        //验证用户名和密码
        if (user == null){
            JOptionPane.showMessageDialog ( this,"用户名不存在！","错误",JOptionPane.ERROR_MESSAGE );
            return;
        }
        if (!user.getPassword ().equals ( password )){
            JOptionPane.showMessageDialog ( this,"密码错误","错误",JOptionPane.ERROR_MESSAGE );
            return;
        }
        //验证身份是否匹配
        int selectedRole = (roleIndex == 1) ? 0 : 1;
        if (user.getRole () != selectedRole){
            JOptionPane.showMessageDialog ( this,"身份选择错误","错误",JOptionPane.ERROR_MESSAGE );
            return;
        }

        //登录成功，跳转主界面
        JOptionPane.showMessageDialog ( this,"登录成功","欢迎",JOptionPane.INFORMATION_MESSAGE );
        new MainFrame(user).setVisible(true);
        this.dispose ();//关闭登录窗口
    }

    //程序入口
    public static void main(String[] args) {
        //设置外观
        try{
            UIManager.setLookAndFeel ( UIManager.getSystemLookAndFeelClassName () );
        } catch (Exception e){
            e.printStackTrace ();
        }
        //显示登录窗口
        new LoginFrame ().setVisible ( true );
    }
}
