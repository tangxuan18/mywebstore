package controller;

import bean.Page;
import bean.User;
import org.apache.commons.beanutils.BeanUtils;
import service.UserService;
import service.UserServiceImpl;
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        Map<String, String> map = MyFileUploadUtils.parseRequest(request);
//        String op = map.get("op");
        String op = request.getParameter("op");
        if (StringUtils.isEmpty(op)) {
            response.getWriter().println("<script>alert('op参数为空！');</script>");
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
            case "uploadPersonalInfo":
                uploadPersonalInfo(request, response);
                break;
        }
    }

    private void uploadPersonalInfo(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> map = MyFileUploadUtils.parseRequest(request);
    }

    private void register(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String[]> parameterMap = request.getParameterMap();
        User user = new User();
        try {
            BeanUtils.populate(user, parameterMap);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('参数封装失败！');</script>");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('参数封装失败！');</script>");
        }
        /*在后端给对象属性赋值*/
        user.setRegistTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        user.setActivationStatus("N");
        /*发送验证邮件*/
        String activationCode = UUID.randomUUID().toString();
        user.setActivationCode(activationCode);
        String activationMassage = "<a href='http://192.168.8.66/WebBookStore/userServlet?op=activateEmail&uuid=" + activationCode + "'>点我激活webStore账号</a>";
        MailUtils.sendMail(user.getEmail(), activationMassage);

        int result = userService.register(user); // 传入对象
        switch (result) {
            case 0:
                response.getWriter().println("<script>alert('服务器开小差了');</script>");
                break;
            case 1:
                response.getWriter().println("<script>alert('注册成功，请继续验证邮箱！');</script>");
                response.setHeader("refresh", "0, url=" + request.getContextPath() + "/index.jsp");
                break;
        }
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        System.out.println(request.getSession().getAttribute("user") + "安全退出！");
/*        if(request.getSession().getAttribute("user") == null){
            response.getWriter().println("<script>alert('账号过期，请重新登录！');</script>");
            return;
        }*/
        request.getSession().removeAttribute("user");
        response.getWriter().println("<script>alert('安全退出成功！');</script>");
        response.setHeader("refresh", "0, url=" + request.getContextPath() + "/index.jsp");
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        /*检查验证码*/
        String checkcode_session = (String) request.getSession().getAttribute("checkcode_session");
        String verifyCode = request.getParameter("verifyCode");
        if (!checkcode_session.equals(verifyCode)) {
            response.getWriter().println("<script>alert('验证码错误');</script>");
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
            response.getWriter().println("<script>alert('用户名或密码错误');</script>");
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/index.jsp");
            return;
        } else {
            System.out.println("currentUser = " + currentUser);
            // currentUser存入session域
            request.getSession().setAttribute("user", currentUser);
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/index.jsp");
        }
    }

    private void addUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String nickname = request.getParameter("nickname");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        Date birthday = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            birthday = sdf.parse(request.getParameter("birthday"));
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('参数类型错误！');</script>");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String op = request.getParameter("op");
        if (StringUtils.isEmpty(op)) {
            response.getWriter().println("<script>alert('op参数为空！');</script>");
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
        }
    }

    private void findPageUsers(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int currentPageNum = 0;
        try {
            currentPageNum = Integer.parseInt(request.getParameter("num"));
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('num参数类型错误！');</script>");
        }
        Page currentPage = userService.listPageUsers(currentPageNum);
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
            request.getRequestDispatcher("/admin/user/userList.jsp").forward(request, response);
        }
    }

    private void activateEmail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String activationCode = request.getParameter("uuid");
        if (StringUtils.isEmpty(activationCode)) {
            response.getWriter().println("<script>alert('uuid参数为空！');</script>");
            return;
        }
        User user = userService.getUserByActivationCode(activationCode);
        if (user == null) {
            response.getWriter().println("<script>alert('邮箱验证码错误');</script>");
            response.setHeader("refresh", "0, url=" + request.getContextPath() + "/index.jsp");
            return;
        }
        int result = userService.updateActivationStatus(user);
        switch (result) {
            case 0:
                response.getWriter().println("<script>alert('更改激活状态失败');</script>");
                break;
            case 1:
                response.sendRedirect(request.getContextPath() + "/user/activateEmailSuccess.jsp");
                break;
        }
    }
}
