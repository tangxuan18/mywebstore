package dao;

import bean.CartItem;
import bean.OrderItem;
import bean.Product;

import java.util.List;

public interface ProductDao {
    int insertProduct(Product product);

    List<Product> listPageProducts(int currentPageNum);

    int countTotalProductCount();

    int deleteOneProduct(int pid);

    Product getProduct(int pid);

    int updateProduct(Product product);

    List<Product> listPageSearchedProducts(int currentPageNum, String searchSql, List sqlParaList);

    int countSearchedProductCount(String notPageSearchSql, List notPageSqlParaList);

    List<Product> listTopProducts();

    List<Product> listHotProducts();

    List<Product> listProductsByCid(int cid);

    Product getProductByPid(int pid);

    List<Product> listProductsByKeyword(String keyword);

    int updateTotalStockForPlaceOrder(CartItem cartItem);

    int updateTotalStockForCancelOrder(OrderItem orderItem);
}
