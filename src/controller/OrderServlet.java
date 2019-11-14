package controller;

import bean.Order;
import bean.OrderItem;
import bean.Page;
import bean.User;
import service.OrderService;
import service.impl.OrderServiceImpl;
import service.ProductService;
import service.impl.ProductServiceImpl;
import utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author GFS
 */
@WebServlet("/orderServlet")
public class OrderServlet extends HttpServlet {

    private OrderService orderService = new OrderServiceImpl();
    private ProductService productService = new ProductServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String op = request.getParameter("op");
        if (StringUtils.isEmpty(op)) {
            response.getWriter().println("<script>alert('op参数为空！');</script>");
            return;
        }
        switch (op) {
            // 确认订单
            case "placeOrder":
                placeOrder(request, response);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + op);
        }
    }

    /**
     * 下单
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    private void placeOrder(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String jsp = request.getParameter("orderJsp");
        String[] selectedCartItemIds = request.getParameterValues("ids");
        if (selectedCartItemIds == null) {
            response.getWriter().println("<script>alert('请选择商品下单！ids参数为空！');</script>");
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/cartServlet?op=findCart&cartJsp=shoppingcart");
            return;
        }
        String receiverName = request.getParameter("receiverName");
        String receiverMobile = request.getParameter("receiverMobile");
        String receiverAddress = request.getParameter("receiverAddress");
        String username = request.getParameter("username");
        if (StringUtils.isEmpty(receiverName) || StringUtils.isEmpty(receiverMobile) || StringUtils.isEmpty(receiverAddress) || StringUtils.isEmpty(username)) {
            response.getWriter().println("<script>alert('receiverName/receiverMobile/receiverAddress/username参数为空！');</script>");
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/cartServlet?op=findCart&cartJsp=shoppingcart");
            return;
        }
        int[] selectedCartItemIdArray = new int[selectedCartItemIds.length];
        double totalPrice = 0;
        int uid = 0;
        try {
            totalPrice = Double.parseDouble(request.getParameter("money"));
            for (int i = 0; i < selectedCartItemIds.length; i++) {
                selectedCartItemIdArray[i] = Integer.parseInt(selectedCartItemIds[i]);
            }
            uid = Integer.parseInt(request.getParameter("uid"));
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('uid/totalPrice/selectedPidArray参数格式错误！');</script>");
        }
        /*封装前端参数*/
        Order order = new Order();
        String orderNum = UUID.randomUUID().toString();
        order.setOrderNum(orderNum); // 订单号
        order.setTotalPrice(totalPrice); // 总价
        order.setReceiverName(receiverName);
        order.setReceiverMobile(receiverMobile);
        order.setReceiverAddress(receiverAddress);
        order.setPayStatus(1); // 订单状态
        order.setOrderTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())); // 下单时间
        order.setUid(uid);
        order.setUsername(username);
        /*（一）验证库存*/
        int confirmStockResult = orderService.confirmStock(selectedCartItemIdArray);
        if(confirmStockResult == 0){
            response.getWriter().println("<script>alert('库存不足！');</script>");
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/cartServlet?op=findCart&cartJsp=shoppingcart");
            return;
        }
        /*（六）查询orderList*/
        List<Order> orderList = orderService.placeOrder(order, selectedCartItemIdArray, uid);
//        System.out.println("orderList = " + orderList);
        if (orderList == null) {
            response.getWriter().println("<script>alert('服务器开小差了！');</script>");
            return;
        } else if (orderList != null && orderList.size() == 0) {
            response.getWriter().println("<script>alert('尚无订单！');</script>");
            return;
        } else {
            request.setAttribute("orders", orderList);
            request.getRequestDispatcher("/"+ jsp + ".jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String op = request.getParameter("op");
        if (StringUtils.isEmpty(op)) {
            response.getWriter().println("<script>alert('op参数为空！');</script>");
            return;
        }
        switch (op) {
            case "findPageOrders":
                findPageOrders(request, response);
                break;
            case "orderDetail":
                findOrderDetail(request, response);
                break;
            case "findUserOrders":
                findUserOrders(request, response);
                break;
            case "cancelOrder":
                cancelOrder(request, response);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + op);
        }
    }

    private void cancelOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String payStatus = request.getParameter("state");
        String oid = request.getParameter("oid");
        if (StringUtils.isEmpty(payStatus)) {
            response.getWriter().println("<script>alert('前端参数为空！');</script>");
            return;
        }
        int orderId = 0;
        try{
            orderId = Integer.parseInt(oid);
        }catch (Exception e){
            e.printStackTrace();
            response.getWriter().println("<script>alert('oid参数格式错误！');</script>");
        }
        Order order = new Order();
        order.setPayStatus(0);
        order.setOrderId(orderId);
        /*（一）*/
        int result  = orderService.cancelOrder(order);
        switch (result){
            case 0:
                response.getWriter().println("<script>alert('服务器开小差了！');</script>");
            case 1:
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/orderServlet?op=findUserOrders");
        }
    }

    private void findUserOrders(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        User user = (User) request.getSession().getAttribute("user"); //从session域获取User对象
        if(user == null){
            response.getWriter().println("<script>alert('请先登录！');</script>");
            return;
        }
        int uid = user.getUid();
        List<Order> orderList = orderService.listOrdersByUid(uid);
        if (orderList == null) {
            response.getWriter().println("<script>alert('服务器开小差了！');</script>");
            return;
        } else if (orderList != null && orderList.size() == 0) {
            response.getWriter().println("<script>alert('尚无订单！');</script>");
            return;
        } else {
            request.setAttribute("orders", orderList);
            request.getRequestDispatcher("/myOrders.jsp").forward(request, response);
        }
    }

    private void findOrderDetail(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String orderNum = request.getParameter("oid");
        if (StringUtils.isEmpty(orderNum)) {
            response.getWriter().println("<script>alert('op参数为空！');</script>");
            return;
        }
        List<OrderItem> orderItemList = orderService.listOrderItems(orderNum);
        System.out.println("orderItemList = " + orderItemList);
        if (orderItemList == null) {
            response.getWriter().println("<script>alert('服务器开小差了！');</script>");
            return;
        } else if (orderItemList != null && orderItemList.size() == 0) {
            response.getWriter().println("<script>alert('尚无订单详情！');</script>");
            return;
        } else {
            request.setAttribute("orderItems", orderItemList);
            request.getRequestDispatcher("admin/order/orderDetails.jsp").forward(request, response);
        }
    }

    private void findPageOrders(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int currentPageNum = 0;
        try {
            currentPageNum = Integer.parseInt(request.getParameter("num"));
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('num参数类型错误！');</script>");
        }
        Page currentPage = orderService.listPageOrders(currentPageNum);
//        System.out.println(currentPage);
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
            request.getRequestDispatcher("/admin/order/orderList.jsp").forward(request, response);
        }
    }
}
