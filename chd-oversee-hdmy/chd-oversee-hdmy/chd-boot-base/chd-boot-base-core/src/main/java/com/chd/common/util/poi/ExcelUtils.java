package com.chd.common.util.poi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.chd.common.annotation.ExcelData;
import com.chd.common.api.vo.Result;
import com.chd.common.core.dict.DictUtils;
import com.chd.common.core.text.ExcelHeader;
import com.chd.common.exception.ServiceException;
import com.chd.common.util.*;
import com.chd.common.util.sensitive.JsonSensitive;
import com.chd.common.util.sensitive.SensitiveStrategyService;
import com.chd.config.ChdBaseConfig;
import com.chd.config.WebMvcConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chd.common.annotation.Excel;
import com.chd.common.annotation.Excels;
import com.chd.common.annotation.Excel.ColumnType;
import com.chd.common.annotation.Excel.Type;
import com.chd.common.core.text.Convert;

/**
 * Excel相关处理
 */
public class ExcelUtils<T> {
    private static final Logger log = LoggerFactory.getLogger(ExcelUtils.class);

    /**
     * Excel sheet最大行数，默认65536
     */
    public static final int sheetSize = 65536;

    /**
     * 工作表名称
     */
    private String sheetName;

    /**
     * 导出类型（EXPORT:导出数据；IMPORT：导入模板）
     */
    private Type type;

    /**
     * 工作薄对象
     */
    private Workbook wb;

    /**
     * 工作表对象
     */
    private Sheet sheet;

    /**
     * 样式列表
     */
    private Map<String, CellStyle> styles;

    /**
     * 导入导出数据列表
     */
    private List<T> list;

    /**
     * 注解列表
     */
    private List<Object[]> fields;

    /**
     * 最大高度
     */
    private short maxHeight;

    /**
     * 统计列表
     */
    private Map<Integer, Double> statistics = new HashMap<Integer, Double>();

    /**
     * 数字格式
     */
    private static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat("######0.00");

    /**
     * 实体对象
     */
    public Class<T> clazz;
    /**
     * 指定到处文件表头
     */
    List<ExcelHeader> exportHeader;
    Map<String, ExcelHeader> exportHeaderMap;


    Map<String, ExcelHeader> exportFieldMap;

    public ExcelUtils(Class<T> clazz) {
        this.clazz = clazz;
    }

    public void init(List<T> list, String sheetName, Type type) {
        if (list == null) {
            list = new ArrayList<T>();
        }
        this.list = list;
        this.sheetName = sheetName;
        this.type = type;
        createExcelField();
        createWorkbook();
    }

    /**
     * 对excel表单默认第一个索引名转换成list
     *
     * @param is 输入流
     * @return 转换后集合
     */
    public List<T> importExcel(InputStream is) throws Exception {
        return importExcel(StringUtils.EMPTY, is);
    }

