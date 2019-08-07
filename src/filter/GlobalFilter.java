package filter;

import utils.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class GlobalFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        /*设置全局的字符编码格式*/
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String requestURI = request.getRequestURI();
//      System.out.println("global filter:" + requestURI);
        request.setCharacterEncoding("utf-8");

        //设置content-type这个写法有个坑，如果是图片，设置了该属性，则无法显示图片。
        //解决方案是什么？？  requestURI endsWith  css js jpg png
        //写了一个工具类，用于判断当前的请求是否请求的是静态资源
        //如果是静态资源，则不设置content-type属性
        boolean isStatic = StringUtils.isStaticResource(requestURI);
        if(!isStatic){
            response.setContentType("text/html; charset=utf-8");
        }

        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
