package dao;

import bean.Order;
import bean.OrderItem;
import bean.Page;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import utils.DruidUtils;

import java.sql.SQLException;
import java.util.List;

public class OrderDaoImpl implements OrderDao {

    private QueryRunner runner = new QueryRunner(DruidUtils.getDataSource());

    @Override
    public int insertOrder(Order order) {
        try {
            runner.update("insert into t_order values(null, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    order.getOrderNum(), order.getTotalPrice(), order.getReceiverName(), order.getReceiverMobile(),
                    order.getReceiverAddress(), order.getPayStatus(), order.getOrderTime(),
                    order.getUid(), order.getUsername());
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    @Override
    public int insertSelectedItems(OrderItem orderItem) {
        try {
            runner.update("insert into t_orderItem values (null, ?, ?, ?, ?)",
                    orderItem.getUid(), orderItem.getPid(), orderItem.getProductCount(), orderItem.getOrderNum());
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    @Override
    public List<Order> listOrdersByUid(int uid) {
        List<Order> orderList = null;
        try {
            orderList = runner.query("select * from t_order where uid = ? order by orderId desc", new BeanListHandler<>(Order.class), uid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderList;
    }

    @Override
    public List<Order> listPageOrders(int currentPageNum) {
        List<Order> orderList = null;
        try {
            orderList = runner.query("select * from t_order order by orderId desc limit ? offset ?",
                    new BeanListHandler<>(Order.class), Page.getPageSize(), (currentPageNum - 1) * Page.getPageSize()); // 偏移量
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderList;
    }

    @Override
    public int countTotalOrderCount() {
        Long totalCount = 0L; //Long型后加大L
        try {
            totalCount = (Long) runner.query("select count(orderId) from t_order", new ScalarHandler()); // 返回Long类型
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalCount.intValue();
    }

    @Override
    public List<OrderItem> listOrderItems(String orderNum) {
        List<OrderItem> orderItemList = null;
        try {
            orderItemList = runner.query("select * from t_orderItem where orderNum = ?", new BeanListHandler<>(OrderItem.class), orderNum);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItemList;
    }

    @Override
    public int deleteSelectedCartItems(String sql) {
        try {
            runner.update(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    @Override
    public int updatePayStatus(int orderId, int payStatus) {
        try {
            runner.update("update t_order set payStatus = ? where orderId = ?", payStatus, orderId);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    @Override
    public List<OrderItem> listCancellingOrderItems(int orderId) {
        List<OrderItem> orderItemList = null;
        try {
            orderItemList = runner.query("select * from t_orderItem oi inner join t_order o on oi.orderNum = o.orderNum where o.orderId = ?",
                    new BeanListHandler<>(OrderItem.class), orderId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItemList;
    }
}
