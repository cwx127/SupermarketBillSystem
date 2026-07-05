package com.example.supermarketbillsystem.ui;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.example.supermarketbillsystem.dao.SupplierDao;
import com.example.supermarketbillsystem.entity.Supplier;

public class SupplierDialog extends JDialog{
    private boolean saved = false;
    private SupplierDao supplierDao = new SupplierDao ();
    Integer supplierId;
    private JTextField txtsuppliername;
    private JTextField txtcontact;
    private JTextField txtphone;
    private JTextField txtaddress;
    private JTextArea txtdescription;

    /**
     * 账单弹窗构造方法
     * @param parent 弹窗的父窗口，用来让窗口居中显示
     * @param supplierId 账单主键ID；传null代表新增账单，传入数字代表修改对应账单
     */

    public SupplierDialog(JFrame parent,Integer supplierId){
        super(parent,supplierId == null ? "添加供应商" : "修改供应商", true);
        this.supplierId = supplierId;

        setSize ( 400,400 );
        setLocationRelativeTo ( parent );
        setResizable ( false );

        initComponents();

        if (supplierId != null){
            loadSupplierData(supplierId);
        }
    }

    private void initComponents() {
        JPanel panel = new JPanel ();
        panel.setLayout ( null );
        panel.setBorder ( BorderFactory.createEmptyBorder ( 20, 20, 20, 20 ) );

        //====供应商名称====
        JLabel lblsuppliername = new JLabel ( "供应商名称" );
        lblsuppliername.setBounds ( 50, 20, 80, 25 );
        panel.add ( lblsuppliername );
        txtsuppliername = new JTextField ( 20 );
        txtsuppliername.setBounds ( 130, 20, 200, 25 );
        panel.add ( txtsuppliername );

        //====联系人====
        JLabel lblcontact = new JLabel ( "联系人" );
        lblcontact.setBounds ( 50, 55, 80, 25 );
        panel.add ( lblcontact );
        txtcontact = new JTextField ( 20 );
        txtcontact.setBounds ( 130, 55, 200, 25 );
        panel.add ( txtcontact );

        //====联系人电话====
        JLabel lblphone = new JLabel ( "联系人电话" );
        lblphone.setBounds ( 50, 90, 80, 25 );
        panel.add ( lblphone );
        txtphone = new JTextField ( 20 );
        txtphone.setBounds ( 130, 90, 200, 25 );
        panel.add ( txtphone );

        //====地址====
        JLabel lbladdress = new JLabel ( "地址" );
        lbladdress.setBounds ( 50, 125, 80, 25 );
        panel.add ( lbladdress );
        txtaddress = new JTextField ( 20 );
        txtaddress.setBounds ( 130, 125, 200, 25 );
        panel.add ( txtaddress );

        //====供应商描述====
        JLabel lbldescription = new JLabel ( "供应商描述" );
        lbldescription.setBounds ( 50, 160, 80, 25 );
        panel.add ( lbldescription );
        txtdescription = new JTextArea ();
        txtdescription.setBounds ( 130, 160, 200, 50 );
        panel.add ( txtdescription );

        // ===== 保存按钮 =====
        JButton btnSave = new JButton ( "保存" );
        btnSave.setBounds ( 100, 335, 80, 30 );
        btnSave.addActionListener ( e -> saveSupplier () );
        panel.add ( btnSave );

        // ===== 取消按钮 =====
        JButton btnCancel = new JButton ( "取消" );
        btnCancel.setBounds ( 220, 335, 80, 30 );
        btnCancel.addActionListener ( e -> dispose () );
        panel.add ( btnCancel );

        // 将面板添加到对话框
        add ( panel );
    }

        private void loadSupplierData(int id) {
            // 查询所有用户，找到匹配ID的用户
        List< Supplier > suppliers = supplierDao.findAll();
        Supplier supplier = null;
        for (Supplier s : suppliers) {
            if (s.getSupplier_id () == id) {
                supplier = s;
                break;
                }
            }

            //如果找到供应商，填充表单数据
            if (supplier != null){
            txtsuppliername.setText ( supplier.getName () );
            txtdescription.setText ( supplier.getDescription () );
            txtcontact.setText ( supplier.getContact () );
            txtphone.setText ( supplier.getPhone () );
            txtaddress.setText ( supplier.getAddress () );
            }
        }

    private void saveSupplier(){
        String suppliername = txtsuppliername.getText ().trim ();
        String description = txtdescription.getText ().trim ();
        String contact = txtcontact.getText ().trim ();
        String phone = txtphone.getText ().trim ();
        String address = txtaddress.getText ().trim ();

        // ===== 输入校验 =====
        // 供应商名称不能为空
        if (suppliername.isEmpty()) {
            JOptionPane.showMessageDialog(this, "供应商名称不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 供应商描述不能为空
        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "供应商描述不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //联系人不能为空
        if (contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "联系人不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 电话不能为空
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "电话不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 地址不能为空
        if (address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "地址不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //封装供应商对象
        Supplier supplier = new Supplier();
        if (supplierId != null) {
            supplier.setSupplier_id(supplierId);
        }

        supplier.setName(suppliername);
        supplier.setDescription(description);
        supplier.setContact(contact);
        supplier.setPhone(phone);
        supplier.setAddress(address);

        // ===== 执行保存操作 =====
        boolean success;
        if (supplierId == null) {
            success = supplierDao.add(supplier);
        } else {
            success = supplierDao.update(supplier);
        }

        // ===== 显示结果 =====
        if (success) {
            JOptionPane.showMessageDialog(this, supplierId == null ? "添加成功" : "修改成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            saved = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, supplierId == null ? "添加失败" : "修改失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
