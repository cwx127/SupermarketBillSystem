package com.example.supermarketbillsystem.ui;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.example.supermarketbillsystem.dao.BillDao;
import com.example.supermarketbillsystem.dao.SupplierDao;
import com.example.supermarketbillsystem.entity.Bill;
import com.example.supermarketbillsystem.entity.Supplier;

/**
 * 账单添加/修改对话框
 * 支持两种模式：新增模式（不传billId）和修改模式（传入billId）
 * 用于弹出窗口让用户填写或修改账单信息
 */
public class BillDialog extends JDialog{
    /** 保存状态标记，用于通知父窗口（BillPanel）是否成功保存 */
    private boolean saved = false;
    /** 账单数据访问对象，用于执行数据库操作 */
    private BillDao billDao = new BillDao();
    private JTextField txtProduct_name;
    private JTextField txtQuanity;
    private JTextField txtUnit;
    private JTextField txtAmount;
    private JComboBox<String> cmbIsPaid;
    private Integer billId;
    private JComboBox<Supplier> cmbSupplier;
    private JTextArea txtDescription;


    /**
     * 账单弹窗构造方法
     * @param parent 弹窗的父窗口，用来让窗口居中显示
     * @param billId 账单主键ID；传null代表新增账单，传入数字代表修改对应账单
     */

