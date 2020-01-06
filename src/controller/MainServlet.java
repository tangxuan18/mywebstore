package controller;

import bean.Category;
import bean.Product;
import service.CategoryService;
import service.impl.CategoryServiceImpl;
import service.ProductService;
import service.impl.ProductServiceImpl;
import utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/mainServlet")
public class MainServlet extends HttpServlet {

    private CategoryService categoryService = new CategoryServiceImpl();
    private ProductService productService = new ProductServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String op = request.getParameter("op");
        if (StringUtils.isEmpty(op)) {
            response.getWriter().println("<script>alert('op参数为空get！');</script>");
            return;
        }
        switch (op){
            case "findAllCategories":
                findAllCategories(request, response);
                break;
            case "findTopProducts":
                findTopProducts(request, response);
                break;
            case "findHotProducts":
                findHotProducts(request, response);
                break;
            case "findProductsByCid":
                findProductsByCid(request, response);
                break;
            case "findProductByPid":
                findProductByPid(request, response);
                break;
            case "findProductsByKeyword":
                findProductsByKeyword(request, response);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + op);
        }
    }

    private void findProductsByKeyword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        if(StringUtils.isEmpty(keyword)){
            return;
        }
        List<Product> productList = productService.listProductsByKeyword(keyword.trim());
        if (productList == null) {
            response.getWriter().println("<script>alert('服务器开小差了！');</script>");
            return;
        } else if (productList.size() == 0) {
            response.getWriter().println("<script>alert('很遗憾，没有相关商品');</script>");
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/index.jsp");
            return;
        } else {
            request.setAttribute("products", productList);
            request.setAttribute("keyword", keyword);
            request.getRequestDispatcher("/products.jsp").forward(request, response);
        }
    }

    private void findProductByPid(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String cname = request.getParameter("cname");
        int pid = 0;
        try {
            pid = Integer.parseInt(request.getParameter("pid"));
        }catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('cid参数类型错误！');</script>");
        }
        Product product = productService.getProductByPid(pid);
        if (product == null) {
            response.getWriter().println("<script>alert('服务器开小差了！');</script>");
            return;
        }
        request.setAttribute("product", product);
        request.getRequestDispatcher("/productdetail.jsp").forward(request, response);
    }

    private void findProductsByCid(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String cname = request.getParameter("cname");
        int cid = 0;
        try {
            cid = Integer.parseInt(request.getParameter("cid"));
        }catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('cid参数类型错误！');</script>");
        }
        List<Product> productList = productService.listProductsByCid(cid);
        if (productList == null) {
            response.getWriter().println("<script>alert('服务器开小差了！');</script>");
            return;
        } else if (productList.size() == 0) {
            response.getWriter().println("<script>alert('该分类尚无商品！');</script>");
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/index.jsp");
            return;
        } else {
            request.setAttribute("products", productList);
            request.setAttribute("cname", cname);
            request.getRequestDispatcher("/products.jsp").forward(request, response);
        }
    }

    private void findHotProducts(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        List<Product> hotProductList = productService.listHotProducts();
        if (hotProductList == null) {
            response.getWriter().println("<script>alert('服务器开小差了！');</script>");
            return;
        } else if (hotProductList.size() == 0) {
            response.getWriter().println("<script>alert('尚无热门商品！');</script>");
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/index.jsp");
            return;
        } else {
            request.setAttribute("hotProducts", hotProductList);
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }

    private void findTopProducts(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        List<Product> topProductList = productService.listTopProducts();
        if (topProductList == null) {
            response.getWriter().println("<script>alert('服务器开小差了！');</script>");
            return;
        } else if (topProductList.size() == 0) {
            response.getWriter().println("<script>alert('尚无顶尖商品！');</script>");
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/index.jsp");
            return;
        } else {
            request.setAttribute("topProducts", topProductList);
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }

    private void findAllCategories(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String jsp = request.getParameter("jsp");
        List<Category> categoryList = categoryService.findAllCategory();
        if (categoryList == null) {
            response.getWriter().println("<script>alert('服务器开小差了！');</script>");
            return;
        } else if (categoryList != null && categoryList.size() == 0) {
            response.getWriter().println("<script>alert('尚无分类！');</script>");
            return;
        } else if (categoryList != null && categoryList.size() != 0) {
//            System.out.println("categoryList = " + categoryList);
            request.setAttribute("categories", categoryList);
            request.getRequestDispatcher("/" + jsp + ".jsp").forward(request, response);
        }
    }
}
