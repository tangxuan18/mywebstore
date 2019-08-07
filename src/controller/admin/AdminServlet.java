package controller.admin;

import bean.Admin;
import service.AdminService;
import service.AdminServiceImpl;
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
        }
    }

    private void deleteMultiAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] aidArray = request.getParameterValues("aid");
        System.out.println(aidArray);
        if (aidArray == null) {
            response.getWriter().println("<script>alert('��ѡ�����Ա��');</script>");
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/categoryServlet?operation=findAllCategory");
            return;
        }
        for (String aid : aidArray) {
            int result = adminService.deleteOneAdmin(aid);
            switch (result) {
                case 0:
                    response.getWriter().println("<script>alert('��������С���ˣ�');</script>");
                    break;
            }
        }
        response.getWriter().println("<script>alert('ɾ������Ա�ɹ���');</script>");
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
                response.getWriter().println("<script>alert('��������С���ˣ�');</script>");
                break;
            case 1:
                response.getWriter().println("<script>alert('�޸Ĺ���Ա�ɹ���');</script>");
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/adminServlet?operation=findAllAdmin");
                break;
        }
    }

    /**
     * ����aidΪString����Ҳ�У�����
     * @param request
     * @param response
     * @throws IOException
     */
    private void deleteOneAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String aid = request.getParameter("aid");
        int result = adminService.deleteOneAdmin(aid);
        switch (result) {
            case 0:
                response.getWriter().println("<script>alert('��������С���ˣ�');</script>");
                break;
            case 1:
                response.getWriter().println("<script>alert('ɾ������Ա�ɹ���');</script>");
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/adminServlet?operation=findAllAdmin");
                break;
        }
    }

    private void findAllAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        List<Admin> adminList = adminService.findAllAdmin();
        if (adminList == null) {
            response.getWriter().println("<script>alert('��������С���ˣ�');</script>");
            return;
        } else {
            System.out.println("adminList = " + adminList);
            request.setAttribute("admins", adminList);
            request.getRequestDispatcher("/admin/admin/adminList.jsp").forward(request, response);
        }
    }

    private void addAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        /*У��*/
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) { // �ǿ�У��
            response.getWriter().println("<script>alert('�˺Ż����벻��Ϊ�գ�')</script>");
            return;
        }
        if (!confirmPassword.equals(password)) { // ȷ������У��
            response.getWriter().println("<script>alert('�����������벻ͬ��')</script>");
            return;
        }

        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        int result = adminService.addAdmin(admin);
        switch (result) {
            case 0:
                response.getWriter().println("<script>alert('��������С���ˣ�');</script>");
                break;
            case 1:
                response.getWriter().println("<script>alert('��ӹ���Ա�ɹ���');</script>");
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/adminServlet?operation=findAllAdmin");
                break;
        }
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println(request.getSession().getAttribute("currentAdmin") + "��ȫ�˳���");
        request.getSession().removeAttribute("currentAdmin");
        response.getWriter().println("<script>alert('��ȫ�˳��ɹ���');</script>");
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
            response.getWriter().println("<script>alert('�û������������');</script>");
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/index.jsp");
            return;
        } else {
            System.out.println("currentAdmin = " + currentAdmin);
            // currentAdmin����session��
            request.getSession().setAttribute("currentAdmin", currentAdmin);
            // TO EX ���·��..
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/admin/main.jsp");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}


