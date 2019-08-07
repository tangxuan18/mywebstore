package controller.admin;

import bean.Category;
import bean.Page;
import bean.Product;
import org.apache.commons.beanutils.BeanUtils;
import service.CategoryService;
import service.CategoryServiceImpl;
import service.ProductService;
import service.ProductServiceImpl;
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
     * �� �� ��Ʒ
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // �������в�������װ��map��
        Map<String, String> productMap = MyFileUploadUtils.parseRequest(request);
//        String op = request.getParameter("op");
        String op = productMap.get("op");
        if (StringUtils.isEmpty(op)) {
            response.getWriter().println("<script>alert('op����Ϊ��post��');</script>"); // У��ǰ�˴������
            return;
        }
        switch (op) {
            case "addProduct":
                addProduct(request, response, productMap);
                break;
            case "updateProduct":
                updateProduct(request, response, productMap);
                break;

        }
    }

    private void updateProduct(HttpServletRequest request, HttpServletResponse response, Map<String, String> productMap) throws IOException {
//        System.out.println("oldProductMap = " + productMap);
        int pid = 0;
        try {
            pid = Integer.parseInt(productMap.get("id"));
        }catch (Exception e){
            e.printStackTrace();
            response.getWriter().println("<script>alert('pid�������ʹ���');</script>");
        }
        String imgUrl = productMap.get("imgUrl");
        if(!StringUtils.isPicture(imgUrl)){ // ���ͼƬû���޸ģ���file��ǩΪ�գ�������һ�����url
            String oldImgUrl = productService.getProduct(pid).getImgUrl();
            productMap.put("imgUrl", oldImgUrl);
        }
//        System.out.println("modifiedProductMap = " + productMap);
        // ��������޸���ͼƬ������productMap���Ķ�

        Product product = new Product();
        try {
            BeanUtils.populate(product, productMap);
//            System.out.println("product = " + product);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        int updateResult = productService.updateProduct(product);
        switch (updateResult) {
            case 0:
                response.getWriter().println("<script>alert('��������С���ˣ�');</script>");
                break;
            case 1:
                response.getWriter().println("<script>alert('�޸���Ʒ�ɹ���');</script>");
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/productServlet?op=findPageProducts&num=" +
                        request.getSession().getAttribute("currentProductPageNum"));
                break;
        }
    }

    private void findProductByUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int pid = 0;
        try {
            pid = Integer.parseInt(request.getParameter("pid"));
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('cid�������ʹ���');</script>");
        }

        Product product = productService.getProduct(pid);
//        System.out.println("product = " + product);
        if (product == null) {
            response.getWriter().println("<script>alert('��������С���ˣ�');</script>");
            return;
        }
        request.setAttribute("product", product);
        request.getRequestDispatcher("/admin/product/updateProduct.jsp").forward(request, response);
    }

    /**
     * �� ɾ ��Ʒ
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String op = request.getParameter("op");
        if (StringUtils.isEmpty(op)) {
            response.getWriter().println("<script>alert('op����Ϊ��get��');</script>"); // У��ǰ�˴������
            return;
        }
        switch (op) {
            case "findAllCategories":
                findAllCategories(request, response);
                break;
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
        }
    }

    private void deleteMultiProducts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] pidStrArray = request.getParameterValues("pid");
        /*string����תint����*/
        int[] pidArray = new int[pidStrArray.length];
        for (int i = 0; i < pidStrArray.length; i++) {
            pidArray[i] = Integer.parseInt(pidStrArray[i]);
        }
