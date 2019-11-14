package controller.user;

import bean.Cart;
import bean.User;
import service.CartService;
import service.impl.CartServiceImpl;
import utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/cartServlet")
public class CartServlet extends HttpServlet {

    private CartService cartService = new CartServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String op = request.getParameter("op");
        if(StringUtils.isEmpty(op)){
            response.getWriter().println("<script>alert('op参数为空！');</script>");
            return;
        }
        switch (op){
            case "addToCart":
                addToCart(request, response);
                break;
            case "findCart":
                findCart(request, response);
                break;
            case "updateOneProductCount":
                updateOneProductCount(request, response);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + op);
        }
    }

    private void updateOneProductCount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String addOrDelete = request.getParameter("addOrDelete");
        int cartItemId = 0;
        try{
            cartItemId = Integer.parseInt(request.getParameter("itemId"));
        }catch (Exception e){
            e.printStackTrace();
            response.getWriter().println("<script>alert('cartItemId参数格式错误！');</script>");
        }
        int updateResult = cartService.updateOneProductCount(cartItemId, addOrDelete);
        switch (updateResult){
            case 0:
                response.getWriter().println("<script>alert('服务器开小差了！');</script>");
                break;
            case 1:
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/cartServlet?op=findCart&cartJsp=shoppingcart");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + updateResult);
        }
    }

    private void findCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 本来设置jsp这个参数就是为了能dispatcher到不同的页面
        // placeOrder页面也可以通过查询
        String toJsp = request.getParameter("cartJsp");
        User user = (User) request.getSession().getAttribute("user");
        int uid = user.getUid();
        Cart cart = cartService.getCart(uid);
        if (cart == null) {
            response.getWriter().println("<script>alert('服务器开小差了！');</script>");
            return;
        }
        // 存入request域
        request.setAttribute("cart", cart);
//        // 存入session，给placeOrder页面从session获取Cart，并没有通过查询
        request.getSession().setAttribute("cart", cart);
        request.getRequestDispatcher("/" + toJsp + ".jsp").forward(request, response);
    }

    private void addToCart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //从session域获取User对象
        User currentUser = (User) request.getSession().getAttribute("user");
        // 如果没有完成邮箱验证，不允许购物
        if ("N".equals(currentUser.getActivationStatus())) {
            response.getWriter().println("<script>alert('请查收邮件并完成邮箱验证');</script>");
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/index.jsp");
            return;
        }
        int uid = currentUser.getUid();
        int pid = 0;
        int productCount = 0;
        try{
            pid = Integer.parseInt(request.getParameter("pid"));
            productCount = Integer.parseInt(request.getParameter("productCount"));
        }catch (Exception e){
            e.printStackTrace();
            response.getWriter().println("<script>alert('pid/productCount参数类型错误');</script>");
        }
        int result = cartService.saveCartItem(uid, pid, productCount);
        switch (result){
            case 0:
                response.getWriter().println("<script>alert('服务器开小差了');</script>");
                break;
            case 1:
                response.getWriter().println("<script>alert('添加到购物车成功！');</script>");
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/index.jsp");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + result);
        }
    }
}
