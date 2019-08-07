package controller.admin;

import bean.Category;
import bean.Page;
import service.CategoryService;
import service.CategoryServiceImpl;
import utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebServlet("/admin/categoryServlet")
public class CategoryServlet extends HttpServlet {

    private CategoryService categoryService = new CategoryServiceImpl();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
/*        String operation = null;
        try{
            operation = request.getParameter("operation");
        }catch (Exception e){
            e.printStackTrace();
            response.getWriter().println("<script>alert('参数格式错误！');</script>"); // 校验前端传输参数
        }*/
        String operation = request.getParameter("operation");
        if (operation == null) {
            response.getWriter().println("<script>alert('operation参数格式错误！');</script>"); // 校验前端传输参数
            return;
        }
        switch (operation) {
            case "addCategory":
                addCategory(request, response);
                break;
            case "findAllCategories":
                findAllCategory(request, response);
                break;
            case "updateCategory":
                updateCategory(request, response);
                break;
            case "deleteCategory":
                deleteCategory(request, response);
                break;
            case "deleteMulti":
                deleteMultiCategory(request, response);
                break;
            case "findPageCategories":
                findPageCategories(request, response);
                break;
        }

    }

    private void findPageCategories(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int currentPageNum = 0;
        try {
            currentPageNum = Integer.parseInt(request.getParameter("num"));
            if (currentPageNum < 1) {
                response.getWriter().println("<script>alert('已经是第一页！');</script>");
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/categoryServlet?operation=findPageCategories&num=" +
                        request.getSession().getAttribute("currentCategoryPageNum"));
                return;
            }
            int totalPageNum = (int) request.getServletContext().getAttribute("totalPageNum");
            if (currentPageNum > totalPageNum) {
                response.getWriter().println("<script>alert('已经是最后一页！');</script>");
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/categoryServlet?operation=findPageCategories&num=" +
                        request.getSession().getAttribute("currentCategoryPageNum"));
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('参数格式错误！');</script>"); // 校验前端传输参数
        }
        // 如果没有分类数据，得到null还是非null？？？ ————非null，各属性都有值
        Page currentPage = categoryService.findPageCategories(currentPageNum);
        System.out.println(currentPage);
        if (currentPage == null) {
            response.getWriter().println("<script>alert('服务器开小差了！');</script>");
            return;
        } else if (currentPage.getList() != null && currentPage.getList().size() == 0) {
            response.getWriter().println("<script>alert('该页尚无分类！');</script>");
            return;
        } else if (currentPage.getList() != null && currentPage.getList().size() != 0) {
            request.setAttribute("page", currentPage);
            // 把当前页码 存入session
            request.getSession().setAttribute("currentCategoryPageNum", currentPage.getCurrentPageNum());
            // 把当前页码 存入context
            request.getServletContext().setAttribute("totalPageNum", currentPage.getTotalPageNum());

            request.getRequestDispatcher("/admin/category/categoryList.jsp").forward(request, response);
        }
    }

    private void deleteMultiCategory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] cidStrArray = request.getParameterValues("cid");
        /*string数组转int数组*/
        int[] cidArray = new int[cidStrArray.length];
        for (int i = 0; i < cidStrArray.length; i++) {
            cidArray[i] = Integer.parseInt(cidStrArray[i]);
        }
//        System.out.println(Arrays.toString(cidArray));
        if (cidArray == null) {
            response.getWriter().println("<script>alert('请选择分类！');</script>");
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/categoryServlet?operation=findPageCategories&num=" +
                    request.getSession().getAttribute("currentCategoryPageNum"));
            return;
        }
        for (int cid : cidArray) {
            int result = categoryService.deleteCategory(cid);
            switch (result) {
                case 0:
                    response.getWriter().println("<script>alert('服务器开小差了！');</script>");
                    break;
            }
        }
        response.getWriter().println("<script>alert('删除分类成功！');</script>");
        response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/categoryServlet?operation=findPageCategories&num=" +
                request.getSession().getAttribute("currentCategoryPageNum"));

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    private void deleteCategory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int cid = 0;
        try {
            cid = Integer.parseInt(request.getParameter("cid"));
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('cid参数类型错误！');</script>");
        }
        int result = categoryService.deleteCategory(cid);
        switch (result) {
            case 0:
                response.getWriter().println("<script>alert('服务器开小差了！');</script>");
                break;
            case 1:
                response.getWriter().println("<script>alert('删除分类成功！');</script>");
                response.setHeader("refresh", "0,    url=" + request.getContextPath() + "/admin/categoryServlet?operation=findPageCategories&num=" +
                        request.getSession().getAttribute("currentCategoryPageNum"));
                break;
        }
    }

    private void updateCategory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String cid = request.getParameter("cid");
        String cname = request.getParameter("cname");
        int result = categoryService.updateCategory(cid, cname);
        switch (result) {
            case 0:
                response.getWriter().println("<script>alert('服务器开小差了！');</script>");
                break;
            case 1:
                response.getWriter().println("<script>alert('修改分类成功！');</script>");
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/categoryServlet?operation=findPageCategories&num=" +
                        request.getSession().getAttribute("currentCategoryPageNum"));
                break;
        }
    }

    private void findAllCategory(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        List<Category> categoryList = categoryService.findAllCategory();
        if (categoryList == null) {
            response.getWriter().println("<script>alert('服务器开小差了！');</script>");
            return;
        } else if (categoryList != null && categoryList.size() == 0) {
            response.getWriter().println("<script>alert('尚无分类！');</script>");
            return;
        } else if (categoryList != null && categoryList.size() != 0) {
            System.out.println("categoryList = " + categoryList);
            request.setAttribute("categories", categoryList); // 存入request域
            request.getServletContext().setAttribute("categories", categoryList); // 存入context域
            request.getRequestDispatcher("/admin/product/addProduct.jsp").forward(request, response);
        }
    }

    private void addCategory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String cname = request.getParameter("cname");
        if (StringUtils.isEmpty(cname)) {
            response.getWriter().println("cname参数不能为空");
            return;
        }

        int addResult = categoryService.addCategory(cname);
        switch (addResult) {
            case 0:
                response.getWriter().println("<script>alert('该分类已存在或服务器开小差了！');</script>");
                break;
            case 1:
                response.getWriter().println("<script>alert('添加分类成功！');</script>");
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/categoryServlet?operation=findPageCategories&num=" +
                        request.getSession().getAttribute("currentCategoryPageNum"));
                break;
        }
    }


}
