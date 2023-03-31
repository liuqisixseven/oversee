package com.chd.common.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件处理
 */
public class FileUtils {


    public static final String[] DEFAULT_ALLOWED_EXTENSION = {
            // 图片
            "bmp", "gif", "jpg", "jpeg", "png",
            // word excel powerpoint
            "doc", "docx", "xls", "xlsx", "ppt", "pptx", "html", "htm", "txt",
            // 压缩文件
            "rar", "zip", "gz", "bz2",
            // 视频格式
            "mp4", "avi", "rmvb",
            // pdf
            "pdf" };

    /**
     * 检查文档是否可以下载
     * @param resource
     * @return
     */
    public static boolean checkAllowDownload(String resource)
    {
        // 禁止目录上跳级别
        if (StringUtils.contains(resource, ".."))
        {
            return false;
        }

        // 检查允许下载的文件规则
        if (ArrayUtils.contains(DEFAULT_ALLOWED_EXTENSION, FileTypeUtils.getFileType(resource)))
        {
            return true;
        }

        // 不在允许下载的文件规则
        return false;
    }

    /**
     * 百分号编码工具方法
     *
     * @param s 需要百分号编码的字符串
     * @return 百分号编码后的字符串
     */
    public static String percentEncode(String s) throws UnsupportedEncodingException
    {
        String encode = URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        return encode.replaceAll("\\+", "%20");
    }

    /**
     * 下载文件名重新编码
     *
     * @param response 响应对象
     * @param realFileName 真实文件名
     * @return
     */
    public static void setAttachmentResponseHeader(HttpServletResponse response, String realFileName) throws UnsupportedEncodingException
    {
        String percentEncodedFileName = percentEncode(realFileName);

        StringBuilder contentDispositionValue = new StringBuilder();
        contentDispositionValue.append("attachment; filename=")
                .append(percentEncodedFileName)
                .append(";")
                .append("filename*=")
                .append("utf-8''")
                .append(percentEncodedFileName);

        response.setHeader("Content-disposition", contentDispositionValue.toString());
    }


    public static void writeBytes(String filePath,HttpServletResponse response,String contentType) throws IOException {

        FileInputStream fis = null;
        File file = new File(filePath);
        OutputStream os = null;
        try{
            if( !file.exists()){
                throw new FileNotFoundException(file.getName()+"文件不存在");
            }
            response.setContentType(contentType);
            os = response.getOutputStream();
            fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int length;
            while(( length = fis.read(b) )>0){
                os.write(b,0,length);
            }
        }catch(IOException e){
            System.out.println("读取文件异常："+file.getAbsolutePath());
            throw e;
        }
        finally{
            if(os!=null){
                try{
                    os.close();
                }catch(IOException e1){
                    e1.printStackTrace();
                }
            }
            if(fis!=null){
                try{
                    fis.close();
                }catch(IOException e1){
                    e1.printStackTrace();
                }
            }
        }

    }
}
