package com.chd.modules.workflow.vo;

import lombok.Data;

import java.util.List;

@Data
public class WorkflowQueryVo {
    private Integer pageNo;
    private Integer pageSize;
    private String userId;
    private String bizId;
    private String state;
    private String sort;
    private String departId;//部门ID
    private String processCategory;
    private String title;
    private boolean exportAll;

    private List<String> idArray;

    public  WorkflowQueryVo(){
    }

    public WorkflowQueryVo(Integer pageNo,Integer pageSize){
        this.pageNo=pageNo;
        this.pageSize=pageSize;
    }


    public int getOffset(){

        if(getPageNo()==null){
            setPageNo(1);
        }
        if(getPageSize()==null){
            setPageSize(10);
        }
        int offset=(getPageNo()-1)*getPageSize();
        if(offset<0){
            offset=0;
        }
        return offset;
    }
    public int getCurrent(){
        if(getPageNo()==null){
            return 1;
        }
        return getPageNo();
    }
    public int getSize(){
        if(getPageSize()==null){
            return 10;
        }
        return getPageSize();
    }
}
