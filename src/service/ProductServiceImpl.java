package service;

import bean.Page;
import bean.Product;
import dao.ProductDao;
import dao.ProductDaoImpl;
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
        Page page = new Page(); // 返回一个Page，且包含复杂查询
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
        StringBuffer sb = new StringBuffer("SELECT p.*, c.cname FROM t_product p INNER JOIN t_category c ON p.cid = c.id WHERE 1 = 1 ");
        StringBuilder sbSearchCount = new StringBuilder("SELECT count(p.id) FROM t_product p INNER JOIN t_category c ON p.cid = c.id WHERE 1 = 1 ");
        List sqlParaList = new ArrayList<>(); // 放？的参数
        List notPageSqlParaList = new ArrayList<>(); // 放？的参数
        if(!StringUtils.isEmpty(product.getProductNum())){
            sb.append("AND p.productNum = ? ");
            sbSearchCount.append("AND p.productNum = ? ");
            sqlParaList.add(product.getProductNum());
            notPageSqlParaList.add(product.getProductNum());
        }
        if(product.getCid() != 0){
            sb.append("AND p.cid = ? ");
            sbSearchCount.append("AND p.cid = ? ");
            sqlParaList.add(product.getCid());
            notPageSqlParaList.add(product.getCid());
        }
        if(!StringUtils.isEmpty(product.getProductName())){
            sb.append("AND p.productName LIKE ? ");
            sbSearchCount.append("AND p.productName LIKE ? ");
            sqlParaList.add("%" + product.getProductName() + "%"); // 通配符
            notPageSqlParaList.add("%" + product.getProductName() + "%"); // 通配符
        }
        if (!StringUtils.isEmpty(product.getMinWebStorePrice())) {
            sb.append("AND p.webStorePrice >= ? ");
            sbSearchCount.append("AND p.webStorePrice >= ? ");
            sqlParaList.add(product.getMinWebStorePrice());
            notPageSqlParaList.add(product.getMinWebStorePrice());
        }
        if (!StringUtils.isEmpty(product.getMaxWebStorePrice())) {
            sb.append("AND p.webStorePrice <= ? ");
            sbSearchCount.append("AND p.webStorePrice <= ? ");
            sqlParaList.add(product.getMaxWebStorePrice());
            notPageSqlParaList.add(product.getMaxWebStorePrice());
        }
        String countSearchSql = sbSearchCount.toString();
        System.out.println("countSearchSql = " + countSearchSql);
        System.out.print("notPageSqlParaList = ");
        for (Object o : sqlParaList) {
            System.out.print( o + " ");
        }
        System.out.println();
        sb.append("order by p.id desc limit ? offset ? "); //分页
        // 如果要分页，要多加2个参数
        sqlParaList.add(Page.getPageSize());
        sqlParaList.add((currentPageNum - 1) * Page.getPageSize());
        String searchSql = sb.toString();
//        System.out.println("searchSql = " + searchSql);
//        System.out.print("sqlParaList = ");
//        for (Object o : sqlParaList) {
//            System.out.print( o + " ");
//        }
//        System.out.println();

        List<Product> pageProductList = productDao.listPageSearchedProducts(currentPageNum, searchSql, sqlParaList); // 调用Dao层
        page.setList(pageProductList);

        //TO DO
        page.setTotalRecordsNum(productDao.countSearchedProductCount(countSearchSql, notPageSqlParaList)); // 调用Dao层
        page.setCurrentPageNum(currentPageNum);
        page.setTotalPageNum(((int) Math.ceil(page.getTotalRecordsNum() * 1.0 / Page.getPageSize()))); // 总页数 = 总记录数/每页记录数
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
