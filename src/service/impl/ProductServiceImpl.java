package service.impl;

import bean.Page;
import bean.Product;
import dao.ProductDao;
import dao.impl.ProductDaoImpl;
import service.ProductService;
import utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductServiceImpl implements ProductService {

    ProductDao productDao = new ProductDaoImpl();

    @Override
    public int saveProduct(Product product) {
        return productDao.insertProduct(product);
    }

    @Override
    public Page listPageProducts(int currentPageNum) {
        Page page = new Page();
        page.setList(productDao.listPageProducts(currentPageNum));
        page.setTotalRecordsNum(productDao.countTotalProductCount());
        page.setCurrentPageNum(currentPageNum);
        page.setTotalPageNum(((int) Math.ceil(page.getTotalRecordsNum() * 1.0 / Page.getPageSize())));
        page.setPrevPageNum(currentPageNum - 1);
        page.setNextPageNum(currentPageNum + 1);
        return page;
    }

    @Override
    public int deleteOneProduct(int pid) {
        return productDao.deleteOneProduct(pid);
    }

    @Override
    public Product getProduct(int pid) {
        return productDao.getProduct(pid);
    }

    @Override
    public int updateProduct(Product product) {
        return productDao.updateProduct(product);
    }

    @Override
    public Page listSearchedPageProducts(Product product, int currentPageNum) {
        Page page = new Page();
        StringBuffer sbSearchPage = new StringBuffer("SELECT p.*, c.cname FROM t_product p INNER JOIN t_category c ON p.cid = c.id WHERE 1 = 1 ");
        StringBuilder sbSearchCount = new StringBuilder("SELECT count(p.id) FROM t_product p INNER JOIN t_category c ON p.cid = c.id WHERE 1 = 1 ");
        List pageSqlParaList = new ArrayList<>(); // 放？的参数
        List countSqlParaList = new ArrayList<>(); // 放？的参数
        if (!StringUtils.isEmpty(product.getProductNum())) {
            sbSearchPage.append("AND p.productNum = ? ");
            sbSearchCount.append("AND p.productNum = ? ");
            pageSqlParaList.add(product.getProductNum());
            countSqlParaList.add(product.getProductNum());
        }
        if (product.getCid() != 0) {
            sbSearchPage.append("AND p.cid = ? ");
            sbSearchCount.append("AND p.cid = ? ");
            pageSqlParaList.add(product.getCid());
            countSqlParaList.add(product.getCid());
        }
        if (!StringUtils.isEmpty(product.getProductName())) {
            sbSearchPage.append("AND p.productName LIKE ? ");
            sbSearchCount.append("AND p.productName LIKE ? ");
            // 通配符
            pageSqlParaList.add("%" + product.getProductName() + "%");
            countSqlParaList.add("%" + product.getProductName() + "%");
        }
        if (!StringUtils.isEmpty(product.getMinWebStorePrice())) {
            sbSearchPage.append("AND p.webStorePrice >= ? ");
            sbSearchCount.append("AND p.webStorePrice >= ? ");
            pageSqlParaList.add(product.getMinWebStorePrice());
            countSqlParaList.add(product.getMinWebStorePrice());
        }
        if (!StringUtils.isEmpty(product.getMaxWebStorePrice())) {
            sbSearchPage.append("AND p.webStorePrice <= ? ");
            sbSearchCount.append("AND p.webStorePrice <= ? ");
            pageSqlParaList.add(product.getMaxWebStorePrice());
            countSqlParaList.add(product.getMaxWebStorePrice());
        }
        // 查询总数的sql到此为止
        String searchCountSql = sbSearchCount.toString();
//        System.out.println("countSearchSql = " + countSearchSql);
//        System.out.print("notPageSqlParaList = ");
        for (Object o : pageSqlParaList) {
            System.out.print(o + " ");
        }
        System.out.println();
        sbSearchPage.append("order by p.id desc limit ? offset ? "); //分页
        // 如果要分页，要多加2个参数
        pageSqlParaList.add(Page.getPageSize());
        pageSqlParaList.add((currentPageNum - 1) * Page.getPageSize());
        String searchPageSql = sbSearchPage.toString();
//        System.out.println("searchSql = " + searchSql);
//        System.out.print("sqlParaList = ");
//        for (Object o : sqlParaList) {
//            System.out.print( o + " ");
//        }
//        System.out.println();
        List<Product> pageProductList = productDao.listPageSearchedProducts(currentPageNum, searchPageSql, pageSqlParaList);
        page.setList(pageProductList);
        page.setTotalRecordsNum(productDao.countSearchedProductCount(searchCountSql, countSqlParaList));
        page.setTotalPageNum(((int) Math.ceil(page.getTotalRecordsNum() * 1.0 / Page.getPageSize())));
        page.setPrevPageNum(currentPageNum - 1);
        page.setNextPageNum(currentPageNum + 1);
        return page;
    }

    @Override
    public List<Product> listTopProducts() {
        return productDao.listTopProducts();
    }

    @Override
    public List<Product> listHotProducts() {
        return productDao.listHotProducts();
    }

    @Override
    public List<Product> listProductsByCid(int cid) {
        return productDao.listProductsByCid(cid);
    }

    @Override
    public Product getProductByPid(int pid) {
        return productDao.getProductByPid(pid);
    }

    @Override
    public List<Product> listProductsByKeyword(String keyword) {
        return productDao.listProductsByKeyword(keyword);
    }

}
