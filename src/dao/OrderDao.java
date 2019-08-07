package dao;

import bean.Order;
import bean.OrderItem;

import java.util.List;

public interface OrderDao {
    int insertOrder(Order order);

    int insertSelectedItems(OrderItem orderItem);

    List<Order> listOrdersByUid(int uid);

    List<Order> listPageOrders(int currentPageNum);

    int countTotalOrderCount();

    List<OrderItem> listOrderItems(String orderNum);

    int deleteSelectedCartItems(String sql);

    int updatePayStatus(int orderId, int payStatus);

    List<OrderItem> listCancellingOrderItems(int orderId);
}
