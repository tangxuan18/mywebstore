package service.impl;

import bean.*;
import dao.*;
import dao.impl.CartDaoImpl;
import dao.impl.OrderDaoImpl;
import dao.impl.ProductDaoImpl;
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


    @Override
    public int deleteSelectedCartItems(int[] selectedCartItemIdArray) {
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
    public List<Order> placeOrder(Order order, int[] selectedCartItemIdArray) throws Exception {
        Connection connection = null;
        Savepoint savePointWholeProcess = null;
        try {
            // 从threadLocal取出Connection，引入事务
            connection = DruidUtils.getConnection(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            // 设置mysql不自动提交
            connection.setAutoCommit(false);
            // 保存回滚点
            savePointWholeProcess = connection.setSavepoint();
            // 2 生成一个order
            int insertOrderResult = orderDao.insertOrder(order);
            if (insertOrderResult == 0) {
                System.out.println("添加Order异常");
                throw new Exception("添加Order异常");
            }
            // 3 把cartItems拷贝到orderItems
            int copyResult = this.copyCartItemsToOrderItems(selectedCartItemIdArray, order);
            if (copyResult == 0) {
                System.out.println("添加OrderItem异常");
                throw new Exception("添加OrderItem异常");
            }
            // 4 更改totalStockCount
            int updateResult = this.minusTotalStock(selectedCartItemIdArray);
            if (updateResult == 0) {
                System.out.println("更新库存异常");
                throw new Exception("更新库存异常");
            }
            // 5 删除勾选的selectedCartItems
            int deleteResult = this.deleteSelectedCartItems(selectedCartItemIdArray);
            if (deleteResult == 0) {
                System.out.println("清空购物车异常");
                throw new Exception("清空购物车异常");
            }
            // 如果无异常，正常commit()
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                connection.rollback(savePointWholeProcess);
                System.out.println("JDBC Transaction rolled back to savepoint successfully");
                // 如果出现异常，回滚后commit()
                connection.commit();
                throw new Exception("事务回滚，页面提示用户");
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

    @Override
    public int copyCartItemsToOrderItems(int[] selectedCartItemIdArray, Order order) {
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
            if (result == 0) {
                return result;
            }
        }
        return 1;
    }


    private int minusTotalStock(int[] selectedCartItemIdArray) {
        Connection connection = null;
        try {
            // 单线程Connection，引入事务
            connection = DruidUtils.getConnection(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            // 设置mysql不自动提交
            connection.setAutoCommit(false);
            List<CartItem> selectedItems = listSelectedCartItems(selectedCartItemIdArray);
            for (CartItem cartItem : selectedItems) {
                // 调用productDao
                int updateResult = productDao.updateTotalStockForPlaceOrder(cartItem);
                if (updateResult == 0) {
                    return updateResult;
                }else {
                    connection.commit();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
