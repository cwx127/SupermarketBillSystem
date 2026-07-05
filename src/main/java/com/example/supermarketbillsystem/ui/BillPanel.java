package com.example.supermarketbillsystem.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.example.supermarketbillsystem.dao.BillDao;
import com.example.supermarketbillsystem.dao.SupplierDao;
import com.example.supermarketbillsystem.entity.Bill;
import com.example.supermarketbillsystem.entity.Supplier;

public class BillPanel extends JPanel {
    private JTable table;//账单数据表格，展示查询结果
    private DefaultTableModel tableModel;//表格数据模型，负责填充、刷新表格内容
    private JTextField txtProductName;//商品名称输入框
    private JComboBox<String> cmbIsPaid;// 是否付款下拉选择框
    private BillDao billDao = new BillDao ();// 账单DAO对象，调用数据库查询/增删改方法
    private SupplierDao supplierDao = new SupplierDao();

    public BillPanel(){
        setLayout ( new BorderLayout () );//边界布局

        //====顶部查询区域====
        JPanel searchPanel = new JPanel ();
        searchPanel.add ( new JLabel ("商品名称：") );
        txtProductName = new JTextField (15);
        searchPanel.add ( txtProductName );

        searchPanel.add ( new JLabel ("是否付款：") );
        cmbIsPaid = new JComboBox <> (new String[]{"请选择","是","否"});
        searchPanel.add ( cmbIsPaid );

        JButton btnSearch = new JButton ("查询");
        JButton btnReset = new JButton ("重置");
        searchPanel.add ( btnSearch );
        searchPanel.add ( btnReset );

        add ( searchPanel,BorderLayout.NORTH );

        //====中间表格区域====
        //定义表头数组
        String[] columns = {"账单ID","商品名称","数量","金额","供应商","是否付款","商品描述","交易时间"};
        tableModel = new DefaultTableModel (columns,0){//创建不可编辑的表格模型
            @Override
            public boolean isCellEditable(int row, int column){//所有增删改只能通过底部弹窗按钮操作
                return false;
            }
        };
        table = new JTable (tableModel);//表格所有数据、列信息都由 tableModel 统一管理，实现视图与数据分离
        table.setSelectionMode ( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );//支持按住Ctrl多选、按住Shift区间连续多选，对应「批量删除」功能，一次选中多条账单执行删除
        table.getTableHeader ().setFont ( new Font ( "微软雅黑",Font.BOLD,12 ) );//设置字体

        JScrollPane scrollPane = new JScrollPane (table);//滚动面板
        add(scrollPane,BorderLayout.CENTER);

        //====底部按钮区域====
        JPanel btnPanel = new JPanel ();
        JButton btnAdd = new JButton ("添加数据");
        JButton btnEdit = new JButton ("修改数据");
        JButton btnDelete = new JButton ("删除数据");
        JButton btnExport = new JButton ("导出");

        btnPanel.add ( btnAdd );
        btnPanel.add ( btnEdit );
        btnPanel.add ( btnDelete );
        btnPanel.add ( btnExport );

        add(btnPanel,BorderLayout.SOUTH);

        //====事件绑定====
        btnSearch.addActionListener ( e -> loadData ());
        btnReset.addActionListener( e -> {txtProductName.setText ( "" );
        cmbIsPaid.setSelectedIndex ( 0 );
        loadData ();
        });
        btnAdd.addActionListener ( e -> showAddDialog ());
        btnEdit.addActionListener ( e -> showEditDialog());
        btnDelete.addActionListener ( e -> deleteBills());
        btnExport.addActionListener ( e -> exportData());

        //加载数据
        loadData();
    }

