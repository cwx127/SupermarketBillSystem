# 超市账单管理系统

基于 Java Swing + Oracle 的桌面端超市账单管理应用。

## 技术栈

| 层次 | 技术 |
|------|------|
| 界面层 | Java Swing |
| 数据访问层 | JDBC |
| 数据库 | Oracle 12c |
| 构建工具 | Maven |

## 功能模块

- **用户登录** — 支持普通员工和部门经理两种角色登录，基于角色的菜单权限控制
- **账单管理** — 账单的增删改查，支持商品名称模糊查询、付款状态筛选、供应商下拉选择、CSV 数据导出
- **供应商管理** — 供应商信息的增删改查
- **用户管理** — 用户信息的增删改查（经理专属）
- **报表统计** — 总交易金额/笔数统计、已付/未付金额统计、供应商交易排行

## 项目结构

```
src/main/java/com/example/supermarketbillsystem/
├── entity/          # 实体类（Bill, Supplier, User）
├── dao/             # 数据访问层（BillDao, SupplierDao, UserDao）
├── ui/              # 界面层（LoginFrame, MainFrame, BillPanel, ...）
└── util/            # 工具类（DBUtil）
src/main/resources/
└── images/          # 界面图片资源
```

## 数据库表

- `supplier` — 供应商信息（id, name, contact, phone, address）
- `bill` — 账单信息（id, product_name, quantity, amount, supplier_id, is_paid, create_time）
- `USER` — 用户信息（id, username, password, sex, age, role, create_time）

## 运行方式

1. 确保已安装 JDK 17+ 和 Maven
2. 配置 `DBUtil.java` 中的数据库连接信息（URL、用户名、密码）
3. 在 Oracle 中创建上述三张表
4. 运行：

```bash
mvn clean javafx:run
```

## 数据库配置

修改 `src/main/java/com/example/supermarketbillsystem/util/DBUtil.java`：

```java
private static final String URL = "jdbc:oracle:thin:@127.0.0.1:1521:ORCL";
private static final String USER = "scott";
private static final String PASSWORD = "your_password";
```
