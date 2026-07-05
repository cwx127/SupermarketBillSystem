package com.example.supermarketbillsystem.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.example.supermarketbillsystem.entity.User;

public class MainFrame extends JFrame{

    private User currentUser;
    private JPanel rightPanel;

    public MainFrame(User user){
        this.currentUser = user;//接收传过来的用户信息

        setTitle ( "超市账单管理系统" );
        setSize ( 1000,700 );
        setLocationRelativeTo ( null );
        setDefaultCloseOperation ( JFrame.EXIT_ON_CLOSE );

        //整体布局：左边菜单，右边内容
        setLayout ( new BorderLayout () );//全局节目布局

        //====左侧菜单====
        JPanel leftPanel = new JPanel ();
        leftPanel.setPreferredSize ( new Dimension (200,0) );
        leftPanel.setBackground ( new Color ( 240,240,240 ) );
        leftPanel.setLayout ( new BorderLayout () );

        //欢迎信息
        JLabel lbWelcome = new JLabel ();
        lbWelcome.setPreferredSize ( new Dimension (200,50) );
        leftPanel.add ( lbWelcome,BorderLayout.NORTH );

        //菜单按钮面板
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout ( new GridLayout (5,1,10,10) );
        menuPanel.setBorder ( BorderFactory.createEmptyBorder (20,20,20,20) );

        JButton btnBill = new JButton ("账单管理");
        JButton btnSupplier = new JButton ("供应商管理");
        JButton btnUser = new JButton ("用户管理");
        JButton btnReport = new JButton ("报表管理");
        JButton btnExit = new JButton ("退出系统");

        menuPanel.add ( btnBill );
        menuPanel.add ( btnSupplier );
        menuPanel.add ( btnUser );
        menuPanel.add ( btnReport );
        menuPanel.add ( btnExit );

        leftPanel.add ( menuPanel,BorderLayout.CENTER );

        //====权限控制====
        //普通员工只能看到账单管理和退出
        if (user.getRole () == 0){
            btnSupplier.setEnabled ( false );
            btnUser.setEnabled ( false );
            btnReport.setEnabled ( false );
        }

        //====右侧内容====
        rightPanel = new JPanel ();
        rightPanel.setLayout ( new BorderLayout () );
        rightPanel.add(new JLabel("请选择左侧菜单",JLabel.CENTER),BorderLayout.CENTER);

        add(leftPanel,BorderLayout.WEST);
        add(rightPanel,BorderLayout.CENTER);

        //====按钮事件====
        btnBill.addActionListener(e -> showBillPanel());
        btnSupplier.addActionListener(e -> showSupplierPanel());
        btnUser.addActionListener(e -> showUserPanel());
        btnReport.addActionListener(e -> showReportPanel());
        btnExit.addActionListener(e -> exitSystem());
    }
    //画饼
    private void showBillPanel() {
        rightPanel.removeAll();
        rightPanel.add(new BillPanel(), BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    private void showSupplierPanel() {
        rightPanel.removeAll();
        rightPanel.add(new SupplierPanel(), BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    private void showUserPanel() {
        rightPanel.removeAll();
        rightPanel.add(new UserPanel(), BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    private void showReportPanel() {
        rightPanel.removeAll();
        rightPanel.add(new ReportPanel(), BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    private void exitSystem() {
        int result = JOptionPane.showConfirmDialog(this, "确定要退出系统吗？", "确认", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
