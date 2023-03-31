package com.chd.common.util;


//import sun.misc.BASE64Decoder;

import org.apache.commons.codec.binary.Base64;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
//import java.util.Base64;

public final class Base64ImageUtil {


    private static BufferedImage rotate(Image src, int angel) {
        int src_width = src.getWidth(null);
        int src_height = src.getHeight(null);
        // calculate the new image size
        Rectangle rect_des = CalcRotatedSize(new Rectangle(new Dimension(
                src_width, src_height)), angel);

        BufferedImage res = null;
        res = new BufferedImage(rect_des.width, rect_des.height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = res.createGraphics();
        res = g2.getDeviceConfiguration().createCompatibleImage(rect_des.width, rect_des.height, Transparency.TRANSLUCENT);
        g2.dispose();
        g2 = res.createGraphics();
        // transform
        g2.translate((rect_des.width - src_width) / 2,
                (rect_des.height - src_height) / 2);
        g2.rotate(Math.toRadians(angel), src_width / 2, src_height / 2);
        g2.setColor(new Color(255, 0, 0));
        g2.setStroke(new BasicStroke(1));
        g2.drawImage(src, null, null);
        g2.dispose();
        return res;
    }

    public static Rectangle CalcRotatedSize(Rectangle src, int angel) {
        // if angel is greater than 90 degree, we need to do some conversion
        if (angel >= 90) {
            if (angel / 90 % 2 == 1) {
                int temp = src.height;
                src.height = src.width;
                src.width = temp;
            }
            angel = angel % 90;
        }

        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
        double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
        double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
        double angel_dalta_width = Math.atan((double) src.height / src.width);
        double angel_dalta_height = Math.atan((double) src.width / src.height);

        int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha
                - angel_dalta_width));
        int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha
                - angel_dalta_height));
        int des_width = src.width + len_dalta_width * 2;
        int des_height = src.height + len_dalta_height * 2;
        return new Rectangle(new Dimension(des_width, des_height));
    }

    /**
     * 顺时针旋转
     *
     * @param base64FileStr
     * @param rotate
     * @return
     * @throws IOException
     */
    public static String rotateImage(String base64FileStr, int rotate) throws IOException {
        if (StringUtil.isEmpty(base64FileStr)) {
            return "";
        }




//        Base64.Decoder decoder = Base64.getDecoder();
//        // Base64解码,对字节数组字符串进行Base64解码并生成文件
//        final byte[] byt = decoder.decode(base64FileStr);

        byte[] byt = Base64.decodeBase64(base64FileStr);
        for (int i = 0, len = byt.length; i < len; ++i) {
            // 调整异常数据
            if (byt[i] < 0) {
                byt[i] += 256;
            }
        }
        InputStream input = new ByteArrayInputStream(byt);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedImage src = ImageIO.read(input);
        BufferedImage des = rotate(src, rotate);
        ImageIO.write(des, "png", out);
//        final Base64.Encoder encoder = Base64.getEncoder();
        final byte[] bytes = out.toByteArray();

//        return encoder.encodeToString(bytes);
        return Base64.encodeBase64String(bytes);
    }


    public static boolean GenerateImage(String base64str,String savepath)
    {   //对字节数组字符串进行Base64解码并生成图片
        if (base64str == null) //图像数据为空
            return false;
        // System.out.println("开始解码");
        if(base64str.startsWith("data:image/png;base64,"))base64str = base64str.replace("data:image/png;base64,","");
        if(base64str.startsWith("data:image/jpeg;base64,"))base64str = base64str.replace("data:image/jpeg;base64,","");
//        BASE64Decoder decoder = new BASE64Decoder();
        try
        {
            //Base64解码
//            byte[] b = decoder.decodeBuffer(base64str);
            byte[] b = Base64.decodeBase64(base64str);

            //  System.out.println("解码完成");
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0)
                {//调整异常数据
                    b[i]+=256;
                }
            }
            // System.out.println("开始生成图片");
            //生成jpeg图片
            OutputStream out = new FileOutputStream(savepath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }



    public static void main(String[] args) throws IOException {
//        String base64FileStr = "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";
//        final String image = rotateImage(base64FileStr, 90);
//        System.out.println("data:image/png;base64,"+image);
        String base64FileStr = "data:image/png;base64,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";
        GenerateImage(base64FileStr,"/Users/xin/code/patrolInspection/uploads/overseeUserSignature/5.png");
    }
}

