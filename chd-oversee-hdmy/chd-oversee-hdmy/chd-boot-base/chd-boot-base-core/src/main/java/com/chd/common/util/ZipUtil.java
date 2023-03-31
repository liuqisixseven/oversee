package com.chd.common.util;

import lombok.Data;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author yh128
 * @version 1.0.0
 * @ClassName ZipUtil.java
 * @Description 压缩打包
 * @Param
 * @createTime 2019年12月06日 17:43:00
 */
public class ZipUtil {

    /**
     * WritableByteChannel、FileChannel主要针对的是文件和文件流
     *
     * @param zipFile
     * @return
     * @throws Exception
     */
    public static OutputStream zipFileByChannel(ZipFile zipFile) throws Exception {
        if (zipFile == null) {
            throw new Exception("实体类为空");
        }
        List<File> files = zipFile.getFiles();
        List<FileInputStream> fileInputStreams = zipFile.getFileInputStreams();
        List<String> fileNames = zipFile.getFileNames();

        if (files != null && files.size() > 0) {
            fileInputStreams = new ArrayList<>();
            boolean isGetFileName = false;
            if (fileNames == null || fileNames.size() < 1) {
                fileNames = new ArrayList<>();
                isGetFileName = true;
            }
            for (File file : files) {
                fileInputStreams.add(new FileInputStream(file));
                String name = file.getName();
                if (isGetFileName) {
                    fileNames.add(name);
                }
            }
        }
        if (fileInputStreams == null || fileInputStreams.size() < 1) {
            throw new Exception("文件为空");
        }
        ZipOutputStream zipOutputStream = zipFile.getZipOutputStream();
        WritableByteChannel writableByteChannel = Channels.newChannel(zipOutputStream);
        try {
            for (int i = 0; i < fileInputStreams.size(); i++) {
                FileInputStream fileInputStream = fileInputStreams.get(i);
                FileChannel channel = fileInputStream.getChannel();
                try {
                    zipOutputStream.putNextEntry(new ZipEntry(fileNames.get(i)));
                    channel.transferTo(0, channel.size(), writableByteChannel);
                } finally {
                    channel.close();
                    fileInputStream.close();
                }
            }
        } finally {
            writableByteChannel.close();
            zipOutputStream.close();
        }
        return zipOutputStream;
    }


