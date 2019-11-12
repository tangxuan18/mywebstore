package controller.user;

import bean.User;
import org.apache.commons.beanutils.BeanUtils;
import service.UserService;
import service.impl.UserServiceImpl;
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

/**
 * @author GFS
 */
@WebServlet("/uploadUserInfoServlet")
public class UploadUserInfoServlet extends HttpServlet {

    private UserService userService = new UserServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ��MyFileUploadUtils�������в�������װ��map�У�����ͼƬ
        Map<String, String> map = MyFileUploadUtils.parseRequest(request);
//        System.out.println("request = " + request);
        String op = map.get("op");
        if (StringUtils.isEmpty(op)) {
            response.getWriter().println("<script>alert('op����Ϊ��post��');</script>");
            return;
        }
        switch (op) {
            case "uploadUserInfo":
                // ��map����ȥ
                uploadUserInfo(request, response, map);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + op);
        }
    }

    /**
     *
     * @param request
     * @param response
     * @param map �����ļ���map
     * @throws IOException
     */
    private void uploadUserInfo(HttpServletRequest request, HttpServletResponse response, Map<String, String> map) throws IOException {
        User user = new User();
        try {
            BeanUtils.populate(user, map);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        int result = userService.updateUser(user);
        switch (result) {
            case 0:
                response.getWriter().println("<script>alert('�����û���Ϣʧ��');</script>");
                break;
            case 1:
                response.getWriter().println("<script>alert('�����û���Ϣ�ɹ�');</script>");
                response.setHeader("refresh", "0, url = " + request.getContextPath() + "/index.jsp");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + result);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}
