package dao;

import bean.Category;
import bean.Page;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import utils.DruidUtils;

import java.sql.SQLException;
import java.util.List;

public class CategoryDaoImpl implements CategoryDao {

    QueryRunner runner = new QueryRunner(DruidUtils.getDataSource());

    @Override
    public int insertCategory(String cname) {
        try {
            runner.update("insert into t_category values(null, ?)", cname);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    @Override
    public List<Category> findAllCategory() {
        List<Category> list = null;
        try {
            list = runner.query("select * from t_category order by id asc", new BeanListHandler<>(Category.class));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int updateCategory(String cid, String cname) {
        try {
            runner.update("update t_category set cname = ? where id = ?", cname, cid);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    @Override
    public int deleteCategory(int cid) {
        try {
            runner.update("delete from t_category where id = ?", cid);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    /**
     * 获取一页中的分类列表
     * @param currentPageNum
     * @return
     */
    @Override
    public List<Category> findPageCategories(int currentPageNum) {
        List<Category> categoryList = null;
        try {
            categoryList = runner.query("select * from t_category order by id asc limit ? offset ?", new BeanListHandler<>(Category.class),
                    Page.getPageSize(), (currentPageNum - 1) * Page.getPageSize()); // 偏移量
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryList;
    }

    @Override
    public int findTotalCount() {
        Long totalCount = 0L; //Long型后加大L
        try {
            totalCount = (Long) runner.query("select count(id) from t_category", new ScalarHandler()); // 返回Long类型
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalCount.intValue();
    }
}