    //加载数据到表格
    private void loadData(){
        String productNAme = txtProductName.getText ().trim ();//读取输入框内用户输入的商品关键字,防止用户输入全空格造成无效查询
        Integer isPaid = null;
        if (cmbIsPaid.getSelectedIndex () == 1)isPaid = 1;
        if (cmbIsPaid.getSelectedIndex () == 2)isPaid = 0;

        List<Bill> bills = billDao.search ( productNAme,isPaid );//调用 DAO 执行多条件查询

        tableModel.setRowCount ( 0 );//清空表格
        for (Bill bill : bills){
            //根据supplier_id查供应商名称
            Supplier supplier = supplierDao.findById(bill.getSupplierId ());
            String supplierName = supplier != null ? supplier.getName ():"未知";
            String paidStr = bill.getIsPaid () == 1?"是":"否";

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String timeStr = bill.getCreateTime() != null ? sdf.format(bill.getCreateTime()) : "";

            tableModel.addRow ( new Object[]{//向表格模型新增一行数据
                    bill.getBillId (),
                    bill.getProduct_name (),
                    bill.getQuantity (),
                    bill.getAmount (),
                    supplierName,
                    paidStr,
                    bill.getDescription() != null ? bill.getDescription() : "",
                    timeStr
            });


        }
    }
    //====显示添加对话框====
    private void showAddDialog(){
        //创建弹窗，null 表示新建（不传 billId）
        BillDialog dialog = new BillDialog((JFrame)SwingUtilities.getWindowAncestor ( this ),null);
        dialog.setVisible(true);

        // 如果用户点击了保存（dialog 返回 true）
        if (dialog.isSaved()) {
            loadData ();//刷新表格
        }
    }


    //====显示修改对话框====
    private void showEditDialog(){
        int row = table.getSelectedRow ();
        if (row == -1){// 校验：未选中任何行
            JOptionPane.showMessageDialog ( this,"请选择一行数据" );
            return;
        }
        if (table.getSelectedRows ().length > 1) {//校验：选中多行数据
            JOptionPane.showMessageDialog ( this, "只能选择一行进行修改！" );
            return;
        }

        //获取选中行的ID
        int billId = (int) tableModel.getValueAt ( row,0 );//从表格数据模型取出单元格数据；
        //创建弹窗，传入账单ID表示修改模式
        BillDialog dialog = new BillDialog((JFrame) SwingUtilities.getWindowAncestor ( this ),billId);//创建副窗口
        dialog.setVisible(true);
        //点击保存，刷新表格
        if (dialog.isSaved()){
            loadData ();
        }
    }

    //====删除账单====
    private void deleteBills(){
        int[] rows = table.getSelectedRows ();
        if (rows.length == 0){
            JOptionPane.showMessageDialog ( this,"请选择要删除的数据" );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog ( this,"确定要删除选中的" + rows.length + "条数据吗","确认删除",JOptionPane.YES_NO_OPTION );
        if (confirm != JOptionPane.YES_NO_OPTION) return;

        int[] ids = new int[rows.length];
        for (int i = 0; i < rows.length; i++){
            ids[i] = (int) tableModel.getValueAt ( rows[i],0 );
        }

        if(billDao.delete ( ids )){
            JOptionPane.showMessageDialog ( this,"删除成功" );
            loadData ();
        } else {
            JOptionPane.showMessageDialog ( this,"删除失败！","错误",JOptionPane.ERROR_MESSAGE );
        }
    }

    //====导出数据到文件====
    private void exportData(){
        JFileChooser chooser = new JFileChooser ();
        chooser.setDialogTitle ( "选择保存位置" );
        int result = chooser.showSaveDialog ( this );// 弹出「保存文件」弹窗
        if (result != JFileChooser.APPROVE_OPTION) return;// 用户点取消/关闭窗口，直接退出方法

        File file = chooser.getSelectedFile ();
        //带资源自动关闭的IO流 try-with-resources
        try (PrintWriter writer = new PrintWriter ( file )){
            writer.println ("账单ID，商品名称，数量，金额，供应商，是否付款，商品描述，交易时间");

            int[] selectedRows = table.getSelectedRows ();
            if(selectedRows.length > 0){
                for (int row : selectedRows){
                    writer.println (getRowData(row));
                }
            }

            JOptionPane.showMessageDialog ( this,"导出成功" );
        } catch (IOException e){
            e.printStackTrace ();
            //IO异常捕获：权限不足、路径非法、磁盘满等
            JOptionPane.showMessageDialog ( this,"导出失败","错误",JOptionPane.INFORMATION_MESSAGE );
        }
    }

    private String getRowData(int row){
        StringBuilder sb = new StringBuilder ();
        for(int i = 0; i < tableModel.getColumnCount (); i++){
            sb.append ( tableModel.getValueAt ( row,i )).append ( "," );
        }
        return sb.toString ();
    }

    }


