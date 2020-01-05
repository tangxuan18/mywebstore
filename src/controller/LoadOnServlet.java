package controller;

import bean.Category;
import service.CategoryService;
import service.impl.CategoryServiceImpl;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 加载分类
 */
@WebServlet(value = "/loadOnServlet", loadOnStartup = 0)
public class LoadOnServlet extends HttpServlet {

    private CategoryService categoryService = new CategoryServiceImpl();

    @Override
    public void init(ServletConfig config) throws ServletException {
//        System.out.println("categories = " + config.getServletContext().getAttribute("categories"));
        List<Category> categoryList = categoryService.findAllCategory();
        config.getServletContext().setAttribute("categories", categoryList);
//        System.out.println("categories = " + config.getServletContext().getAttribute("categories"));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}
