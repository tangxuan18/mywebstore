package service;

import bean.Category;
import bean.Page;

import java.util.List;

public interface CategoryService {
    int addCategory(String cname);

    List<Category> findAllCategory();

    int updateCategory(String cid, String cname);

    int deleteCategory(int cid);

    Page findPageCategories(int currentPageNum);
}
