package com.chd.common.util;

import com.chd.common.exception.BizException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.HtmlUtils;

/**
 * HTML 工具类
 *
 * @date: 2022/3/30 14:43
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class HTMLUtils {

    /**
     * 获取HTML内的文本，不包含标签
     *
     * @param html HTML 代码
     */
    public static String getInnerText(String html) {
        if (StringUtils.isNotBlank(html)) {
            //去掉 html 的标签
            String content = html.replaceAll("</?[^>]+>", "");
            // 将多个空格合并成一个空格
            content = content.replaceAll("(&nbsp;)+", "&nbsp;");
            // 反向转义字符
            content = HtmlUtils.htmlUnescape(content);
            return content.trim();
        }
        return "";
    }

    public static String getTitle(String htmlContent){
        String title = null;
        if(StringUtils.isNotEmpty(htmlContent)){
            title= HTMLUtils.getInnerText(htmlContent);
        }
        if(StringUtils.isBlank(title)){
            throw new BizException("具体问题内容不能为空");
        }
        if(StringUtils.isNotBlank(title) && title.length()>200){
            title=title.substring(0,200)+"...";
        }
        return title;
    }


}
