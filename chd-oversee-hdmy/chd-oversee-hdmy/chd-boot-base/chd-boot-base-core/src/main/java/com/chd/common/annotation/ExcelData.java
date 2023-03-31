package com.chd.common.annotation;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExcelData {

    /**
     * 导出时在excel中排序
     */
    private int sort;

    /**
     * 导出到Excel中的名字.
     */
    private String name;

    /**
     * 日期格式, 如: yyyy-MM-dd
     */
    private String dateFormat;

    /**
     * 如果是字典类型，请设置字典的type值 (如: sys_user_sex)
     */
    private String dictType;

    /**
     * 读取内容转表达式 (如: 0=男,1=女,2=未知)
     */
    private String readConverterExp;

    /**
     * 分隔符，读取字符串组内容    @Excel(name = "状态",dictType = "ins_valid_status")
     */
    private String separator;

    /**
     * BigDecimal 精度 默认:-1(默认不开启BigDecimal格式化)
     */
    private int scale;

    /**
     * BigDecimal 舍入规则 默认:BigDecimal.ROUND_HALF_EVEN
     */
    private int roundingMode ;

    /**
     * 导出类型（0数字 1字符串）
     */
    private Excel.ColumnType cellType;

    /**
     * 导出时在excel中每个列的高度 单位为字符
     */
    private double height ;

    /**
     * 导出时在excel中每个列的宽 单位为字符
     */
    private double width;



    /**
     * 文字后缀,如% 90 变成90%
     */
    private String suffix;

    /**
     * 当值为空时,字段的默认值
     */
    private String defaultValue;

    /**
     * 提示信息
     */
    private String prompt;

    /**
     * 设置只能选择不能输入的列内容.
     */
    private String[] combo;

    /**
     * 是否导出数据,应对需求:有时我们需要导出一份模板,这是标题需要但内容需要用户手工填写.
     */
    private boolean isExport ;

    /**
     * 另一个类中的属性名称,支持多级获取,以小数点隔开
     */
    private String targetAttr;

    /**
     * 是否自动统计数据,在最后追加一行统计数据总和
     */
    private boolean isStatistics ;




}
