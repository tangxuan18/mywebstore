package utils;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class MyFileUploadUtils {
    /**
     * 如果是普通字段，键为字段名，值为字段值
     * 如果是文件，键为字段名，值为文件的url
     * 解析request
     * 封装upload.parseRequest(request),以便多文件复用
     * @param request
     * @return 返回一个Map
     */
    public static Map<String, String> parseRequest(HttpServletRequest request) {
        DiskFileItemFactory factory = new DiskFileItemFactory();

        /*Configure a repository (to ensure a secure temp location is used)*/
        ServletContext servletContext = request.getServletContext();
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(repository);

        // 创建 upload 对象
        ServletFileUpload upload = new ServletFileUpload(factory);
        // 设置单独每个文件上传的最大大小
        upload.setFileSizeMax(1024 * 1024 * 1024);
        // 设置overall request size
        upload.setSizeMax(5 * 1024 * 1024 * 1024);

        /*解析request*/
        Map<String, String> resultMap = new LinkedHashMap<>();
        try {
            // Processes an RFC 1867 compliant multipart/form-data stream
            List<FileItem> items = upload.parseRequest(request);
            // 获取迭代器对象
            Iterator<FileItem> iter = items.iterator();
            /*迭代遍历List<FileItem>，逐个封装进Map*/
            while (iter.hasNext()) {
                FileItem item = iter.next();
                if (item.isFormField()) {
                    //上传普通form表单
                    processFormField(item, resultMap);
                } else {
                    //上传文件
                    processUploadedFile(item, resultMap, request);
                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    /**
     * 处理文件
     * @param item
     * @param resultMap
     * @param request
     */
    private static void processUploadedFile(FileItem item, Map<String, String> resultMap, HttpServletRequest request) {
        String fieldName = item.getFieldName(); // Returns the name of the field in the multipart form corresponding to this file item.
        String fileName = item.getName(); // Returns the name of the field in the multipart form corresponding to this file item.
        System.out.println("upload file fieldName: " + fieldName);
        System.out.println("upload file fileName: " + fileName);
        // 设置上传文件夹目录结构，利用文件的hashcode形成目录结构
        String uuid = UUID.randomUUID().toString();
        fileName = uuid + fileName;
        int hashCode = fileName.hashCode(); //4 字节 8个字符
        String hexString = Integer.toHexString(hashCode);
        System.out.println("dir: " + hexString);
        char[] chars = hexString.toCharArray();
        String uploadPath = "upload";
        for (char c : chars) {
            uploadPath = uploadPath + "/" + c;
        }
        String realPath = request.getServletContext().getRealPath(uploadPath + "/" + fileName);
        File file = new File(realPath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            item.write(file); // A convenience method to write an uploaded item to disk.
            // 加入map
            resultMap.put(fieldName, uploadPath + "/" + fileName); // 将来服务器读资源，读相对路径
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理普通表单
     * @param item
     * @param resultMap
     */
    private static void processFormField(FileItem item, Map<String, String> resultMap) {
        String fieldName = item.getFieldName();
        String string = null;
        try {
            string = item.getString("utf-8"); // Returns the contents of the file item as a String, using the specified encoding.
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 加入map
        resultMap.put(fieldName, string);
    }
}
