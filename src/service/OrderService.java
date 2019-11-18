package service;

import bean.Order;
import bean.OrderItem;
import bean.Page;

import java.util.List;

public interface OrderService {

    Page listPageOrders(int currentPageNum);

    List<OrderItem> listOrderItems(String orderNum);

    List<Order> listOrdersByUid(int uid);

    int cancelOrder(Order order);

    String confirmStock(int[] selectedCartItemIdArray);

    int copyCartItemsToOrderItems(int[] selectedCartItemIdArray, Order order);

    int deleteSelectedCartItems(int[] selectedCartItemIdArray);

    List<Order> placeOrder(Order order, int[] selectedCartItemIdArray) throws Exception;
}