    /**
     * 对excel表单指定表格索引名转换成list
     *
     * @param sheetName 表格索引名
     * @param is        输入流
     * @return 转换后集合
     */
    public List<T> importExcel(String sheetName, InputStream is) throws Exception {
        this.type = Type.IMPORT;
        this.wb = WorkbookFactory.create(is);
        List<T> list = new ArrayList<T>();
        Sheet sheet = null;
        if (StringUtils.isNotEmpty(sheetName)) {
            // 如果指定sheet名,则取指定sheet中的内容.
            sheet = wb.getSheet(sheetName);
        } else {
            // 如果传入的sheet名不存在则默认指向第1个sheet.
            sheet = wb.getSheetAt(0);
        }

        if (sheet == null) {
            throw new IOException("文件sheet不存在");
        }

        int rows = sheet.getPhysicalNumberOfRows();

        if (rows > 0) {
            // 定义一个map用于存放excel列的序号和field.
            Map<String, Integer> cellMap = new HashMap<String, Integer>();
            // 获取表头
            Row heard = sheet.getRow(0);
            for (int i = 0; i < heard.getPhysicalNumberOfCells(); i++) {
                Cell cell = heard.getCell(i);
                if (cell!=null) {
                    String value = this.getCellValue(heard, i).toString();
                    cellMap.put(value, i);
                } else {
                    cellMap.put(null, i);
                }
            }
            // 有数据时才处理 得到类的所有field.
            Field[] allFields = clazz.getDeclaredFields();
            // 定义一个map用于存放列的序号和field.
            Map<Integer, Field> fieldsMap = new HashMap<Integer, Field>();
            for (int col = 0; col < allFields.length; col++) {
                Field field = allFields[col];
                Excel attr = field.getAnnotation(Excel.class);
                if (attr != null && (attr.type() == Type.ALL || attr.type() == type)) {
                    // 设置类的私有字段属性可访问.
                    field.setAccessible(true);
                    Integer column = cellMap.get(attr.name());
                    if (column != null) {
                        fieldsMap.put(column, field);
                    }
                }
            }
            for (int i = 1; i < rows; i++) {
                // 从第2行开始取数据,默认第一行是表头.
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                T entity = null;
                for (Map.Entry<Integer, Field> entry : fieldsMap.entrySet()) {
                    Object val = this.getCellValue(row, entry.getKey());

                    // 如果不存在实例则新建.
                    entity = (entity == null ? clazz.newInstance() : entity);
                    // 从map中得到对应列的field.
                    Field field = fieldsMap.get(entry.getKey());
                    // 取得类型,并根据对象类型设置值.
                    Class<?> fieldType = field.getType();
                    if (String.class == fieldType) {
                        String s = Convert.toStr(val);
                        if (StringUtils.endsWith(s, ".0")) {
                            val = StringUtils.substringBefore(s, ".0");
                        } else {
                            String dateFormat = field.getAnnotation(Excel.class).dateFormat();
                            if (StringUtils.isNotEmpty(dateFormat)) {
                                val = DateUtils.textParseDate(String.valueOf(val),dateFormat);//.parseDateToStr(dateFormat, (Date) val);
                            } else {
                                val = Convert.toStr(val);
                            }
                        }
                    } else if ((Integer.TYPE == fieldType || Integer.class == fieldType) && StringUtils.isNumeric(Convert.toStr(val))) {
                        val = Convert.toInt(val);
                    } else if (Long.TYPE == fieldType || Long.class == fieldType) {
                        val = Convert.toLong(val);
                    } else if (Double.TYPE == fieldType || Double.class == fieldType) {
                        val = Convert.toDouble(val);
                    } else if (Float.TYPE == fieldType || Float.class == fieldType) {
                        val = Convert.toFloat(val);
                    } else if (BigDecimal.class == fieldType) {
                        val = Convert.toBigDecimal(val);
                    } else if (Date.class == fieldType) {
                        if (val instanceof String) {
                            val = DateUtils.textParseDate((String)val,DateUtils.FORMAT_DATE);
                        } else if (val instanceof Double) {
                            val = DateUtil.getJavaDate((Double) val);
                        }else if(val instanceof BigDecimal){
                            val = DateUtils.textParseDate(String.valueOf(val),DateUtils.FORMAT_DATE);
                        }else{
                            if(val!=null) {
                                val = DateUtils.textParseDate(String.valueOf(val), DateUtils.FORMAT_DATE);
                            }
                        }
                    } else if (Boolean.TYPE == fieldType || Boolean.class == fieldType) {
                        val = Convert.toBool(val, false);
                    }
                    if (fieldType!=null) {
                        Excel attr = field.getAnnotation(Excel.class);
                        String propertyName = field.getName();
                        if (StringUtils.isNotEmpty(attr.targetAttr())) {
                            propertyName = field.getName() + "." + attr.targetAttr();
                        } else if (StringUtils.isNotEmpty(attr.readConverterExp())) {
                            val = reverseByExp(Convert.toStr(val), attr.readConverterExp(), attr.separator());
                        } else if (StringUtils.isNotEmpty(attr.dictType())) {
                            val = reverseDictByExp(Convert.toStr(val), attr.dictType(), attr.separator());
                        }
                        ReflectUtils.invokeSetter(entity, propertyName, val);
                    }
                }
                list.add(entity);
            }
        }
        return list;
    }

    /**
     * 对list数据源将其里面的数据导入到excel表单
     *
     * @param list      导出数据集合
     * @param sheetName 工作表的名称
     * @return 结果
     */
    public Result exportExcel(List<T> list, String sheetName) {
        this.init(list, sheetName, Type.EXPORT);
        return exportExcel();
    }

    /**
     * @param list
     * @param sheetName
     * @param exportHeader 指定导出表头
     * @return
     */
    public Result exportExcel(List<T> list, String sheetName, String fileName, List<ExcelHeader> exportHeader) {
        this.exportHeader = exportHeader;
        this.exportHeaderMap = new HashMap<>();
        this.init(list, sheetName, Type.EXPORT);
        return exportExcel(fileName);
    }

