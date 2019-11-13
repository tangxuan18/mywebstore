package controller.user;

import bean.Page;
import bean.User;
import org.apache.commons.beanutils.BeanUtils;
import service.UserService;
import service.impl.UserServiceImpl;
import utils.MailUtils;
import utils.MyFileUploadUtils;
import utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@WebServlet("/userServlet")
public class UserServlet extends HttpServlet {

    private UserService userService = new UserServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        Map<String, String> map = MyFileUploadUtils.parseRequest(request);
//        String op = map.get("op");
        String op = request.getParameter("op");
        if (StringUtils.isEmpty(op)) {
            response.getWriter().println("<script>alert('op����Ϊ�գ�');</script>");
            return;
        }
        switch (op) {
            // TO DO
            case "addUser":
                addUser(request, response);
                break;
            case "login":
                login(request, response);
                break;
            case "regist":
                register(request, response);
                break;
/*            case "uploadPersonalInfo":
                uploadPersonalInfo(request, response);
                break;*/
            default:
                throw new IllegalStateException("Unexpected value: " + op);
        }
    }


    private void register(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String[]> parameterMap = request.getParameterMap();
        User user = new User();
        try {
            BeanUtils.populate(user, parameterMap);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('������װʧ�ܣ�');</script>");
        }
        user.setRegistTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        user.setActivationStatus("N");
        /*������֤�ʼ�*/
        String activationCode = UUID.randomUUID().toString();
        user.setActivationCode(activationCode);
        // �������䷽��
//        String activationMassage = "<a href='http://192.168.8.66/WebBookStore/userServlet?op=activateEmail&uuid=" + activationCode + "'>���Ҽ���webStore�˺�</a>";
        String serverIp = "127.0.0.1";
        String activationMassageStr = "<a href='http://" + serverIp + request.getContextPath()+ "/userServlet?op=activateEmail&uuid=" + activationCode + "'>���Ҽ���webStore�˺�</a>";
        MailUtils.sendMail(user.getEmail(), activationMassageStr);
        int result = userService.register(user);
        switch (result) {
            case 0:
                response.getWriter().println("<script>alert('��������С����');</script>");
                break;
            case 1:
                response.getWriter().println("<script>alert('ע��ɹ����������֤���䣡');</script>");
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/index.jsp");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + result);
        }
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        System.out.println(request.getSession().getAttribute("user") + "��ȫ�˳���");
/*        if(request.getSession().getAttribute("user") == null){
            response.getWriter().println("<script>alert('�˺Ź��ڣ������µ�¼��');</script>");
            return;
        }*/
        request.getSession().removeAttribute("user");
        response.getWriter().println("<script>alert('��ȫ�˳��ɹ���');</script>");
        response.setHeader("refresh", "0, url=" + request.getContextPath() + "/index.jsp");
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        /*�����֤��*/
        String checkcodeSession = (String) request.getSession().getAttribute("checkcode_session");
        String verifyCode = request.getParameter("verifyCode");
        if (!checkcodeSession.equals(verifyCode)) {
            response.getWriter().println("<script>alert('��֤�����');</script>");
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/user/login.jsp");
            return;
        }
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        User currentUser = userService.getLoginUser(user);
        if (currentUser == null) {
            response.getWriter().println("<script>alert('�û������������');</script>");
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/index.jsp");
            return;
        } else {
            System.out.println("currentUser = " + currentUser);
            // currentUser����session��
            request.getSession().setAttribute("user", currentUser);
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/index.jsp");
        }
    }

    private void addUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
/*        String username = request.getParameter("username");
        String nickname = request.getParameter("nickname");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String birthday = request.getParameter("birthday");*/
        Map<String, String[]> parameterMap = request.getParameterMap();
        User user = new User();
        try {
            BeanUtils.populate(user, parameterMap);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('BeanUtils������װʧ�ܣ�');</script>");
        }
        user.setRegistTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        user.setActivationCode("vip��̨ע��");
        // ������ע��
        user.setActivationStatus("Y");
        int result = userService.addUserBackground(user);
        switch (result) {
            case 0:
                response.getWriter().println("<script>alert('��������С����');</script>");
                break;
            case 1:
                response.getWriter().println("<script>alert('��̨�����ͨ�û��ɹ���');</script>");
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "userServlet?op=findPageUsers&num=1");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + result);
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
            case "logout":
                logout(request, response);
                break;
            case "activateEmail":
                activateEmail(request, response);
                break;
            case "findPageUsers":
                findPageUsers(request, response);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + op);
        }
    }

    private void findPageUsers(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int currentPageNum = 0;
        try {
            currentPageNum = Integer.parseInt(request.getParameter("num"));
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('num�������ʹ���');</script>");
        }
        Page currentPage = userService.listPageUsers(currentPageNum);
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
            request.getRequestDispatcher("/admin/user/userList.jsp").forward(request, response);
        }
    }

    private void activateEmail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String activationCode = request.getParameter("uuid");
        if (StringUtils.isEmpty(activationCode)) {
            response.getWriter().println("<script>alert('uuid����Ϊ�գ�');</script>");
            return;
        }
        User user = userService.getUserByActivationCode(activationCode);
        if (user == null) {
            response.getWriter().println("<script>alert('������֤�����');</script>");
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/index.jsp");
            return;
        }
        int result = userService.updateActivationStatus(user);
        switch (result) {
            case 0:
                response.getWriter().println("<script>alert('���ļ���״̬ʧ��');</script>");
                break;
            case 1:
                response.sendRedirect(request.getContextPath() + "/user/activateEmailSuccess.jsp");
                break;
        }
    }
}
