package dao.impl;

import bean.Cart;
import bean.CartItem;
import bean.Product;
import dao.CartDao;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import utils.DruidUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author GFS
 */
public class CartDaoImpl implements CartDao {

    private QueryRunner runner = new QueryRunner(DruidUtils.getDataSource());

    @Override
    public int countCartItemCount(int uid, int pid) {
        Long count = 0L;
        try {
            count = (Long) runner.query("select count(cartItemId) from t_cartItem where uid = ? and pid = ?",
                    new ScalarHandler(), uid, pid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count.intValue();
    }

    @Override
    public int insertCartItem(int uid, int pid, int productCount) {
        try {
            runner.update("insert into t_cartItem values(null, ?, ?, ?)",
                    uid, pid, productCount);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    @Override
    public int plusOneProductCount(int cartItemId) {
        try {
            runner.update("update t_cartItem set productCount = productCount + 1 where cartItemId = ?", cartItemId);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    @Override
    public int minusOneProductCount(int cartItemId) {
        try {
            runner.update("update t_cartItem set productCount = productCount - 1 where cartItemId = ?", cartItemId);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    @Override
    public int getProductCount(int cartItemId) {
        int count = 0;
        try {
            count = (int)runner.query("select productCount from t_cartItem where cartItemId = ?",
                    new ScalarHandler(), cartItemId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public int deleteCartItem(int cartItemId) {
        try {
            runner.update("delete from t_cartItem where cartItemId = ?", cartItemId);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    @Override
    public List<CartItem> listSelectedCartItems(String sql) {
        List<CartItem> cartItemList = null;
        try {
            cartItemList = runner.query(sql, new ResultSetHandler<List<CartItem>>() {
                @Override
                public List<CartItem> handle(ResultSet resultSet) throws SQLException {
                    List<CartItem> cartItems = new ArrayList<>();
                    while (resultSet.next()) {
                        CartItem cartItem = new CartItem();
                        cartItem.setCartItemId(resultSet.getInt("cartItemId"));
                        cartItem.setUid(resultSet.getInt("uid"));

                        Product product = new Product();
                        product.setId(resultSet.getInt("pid"));
                        product.setProductName(resultSet.getString("productName"));
                        product.setImgUrl(resultSet.getString("imgUrl"));
                        product.setWebStorePrice(resultSet.getDouble("webStorePrice"));
                        cartItem.setProduct(product);

                        cartItem.setProductCount(resultSet.getInt("productCount"));
                        cartItems.add(cartItem);
                    }
                    return cartItems;
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        System.out.println("selectedCartItemList = " + cartItemList);
        return cartItemList;
    }

    @Override
    public int updateCartItem(int uid, int pid, int productCount) {
        try {
            runner.update("update t_cartItem set productCount = productCount + ? where uid = ? and pid = ?", productCount, uid, pid);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    /**
     * 操作ResultSetHandler,因为要封装List<CartItem>对象，里面有Product对象，对象套对象
     * @param uid
     * @return
     */
    @Override
    public Cart getCart(int uid) {
        Cart cart = new Cart();
        List<CartItem> cartItems = null;
        try {
            // TO EX 多表联查并不需要外键
            cartItems = runner.query("select c.*, p.* from t_cartItem c inner join t_product p on c.pid = p.id where uid = ?", new ResultSetHandler<List<CartItem>>() {
                /**
                 *
                 * @param resultSet
                 * @return
                 * @throws SQLException
                 */
                @Override
                public List<CartItem> handle(ResultSet resultSet) throws SQLException {
                    List<CartItem> cartItems = new ArrayList<>();
                    while (resultSet.next()) {
                        CartItem cartItem = new CartItem();
                        cartItem.setCartItemId(resultSet.getInt("cartItemId"));
                        cartItem.setUid(resultSet.getInt("uid"));

                        Product product = new Product();
                        product.setId(resultSet.getInt("pid"));
                        product.setProductName(resultSet.getString("productName"));
                        product.setImgUrl(resultSet.getString("imgUrl"));
                        product.setWebStorePrice(resultSet.getDouble("webStorePrice"));
                        cartItem.setProduct(product);

                        cartItem.setProductCount(resultSet.getInt("productCount"));
                        cartItems.add(cartItem);
                    }
//                    System.out.println("cartItems = " + cartItems);
                    return cartItems;
                }
            }, uid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        cart.setCartItems(cartItems);
//        System.out.println("cart = " + cart);
        return cart;
    }


}
