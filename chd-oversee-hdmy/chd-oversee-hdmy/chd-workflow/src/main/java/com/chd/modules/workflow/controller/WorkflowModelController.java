package com.chd.modules.workflow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chd.common.api.vo.Result;
import com.chd.modules.workflow.service.WorkflowImageService;
import com.chd.modules.workflow.service.WorkflowModelService;
import com.chd.modules.workflow.vo.WorkflowModelFormVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.impl.util.IoUtil;
import org.flowable.engine.repository.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.InputStream;

@RestController
@RequestMapping("/workflow/model")
@Slf4j
public class WorkflowModelController {
    @Autowired
    private WorkflowModelService workflowModelService;
    @Autowired
    private WorkflowImageService workflowImageService;

    @PostMapping("/save")
    public Result<Void> save(@RequestBody WorkflowModelFormVo form) {
        boolean checkResult=workflowModelService.checkBpmnModelData(form.getEditorSourceValue());
        if(checkResult) {
            workflowModelService.save(form);
            return Result.OK("保存成功");
        }
        return Result.error("数据校验不通过");
    }


    @GetMapping("/importModel")
    public Result importModel(){

        try {
            WorkflowModelFormVo modelFormVo = new WorkflowModelFormVo();
            modelFormVo.setCategory("OA");
            String str = IOUtils.toString(new FileInputStream("/Users/ljc/Downloads/test.bpmn20.xml"));
            modelFormVo.setEditorSourceValue(str);
            modelFormVo.setKey("OA01");
            workflowModelService.save(modelFormVo);
        }catch (Exception ex){
            ex.printStackTrace();
        }
return Result.OK("ok");
    }

    @GetMapping("/detail/{id}")
    public Result getById(@PathVariable String id) {
        return Result.OK(workflowModelService.getModelById(id));
    }

    @GetMapping("/list")
    public Result findListByPage(WorkflowModelFormVo form) {
        IPage<Model> iPage = workflowModelService.findModelList(form);
        return Result.OK(iPage);
    }

    /** 部署 */
    @PostMapping("/deploy/{id}")
    public Result<Void> deploy(@PathVariable String id) {
        workflowModelService.deploy(id);
        return Result.OK("部署成功");
    }

    @DeleteMapping("/del")
    public Result<Void> delete(String id) {
        workflowModelService.deleteModel(id);
        return Result.OK("删除成功");
    }

    @GetMapping(value = "/image/{id}")
    public void image(@PathVariable String id, HttpServletResponse response) {
        try {
            BpmnModel bm = workflowModelService.getModel(id);
            if(bm!=null){
                InputStream inputStream =  workflowImageService.generateDiagram(bm);
                byte[] b = IoUtil.readInputStream(inputStream, "image inputStream name");
                response.setHeader("Content-type", "image/png;charset=UTF-8");
                response.getOutputStream().write(b);
            }
        } catch (Exception e) {
            log.error("生成流程图失败" + e);

        }
    }


}
