package service;

import bean.Order;
import bean.OrderItem;
import bean.Page;

import java.sql.SQLException;
import java.util.List;

public interface OrderService {

    Page listPageOrders(int currentPageNum);

    List<OrderItem> listOrderItems(String orderNum) throws SQLException;

    List<Order> listOrdersByUid(int uid);

    int cancelOrder(Order order);

    String confirmStock(int[] selectedCartItemIdArray);

    List<Order> placeOrder(Order order, int[] selectedCartItemIdArray) throws Exception;

    int updatePayStatus(Order order);
}
