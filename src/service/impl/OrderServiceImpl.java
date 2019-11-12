package service.impl;

import bean.*;
import dao.*;
import dao.impl.CartDaoImpl;
import dao.impl.OrderDaoImpl;
import dao.impl.ProductDaoImpl;
import service.OrderService;

import java.util.List;

public class OrderServiceImpl implements OrderService {

    private OrderDao orderDao = new OrderDaoImpl();

    private CartDao cartDao = new CartDaoImpl();

    private ProductDao productDao = new ProductDaoImpl();


    public int deleteSelectedCartItems(int[] selectedCartItemIdArray) {
        StringBuffer paramSb = new StringBuffer("(");
        for (int id : selectedCartItemIdArray) {
            paramSb.append(id).append(",");
        }
        String substring = paramSb.substring(0, paramSb.length() - 1);
        String params = substring + ")";
        String sql = "delete from t_cartItem where cartItemId in " + params;
        int result = orderDao.deleteSelectedCartItems(sql);
        return result;
    }

    @Override
    public List<Order> placeOrder(Order order, int[] selectedCartItemIdArray, int uid) {
        List<Order> orderList = null;
        /*（二）生成一个order*/
        int insertOrderResult = orderDao.insertOrder(order);
        if(insertOrderResult == 0){
            return null;
        }
        //（二、三）的顺序不能颠倒，因为orderItem依赖order，必须先存在orderNum
        /*（三）把cartItems拷贝到orderItems*/
        int copyResult = this.copyCartItemsToOrderItems(selectedCartItemIdArray, order);
        if(copyResult == 0){
            return null;
        }
        /*（四）更改totalStockCount*/
        int updateResult = this.subTotalStock(selectedCartItemIdArray);
        if(updateResult == 0){
            return null;
        }
        /*（五）删除指定cartItems*/
        int deleteResult = this.deleteSelectedCartItems(selectedCartItemIdArray);
        if(deleteResult == 0){
            return null;
        }
        /*（六）查询orderList*/
        orderList = orderDao.listOrdersByUid(uid);
        return orderList;
    }

    @Override
    public Page listPageOrders(int currentPageNum) {
        Page<Order> page = new Page<Order>();
        page.setList(orderDao.listPageOrders(currentPageNum));
        page.setTotalRecordsNum(orderDao.countTotalOrderCount());
        page.setCurrentPageNum(currentPageNum);
        page.setTotalPageNum(((int) Math.ceil(page.getTotalRecordsNum() * 1.0 / Page.getPageSize())));
        page.setPrevPageNum(currentPageNum - 1);
        page.setNextPageNum(currentPageNum + 1);
        return page;
    }

    @Override
    public List<OrderItem> listOrderItems(String orderNum) {
        return orderDao.listOrderItems(orderNum);
    }

    @Override
    public List<Order> listOrdersByUid(int uid) {
        return orderDao.listOrdersByUid(uid);
    }

    @Override
    public int cancelOrder(Order order) {
        int orderId = order.getOrderId();
        int payStatus = order.getPayStatus();
        /*（一）查询orderItems*/
        List<OrderItem> orderItemList = orderDao.listCancellingOrderItems(orderId);
        if(orderItemList == null){
            return 0;
        }
        /*（二）修改totalStock*/
        int addResult = this.addTotalStock(orderItemList);
        if(addResult == 0){
            return addResult;
        }
        /*（三）修改order的payStatus*/
        int updateResult = orderDao.updatePayStatus(orderId, payStatus);
        if(updateResult == 0){
            return updateResult;
        }
        return 1;
    }

    private int addTotalStock(List<OrderItem> orderItemList) {
        String sqlAddTotalStock;
        for (OrderItem orderItem : orderItemList) {
            int orderItemId = orderItem.getOrderItemId();
            int productCount = orderItem.getProductCount();
            sqlAddTotalStock = "update t_product p inner join t_orderItem o on o.pid = p.id set totalStockCount = totalStockCount + " + productCount +  " where orderItemId = " + orderItemId;
            int result = productDao.updateTotalStock(sqlAddTotalStock);
            if(result == 0){
                return result;
            }
        }
        return 1;
    }

    @Override
    public int confirmStock(int[] selectedCartItemIdArray) {
        List<CartItem> selectedCartItemList = listSelectedCartItems(selectedCartItemIdArray);
        for (CartItem cartItem : selectedCartItemList) {
            int productCount = cartItem.getProductCount();
            int pid = cartItem.getProduct().getId();
            int totalStockCount = productDao.getProductByPid(pid).getTotalStockCount();
            if (totalStockCount < productCount) {
                return 0;
            }
        }
        return 1;
    }

    private List<CartItem> listSelectedCartItems(int[] selectedCartItemIdArray) {
        StringBuffer paramSb = new StringBuffer("(");
        for (int id : selectedCartItemIdArray) {
            paramSb.append(id).append(",");
        }
        String substring = paramSb.substring(0, paramSb.length() - 1);
        String params = substring + ")";
        String sqlCartItem = "select c.*, p.* from t_cartItem c inner join t_product p on c.pid = p.id where cartItemId in " + params;
        return cartDao.listSelectedCartItems(sqlCartItem);
    }

    public int copyCartItemsToOrderItems(int[] selectedCartItemIdArray, Order order) {
        List<CartItem> selectedItems = listSelectedCartItems(selectedCartItemIdArray);
        /*保存到orderItem*/
        OrderItem orderItem = new OrderItem();
        for (CartItem selectedItem : selectedItems) {
            orderItem.setUid(selectedItem.getUid());
            orderItem.setPid(selectedItem.getProduct().getId());
            orderItem.setProductCount(selectedItem.getProductCount());
            orderItem.setOrderNum(order.getOrderNum());
            int result = orderDao.insertSelectedItems(orderItem); // 调用orderDao
            if (result == 0) {
                return result;
            }
        }
        return 1;
    }

    @Override
    public int insertOrder(Order order) {
        return orderDao.insertOrder(order);
    }

    /**
     * 下单后更改库存，引入事务
     *
     * @param selectedCartItemIdArray
     * @return
     */
    public int subTotalStock(int[] selectedCartItemIdArray) {
        List<CartItem> selectedItems = listSelectedCartItems(selectedCartItemIdArray);
/*        Connection connection = null;
        Savepoint savepoint = null;
        try {
            connection = DruidUtils.getConnection(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.setAutoCommit(false);
            String sqlStock;
            for (CartItem cartItem : selectedItems) {
                int productCount = cartItem.getProductCount();
                int cartItemId = cartItem.getCartItemId();
                sqlStock = "update t_product p inner join t_cartItem c on c.pid = p.id set p.totalStockCount = p.totalStockCount - " + productCount + " where cartItemId = " + cartItemId;
                int updateResult = productDao.subTotalStock(sqlStock); // 调用productDao
                if (updateResult == 0) {
                    return updateResult;
                }
                savepoint = connection.setSavepoint();
            }
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (savepoint == null) {
                    connection.rollback();
                    return 0;
                } else {
                    connection.rollback(savepoint);
                    connection.commit();
                    return 1;
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }*/
        String sqlStock;
        for (CartItem cartItem : selectedItems) {
            int productCount = cartItem.getProductCount();
            int cartItemId = cartItem.getCartItemId();
            sqlStock = "update t_product p inner join t_cartItem c on c.pid = p.id set p.totalStockCount = p.totalStockCount - " + productCount + " where cartItemId = " + cartItemId;
            int updateResult = productDao.updateTotalStock(sqlStock); // 调用productDao
            if (updateResult == 0) {
                return updateResult;
            }
        }
        return 1;
    }

}
