package com.example.supermarketbillsystem.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.JButton;
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

import com.example.supermarketbillsystem.dao.SupplierDao;
import com.example.supermarketbillsystem.entity.Supplier;


public class SupplierPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSupplierName;
    private SupplierDao supplierDao = new SupplierDao ();

    public SupplierPanel(){
        setLayout ( new BorderLayout () );

        //====顶部查询区域====
        JPanel searchPanel = new JPanel ();
        searchPanel.add ( new JLabel ("供应商名称：") );
        txtSupplierName = new JTextField (15);
        searchPanel.add ( txtSupplierName );

        JButton btnSearch = new JButton ("查询");
        JButton btnReset = new JButton ("重置");
        searchPanel.add ( btnSearch );
        searchPanel.add ( btnReset );

        add ( searchPanel,BorderLayout.NORTH );

        //====中间表格区域====
        //定义表头数组
        String[] columns = {"编号","供应商名称","供应商描述","联系人","电话","地址"};
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

        btnPanel.add ( btnAdd );
        btnPanel.add ( btnEdit );
        btnPanel.add ( btnDelete );

        add(btnPanel,BorderLayout.SOUTH);

        //====事件绑定====
        btnSearch.addActionListener ( e -> loadData ());
        btnReset.addActionListener( e -> {txtSupplierName.setText ( "" );
            loadData ();
        });
        btnAdd.addActionListener ( e -> showAddDialog ());
        btnEdit.addActionListener ( e -> showEditDialog());
        btnDelete.addActionListener ( e -> deleteSupplier ());

        //加载数据
        loadData();
    }

    private void loadData(){
        String suppliername = txtSupplierName.getText ().trim ();

        List<Supplier> suppliers = supplierDao.search(suppliername);

        tableModel.setRowCount ( 0 );

        for (Supplier supplier : suppliers) {
            tableModel.addRow(new Object[]{
                    supplier.getSupplier_id(),
                    supplier.getName(),
                    supplier.getDescription(),
                    supplier.getContact(),
                    supplier.getPhone(),
                    supplier.getAddress()
            });
        }
    }

    //====显示添加对话框====
    private void showAddDialog(){
        //创建弹窗，null 表示新建（不传 billId）
        SupplierDialog dialog = new SupplierDialog((JFrame)SwingUtilities.getWindowAncestor ( this ),null);
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
        int supplierId = (int) tableModel.getValueAt ( row,0 );//从表格数据模型取出单元格数据；
        //创建弹窗，传入账单ID表示修改模式
        SupplierDialog dialog = new SupplierDialog((JFrame) SwingUtilities.getWindowAncestor ( this ),supplierId);//创建副窗口
        dialog.setVisible(true);
        //点击保存，刷新表格
        if (dialog.isSaved()){
            loadData ();
        }
    }
    //====删除账单====
    private void deleteSupplier(){
        int[] rows = table.getSelectedRows ();
        if (rows.length == 0){
            JOptionPane.showMessageDialog ( this,"请选择要删除的数据" );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog ( this,"确定要删除选中的" + rows.length + "条数据吗","确认删除",JOptionPane.YES_NO_OPTION );
            if (confirm != JOptionPane.YES_OPTION) return;

        int[] ids = new int[rows.length];
        for (int i = 0; i < rows.length; i++){
            ids[i] = (int) tableModel.getValueAt ( rows[i],0 );
        }

            if (supplierDao.delete(ids)) {
            JOptionPane.showMessageDialog ( this,"删除成功" );
            loadData ();
        } else {
            JOptionPane.showMessageDialog ( this,"删除失败！","错误",JOptionPane.ERROR_MESSAGE );
        }
    }
    }
