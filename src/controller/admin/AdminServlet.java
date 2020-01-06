package controller.admin;

import bean.Admin;
import service.AdminService;
import service.impl.AdminServiceImpl;
import utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/adminServlet")
public class AdminServlet extends HttpServlet {

    private AdminService adminService = new AdminServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String operation = request.getParameter("operation");
        switch (operation) {
            case "login":
                login(request, response);
                break;
            case "logout":
                logout(request, response);
                break;
            case "addAdmin":
                addAdmin(request, response);
                break;
            case "findAllAdmin":
                findAllAdmin(request, response);
                break;
            case "deleteOneAdmin":
                deleteOneAdmin(request, response);
                break;
            case "updateAdmin":
                updateAdmin(request, response);
                break;
            case "deleteMulti":
                deleteMultiAdmin(request, response);
                break;
            default:

        }
    }

    private void deleteMultiAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] aidArray = request.getParameterValues("aid");
        System.out.println(aidArray);
        if (aidArray == null) {
            response.getWriter().println("<script>alert('请选择管理员！');</script>");
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/categoryServlet?operation=findAllCategory");
            return;
        }
        // 采用循环deleteOne的方式
        for (String aid : aidArray) {
            int result = adminService.deleteOneAdmin(aid);
            switch (result) {
                case 0:
                    response.getWriter().println("<script>alert('服务器开小差了！');</script>");
                    break;
                default:
            }
        }
        response.getWriter().println("<script>alert('批量删除管理员成功！');</script>");
        response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/adminServlet?operation=findAllAdmin");
    }

    private void updateAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int aid = Integer.parseInt(request.getParameter("aid"));
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        Admin admin = new Admin();
        admin.setId(aid);
        admin.setUsername(username);
        admin.setPassword(password);
        int result = adminService.updateAdmin(admin);
        switch (result) {
            case 0:
                response.getWriter().println("<script>alert('服务器开小差了！');</script>");
                break;
            case 1:
                response.getWriter().println("<script>alert('修改管理员成功！');</script>");
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/adminServlet?operation=findAllAdmin");
                break;
            default:
        }
    }

    /**
     * 好像aid为String类型也行？？？
     * @param request
     * @param response
     * @throws IOException
     */
    private void deleteOneAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String aid = request.getParameter("aid");
        int result = adminService.deleteOneAdmin(aid);
        switch (result) {
            case 0:
                response.getWriter().println("<script>alert('服务器开小差了！');</script>");
                break;
            case 1:
                response.getWriter().println("<script>alert('删除管理员成功！');</script>");
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/adminServlet?operation=findAllAdmin");
                break;
            default:
        }
    }

    private void findAllAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        List<Admin> adminList = adminService.findAllAdmin();
        if (adminList == null) {
            response.getWriter().println("<script>alert('服务器开小差了！');</script>");
            return;
        } else {
            System.out.println("adminList = " + adminList);
            // 放入request域
            request.setAttribute("admins", adminList);
            request.getRequestDispatcher("/admin/admin/adminList.jsp").forward(request, response);
        }
    }

    private void addAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        /*校验*/
        // 非空校验
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            response.getWriter().println("<script>alert('账号或密码不能为空！')</script>");
            return;
        }
        // 确认密码校验
        if (!confirmPassword.equals(password)) {
            response.getWriter().println("<script>alert('两次输入密码不同！')</script>");
            return;
        }
        // 封装对象
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        // 传入对象
        int result = adminService.addAdmin(admin);
        switch (result) {
            case 0:
                response.getWriter().println("<script>alert('服务器开小差了！');</script>");
                break;
            case 1:
                response.getWriter().println("<script>alert('添加管理员成功！');</script>");
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/adminServlet?operation=findAllAdmin");
                break;
            default:
                response.getWriter().println("<script>alert('switch default服务器开小差了！');</script>");
        }
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println(request.getSession().getAttribute("currentAdmin") + "安全退出！");
        request.getSession().removeAttribute("currentAdmin");
        response.getWriter().println("<script>alert('安全退出成功！');</script>");
//        response.sendRedirect(request.getContextPath() + "/admin/index.jsp");
        response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/index.jsp");
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);

        Admin currentAdmin = adminService.findLoginAdmin(admin);
        if (currentAdmin == null) {
            response.getWriter().println("<script>alert('用户名或密码错误');</script>");
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/index.jsp");
            return;
        } else {
            System.out.println("currentAdmin = " + currentAdmin);
            // currentAdmin存入session域
            request.getSession().setAttribute("currentAdmin", currentAdmin);
            // TO EX 这个路径..
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/main.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}


