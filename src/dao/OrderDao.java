package dao;

import bean.Order;
import bean.OrderItem;

import java.sql.SQLException;
import java.util.List;

public interface OrderDao {
    int insertOrder(Order order) throws SQLException;

    int insertOrderItem(OrderItem orderItem) throws SQLException;

    List<Order> listOrdersByUid(int uid);

    List<Order> listPageOrders(int currentPageNum);

    int countTotalOrderCount();

    List<OrderItem> listOrderItems(String orderNum);

    int deleteSelectedCartItems(String sql) throws SQLException;

    int updatePayStatus(int orderId, int payStatus);

    List<OrderItem> listCancellingOrderItems(int orderId);
}