    /**
     * @param list
     * @param sheetName
     * @param exportHeader 指定导出表头
     * @return
     */
    public Result exportExcel(List<T> list, String sheetName, List<ExcelHeader> exportHeader) {
        this.exportHeader = exportHeader;
        this.exportHeaderMap = new HashMap<>();
        this.init(list, sheetName, Type.EXPORT);
        return exportExcel();
    }

    /**
     * 对list数据源将其里面的数据导入到excel表单
     *
     * @param sheetName 工作表的名称
     * @return 结果
     */
    public Result importTemplateExcel(String sheetName) {
        this.init(null, sheetName, Type.IMPORT);
        return exportExcel();
    }

    public Result exportExcel() {
        return exportExcel("");
    }
    /**
     * 对list数据源将其里面的数据导入到excel表单
     *
     * @return 结果
     */
    public Result exportExcel(String filename) {
        OutputStream out = null;
        try {
            // 取出一共有多少个sheet.
            double sheetNo = Math.ceil(list.size() / sheetSize);
            for (int index = 0; index <= sheetNo; index++) {
                createSheet(sheetNo, index);

                // 产生一行
                Row row = sheet.createRow(0);
                int column = 0;
                // 写入各个字段的列头名称
                for (Object[] os : fields) {
                    Field field = (Field) os[0];
                    Excel excel = (Excel) os[1];
                    this.createCell(field, excel, row, column++);


                }
                if (Type.EXPORT.equals(type)) {
                    fillExcelData(index, row);
                    addStatisticsRow();
                }

//                row.setHeight(setHeight(row,1));

            }
            if(StringUtils.isEmpty(filename)) {
                filename = encodingFilename(sheetName);
            }
            out = new FileOutputStream(getAbsoluteFile(filename));
            wb.write(out);
            return Result.OK("成功",filename);
        } catch (Exception e) {
            log.error("导出Excel异常{}", e.getMessage());
            throw new ServiceException(e);
        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 填充excel数据
     *
     * @param index 序号
     * @param row   单元格行
     */
    public void fillExcelData(int index, Row row) {
        int startNo = index * sheetSize;
        int endNo = Math.min(startNo + sheetSize, list.size());
        for (int i = startNo; i < endNo; i++) {
            row = sheet.createRow(i + 1 - startNo);
            // 得到导出对象.
            T vo = (T) list.get(i);
            int column = 0;
            for (Object[] os : fields) {
                Field field = (Field) os[0];
                Excel excel = (Excel) os[1];
                // 设置实体类私有属性可访问
                field.setAccessible(true);
                this.addCell(excel, row, vo, field, column++);
            }
        }
    }

    /**
     * 创建表格样式
     *
     * @param wb 工作薄对象
     * @return 样式列表
     */
    private Map<String, CellStyle> createStyles(Workbook wb) {
        // 写入各条记录,每条记录对应excel表中的一行
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        Font dataFont = wb.createFont();
        dataFont.setFontName("Arial");
        dataFont.setFontHeightInPoints((short) 10);
        style.setFont(dataFont);
        style.setWrapText(true);
        styles.put("data", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = wb.createFont();
        headerFont.setFontName("Arial");
        headerFont.setFontHeightInPoints((short) 10);
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(headerFont);
        styles.put("header", style);

        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font totalFont = wb.createFont();
        totalFont.setFontName("Arial");
        totalFont.setFontHeightInPoints((short) 10);
        style.setFont(totalFont);
        styles.put("total", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(HorizontalAlignment.LEFT);
        styles.put("data1", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(HorizontalAlignment.CENTER);
        styles.put("data2", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        styles.put("data3", style);

        return styles;
    }

    /**
     * 创建单元格
     */
    public Cell createCell(Field field, Excel attr, Row row, int column) {
        // 创建列
        Cell cell = row.createCell(column);
        // 写入列信息
        String cellName = attr.name();
        ExcelData excelData = new ExcelData();
        excelData.setName(cellName);
        excelData.setWidth(attr.width());
        excelData.setPrompt(attr.prompt());
        excelData.setCombo(attr.combo());
        if (null != this.exportHeaderMap && this.exportHeaderMap.containsKey(field.getName())) {
            ExcelHeader h = this.exportHeaderMap.get(field.getName());
            if (StringUtils.isNotEmpty(h.getLabel())) {
                cellName = h.getLabel();
            }
            Double width = h.getWidth();
            if(null!=width&&width>0d){
                excelData.setWidth(width/4);
            }
        }
        cell.setCellValue(cellName);
        setExcelDataValidation(excelData, row, column);
        cell.setCellStyle(styles.get("header"));
//        int enterCnt = 1;
//        int rwsTemp = cell.toString().getBytes().length/75;
//        if (rwsTemp > enterCnt) {
//            enterCnt = rwsTemp;
//        }
//        if(row.getHeight()<(short)(enterCnt * 650)){
//            row.setHeight((short)(enterCnt * 650));
//        }
        return cell;
    }

    /**
     * 设置自适应行高的方法
     */
//    public int setHeight(Row row, int cellIndex){
//        Cell cell = row.getCell(cellIndex);
//        //1.先取出内容 计算长度 （这个方法在后面...）
//        String content = getCellContent(cell);
//        System.out.println("我想看看取出的内容对不对"+content);
//        //计算字体的高度
//        CellStyle cellStyle = cell.getCellStyle();
//        cellStyle.getFontIndexAsInt();
//        HSSFFont font = cellStyle.getFont(row.getSheet().getWorkbook());
//        //字体的高度
//        short fontHeight = font.getFontHeight();
//        System.out.println("我这里输出的是11号的字体的高度，貌似要除以20才是Excel的"+fontHeight);//11号字体的高度  这里不重要（本方法也用不着）  这里设置每一行都是18磅
//        //计算字符的宽度
//        //代入默认字符宽度8：//字符像素宽度 = 字体宽度 * 字符个数 + 边距
//        //像素 = 5 + (字符个数 * 7)
//        int i = 5 + (content.toCharArray().length * 7);
//        //POI中的字符宽度算法是：double 宽度 = (字符个数 * (字符宽度 - 1) + 5) / (字符宽度 - 1) * 256;
//        double poiWidth = content.length() * 12 / 7 * 256;
//        //然后再四舍五入成整数。
//        int width = (int) Math.ceil(poiWidth);
//        System.out.println("我这里计算的是字符串长度的宽度"+width);
//        //2.除以宽度 计算行数乘以行高   (256*80) 是我设置的我的单元格列的宽度
//        double rowNum = (double) width/(256*80);
//        System.out.println("我是计算出来的行数"+rowNum);
//        int rowNums = (int)Math.ceil(rowNum);
//        System.out.println("在这里计算的是多少行，我想知道算对了没"+rowNums);
//        int height = rowNums* 420;//这个420 是我为每一行设置的高度 有点大，可以用字体的高度加一点（前面方法中有计算字体高度的方法）
//        return height;//直接设置行高为这个值就OK了
//    }

//    private static String getCellContent(Cell cell) {
//        if(null == cell){
//            return "";
//        }
//        String result = cell.getStringCellValue().trim();
//
//        return result;
//    }

    /**
     * 设置单元格信息
     *
     * @param value 单元格值
     * @param attr  注解相关
     * @param cell  单元格信息
     */
    public void setCellVo(Object value, Excel attr, Cell cell) {
        if (ColumnType.STRING == attr.cellType()) {
            cell.setCellValue(value==null ? attr.defaultValue() : value + attr.suffix());
        } else if (ColumnType.NUMERIC == attr.cellType()) {
            if (value!=null) {
                cell.setCellValue(StringUtils.contains(Convert.toStr(value), ".") ? Convert.toDouble(value) : Convert.toInt(value));
            }
        } else if (ColumnType.IMAGE == attr.cellType()) {
            ClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, (short) cell.getColumnIndex(), cell.getRow().getRowNum(), (short) (cell.getColumnIndex() + 1), cell.getRow().getRowNum() + 1);
            String imagePath = Convert.toStr(value);
            if (StringUtils.isNotEmpty(imagePath)) {
                byte[] data = ImageUtils.getImage(imagePath);
                getDrawingPatriarch(cell.getSheet()).createPicture(anchor, cell.getSheet().getWorkbook().addPicture(data, getImageType(data)));
            }
        }
    }

    /**
     * 获取画布
     */
    public static Drawing<?> getDrawingPatriarch(Sheet sheet) {
        if (sheet.getDrawingPatriarch() == null) {
            sheet.createDrawingPatriarch();
        }
        return sheet.getDrawingPatriarch();
    }

    /**
     * 获取图片类型,设置图片插入类型
     */
    public int getImageType(byte[] value) {
        String type = FileTypeUtils.getFileExtendName(value);
        if ("JPG".equalsIgnoreCase(type)) {
            return Workbook.PICTURE_TYPE_JPEG;
        } else if ("PNG".equalsIgnoreCase(type)) {
            return Workbook.PICTURE_TYPE_PNG;
        }
        return Workbook.PICTURE_TYPE_JPEG;
    }

    /**
     * 创建表格样式
     */
    public void setExcelDataValidation(ExcelData attr, Row row, int column) {
        if (attr.getName().indexOf("注：") >= 0) {
            sheet.setColumnWidth(column, 6000);
        } else {
            if(attr.getWidth()==0d){
                attr.setWidth(100d);
            }
            if(attr.getWidth()>255){
                attr.setWidth(200d);
            }
            // 设置列宽
            sheet.setColumnWidth(column, (int) ((attr.getWidth() + 0.72) * 256));
            int colWidth = sheet.getColumnWidth(column) + 100;
            if(colWidth<255*256){
                sheet.setColumnWidth(column, colWidth < 3000 ? 3000 : colWidth);
            }else{
                sheet.setColumnWidth(column,6000 );
            }
        }
        // 如果设置了提示信息则鼠标放上去提示.
        if (StringUtils.isNotEmpty(attr.getPrompt())) {
            // 这里默认设了2-101列提示.
            setXSSFPrompt(sheet, "", attr.getPrompt(), 1, 100, column, column);
        }
        // 如果设置了combo属性则本列只能选择不能输入
        if (attr.getCombo().length > 0) {
            // 这里默认设了2-101列只能选择不能输入.
            setXSSFValidation(sheet, attr.getCombo(), 1, 100, column, column);
        }
    }

    /**
     * 创建表格样式
     */
    public void setDataValidation(Excel attr, Row row, int column) {
        if (attr.name().indexOf("注：") >= 0) {
            sheet.setColumnWidth(column, 6000);
        } else {
            // 设置列宽
            sheet.setColumnWidth(column, (int) ((attr.width() + 0.72) * 256));
        }
        // 如果设置了提示信息则鼠标放上去提示.
        if (StringUtils.isNotEmpty(attr.prompt())) {
            // 这里默认设了2-101列提示.
            setXSSFPrompt(sheet, "", attr.prompt(), 1, 100, column, column);
        }
        // 如果设置了combo属性则本列只能选择不能输入
        if (attr.combo().length > 0) {
            // 这里默认设了2-101列只能选择不能输入.
            setXSSFValidation(sheet, attr.combo(), 1, 100, column, column);
        }
    }

    /**
     * 添加单元格
     */
    public Cell addCell(Excel attr, Row row, T vo, Field field, int column) {
        Cell cell = null;
        try {
            // 设置行高
//            row.setHeight(maxHeight);
            if(row.getHeight()<maxHeight){
                row.setHeight(maxHeight);
            }
            // 根据Excel中设置情况决定是否导出,有些情况需要保持为空,希望用户填写这一列.
            if (attr.isExport()) {
                // 创建cell
                cell = row.createCell(column);
                int align = attr.align().value();
                cell.setCellStyle(styles.get("data" + (align >= 1 && align <= 3 ? align : "")));

                // 用于读取对象中的属性
                Object value = SensitiveStrategyService.mask(field.getAnnotation(JsonSensitive.class), getTargetValue(vo, field, attr));

                String dateFormat = attr.dateFormat();
                String readConverterExp = attr.readConverterExp();
                String separator = attr.separator();
                String dictType = attr.dictType();
                if (StringUtils.isNotEmpty(dateFormat) && value!=null) {
                    cell.setCellValue(DateUtils.formatDate( (Date) value,dateFormat));
                } else if (StringUtils.isNotEmpty(readConverterExp) && value!=null) {
                    cell.setCellValue(convertByExp(Convert.toStr(value), readConverterExp, separator));
                } else if (StringUtils.isNotEmpty(dictType) && value!=null) {
                    cell.setCellValue(convertDictByExp(Convert.toStr(value), dictType, separator));
                } else if (value instanceof BigDecimal && -1 != attr.scale()) {
                    cell.setCellValue((((BigDecimal) value).setScale(attr.scale(), attr.roundingMode())).toString());
                } else {
                    // 设置列类型
                    setCellVo(value, attr, cell);
                }
                int enterCnt = 1;
//                int rwsTemp = cell.toString().getBytes().length/sheet.getColumnWidth(column)/256;
                int rwsTemp = cell.toString().getBytes().length/(sheet.getColumnWidth(column)/256);
                if (rwsTemp > enterCnt) {
                    enterCnt = rwsTemp;
                }
                if(row.getHeight()<(short)(enterCnt * 300)){
                    row.setHeight((short)(enterCnt * 300));
                }
                addStatisticsData(column, Convert.toStr(value), attr);
            }
        } catch (Exception e) {
            log.error("导出Excel失败{}", e);
        }
        return cell;
    }

    /**
     * 设置 POI XSSFSheet 单元格提示
     *
     * @param sheet         表单
     * @param promptTitle   提示标题
     * @param promptContent 提示内容
     * @param firstRow      开始行
     * @param endRow        结束行
     * @param firstCol      开始列
     * @param endCol        结束列
     */
    public void setXSSFPrompt(Sheet sheet, String promptTitle, String promptContent, int firstRow, int endRow, int firstCol, int endCol) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = helper.createCustomConstraint("DD1");
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        DataValidation dataValidation = helper.createValidation(constraint, regions);
        dataValidation.createPromptBox(promptTitle, promptContent);
        dataValidation.setShowPromptBox(true);
        sheet.addValidationData(dataValidation);
    }

    /**
     * 设置某些列的值只能输入预制的数据,显示下拉框.
     *
     * @param sheet    要设置的sheet.
     * @param textlist 下拉框显示的内容
     * @param firstRow 开始行
     * @param endRow   结束行
     * @param firstCol 开始列
     * @param endCol   结束列
     * @return 设置好的sheet.
     */
    public void setXSSFValidation(Sheet sheet, String[] textlist, int firstRow, int endRow, int firstCol, int endCol) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        // 加载下拉列表内容
        DataValidationConstraint constraint = helper.createExplicitListConstraint(textlist);
        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        // 数据有效性对象
        DataValidation dataValidation = helper.createValidation(constraint, regions);
        // 处理Excel兼容性问题
        if (dataValidation instanceof XSSFDataValidation) {
            dataValidation.setSuppressDropDownArrow(true);
            dataValidation.setShowErrorBox(true);
        } else {
            dataValidation.setSuppressDropDownArrow(false);
        }

        sheet.addValidationData(dataValidation);
    }

    /**
     * 解析导出值 0=男,1=女,2=未知
     *
     * @param propertyValue 参数值
     * @param converterExp  翻译注解
     * @param separator     分隔符
     * @return 解析后值
     */
    public static String convertByExp(String propertyValue, String converterExp, String separator) {
        StringBuilder propertyString = new StringBuilder();
        String[] convertSource = converterExp.split(",");
        for (String item : convertSource) {
            String[] itemArray = item.split("=");
            if (StringUtils.containsAny(separator, propertyValue)) {
                for (String value : propertyValue.split(separator)) {
                    if (itemArray[0].equals(value)) {
                        propertyString.append(itemArray[1] + separator);
                        break;
                    }
                }
            } else {
                if (itemArray[0].equals(propertyValue)) {
                    return itemArray[1];
                }
            }
        }
        return StringUtils.stripEnd(propertyString.toString(), separator);
    }

    /**
     * 反向解析值 男=0,女=1,未知=2
     *
     * @param propertyValue 参数值
     * @param converterExp  翻译注解
     * @param separator     分隔符
     * @return 解析后值
     */
    public static String reverseByExp(String propertyValue, String converterExp, String separator) {
        StringBuilder propertyString = new StringBuilder();
        String[] convertSource = converterExp.split(",");
        for (String item : convertSource) {
            String[] itemArray = item.split("=");
            if (StringUtils.containsAny(separator, propertyValue)) {
                for (String value : propertyValue.split(separator)) {
                    if (itemArray[1].equals(value)) {
                        propertyString.append(itemArray[0] + separator);
                        break;
                    }
                }
            } else {
                if (itemArray[1].equals(propertyValue)) {
                    return itemArray[0];
                }
            }
        }
        return StringUtils.stripEnd(propertyString.toString(), separator);
    }

    /**
     * 解析字典值
     *
     * @param dictValue 字典值
     * @param dictType  字典类型
     * @param separator 分隔符
     * @return 字典标签
     */
    public static String convertDictByExp(String dictValue, String dictType, String separator) {
        return DictUtils.getDictLabel(dictType, dictValue, separator);
    }

    /**
     * 反向解析值字典值
     *
     * @param dictLabel 字典标签
     * @param dictType  字典类型
     * @param separator 分隔符
     * @return 字典值
     */
    public static String reverseDictByExp(String dictLabel, String dictType, String separator) {
        return DictUtils.getDictValue(dictType, dictLabel, separator);
    }

    /**
     * 合计统计信息
     */
    private void addStatisticsData(Integer index, String text, Excel entity) {
        if (entity != null && entity.isStatistics()) {
            Double temp = 0D;
            if (!statistics.containsKey(index)) {
                statistics.put(index, temp);
            }
            try {
                temp = Double.valueOf(text);
            } catch (NumberFormatException e) {
            }
            statistics.put(index, statistics.get(index) + temp);
        }
    }

    /**
     * 创建统计行
     */
    public void addStatisticsRow() {
        if (statistics.size() > 0) {
            Cell cell = null;
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            Set<Integer> keys = statistics.keySet();
            cell = row.createCell(0);
            cell.setCellStyle(styles.get("total"));
            cell.setCellValue("合计");

            for (Integer key : keys) {
                cell = row.createCell(key);
                cell.setCellStyle(styles.get("total"));
                cell.setCellValue(DOUBLE_FORMAT.format(statistics.get(key)));
            }
            statistics.clear();
        }
    }

    /**
     * 编码文件名
     */
    public static String encodingFilename(String filename) {
        filename = UUID.randomUUID().toString() + "_" + filename + ".xlsx";
        return filename;
    }

    /**
     * 获取下载路径
     *
     * @param filename 文件名称
     */
    public String getAbsoluteFile(String filename) {
        ChdBaseConfig chdBaseConfig = SpringContextUtils.getBean(ChdBaseConfig.class);
        String downloadPath = chdBaseConfig.getPath().getUpload()+"/download/" + filename;
        File desc = new File(downloadPath);
        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        return downloadPath;
    }

    /**
     * 获取bean中的属性值
     *
     * @param vo    实体对象
     * @param field 字段
     * @param excel 注解
     * @return 最终的属性值
     * @throws Exception
     */
    private Object getTargetValue(T vo, Field field, Excel excel) throws Exception {
        Object o = field.get(vo);
        if (StringUtils.isNotEmpty(excel.targetAttr())) {
            String target = excel.targetAttr();
            if (target.indexOf(".") > -1) {
                String[] targets = target.split("[.]");
                for (String name : targets) {
                    o = getValue(o, name);
                }
            } else {
                o = getValue(o, target);
            }
        }
        return o;
    }

    /**
     * 以类的属性的get方法方法形式获取值
     *
     * @param o
     * @param name
     * @return value
     * @throws Exception
     */
    private Object getValue(Object o, String name) throws Exception {
        if (o!=null && StringUtils.isNotEmpty(name)) {
            Class<?> clazz = o.getClass();
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            o = field.get(o);
        }
        return o;
    }


    /**
     * 得到所有定义字段
     */
    private void createExcelField() {
        this.fields = new ArrayList<Object[]>();
        if(null==exportFieldMap){
            exportFieldMap = new HashMap<>();
        }

        List<Field> tempFields = new ArrayList<>();

        tempFields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
        tempFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        List<Field> specialFields = new ArrayList<>();
        //使用前端传入的header配置
        if (null != this.exportHeader && this.exportHeader.size() > 0) {

            for (int i=0;i<this.exportHeader.size();i++) {
                ExcelHeader xh = this.exportHeader.get(i);
                if(null!=xh){
                    xh.setSort(i);
                    for (Field field : tempFields) {
                        if (null != xh.getName() && xh.getName().equalsIgnoreCase(field.getName())) {
                            specialFields.add(field);
                            exportHeaderMap.put(xh.getName(), xh);

                            exportHeaderMap.put(field.getName(), xh);
                        }
                    }
                }
            }
        } else {
            specialFields.addAll(tempFields);
        }

        for (Field field : specialFields) {
            // 单注解
            if (field.isAnnotationPresent(Excel.class)) {
                putToField(field, field.getAnnotation(Excel.class));
            }

            // 多注解
            if (field.isAnnotationPresent(Excels.class)) {
                Excels attrs = field.getAnnotation(Excels.class);
                Excel[] excels = attrs.value();
                for (Excel excel : excels) {
                    putToField(field, excel);
                }
            }
        }
        this.fields = this.fields.stream().sorted(Comparator.comparing(objects -> {
            if(null!=objects&&objects.length>0){
                Field field = (Field) objects[0];
                if(null!=field&&StringUtil.isNotEmpty(field.getName())){
                    ExcelHeader excelHeader = exportFieldMap.get(field.getName());
                    if(null!=excelHeader){
                        return excelHeader.getSort();
                    }
                }
            }
            return ((Excel) objects[1]).sort();
        })).collect(Collectors.toList());
        this.maxHeight = getRowHeight();
    }

    /**
     * 根据注解获取最大行高
     */
    public short getRowHeight() {
        double maxHeight = 0;
        for (Object[] os : this.fields) {
            Excel excel = (Excel) os[1];
            maxHeight = maxHeight > excel.height() ? maxHeight : excel.height();
        }
        return (short) (maxHeight * 20);
    }

    /**
     * 放到字段集合中
     */
    private void putToField(Field field, Excel attr) {
        if (attr != null && (attr.type() == Type.ALL || attr.type() == type)) {
            this.fields.add(new Object[] {field, attr});
        }
    }

    /**
     * 创建一个工作簿
     */
    public void createWorkbook() {
        this.wb = new SXSSFWorkbook(500);
    }

    /**
     * 创建工作表
     *
     * @param sheetNo sheet数量
     * @param index   序号
     */
    public void createSheet(double sheetNo, int index) {
        this.sheet = wb.createSheet();
        this.styles = createStyles(wb);
        // 设置工作表的名称.
        if (sheetNo == 0) {
            wb.setSheetName(index, sheetName);
        } else {
            wb.setSheetName(index, sheetName + index);
        }

    }

    /**
     * 获取单元格值
     *
     * @param row    获取的行
     * @param column 获取单元格列号
     * @return 单元格值
     */
    public Object getCellValue(Row row, int column) {
        if (row == null) {
            return row;
        }
        Object val = "";
        try {
            Cell cell = row.getCell(column);
            if (cell!=null) {
                if (cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                    val = cell.getNumericCellValue();
                    if (DateUtil.isCellDateFormatted(cell)) {
                        val = DateUtil.getJavaDate((Double) val); // POI Excel 日期格式转换
                    } else {
                        if ((Double) val % 1 != 0) {
                            val = new BigDecimal(val.toString());
                        } else {
                            val = new DecimalFormat("0").format(val);
                        }
                    }
                } else if (cell.getCellType() == CellType.STRING) {
                    val = cell.getStringCellValue();
                } else if (cell.getCellType() == CellType.BOOLEAN) {
                    val = cell.getBooleanCellValue();
                } else if (cell.getCellType() == CellType.ERROR) {
                    val = cell.getErrorCellValue();
                }

            }
        } catch (Exception e) {
            return val;
        }
        return val;
    }
    /**
     * 自定义标题和内容导出数据
     * @author Zengzhiqiang  2021/10/14 11:40
     * @param sheetName 工作蒲名字
     * @param title 标题数组
     * @param values 内容数组
     * @return com.sinosoft.ins.common.core.domain.AjaxResult
     */
    public Result customWorkBook(String sheetName, String[] title, String [][]values) {
        int[] width = new int[title.length];
        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        createWorkbook();
        String filename = "";
        OutputStream out = null;
        try{
            // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
            Sheet sheet = wb.createSheet(sheetName);

            // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
            Row row = sheet.createRow(0);
            // 第四步，创建单元格，并设置值表头 设置表头居中
            this.styles = createStyles(wb);


            Cell cell = null;

            // 创建标题
            for (int i = 0; i < title.length; i++) {
                cell = row.createCell(i);
                cell.setCellValue(title[i]);
                cell.setCellStyle(styles.get("header"));
            }

            // 创建内容
            for (int i = 0; i < values.length; i++) {
                row = sheet.createRow(i + 1);
                for (int j = 0; j < values[i].length; j++) {
                    //将内容按顺序赋给对应的列对象
                    Cell rowCell = row.createCell(j);
                    rowCell.setCellValue(values[i][j]);
                    rowCell.setCellStyle(styles.get("data2"));

                    // 根据内容设置获取列宽
                    int length = values[i][j].getBytes().length * 256 + 200;
                    //这里把宽度最大限制到15000
                    if (length > 15000){
                        length = 15000;
                    }
                    if(length < 5000) {
                        length = 5000;
                    }
                    width[j] = length;
                }
            }

            // 设置自适应列宽
            for (int i = 0; i < width.length; i++) {
                sheet.setColumnWidth(i, width[i]);
            }

            if(StringUtils.isEmpty(filename)) {
                filename = encodingFilename(sheetName);
            }
            out = new FileOutputStream(getAbsoluteFile(filename));
            wb.write(out);
            return Result.OK("成功",filename);
        }catch (Exception e) {
            log.error("导出Excel异常{}", e.getMessage());
            throw new ServiceException(e);
        }
    }
}
