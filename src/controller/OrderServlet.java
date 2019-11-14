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
            response.getWriter().println("<script>alert('op����Ϊ�գ�');</script>");
            return;
        }
        switch (op) {
            // ȷ�϶���
            case "placeOrder":
                placeOrder(request, response);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + op);
        }
    }

    /**
     * �µ�
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    private void placeOrder(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String jsp = request.getParameter("orderJsp");
        String[] selectedCartItemIds = request.getParameterValues("ids");
        if (selectedCartItemIds == null) {
            response.getWriter().println("<script>alert('��ѡ����Ʒ�µ���ids����Ϊ�գ�');</script>");
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/cartServlet?op=findCart&cartJsp=shoppingcart");
            return;
        }
        String receiverName = request.getParameter("receiverName");
        String receiverMobile = request.getParameter("receiverMobile");
        String receiverAddress = request.getParameter("receiverAddress");
        String username = request.getParameter("username");
        if (StringUtils.isEmpty(receiverName) || StringUtils.isEmpty(receiverMobile) || StringUtils.isEmpty(receiverAddress) || StringUtils.isEmpty(username)) {
            response.getWriter().println("<script>alert('receiverName/receiverMobile/receiverAddress/username����Ϊ�գ�');</script>");
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
            response.getWriter().println("<script>alert('uid/totalPrice/selectedPidArray������ʽ����');</script>");
        }
        /*��װǰ�˲���*/
        Order order = new Order();
        String orderNum = UUID.randomUUID().toString();
        order.setOrderNum(orderNum); // ������
        order.setTotalPrice(totalPrice); // �ܼ�
        order.setReceiverName(receiverName);
        order.setReceiverMobile(receiverMobile);
        order.setReceiverAddress(receiverAddress);
        order.setPayStatus(1); // ����״̬
        order.setOrderTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())); // �µ�ʱ��
        order.setUid(uid);
        order.setUsername(username);
        /*��һ����֤���*/
        int confirmStockResult = orderService.confirmStock(selectedCartItemIdArray);
        if(confirmStockResult == 0){
            response.getWriter().println("<script>alert('��治�㣡');</script>");
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/cartServlet?op=findCart&cartJsp=shoppingcart");
            return;
        }
        /*��������ѯorderList*/
        List<Order> orderList = orderService.placeOrder(order, selectedCartItemIdArray, uid);
//        System.out.println("orderList = " + orderList);
        if (orderList == null) {
            response.getWriter().println("<script>alert('��������С���ˣ�');</script>");
            return;
        } else if (orderList != null && orderList.size() == 0) {
            response.getWriter().println("<script>alert('���޶�����');</script>");
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
            response.getWriter().println("<script>alert('op����Ϊ�գ�');</script>");
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
            response.getWriter().println("<script>alert('ǰ�˲���Ϊ�գ�');</script>");
            return;
        }
        int orderId = 0;
        try{
            orderId = Integer.parseInt(oid);
        }catch (Exception e){
            e.printStackTrace();
            response.getWriter().println("<script>alert('oid������ʽ����');</script>");
        }
        Order order = new Order();
        order.setPayStatus(0);
        order.setOrderId(orderId);
        /*��һ��*/
        int result  = orderService.cancelOrder(order);
        switch (result){
            case 0:
                response.getWriter().println("<script>alert('��������С���ˣ�');</script>");
            case 1:
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/orderServlet?op=findUserOrders");
        }
    }

    private void findUserOrders(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        User user = (User) request.getSession().getAttribute("user"); //��session���ȡUser����
        if(user == null){
            response.getWriter().println("<script>alert('���ȵ�¼��');</script>");
            return;
        }
        int uid = user.getUid();
        List<Order> orderList = orderService.listOrdersByUid(uid);
        if (orderList == null) {
            response.getWriter().println("<script>alert('��������С���ˣ�');</script>");
            return;
        } else if (orderList != null && orderList.size() == 0) {
            response.getWriter().println("<script>alert('���޶�����');</script>");
            return;
        } else {
            request.setAttribute("orders", orderList);
            request.getRequestDispatcher("/myOrders.jsp").forward(request, response);
        }
    }

    private void findOrderDetail(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String orderNum = request.getParameter("oid");
        if (StringUtils.isEmpty(orderNum)) {
            response.getWriter().println("<script>alert('op����Ϊ�գ�');</script>");
            return;
        }
        List<OrderItem> orderItemList = orderService.listOrderItems(orderNum);
        System.out.println("orderItemList = " + orderItemList);
        if (orderItemList == null) {
            response.getWriter().println("<script>alert('��������С���ˣ�');</script>");
            return;
        } else if (orderItemList != null && orderItemList.size() == 0) {
            response.getWriter().println("<script>alert('���޶������飡');</script>");
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
            response.getWriter().println("<script>alert('num�������ʹ���');</script>");
        }
        Page currentPage = orderService.listPageOrders(currentPageNum);
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
            request.getRequestDispatcher("/admin/order/orderList.jsp").forward(request, response);
        }
    }
}
