package com.chd.common.core.dict;


import java.util.List;

import com.chd.common.util.SpringContextUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 字典工具类
 */
public class DictUtils
{
    /**
     * 分隔符
     */
    public static final String SEPARATOR = ",";



    /**
     * 获取字典缓存
     *
     * @param key 参数键
     * @return dictDatas 字典数据列表
     */
    public static List<IDictItem> getDictCache(String key)
    {
        List<IDictItem> cacheObj = SpringContextUtils.getBean(IDictCache.class).getDictItems(key);
        return cacheObj;
    }

    /**
     * 根据字典类型和字典值获取字典标签
     *
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @return 字典标签
     */
    public static String getDictLabel(String dictType, String dictValue)
    {
        return getDictLabel(dictType, dictValue, SEPARATOR);
    }

    /**
     * 根据字典类型和字典标签获取字典值
     *
     * @param dictType 字典类型
     * @param dictLabel 字典标签
     * @return 字典值
     */
    public static String getDictValue(String dictType, String dictLabel)
    {
        return getDictValue(dictType, dictLabel, SEPARATOR);
    }

    /**
     * 根据字典类型和字典值获取字典标签
     *
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @param separator 分隔符
     * @return 字典标签
     */
    public static String getDictLabel(String dictType, String dictValue, String separator)
    {
        StringBuilder propertyString = new StringBuilder();
        List<IDictItem> datas = getDictCache(dictType);
        if(CollectionUtils.isEmpty(datas)){
            return null;
        }

        if (StringUtils.containsAny(separator, dictValue) )
        {
            for (IDictItem dict : datas)
            {
                for (String value : dictValue.split(separator))
                {
                    if (value.equals(dict.getValue()))
                    {
                        propertyString.append(dict.getText() + separator);
                        break;
                    }
                }
            }
        }
        else
        {
            for (IDictItem dict : datas)
            {
                if (dictValue.equals(dict.getValue()))
                {
                    return dict.getText();
                }
            }
        }
        return StringUtils.stripEnd(propertyString.toString(), separator);
    }

    /**
     * 根据字典类型和字典标签获取字典值
     *
     * @param dictType 字典类型
     * @param dictLabel 字典标签
     * @param separator 分隔符
     * @return 字典值
     */
    public static String getDictValue(String dictType, String dictLabel, String separator)
    {
        StringBuilder propertyString = new StringBuilder();
        List<IDictItem> datas = getDictCache(dictType);
        if(CollectionUtils.isEmpty(datas)){
            return null;
        }

        if (StringUtils.containsAny(separator, dictLabel) )
        {
            for (IDictItem dict : datas)
            {
                for (String label : dictLabel.split(separator))
                {
                    if (label.equals(dict.getText()))
                    {
                        propertyString.append(dict.getValue() + separator);
                        break;
                    }
                }
            }
        }
        else
        {
            for (IDictItem dict : datas)
            {
                if (dictLabel.equals(dict.getText()))
                {
                    return dict.getValue();
                }
            }
        }
        return StringUtils.stripEnd(propertyString.toString(), separator);
    }


}
