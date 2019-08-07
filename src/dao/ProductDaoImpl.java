package dao;

import bean.Page;
import bean.Product;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import utils.DruidUtils;

import java.sql.SQLException;
import java.util.List;

public class ProductDaoImpl implements ProductDao {

    private QueryRunner runner = new QueryRunner(DruidUtils.getDataSource());

    @Override
    public int insertProduct(Product product) {
        try {
            runner.update("insert into t_product values(null, ?, ?, ?, ?, ?, ?, ?, ?)",
                    product.getCid(), product.getProductNum(), product.getTotalStockCount(), product.getProductName(),
                    product.getWebStorePrice(), product.getMarketPrice(), product.getImgUrl(), product.getDescription());
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    @Override
    public List<Product> listPageProducts(int currentPageNum) {
        List<Product> productList = null;
        try {
            productList = runner.query("select p.*, c.cname from t_product p inner join t_category c on p.cid = c.id order by id desc limit ? offset ?",
                    new BeanListHandler<>(Product.class), Page.getPageSize(), (currentPageNum - 1) * Page.getPageSize()); // 偏移量
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    @Override
    public List<Product> listPageSearchedProducts(int currentPageNum, String searchSql, List sqlParaList) {
        List<Product> productList = null;
        try {
            productList = runner.query(searchSql, new BeanListHandler<>(Product.class),
                    sqlParaList.toArray());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    @Override
    public int countSearchedProductCount(String countSearchSql, List notPageSqlParaList) {
        Long count = 0L;
        try {
            count = (Long) runner.query(countSearchSql, new ScalarHandler(), notPageSqlParaList.toArray());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count.intValue();
    }

    @Override
    public List<Product> listTopProducts() {
        List<Product> productList = null;
        try {
            productList = runner.query("select * from t_product where webStorePrice > 2000", new BeanListHandler<>(Product.class));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    @Override
    public List<Product> listHotProducts() {
        List<Product> productList = null;
        try {
            productList = runner.query("select * from t_product where webStorePrice between 1000 and 2000", new BeanListHandler<>(Product.class));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    @Override
    public List<Product> listProductsByCid(int cid) {
        List<Product> productList = null;
        try {
            productList = runner.query("select * from t_product where cid = ?", new BeanListHandler<>(Product.class), cid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    @Override
    public Product getProductByPid(int pid) {
        Product product = null;
        try {
            product = runner.query("select * from t_product where id = ?", new BeanHandler<>(Product.class), pid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

    @Override
    public List<Product> listProductsByKeyword(String keyword) {
        List<Product> productList = null;
        try {
            productList = runner.query("select p.*, c.cname from t_product p inner join t_category c on p.cid = c.id where c.cname like ? or p.productName like ?", new BeanListHandler<>(Product.class),
                    "%"+keyword+"%", "%"+keyword+"%");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    @Override
    public int updateTotalStock(String sql) {
        try {
            runner.update(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    @Override
    public int countTotalProductCount() {
        Long totalCount = 0L; //Long型后加大L
        try {
            totalCount = (Long) runner.query("select count(id) from t_product", new ScalarHandler()); // 返回Long类型
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalCount.intValue();
    }

    @Override
    public int deleteOneProduct(int pid) {
        try {
            runner.update("delete from t_product where id = ?", pid);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    @Override
    public Product getProduct(int pid) {
        Product product = null;
        try {
            product = runner.query("select * from t_product where id = ?",
                    new BeanHandler<>(Product.class), pid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

    @Override
    public int updateProduct(Product product) {
        try {
            runner.update("update t_product set cid = ?, productNum = ?, totalStockCount = ?, productName = ?, webStorePrice = ?, marketPrice = ?, imgUrl = ?, description = ? where id = ?",
                    product.getCid(), product.getProductNum(), product.getTotalStockCount(), product.getProductName(), product.getWebStorePrice(),
                    product.getMarketPrice(), product.getImgUrl(), product.getDescription(), product.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }


}
