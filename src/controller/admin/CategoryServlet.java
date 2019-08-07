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
            response.getWriter().println("<script>alert('������ʽ����');</script>"); // У��ǰ�˴������
        }*/
        String operation = request.getParameter("operation");
        if (operation == null) {
            response.getWriter().println("<script>alert('operation������ʽ����');</script>"); // У��ǰ�˴������
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
                response.getWriter().println("<script>alert('�Ѿ��ǵ�һҳ��');</script>");
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/categoryServlet?operation=findPageCategories&num=" +
                        request.getSession().getAttribute("currentCategoryPageNum"));
                return;
            }
            int totalPageNum = (int) request.getServletContext().getAttribute("totalPageNum");
            if (currentPageNum > totalPageNum) {
                response.getWriter().println("<script>alert('�Ѿ������һҳ��');</script>");
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/categoryServlet?operation=findPageCategories&num=" +
                        request.getSession().getAttribute("currentCategoryPageNum"));
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('������ʽ����');</script>"); // У��ǰ�˴������
        }
        // ���û�з������ݣ��õ�null���Ƿ�null������ ����������null�������Զ���ֵ
        Page currentPage = categoryService.findPageCategories(currentPageNum);
        System.out.println(currentPage);
        if (currentPage == null) {
            response.getWriter().println("<script>alert('��������С���ˣ�');</script>");
            return;
        } else if (currentPage.getList() != null && currentPage.getList().size() == 0) {
            response.getWriter().println("<script>alert('��ҳ���޷��࣡');</script>");
            return;
        } else if (currentPage.getList() != null && currentPage.getList().size() != 0) {
            request.setAttribute("page", currentPage);
            // �ѵ�ǰҳ�� ����session
            request.getSession().setAttribute("currentCategoryPageNum", currentPage.getCurrentPageNum());
            // �ѵ�ǰҳ�� ����context
            request.getServletContext().setAttribute("totalPageNum", currentPage.getTotalPageNum());

            request.getRequestDispatcher("/admin/category/categoryList.jsp").forward(request, response);
        }
    }

    private void deleteMultiCategory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] cidStrArray = request.getParameterValues("cid");
        /*string����תint����*/
        int[] cidArray = new int[cidStrArray.length];
        for (int i = 0; i < cidStrArray.length; i++) {
            cidArray[i] = Integer.parseInt(cidStrArray[i]);
        }
//        System.out.println(Arrays.toString(cidArray));
        if (cidArray == null) {
            response.getWriter().println("<script>alert('��ѡ����࣡');</script>");
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/categoryServlet?operation=findPageCategories&num=" +
                    request.getSession().getAttribute("currentCategoryPageNum"));
            return;
        }
        for (int cid : cidArray) {
            int result = categoryService.deleteCategory(cid);
            switch (result) {
                case 0:
                    response.getWriter().println("<script>alert('��������С���ˣ�');</script>");
                    break;
            }
        }
        response.getWriter().println("<script>alert('ɾ������ɹ���');</script>");
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
            response.getWriter().println("<script>alert('cid�������ʹ���');</script>");
        }
        int result = categoryService.deleteCategory(cid);
        switch (result) {
            case 0:
                response.getWriter().println("<script>alert('��������С���ˣ�');</script>");
                break;
            case 1:
                response.getWriter().println("<script>alert('ɾ������ɹ���');</script>");
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
                response.getWriter().println("<script>alert('��������С���ˣ�');</script>");
                break;
            case 1:
                response.getWriter().println("<script>alert('�޸ķ���ɹ���');</script>");
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/categoryServlet?operation=findPageCategories&num=" +
                        request.getSession().getAttribute("currentCategoryPageNum"));
                break;
        }
    }

    private void findAllCategory(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        List<Category> categoryList = categoryService.findAllCategory();
        if (categoryList == null) {
            response.getWriter().println("<script>alert('��������С���ˣ�');</script>");
            return;
        } else if (categoryList != null && categoryList.size() == 0) {
            response.getWriter().println("<script>alert('���޷��࣡');</script>");
            return;
        } else if (categoryList != null && categoryList.size() != 0) {
            System.out.println("categoryList = " + categoryList);
            request.setAttribute("categories", categoryList); // ����request��
            request.getServletContext().setAttribute("categories", categoryList); // ����context��
            request.getRequestDispatcher("/admin/product/addProduct.jsp").forward(request, response);
        }
    }

    private void addCategory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String cname = request.getParameter("cname");
        if (StringUtils.isEmpty(cname)) {
            response.getWriter().println("cname��������Ϊ��");
            return;
        }

        int addResult = categoryService.addCategory(cname);
        switch (addResult) {
            case 0:
                response.getWriter().println("<script>alert('�÷����Ѵ��ڻ��������С���ˣ�');</script>");
                break;
            case 1:
                response.getWriter().println("<script>alert('��ӷ���ɹ���');</script>");
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/categoryServlet?operation=findPageCategories&num=" +
                        request.getSession().getAttribute("currentCategoryPageNum"));
                break;
        }
    }


}
