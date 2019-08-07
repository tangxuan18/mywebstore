package service;

import bean.Page;
import bean.Product;

import java.util.List;

public interface ProductService {
    int saveProduct(Product product);

    Page listPageProducts(int currentPageNum);

    int deleteOneProduct(int pid);

    Product getProduct(int pid);

    int updateProduct(Product product);

    Page listSearchedPageProducts(Product product, int currentPageNum);

    List<Product> listTopProducts();

    List<Product> listHotProducts();

    List<Product> listProductsByCid(int cid);

    Product getProductByPid(int pid);

    List<Product> listProductsByKeyword(String keyword);

}
