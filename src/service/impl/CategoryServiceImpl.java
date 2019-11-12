package service.impl;

import bean.Category;
import bean.Page;
import dao.CategoryDao;
import dao.impl.CategoryDaoImpl;
import service.CategoryService;

import java.util.List;

public class CategoryServiceImpl implements CategoryService {

    CategoryDao categoryDao = new CategoryDaoImpl();

    @Override
    public int addCategory(String cname) {
        return categoryDao.insertCategory(cname);
    }

    @Override
    public List<Category> findAllCategory() {
        return categoryDao.findAllCategory();
    }

    @Override
    public int updateCategory(String cid, String cname) {
        return categoryDao.updateCategory(cid, cname);
    }

    @Override
    public int deleteCategory(int cid) {
        return categoryDao.deleteCategory(cid);
    }

    /**
     * 封装包含复杂属性的Page
     * @param currentPageNum
     * @return 一个Page
     */
    @Override
    public Page findPageCategories(int currentPageNum) {
        // 返回一个Page，且包含复杂查询
        Page page = new Page();
        page.setList(categoryDao.findPageCategories(currentPageNum));
        page.setTotalRecordsNum(categoryDao.findTotalCount());
        page.setCurrentPageNum(currentPageNum);
        page.setTotalPageNum(((int) Math.ceil(page.getTotalRecordsNum() * 1.0 / Page.getPageSize())));
        page.setPrevPageNum(currentPageNum - 1);
        page.setNextPageNum(currentPageNum + 1);
        return page;
    }
}
