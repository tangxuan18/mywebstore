package service.impl;

import bean.*;
import dao.*;
import dao.impl.CartDaoImpl;
import dao.impl.OrderDaoImpl;
import dao.impl.ProductDaoImpl;
import exception.PlaceOrderException;
import service.OrderService;
import utils.DruidUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;

/**
 * @author GFS
 */
public class OrderServiceImpl implements OrderService {

    private OrderDao orderDao = new OrderDaoImpl();

    private CartDao cartDao = new CartDaoImpl();

    private ProductDao productDao = new ProductDaoImpl();


    private int deleteSelectedCartItems(int[] selectedCartItemIdArray) throws SQLException {
        StringBuffer paramSb = new StringBuffer("(");
        for (int id : selectedCartItemIdArray) {
            paramSb.append(id).append(",");
        }
        String substring = paramSb.substring(0, paramSb.length() - 1);
        String paramsStr = substring + ")";
        String sql = "delete from t_cartItem where cartItemId in " + paramsStr;
        return orderDao.deleteSelectedCartItems(sql);
    }

    /**
     * 下单操作，分成几个子操作，放在service层
     *
     * @param order
     * @param selectedCartItemIdArray
     * @return
     */
    @Override
    public List<Order> placeOrder(Order order, int[] selectedCartItemIdArray) throws PlaceOrderException {

        Connection connection = null;
        Savepoint savePointWholeProcess = null;
        try {
            // 从threadLocal取出Connection，引入事务
            connection = DruidUtils.getConnection(true);
            // 设置mysql不自动提交
            connection.setAutoCommit(false);
            // 保存回滚点
            savePointWholeProcess = connection.setSavepoint();

            // 2 生成一个order
            int insertOrderResult = orderDao.insertOrder(order);
            // 3 把cartItems拷贝到orderItems
            int copyResult = this.copyCartItemsToOrderItems(selectedCartItemIdArray, order);
            // 4 更改totalStockCount
            int updateResult = this.minusTotalStock(selectedCartItemIdArray);
            // 5 删除勾选的selectedCartItems
            int deleteResult = this.deleteSelectedCartItems(selectedCartItemIdArray);
            // 如果无异常，正常commit()
            connection.commit();
            // 如果任何一步捕获异常
        } catch (Exception e) {
            e.printStackTrace();
            try {
                // 回滚没下单的时候
                connection.rollback(savePointWholeProcess);
                System.out.println("JDBC Transaction rolled back to savepoint successfully");
                // 回滚后commit
                connection.commit();
                // 抛出下单异常
                throw new PlaceOrderException("事务rollback并且commit，页面提示用户");
                // 如果回滚失败，自己处理
            } catch (SQLException e1) {
                e1.printStackTrace();
                System.out.println("JDBC Transaction failed to roll back to savepoint");
            }
        }
        // 6 查询orderList
        return orderDao.listOrdersByUid(order.getUid());

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
        if (orderItemList == null) {
            return 0;
        }
        /*（二）修改totalStock*/
        int addResult = this.addTotalStock(orderItemList);
        if (addResult == 0) {
            return addResult;
        }
        /*（三）修改order的payStatus*/
        int updateResult = orderDao.updatePayStatus(orderId, payStatus);
        if (updateResult == 0) {
            return updateResult;
        }
        return 1;
    }

    private int addTotalStock(List<OrderItem> orderItemList) {
        for (OrderItem orderItem : orderItemList) {
            int result = productDao.updateTotalStockForCancelOrder(orderItem);
            if (result == 0) {
                return result;
            }
        }
        return 1;
    }

    @Override
    public String confirmStock(int[] selectedCartItemIdArray) {
        List<CartItem> selectedCartItemList = listSelectedCartItems(selectedCartItemIdArray);
        for (CartItem cartItem : selectedCartItemList) {
            // 根据cartItem查出需要的pcount
            int productCount = cartItem.getProductCount();
            // 根据pid查出实际库存
            int pid = cartItem.getProduct().getId();
            int totalStockCount = productDao.getProductByPid(pid).getTotalStockCount();
            // 如果库存<需求，返回库存不足的
            if (totalStockCount < productCount) {
                return cartItem.getProduct().getProductName();
            }
        }
        return "enough";
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


    private int copyCartItemsToOrderItems(int[] selectedCartItemIdArray, Order order) throws SQLException {
        // 根据selectedCartItemIdArray查询selectedCartItems
        List<CartItem> selectedCartItems = listSelectedCartItems(selectedCartItemIdArray);
        /*抽取部分信息保存到orderItem*/
        OrderItem orderItem = new OrderItem();
        // 遍历cartItems，把信息封装到orderItem
        for (CartItem selectedCartItem : selectedCartItems) {
            orderItem.setUid(selectedCartItem.getUid());
            orderItem.setPid(selectedCartItem.getProduct().getId());
            orderItem.setProductCount(selectedCartItem.getProductCount());
            orderItem.setOrderNum(order.getOrderNum());
            // 调用dao层
            int result = orderDao.insertOrderItem(orderItem);
/*            if (result == 0) {
                return result;
            }*/
        }
        return 1;
    }


    private int minusTotalStock(int[] selectedCartItemIdArray) throws SQLException {
        List<CartItem> selectedItems = listSelectedCartItems(selectedCartItemIdArray);
//        int i = 0;
        for (CartItem cartItem : selectedItems) {
            // 调用productDao
            int updateResult = productDao.updateTotalStockForPlaceOrder(cartItem);
/*            i++;
            if (i == 3) {
                i = 1 / 0;
            }*/
        }
        return 1;
    }
}