    public BillDialog(JFrame parent, Integer billId){
        super(parent,billId == null ? "添加商品" : "修改商品",true);
        this.billId = billId;

        // 设置弹窗大小和位置
        setSize(400, 440);
        setLocationRelativeTo(parent);  // 相对于父窗口居中
        setResizable(false);  // 禁止调整大小

        // 初始化界面组件
        initComponents();

        // 如果是修改模式，加载用户已有数据
        if (billId != null) {
            loadBillData(billId);
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

        // ===== 商品名称 =====
        JLabel lblProduct_name = new JLabel("商品名称：");
        lblProduct_name.setBounds(50, 20, 80, 25);
        panel.add(lblProduct_name);
        txtProduct_name = new JTextField(20);
        txtProduct_name.setBounds(130, 20, 200, 25);
        panel.add(txtProduct_name);

        // ===== 交易数量 =====
        JLabel lblQuanity = new JLabel("交易数量：");
        lblQuanity.setBounds(50, 55, 80, 25);
        panel.add(lblQuanity);
        txtQuanity = new JTextField (20);
        txtQuanity.setBounds(130, 55, 200, 25);
        panel.add(txtQuanity);

        // ===== 交易单位 =====
        JLabel lblUnit = new JLabel("交易单位：");
        lblUnit.setBounds(50, 90, 80, 25);
        panel.add(lblUnit);
        txtUnit = new JTextField (20);
        txtUnit.setBounds(130, 90, 200, 25);
        panel.add(txtUnit);

        // ===== 交易金额 =====
        JLabel lblAmount = new JLabel("交易金额：");
        lblAmount.setBounds(50, 125, 80, 25);
        panel.add(lblAmount);
        txtAmount = new JTextField (20);
        txtAmount.setBounds(130, 125, 200, 25);
        panel.add(txtAmount);

        // ===== 是否付款 =====
        JLabel lblIsPaid = new JLabel("是否付款：");
        lblIsPaid.setBounds(50, 160, 80, 25);
        panel.add(lblIsPaid);
        cmbIsPaid = new JComboBox<>(new String[]{"是", "否"});
        cmbIsPaid.setBounds(130, 160, 200, 25);
        panel.add(cmbIsPaid);

        // ===== 所属供应商 =====
        JLabel lblSupplier = new JLabel("所属供应商：");
        lblSupplier.setBounds(50, 195, 80, 25);
        panel.add(lblSupplier);

        SupplierDao supplierDao = new SupplierDao ();
        List<Supplier> suppliers = supplierDao.findAll ();
        cmbSupplier = new JComboBox <> ();
        for (Supplier sup : suppliers) {
            cmbSupplier.addItem ( sup );
        }

        // ===== 商品描述 =====
        JLabel lblDescription = new JLabel("商品描述：");
        lblDescription.setBounds(50, 230, 80, 25);
        panel.add(lblDescription);
        txtDescription = new JTextArea();
        txtDescription.setBounds(130, 230, 200, 50);
        panel.add(txtDescription);

        // ===== 账单时间 =====
        JLabel lblCreateTime = new JLabel("账单时间：");
        lblCreateTime.setBounds(50, 290, 80, 25);
        panel.add(lblCreateTime);
        JTextField txtCreateTime = new JTextField(20);
        txtCreateTime.setBounds(130, 290, 200, 25);
        txtCreateTime.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        txtCreateTime.setEditable(false);
        panel.add(txtCreateTime);

        // 设置显示渲染器
        cmbSupplier.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Supplier) {
                    setText(((Supplier) value).getName());
                }
                return this;
            }
        });



        cmbSupplier.setBounds(130, 195, 200, 25);
        panel.add(cmbSupplier);

        // ===== 保存按钮 =====
        JButton btnSave = new JButton("保存");
        btnSave.setBounds(100, 335, 80, 30);
        btnSave.addActionListener(e -> saveBill());
        panel.add(btnSave);

        // ===== 取消按钮 =====
        JButton btnCancel = new JButton("取消");
        btnCancel.setBounds(220, 335, 80, 30);
        btnCancel.addActionListener(e -> dispose());
        panel.add(btnCancel);

        // 将面板添加到对话框
        add(panel);
    }
    /**
     * 加载账单数据到表单（修改模式使用）
     * 根据账单ID从数据库查询用户信息，然后填充到各个输入框中
     * @param id 账单ID
     */
    private void loadBillData(int id) {
        // 查询所有账单，找到匹配ID的账单
        List<Bill> bills = billDao.findAll();
        Bill bill = null;
        for (Bill b : bills) {
            if (b.getBillId() == id) {
                bill = b;
                break;
            }
        }

        // 如果找到产品，填充表单数据
        if (bill != null) {
            txtProduct_name.setText(bill.getProduct_name ());
            txtQuanity.setText(String.valueOf ( bill.getQuantity () ));
            txtUnit.setText ( bill.getUnit() );
            txtAmount.setText ( String.valueOf ( bill.getAmount ()));
            cmbIsPaid.setSelectedIndex(bill.getIsPaid () == 1 ? 0 : 1);  // 1=是(索引0)，0=否(索引1)

            int supplierId = bill.getSupplierId ();
            for (int i = 0; i < cmbSupplier.getItemCount (); i++){
                Supplier sup = (Supplier)
                        cmbSupplier.getItemAt ( i );
                if (sup.getSupplier_id () == supplierId){
                    cmbSupplier.setSelectedIndex (i);
                    break;
                }
            }

            txtDescription.setText ( bill.getDescription () );

            }
        }

    /**
     * 保存用户数据
     * 先进行输入校验，然后根据模式选择调用新增或修改方法
     */
    private void saveBill() {
        // 获取用户输入的表单数据（去除首尾空格）
        String product_name = txtProduct_name.getText().trim();
        String quality = txtQuanity.getText ().trim ();
        String unit = txtUnit.getText ().trim ();
        String amount = txtAmount.getText ().trim ();
        int is_paid = cmbIsPaid.getSelectedIndex ();

        Supplier selectSupplier = (Supplier) cmbSupplier.getSelectedItem ();
        int supplier_id = selectSupplier
                 != null ? selectSupplier.getSupplier_id () : 0;

        String description = txtDescription.getText ().trim ();

            // ===== 输入校验 =====
            // 商品名称不能为空
            if (product_name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "商品名称不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // 商品数量不能为空
            if (quality.isEmpty()) {
                JOptionPane.showMessageDialog ( this, "商品数量不能为空！", "提示", JOptionPane.WARNING_MESSAGE );
                return;
            }
            int num1;
            try {
                num1 = Integer.parseInt(quality);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "商品数量必须是数字！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // 交易金额不能为空
            if (amount.isEmpty()) {
                JOptionPane.showMessageDialog(this, "交易金额不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double num2;
            try {
                num2 = Double.parseDouble(amount);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "交易金额必须是数字！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // 供应商名称不能为空
            // 校验供应商下拉框
            Supplier selectedSupplier = (Supplier) cmbSupplier.getSelectedItem();
            if (selectedSupplier == null) {
                JOptionPane.showMessageDialog(this, "请选择供应商！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // ===== 封装账单对象 =====
            Bill bill = new Bill ();
            if (billId != null) {
                bill.setBillId ( billId );  // 修改模式需要设置ID
            }

            // 设置其他字段
            bill.setProduct_name ( product_name );
            bill.setQuantity(Integer.parseInt(quality.trim() ) );
            bill.setUnit ( unit );
            bill.setAmount ( Double.parseDouble ( amount.trim () ) );
            bill.setIsPaid ( cmbIsPaid.getSelectedIndex () == 0 ? 1 : 0 );
            Supplier sup = (Supplier) cmbSupplier.getSelectedItem();
            bill.setSupplierId(sup.getSupplier_id());
            bill.setDescription ( description );

            // ===== 执行保存操作 =====
            boolean success;
            if (billId == null) {
                // 新增模式：调用add方法
                success = billDao.add(bill);
            } else {
                // 修改模式：调用update方法
                success = billDao.update(bill);
            }

            // ===== 显示结果 =====
            if (success) {
                JOptionPane.showMessageDialog(this, billId == null ? "添加成功" : "修改成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                saved = true;  // 设置保存成功标记
                dispose();  // 关闭对话框
            } else {
                JOptionPane.showMessageDialog(this, billId == null ? "添加失败" : "修改失败", "错误", JOptionPane.ERROR_MESSAGE);
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
