package filter;

import bean.Admin;
import bean.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class UserFilter implements Filter {
    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String requestURI = request.getRequestURI();
        Admin currentAdmin = (Admin) request.getSession().getAttribute("currentAdmin");
        User user = (User) request.getSession().getAttribute("user");
        if (requestURI.endsWith("index.jsp") || requestURI.endsWith("login.jsp") || requestURI.endsWith("regist.jsp")) {
            chain.doFilter(req, resp);
            // 不return的话会 放行请求放行请求循环
            return;
        }
        /*未登录管理员账号不可访问admin目录下的资源，而将重定向至管理员后台登录页*/
        if (currentAdmin == null) {
            // 只有这个的错误：如果访问登录页，会直接重定向
            if (requestURI.contains("admin")) {
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/index.jsp");
//                System.out.println("adminFilter:" + requestURI);
            }
        }
        // 未登录用户账号不可访问order目录下的资源，而将重定向至管理登录页
        if (user == null) {
            if (requestURI.endsWith("myOrders.jsp") || requestURI.endsWith("placeOrder.jsp") || requestURI.endsWith("shoppingcart.jsp")) {
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/user/login.jsp");
//                System.out.println("userFilter:" + requestURI);
            }
        }
        chain.doFilter(request, response);
    }

//    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
//        HttpServletRequest request = (HttpServletRequest) req;
//        HttpServletResponse response = (HttpServletResponse) resp;
//        Admin admin = (Admin) request.getSession().getAttribute("admin");
//        User users = (User) request.getSession().getAttribute("user");
//        String url = request.getRequestURI();
//        boolean check = url.contains("/admin") && (!(url.endsWith("admin/") || url.endsWith("main.jsp")));
//        boolean check1 = url.contains("/user") && !url.contains("/admin/user/");
//        boolean staticR = StringUtils.judgeStaticResource(url);
//        if (url.contains("userServlet")) {
//            chain.doFilter(req, resp);
//            return;
//        }
//        if (admin == null && check) {
//            if (!staticR) {
//                response.setHeader("refresh", "0;url=" + request.getContextPath() + "/admin/main.jsp");
//            }
////            request.getRequestDispatcher("/admin/main.jsp").forward(request,response);
//        }
//        if (users == null && check1) {
//            if (!staticR) {
//                response.setHeader("refresh", "0;url=" + request.getContextPath() + "/login.jsp");
//            }
//        }
//        chain.doFilter(req, resp);
//    }


    public void init(FilterConfig config) throws ServletException {

    }

}
