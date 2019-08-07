package controller;

import bean.User;
import org.apache.commons.beanutils.BeanUtils;
import service.UserService;
import service.UserServiceImpl;
import utils.MyFileUploadUtils;
import utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@WebServlet("/uploadUserInfoServlet")
public class UploadUserInfoServlet extends HttpServlet {

    private UserService userService = new UserServiceImpl();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // �������в�������װ��map��
        Map<String, String> map = MyFileUploadUtils.parseRequest(request);
//        System.out.println("request = " + request);
        String op = map.get("op");
        if (StringUtils.isEmpty(op)) {
            response.getWriter().println("<script>alert('op����Ϊ��post��');</script>"); // У��ǰ�˴������
            return;
        }
        switch (op) {
            case "uploadUserInfo":
                uploadUserInfo(request, response, map);
                break;
        }
    }

    private void uploadUserInfo(HttpServletRequest request, HttpServletResponse response, Map<String, String> map) throws IOException {
//        System.out.println("request = " + request);
        User user = new User();
        try {
            BeanUtils.populate(user, map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        int result = userService.updateUser(user);
        switch (result) {
            case 0:
                response.getWriter().println("<script>alert('�����û���Ϣʧ��');</script>");
                break;
            case 1:
                response.getWriter().println("<script>alert('�����û���Ϣ�ɹ����´ε�¼��Ч');</script>");
                response.setHeader("refresh", "0, url = " + request.getContextPath() + "/index.jsp");
                break;
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}
