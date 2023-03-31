package com.chd.modules.oversee.issue.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chd.common.util.Base64ImageUtil;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.issue.entity.OverseeUserSignature;
import com.chd.modules.oversee.issue.entity.OverseeUserSignatureQueryVo;
import com.chd.modules.oversee.issue.mapper.OverseeUserSignatureMapper;
import com.chd.modules.oversee.issue.service.IOverseeUserSignatureService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.List;

@Service
public class OverseeUserSignatureServiceImpl implements IOverseeUserSignatureService {

    @Autowired
    private OverseeUserSignatureMapper overseeUserSignatureMapper;

    @Value("${chd.path.upload}")
    private String upLoadPath;

    static String OVERSEE_USER_SIGNATURE_DIRECTORY = (File.separator + "overseeUserSignature");

    @Override
    public Integer insert(OverseeUserSignature overseeUserSignature) {
        int insertCount = 0;
        synchronized (OverseeUserSignature.class){
            if(StringUtils.isNotEmpty(overseeUserSignature.getUserId())){
                OverseeUserSignatureQueryVo overseeUserSignatureQueryVo = new OverseeUserSignatureQueryVo();
                overseeUserSignatureQueryVo.setUserId(overseeUserSignature.getUserId());
                IPage<OverseeUserSignature> overseeUserSignatureIPage = overseeUserSignatureMapper.selectListPage(overseeUserSignatureQueryVo);
                if(null!=overseeUserSignatureIPage&&null!=overseeUserSignatureIPage.getRecords()&&overseeUserSignatureIPage.getRecords().size()>0){
                    if(null!=overseeUserSignatureIPage.getRecords().get(0)){
                        overseeUserSignatureQueryVo.setId(overseeUserSignatureIPage.getRecords().get(0).getId());
                    }
                }
            }

            overseeUserSignature.setUpdateTime(new Date());
            overseeUserSignature.setCreateTime(overseeUserSignature.getUpdateTime());
            overseeUserSignature.setCreateBy(overseeUserSignature.getUpdateBy());
            insertCount = overseeUserSignatureMapper.insert(overseeUserSignature);
        }
        return insertCount;
    }

    @Override
    public IPage<OverseeUserSignature> queryListPage(OverseeUserSignatureQueryVo query) {
        return overseeUserSignatureMapper.selectListPage(query);
    }

    @Override
    public Integer updateById(OverseeUserSignature overseeUserSignature) {
        overseeUserSignature.setUpdateTime(new Date());
        Integer count = overseeUserSignatureMapper.updateById(overseeUserSignature);
        if(null!=count&&count>0){
            String signatureLocalPath = upLoadPath + OVERSEE_USER_SIGNATURE_DIRECTORY + File.separator + overseeUserSignature.getId() +".svg";
            writeOverseeUserSignatureData(signatureLocalPath,overseeUserSignature.getSignatureData());
        }
        return count;
    }

    @Override
    public List<OverseeUserSignature> getSignatureListLocalPath(List<OverseeUserSignature> overseeUserSignatureList){
        if(null!=overseeUserSignatureList&&overseeUserSignatureList.size()>0){
            for(OverseeUserSignature overseeUserSignature : overseeUserSignatureList){
                getSignatureLocalPath(overseeUserSignature);
            }
        }
        return overseeUserSignatureList;
    }

    @Override
    public OverseeUserSignature getSignatureLocalPath(OverseeUserSignature overseeUserSignature){
        if(null!=overseeUserSignature){
            if(StringUtil.isNotEmpty(overseeUserSignature.getSignatureData())){
                File overseeUserSignatureDirectory = new File(upLoadPath + OVERSEE_USER_SIGNATURE_DIRECTORY);
                if(!overseeUserSignatureDirectory.exists()){
                    overseeUserSignatureDirectory.mkdirs();
                }
                String signatureLocalPath = upLoadPath + OVERSEE_USER_SIGNATURE_DIRECTORY + File.separator + overseeUserSignature.getId() + ".png";
                File overseeUserSignatureFile = new File(signatureLocalPath);
                if(!overseeUserSignatureFile.exists()){
                    try{
                        Base64ImageUtil.GenerateImage(overseeUserSignature.getSignatureData(),signatureLocalPath);
//                        writeOverseeUserSignatureData(overseeUserSignatureFile,overseeUserSignature.getSignatureData());
                        overseeUserSignature.setSignatureLocalPath(signatureLocalPath);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    overseeUserSignature.setSignatureLocalPath(signatureLocalPath);
                }
            }
        }
        return overseeUserSignature;
    }

    private boolean writeOverseeUserSignatureData(String signatureLocalPath,String signatureData){
        if(StringUtil.isNotEmpty(signatureData)&&StringUtil.isNotEmpty(signatureLocalPath)){
            File overseeUserSignatureFile = new File(signatureLocalPath);
            if(overseeUserSignatureFile.exists()){
                overseeUserSignatureFile.delete();
            }
            return writeOverseeUserSignatureData(overseeUserSignatureFile,signatureData);
        }
        return false;
    }

    private boolean writeOverseeUserSignatureData(File overseeUserSignatureFile,String signatureData){
        boolean isWrite = false;
        if(StringUtil.isNotEmpty(signatureData)&&null!=overseeUserSignatureFile){
            try{
                FileUtils.write(overseeUserSignatureFile,signatureData,"UTF-8");
                isWrite = true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return isWrite;
    }

    @Override
    public Integer deleteById(Long id) {
        return overseeUserSignatureMapper.deleteById(id);
    }

    @Override
    public OverseeUserSignature findById(Long id) {
        return overseeUserSignatureMapper.findById(id);
    }

    @Override
    public OverseeUserSignature findByUserId(String userId) {
        return overseeUserSignatureMapper.findByUserId(userId);
    }

    @Override
    public Integer deleteBatchById(List<Long> ids) {
        return overseeUserSignatureMapper.deleteBatchById(ids);
    }
}
