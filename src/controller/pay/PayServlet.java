package controller.pay;

import bean.OrderItem;
import service.OrderService;
import service.impl.OrderServiceImpl;
import utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Shane Tang
 * @create 2020-01-08 20:22
 */
@WebServlet("/payServlet")
public class PayServlet extends HttpServlet {

    private OrderService orderService = new OrderServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String op = request.getParameter("op");
        if (StringUtils.isEmpty(op)) {
            response.getWriter().println("<script>alert('op参数为空！');</script>");
            return;
        }
        switch (op) {
            case "payOrder":
                payOrder(request, response);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + op);
        }
    }

    private void payOrder(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String orderNum = request.getParameter("oid");
        if (StringUtils.isEmpty(orderNum)) {
            response.getWriter().println("<script>alert('op参数为空！');</script>");
            return;
        }
        List<OrderItem> orderItemList = null;
        try {
            orderItemList = orderService.listOrderItems(orderNum);
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        System.out.println("orderItemList = " + orderItemList);
        if (orderItemList == null) {
            response.getWriter().println("<script>alert('服务器开小差了！');</script>");
            return;
        } else if (orderItemList.size() == 0) {
            response.getWriter().println("<script>alert('尚无订单详情！');</script>");
            return;
        } else {
            request.setAttribute("orderItems", orderItemList);
            request.getRequestDispatcher("pay/payOrder.jsp").forward(request, response);
        }
    }
}
