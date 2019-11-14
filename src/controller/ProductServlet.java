package controller;

import bean.Category;
import bean.Page;
import bean.Product;
import org.apache.commons.beanutils.BeanUtils;
import service.CategoryService;
import service.impl.CategoryServiceImpl;
import service.ProductService;
import service.impl.ProductServiceImpl;
import utils.MyFileUploadUtils;
import utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/productServlet")
public class ProductServlet extends HttpServlet {

    private ProductService productService = new ProductServiceImpl();
    private CategoryService categoryService = new CategoryServiceImpl();

    /**
     * 增 改 商品
     * 包含文件上传
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> productMap = MyFileUploadUtils.parseRequest(request);
        String op = productMap.get("op");
        if (StringUtils.isEmpty(op)) {
            response.getWriter().println("<script>alert('op参数为空post！');</script>");
            return;
        }
        switch (op) {
            case "addProduct":
                addProduct(request, response, productMap);
                break;
            case "updateProduct":
                updateProduct(request, response, productMap);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + op);
        }
    }

    private void updateProduct(HttpServletRequest request, HttpServletResponse response, Map<String, String> productMap) throws IOException {
        int pid = 0;
        try {
            pid = Integer.parseInt(productMap.get("id"));
        }catch (Exception e){
            e.printStackTrace();
            response.getWriter().println("<script>alert('pid参数类型错误！');</script>");
        }
        String imgUrl = productMap.get("imgUrl");
        // 如果图片没做修改，则file标签为空，会传过来一个随机url，此时不能修改imgUrl
        if(!StringUtils.isPicture(imgUrl)){
            String oldImgUrl = productService.getProduct(pid).getImgUrl();
            productMap.put("imgUrl", oldImgUrl);
        }
//        System.out.println("modifiedProductMap = " + productMap);
        // 否则，如果修改了图片，不对productMap做改动
        Product product = new Product();
        try {
            BeanUtils.populate(product, productMap);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        int updateResult = productService.updateProduct(product);
        switch (updateResult) {
            case 0:
                response.getWriter().println("<script>alert('服务器开小差了！');</script>");
                break;
            case 1:
                response.getWriter().println("<script>alert('修改商品成功！');</script>");
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/productServlet?op=findPageProducts&num=" +
                        request.getSession().getAttribute("currentProductPageNum"));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + updateResult);
        }
    }

    private void findProductByUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int pid = 0;
        try {
            pid = Integer.parseInt(request.getParameter("pid"));
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('cid参数类型错误！');</script>");
        }
        Product product = productService.getProduct(pid);
//        System.out.println("product = " + product);
        if (product == null) {
            response.getWriter().println("<script>alert('服务器开小差了！');</script>");
            return;
        }
        request.setAttribute("product", product);
        request.getRequestDispatcher("/admin/product/updateProduct.jsp").forward(request, response);
    }

    /**
     * 查 删 商品
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String op = request.getParameter("op");
        if (StringUtils.isEmpty(op)) {
            response.getWriter().println("<script>alert('op参数为空get！');</script>");
            return;
        }
        switch (op) {
            case "findPageProducts":
                findPageProducts(request, response);
                break;
            case "deleteOne":
                deleteOneProduct(request, response);
                break;
            case "findProductByUpdate":
                findProductByUpdate(request, response);
                break;
            case "searchPageProducts":
                searchPageProducts(request, response);
                break;
            case "deleteMultiProducts":
                deleteMultiProducts(request, response);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + op);
        }
    }

    private void deleteMultiProducts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] pidStrArray = request.getParameterValues("pid");
        /*string数组转int数组*/
        int[] pidArray = new int[pidStrArray.length];
        for (int i = 0; i < pidStrArray.length; i++) {
            pidArray[i] = Integer.parseInt(pidStrArray[i]);
        }
//        System.out.println(Arrays.toString(cidArray));
        if (pidStrArray == null) {
            response.getWriter().println("<script>alert('请选择分类！');</script>");
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/categoryServlet?operation=findPageCategories&num=" +
                    request.getSession().getAttribute("currentCategoryPageNum"));
            return;
        }
        for (int pid : pidArray) {
            int result = productService.deleteOneProduct(pid);
            switch (result) {
                case 0:
                    response.getWriter().println("<script>alert('服务器开小差了！');</script>");
                    break;
            }
        }
        response.getWriter().println("<script>alert('批量删除商品成功！');</script>");
        response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/productServlet?op=findPageProducts&num=" +
                request.getSession().getAttribute("currentProductPageNum"));
    }

    /**
     * 模糊查询
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    private void searchPageProducts(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        /*接收前端数据*/
//        Map<String, String[]> parameterMap = request.getParameterMap(); // String[]无法用BeanUtils
//        Map<String, String> requestMap = MyFileUploadUtils.parseRequest(request); // 参数又无法传过来
        int currentPageNum = 1;
        /*如果用户不输入，应该传null，而不应该传0之类的；如果用户输入，又应该传正确的类型*/
        String productNum = request.getParameter("productNum");
        String productName = request.getParameter("productName");
        int cid = 0;
        String minWebStorePrice = request.getParameter("minWebStorePrice");
        String maxWebStorePrice = request.getParameter("maxWebStorePrice");
        /*封装Product，如果用户不输入，就不封装；如果用户输入，应该封装正确的类型*/
        Product product = new Product();
        product.setProductNum(productNum);
        product.setProductName(productName);
        product.setMinWebStorePrice(minWebStorePrice);
        product.setMaxWebStorePrice(maxWebStorePrice);
        String num = request.getParameter("num");
        try {
            currentPageNum = Integer.parseInt(request.getParameter("num"));
            cid = Integer.parseInt(request.getParameter("cid"));
        }catch (Exception e){
            e.printStackTrace();
            response.getWriter().println("<script>alert('num/cid参数类型错误！');</script>");
        }
        product.setCid(cid);
        System.out.println("product = " + product);
        // 把搜索条件存入session域，下次打开时记住
        request.getSession().setAttribute("searchProduct", product);
        Page currentPage = productService.listSearchedPageProducts(product, currentPageNum);
        if (currentPage == null) {
            response.getWriter().println("<script>alert('服务器开小差了！');</script>");
            return;
        } else if (currentPage.getList() != null && currentPage.getList().size() == 0) {
            response.getWriter().println("<script>alert('该页尚无商品！');</script>");
            return;
        } else if (currentPage.getList() != null && currentPage.getList().size() != 0) {
            // 当前page存入request, 以便翻页时获取
            request.setAttribute("page", currentPage);
            // 把当前页码 存入session
            request.getSession().setAttribute("currentProductPageNum", currentPage.getCurrentPageNum());
            // 转发
            request.getRequestDispatcher("/admin/product/searchedProductList.jsp").forward(request, response);
        }
    }

    private void deleteOneProduct(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int pid = 0;
        try {
            pid = Integer.parseInt(request.getParameter("pid"));
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('cid参数类型错误！');</script>");
        }
        int result = productService.deleteOneProduct(pid);
        switch (result) {
            case 0:
                response.getWriter().println("<script>alert('服务器开小差了！');</script>");
                break;
            case 1:
                response.getWriter().println("<script>alert('删除商品成功！');</script>");
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/productServlet?op=findPageProducts&num=" +
                        request.getSession().getAttribute("currentProductPageNum"));
                break;
        }
    }

    private void findPageProducts(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int currentPageNum = 0;
        try {
            currentPageNum = Integer.parseInt(request.getParameter("num"));
        }catch (Exception e){
            e.printStackTrace();
            response.getWriter().println("<script>alert('num参数类型错误！');</script>");
        }
        Page currentPage = productService.listPageProducts(currentPageNum);
        if (currentPage == null) {
            response.getWriter().println("<script>alert('服务器开小差了！');</script>");
            return;
        } else if (currentPage.getList() != null && currentPage.getList().size() == 0) {
            response.getWriter().println("<script>alert('该页尚无商品！');</script>");
            return;
        } else if (currentPage.getList() != null && currentPage.getList().size() != 0) {
            request.setAttribute("page", currentPage);
            // 把当前页码 存入session
            request.getSession().setAttribute("currentProductPageNum", currentPage.getCurrentPageNum());
            request.getRequestDispatcher("/admin/product/productList.jsp").forward(request, response);
        }

    }

    private void addProduct(HttpServletRequest request, HttpServletResponse response, Map<String, String> productMap) throws IOException {
        Product product = new Product();
        try {
            BeanUtils.populate(product, productMap);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        int addResult = productService.saveProduct(product);
        switch (addResult) {
            case 0:
                response.getWriter().println("<script>alert('服务器开小差了！');</script>");
                break;
            case 1:
                response.getWriter().println("<script>alert('添加商品成功！');</script>");
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/productServlet?op=findPageProducts&num=" +
                        request.getSession().getAttribute("currentProductPageNum"));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + addResult);
        }
    }
}