//        System.out.println(Arrays.toString(cidArray));
        if (pidStrArray == null) {
            response.getWriter().println("<script>alert('��ѡ����࣡');</script>");
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/categoryServlet?operation=findPageCategories&num=" +
                    request.getSession().getAttribute("currentCategoryPageNum"));
            return;
        }
        for (int pid : pidArray) {
            int result = productService.deleteOneProduct(pid);
            switch (result) {
                case 0:
                    response.getWriter().println("<script>alert('��������С���ˣ�');</script>");
                    break;
            }
        }
        response.getWriter().println("<script>alert('����ɾ����Ʒ�ɹ���');</script>");
        response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/productServlet?op=findPageProducts&num=" +
                request.getSession().getAttribute("currentProductPageNum"));
    }

    private void searchPageProducts(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        /*����ǰ������*/
//        Map<String, String[]> parameterMap = request.getParameterMap(); // String[]�޷���BeanUtils
//        Map<String, String> requestMap = MyFileUploadUtils.parseRequest(request); // �������޷�������
        int currentPageNum = 1;
        /*����û������룬Ӧ�ô�null������Ӧ�ô�0֮��ģ�����û����룬��Ӧ�ô���ȷ������*/
        String productNum = request.getParameter("productNum");
        String productName = request.getParameter("productName");
        int cid = 0;
        String minWebStorePrice = request.getParameter("minWebStorePrice");
        String maxWebStorePrice = request.getParameter("maxWebStorePrice");
        /*��װProduct������û������룬�Ͳ���װ������û����룬Ӧ�÷�װ��ȷ������*/
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
            response.getWriter().println("<script>alert('num/cid�������ʹ���');</script>");
        }
        product.setCid(cid);
        System.out.println("product = " + product);
        request.setAttribute("searchProduct", product); // ��������������request��
        /*����Service��*/
        Page currentPage = productService.listSearchedPageProducts(product, currentPageNum);
        System.out.println(currentPage);
        if (currentPage == null) {
            response.getWriter().println("<script>alert('��������С���ˣ�');</script>");
            return;
        } else if (currentPage.getList() != null && currentPage.getList().size() == 0) {
            response.getWriter().println("<script>alert('��ҳ������Ʒ��');</script>");
            return;
        } else if (currentPage.getList() != null && currentPage.getList().size() != 0) {
            // ��ǰpage����request, �Ա㷭ҳʱ��ȡ
            request.setAttribute("page", currentPage);
            // �ѵ�ǰҳ�� ����session
            request.getSession().setAttribute("currentProductPageNum", currentPage.getCurrentPageNum());
            // ת��
            request.getRequestDispatcher("/admin/product/searchedProductList.jsp").forward(request, response);
        }
    }

    private void findAllCategories(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String jsp = request.getParameter("jsp");
        List<Category> categoryList = categoryService.findAllCategory();
        if (categoryList == null) {
            response.getWriter().println("<script>alert('��������С���ˣ�');</script>");
            return;
        } else if (categoryList != null && categoryList.size() == 0) {
            response.getWriter().println("<script>alert('���޷��࣡');</script>");
            return;
        } else if (categoryList != null && categoryList.size() != 0) {
//            System.out.println("categoryList = " + categoryList);
            request.setAttribute("categories", categoryList);
            request.getRequestDispatcher("/admin/product/" + jsp + ".jsp").forward(request, response);
//            Object contextCategories = getServletContext().getAttribute("categories");
//            System.out.println("contextCategories = " + contextCategories);
        }
    }

    private void deleteOneProduct(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int pid = 0;
        try {
            pid = Integer.parseInt(request.getParameter("pid"));
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('cid�������ʹ���');</script>");
        }
        int result = productService.deleteOneProduct(pid);
        switch (result) {
            case 0:
                response.getWriter().println("<script>alert('��������С���ˣ�');</script>");
                break;
            case 1:
                response.getWriter().println("<script>alert('ɾ����Ʒ�ɹ���');</script>");
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
            response.getWriter().println("<script>alert('num�������ʹ���');</script>");
        }
        Page currentPage = productService.listPageProducts(currentPageNum);
//        System.out.println(currentPage);
        if (currentPage == null) {
            response.getWriter().println("<script>alert('��������С���ˣ�');</script>");
            return;
        } else if (currentPage.getList() != null && currentPage.getList().size() == 0) {
            response.getWriter().println("<script>alert('��ҳ������Ʒ��');</script>");
            return;
        } else if (currentPage.getList() != null && currentPage.getList().size() != 0) {
            request.setAttribute("page", currentPage);
            // �ѵ�ǰҳ�� ����session
            request.getSession().setAttribute("currentProductPageNum", currentPage.getCurrentPageNum());
            request.getRequestDispatcher("/admin/product/productList.jsp").forward(request, response);
        }

    }

    private void addProduct(HttpServletRequest request, HttpServletResponse response, Map<String, String> productMap) throws IOException {
        Product product = new Product();
        try {
            BeanUtils.populate(product, productMap);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        int addResult = productService.saveProduct(product);
        switch (addResult) {
            case 0:
                response.getWriter().println("<script>alert('��������С���ˣ�');</script>");
                break;
            case 1:
                response.getWriter().println("<script>alert('�����Ʒ�ɹ���');</script>");
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/productServlet?op=findPageProducts&num=" +
                        request.getSession().getAttribute("currentProductPageNum"));
                break;
        }
    }
}

