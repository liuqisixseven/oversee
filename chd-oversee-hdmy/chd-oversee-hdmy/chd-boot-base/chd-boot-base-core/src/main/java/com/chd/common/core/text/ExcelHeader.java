package com.chd.common.core.text;


import java.io.Serializable;

public class ExcelHeader implements Serializable {
    /**
     * 中文名称
     */
    private String label;
    /**
     * 对象字段名
     */
    private String name;

    private Double width;

    private Boolean checked;

    private int sort;


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
