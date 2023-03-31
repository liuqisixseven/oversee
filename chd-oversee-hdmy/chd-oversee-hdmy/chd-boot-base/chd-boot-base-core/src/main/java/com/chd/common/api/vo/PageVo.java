package com.chd.common.api.vo;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public abstract class PageVo<T> extends Page {
    public PageVo() {
    }
    public PageVo(long current, long size) {
        super(current, size);
    }
    public PageVo(long current, long size, long total) {
        super(current, size, total);
    }
    public PageVo(long current, long size, boolean searchCount) {
        super(current, size, searchCount);
    }
    public PageVo(long current, long size, long total, boolean searchCount) {
        super(current, size, total, searchCount);
    }
    public Long getPageNo(){
        return current;
    }
    public void setPageNo(Long pageNo){
        if(pageNo!=null) {
            setCurrent(pageNo.longValue());
        }
    }
    public Long getPageSize(){
        return size;
    }
    public void setPageSize(Long pageSize){
        if(pageSize!=null) {
            setSize(pageSize.longValue());
        }
    }

}