    /**
     * 使用ReadableByteChannel、WritableByteChannel拷贝文件到压缩包
     *
     * @param zipFile
     * @return
     * @throws Exception
     */
    public static OutputStream zipFileByWRChannel(ZipFile zipFile) throws Exception {
        if (zipFile == null) {
            throw new Exception("实体类为空");
        }
        List<InputStream> inputStreams = zipFile.getInputStreams();
        List<String> fileNames = zipFile.getFileNames();

        if (inputStreams == null || inputStreams.size() < 1 || fileNames == null || fileNames.size() < 1 || fileNames.size() < inputStreams.size()) {
            throw new Exception("无数据");
        }
        ZipOutputStream zipOutputStream = zipFile.getZipOutputStream();
        WritableByteChannel writableByteChannel;
        writableByteChannel = Channels.newChannel(zipOutputStream);
        try {
            for (int i = 0; i < inputStreams.size(); i++) {
                InputStream inputStream = inputStreams.get(i);
                ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);
                try {
                    zipOutputStream.putNextEntry(new ZipEntry(fileNames.get(i)));
                    ByteBuffer byteBuffer = ByteBuffer.allocate(9999);
                    while (readableByteChannel.read(byteBuffer) != -1) {
                        byteBuffer.flip();
                        while (byteBuffer.hasRemaining()) {
                            writableByteChannel.write(byteBuffer);
                        }
                        byteBuffer.clear();
                    }
                } finally {
                    readableByteChannel.close();
                    inputStream.close();
                }
            }
        } finally {
            writableByteChannel.close();
            zipOutputStream.close();
        }
        return zipOutputStream;
    }

    /**
     * 使用Buffered
     *
     * @param zipFile
     * @return
     * @throws Exception
     */
    public static OutputStream zipFileByBuffered(ZipFile zipFile) throws Exception {
        if (zipFile == null) {
            throw new Exception("实体类为空");
        }
        List<InputStream> inputStreams = zipFile.getInputStreams();
        List<String> fileNames = zipFile.getFileNames();
        if (inputStreams == null || inputStreams.size() < 1 || fileNames == null || fileNames.size() < 1 || fileNames.size() < inputStreams.size()) {
            throw new Exception("无数据");
        }
        ZipOutputStream zipOutputStream = zipFile.getZipOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(zipOutputStream);
        try {
            for (int i = 0; i < inputStreams.size(); i++) {
                InputStream inputStream = inputStreams.get(i);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                try {
                    zipOutputStream.putNextEntry(new ZipEntry(fileNames.get(i)));
                    byte[] bytes = new byte[1024];
                    while (bufferedInputStream.read(bytes) != -1) {
                        bufferedOutputStream.write(bytes);
                    }
                } finally {
                    bufferedInputStream.close();
                    inputStream.close();
                }
            }
        } finally {
            bufferedOutputStream.close();
            zipOutputStream.close();
        }
        return zipOutputStream;
    }

    /**
     * 对于在线图片的操作
     *
     * @param zipFile
     * @return
     * @throws Exception
     */
    public static OutputStream zipFileByImage(ZipFile zipFile) throws Exception {
        if (zipFile == null) {
            throw new Exception("实体类为空");
        }
        List<String> urls = zipFile.getUrls();
        List<String> fileNames = zipFile.getFileNames();
        if (urls == null || urls.size() < 1 || fileNames == null || fileNames.size() < 1 || fileNames.size() < urls.size()) {
            throw new Exception("图片路径为空|图片名称为空|图片名称不够");
        }
        ZipOutputStream zipOutputStream = zipFile.getZipOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(zipOutputStream);
        try {
            for (int i = 0; i < urls.size(); i++) {
                zipOutputStream.putNextEntry(new ZipEntry(fileNames.get(i)));
                BufferedImage read = ImageIO.read(new URL(urls.get(i)));
                ImageIO.write(read, "jpg", bufferedOutputStream);
            }
        } finally {
            bufferedOutputStream.close();
            zipOutputStream.close();
        }
        return zipOutputStream;
    }


    @Data
    public static class ZipFile {
        @NotNull
        private String zipFile;
        @NotNull
        private ZipOutputStream zipOutputStream;
        private List<File> files;
        private List<FileInputStream> fileInputStreams;
        private List<InputStream> inputStreams;
        private List<String> fileNames;
        private List<String> urls;
    }

    /**
     * 根据链接获取流
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static InputStream readInputStream(String path) throws Exception {
        if (path.indexOf("http") < 0) {
            return new FileInputStream(path);
        } else {
            URL url = new URL(path);
            // 打开链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置请求方式为"GET"
            conn.setRequestMethod("GET");
            // 超时响应时间为5秒
            conn.setConnectTimeout(5 * 1000);
            // 通过输入流获取图片数据
            return conn.getInputStream();
        }
    }

    public static OutputStream zipFiles(List<String> files, File file, HttpServletResponse response) throws Exception {
        List<String> nameList = new ArrayList<>();
        List<InputStream> inputStreams = new ArrayList<>();
        files.forEach(a -> {
            String[] str = a.split("/|\\\\");
            nameList.add(str[str.length - 1]);
            try {
                inputStreams.add(readInputStream(a));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ZipFile zipFile = new ZipFile();
        zipFile.setFileNames(nameList);
        zipFile.setInputStreams(inputStreams);
        // 这里面的OutputStream可以使用HttpServletResponse中的OutputStream
        zipFile.setZipOutputStream(new ZipOutputStream(new FileOutputStream(file)));

        return zipFileByWRChannel(zipFile);
    }

    public static void main(String[] args) throws Exception {

    }

}