
package utils;

public class StringUtils {


    /**
     * 判断当前请求的资源是否是静态资源
     * */
    public static boolean isStaticResource(String requestURI) {
        if(requestURI.endsWith("js") || requestURI.endsWith("css")
                || requestURI.endsWith("jpg") || requestURI.endsWith("ico")){
            return  true;
        }
        return false;
    }

    /**
     * 判断当前请求的资源是否是图片
     * */
    public static boolean isPicture(String requestURI) {
        if(requestURI.endsWith(".png") || requestURI.endsWith(".jpg") ||
                requestURI.endsWith(".gif") || requestURI.endsWith(".ico")){
            return  true;
        }
        return false;
    }


    public static boolean isEmpty(String string) {
        if(string == null || "".equals(string.trim())){
            return true;
        }
        return false;
    }



    public final static  boolean judgeStaticResource(String string) {
        if (string.endsWith(".jsp") || !string.contains("\\.")) {
            return false;
        } else {
            return true;
        }
    }
}
