package dao;

import bean.Category;

import java.util.List;

public interface CategoryDao {
    int insertCategory(String cname);

    List<Category> findAllCategory();

    int updateCategory(String cid, String cname);

    int deleteCategory(int cid);

    List<Category> findPageCategories(int currentPageNum);

    int findTotalCount();
}
