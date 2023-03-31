<template>
  <a-spin :spinning="confirmLoading">
    <a-form ref="formRef" class="antd-modal-form" :labelCol="labelCol" :wrapperCol="wrapperCol">
      <a-row>
        <a-col :span="24">
          <a-form-item label="orgId" v-bind="validateInfos.orgId">
            <a-input v-model:value="formData.orgId" placeholder="请输入orgId" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="orgName" v-bind="validateInfos.orgName">
            <a-input v-model:value="formData.orgName" placeholder="请输入orgName" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="orgShortName" v-bind="validateInfos.orgShortName">
            <a-input v-model:value="formData.orgShortName" placeholder="请输入orgShortName" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="zzjs" v-bind="validateInfos.zzjs">
            <a-input v-model:value="formData.zzjs" placeholder="请输入zzjs" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="jtCode" v-bind="validateInfos.jtCode">
            <a-input v-model:value="formData.jtCode" placeholder="请输入jtCode" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="jtCode2" v-bind="validateInfos.jtCode2">
            <a-input v-model:value="formData.jtCode2" placeholder="请输入jtCode2" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="managerId" v-bind="validateInfos.managerId">
            <a-input v-model:value="formData.managerId" placeholder="请输入managerId" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="areaCode" v-bind="validateInfos.areaCode">
            <a-input v-model:value="formData.areaCode" placeholder="请输入areaCode" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="znCode" v-bind="validateInfos.znCode">
            <a-input v-model:value="formData.znCode" placeholder="请输入znCode" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="streetAddress" v-bind="validateInfos.streetAddress">
            <a-input v-model:value="formData.streetAddress" placeholder="请输入streetAddress" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="postCode" v-bind="validateInfos.postCode">
            <a-input v-model:value="formData.postCode" placeholder="请输入postCode" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="国有经济、集体经济、私营经济、有限责任公司、联营经济、股份合作、外商投资、港澳台投资、其他经济" v-bind="validateInfos.jjType">
            <a-input v-model:value="formData.jjType" placeholder="请输入国有经济、集体经济、私营经济、有限责任公司、联营经济、股份合作、外商投资、港澳台投资、其他经济" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="农、林、牧、渔业，采掘业，制造业，电力、煤气及水的生成和供应业，建筑业，地址勘查业、水利管理业，交通运输仓储业及邮电通信业，批发和零售贸易、餐饮业，金融保险业，房地产业，社会服务业，卫生、体育和社会福利业，教育文化艺术及广电业，科学研究和综合技术服务业，国家机关政党机关和社会团体，其他行业" v-bind="validateInfos.hyType">
            <a-input v-model:value="formData.hyType" placeholder="请输入农、林、牧、渔业，采掘业，制造业，电力、煤气及水的生成和供应业，建筑业，地址勘查业、水利管理业，交通运输仓储业及邮电通信业，批发和零售贸易、餐饮业，金融保险业，房地产业，社会服务业，卫生、体育和社会福利业，教育文化艺术及广电业，科学研究和综合技术服务业，国家机关政党机关和社会团体，其他行业" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="area" v-bind="validateInfos.area">
	          <a-input-number v-model:value="formData.area" placeholder="请输入area" style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="employersNumber" v-bind="validateInfos.employersNumber">
	          <a-input-number v-model:value="formData.employersNumber" placeholder="请输入employersNumber" style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="gdValue" v-bind="validateInfos.gdValue">
	          <a-input-number v-model:value="formData.gdValue" placeholder="请输入gdValue" style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="anIncome" v-bind="validateInfos.anIncome">
	          <a-input-number v-model:value="formData.anIncome" placeholder="请输入anIncome" style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="anProfit" v-bind="validateInfos.anProfit">
	          <a-input-number v-model:value="formData.anProfit" placeholder="请输入anProfit" style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="mainProducts" v-bind="validateInfos.mainProducts">
            <a-input v-model:value="formData.mainProducts" placeholder="请输入mainProducts" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="生产型企业，经营型企业，基建型企业，发展型企业，电煤供应企业" v-bind="validateInfos.constructionType">
            <a-input v-model:value="formData.constructionType" placeholder="请输入生产型企业，经营型企业，基建型企业，发展型企业，电煤供应企业" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="煤炭业务，发电 业务，化工业务，港口业务，航运业务，营销业务" v-bind="validateInfos.businessType">
            <a-input v-model:value="formData.businessType" placeholder="请输入煤炭业务，发电 业务，化工业务，港口业务，航运业务，营销业务" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="parentOrgId" v-bind="validateInfos.parentOrgId">
            <a-input v-model:value="formData.parentOrgId" placeholder="请输入parentOrgId" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="标识组织节点所在的级别，如一级为公司，二级为部门，三级为处室等，依此类推" v-bind="validateInfos.orgLevel">
	          <a-input-number v-model:value="formData.orgLevel" placeholder="请输入标识组织节点所在的级别，如一级为公司，二级为部门，三级为处室等，依此类推" style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="upperSupervisorId" v-bind="validateInfos.upperSupervisorId">
            <a-input v-model:value="formData.upperSupervisorId" placeholder="请输入upperSupervisorId" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="upperSupervisorName" v-bind="validateInfos.upperSupervisorName">
            <a-input v-model:value="formData.upperSupervisorName" placeholder="请输入upperSupervisorName" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="displayOrder" v-bind="validateInfos.displayOrder">
            <a-input v-model:value="formData.displayOrder" placeholder="请输入displayOrder" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="description" v-bind="validateInfos.description">
            <a-input v-model:value="formData.description" placeholder="请输入description" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="1－增加，2－删除，3－修改" v-bind="validateInfos.operationCode">
	          <a-input-number v-model:value="formData.operationCode" placeholder="请输入1－增加，2－删除，3－修改" style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="同步时间(时间格式：yyyy-MM-dd HH:mm:ss" v-bind="validateInfos.synTime">
            <a-input v-model:value="formData.synTime" placeholder="请输入同步时间(时间格式：yyyy-MM-dd HH:mm:ss" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="1－已处理，0－未处理" v-bind="validateInfos.synFlag">
	          <a-input-number v-model:value="formData.synFlag" placeholder="请输入1－已处理，0－未处理" style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="默认值0，当一次处理成功则仍然为0；处理失败则加1" v-bind="validateInfos.retryTimes">
	          <a-input-number v-model:value="formData.retryTimes" placeholder="请输入默认值0，当一次处理成功则仍然为0；处理失败则加1" style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="gkOrgId" v-bind="validateInfos.gkOrgId">
            <a-input v-model:value="formData.gkOrgId" placeholder="请输入gkOrgId" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="jsm" v-bind="validateInfos.jsm">
            <a-input v-model:value="formData.jsm" placeholder="请输入jsm" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
  </a-spin>
</template>

<script lang="ts" setup>
  import { ref, reactive, defineExpose, nextTick, defineProps, computed } from 'vue';
  import { defHttp } from '/@/utils/http/axios';
  import { useMessage } from '/@/hooks/web/useMessage';
  import moment from 'moment';
  import { getValueType } from '/@/utils';
  import { saveOrUpdate } from '../MyOrg.api';
  import { Form } from 'ant-design-vue';
  
  const props = defineProps({
    disabled: { type: Boolean, default: false },
  });
  const formRef = ref();
  const useForm = Form.useForm;
  const emit = defineEmits(['register', 'ok']);
  const formData = reactive<Record<string, any>>({
    id: '',
    orgId: '',   
    id: '',
    orgName: '',   
    id: '',
    orgShortName: '',   
    id: '',
    zzjs: '',   
    id: '',
    jtCode: '',   
    id: '',
    jtCode2: '',   
    id: '',
    managerId: '',   
    id: '',
    areaCode: '',   
    id: '',
    znCode: '',   
    id: '',
    streetAddress: '',   
    id: '',
    postCode: '',   
    id: '',
    jjType: '',   
    id: '',
    hyType: '',   
    id: '',
    area: undefined,
    id: '',
    employersNumber: undefined,
    id: '',
    gdValue: undefined,
    id: '',
    anIncome: undefined,
    id: '',
    anProfit: undefined,
    id: '',
    mainProducts: '',   
    id: '',
    constructionType: '',   
    id: '',
    businessType: '',   
    id: '',
    parentOrgId: '',   
    id: '',
    orgLevel: undefined,
    id: '',
    upperSupervisorId: '',   
    id: '',
    upperSupervisorName: '',   
    id: '',
    displayOrder: '',   
    id: '',
    description: '',   
    id: '',
    operationCode: undefined,
    id: '',
    synTime: '',   
    id: '',
    synFlag: undefined,
    id: '',
    retryTimes: undefined,
    id: '',
    gkOrgId: '',   
    id: '',
    jsm: '',   
  });
  const { createMessage } = useMessage();
  const labelCol = ref<any>({ xs: { span: 24 }, sm: { span: 5 } });
  const wrapperCol = ref<any>({ xs: { span: 24 }, sm: { span: 16 } });
  const confirmLoading = ref<boolean>(false);
  //表单验证
  const validatorRules = {
    orgId: [{ required: true, message: '请输入orgId!'},],
    orgName: [{ required: true, message: '请输入orgName!'},],
    managerId: [{ required: true, message: '请输入managerId!'},],
    constructionType: [{ required: true, message: '请输入生产型企业，经营型企业，基建型企业，发展型企业，电煤供应企业!'},],
    businessType: [{ required: true, message: '请输入煤炭业务，发电 业务，化工业务，港口业务，航运业务，营销业务!'},],
    parentOrgId: [{ required: true, message: '请输入parentOrgId!'},],
    operationCode: [{ required: true, message: '请输入1－增加，2－删除，3－修改!'},],
    synTime: [{ required: true, message: '请输入同步时间(时间格式：yyyy-MM-dd HH:mm:ss!'},],
    synFlag: [{ required: true, message: '请输入1－已处理，0－未处理!'},],
    retryTimes: [{ required: true, message: '请输入默认值0，当一次处理成功则仍然为0；处理失败则加1!'},],
  };
  const { resetFields, validate, validateInfos } = useForm(formData, validatorRules, { immediate: true });
  
  /**
   * 新增
   */
  function add() {
    edit({});
  }

  /**
   * 编辑
   */
  function edit(record) {
    nextTick(() => {
      resetFields();
      //赋值
      Object.assign(formData, record);
    });
  }

  /**
   * 提交数据
   */
  async function submitForm() {
    // 触发表单验证
    await validate();
    confirmLoading.value = true;
    const isUpdate = ref<boolean>(false);
    //时间格式化
    let model = formData;
    if (model.id) {
      isUpdate.value = true;
    }
    //循环数据
    for (let data in model) {
      //如果该数据是数组并且是字符串类型
      if (model[data] instanceof Array) {
        let valueType = getValueType(formRef.value.getProps, data);
        //如果是字符串类型的需要变成以逗号分割的字符串
        if (valueType === 'string') {
          model[data] = model[data].join(',');
        }
      }
    }
    await saveOrUpdate(model, isUpdate.value)
      .then((res) => {
        if (res.success) {
          createMessage.success(res.message);
          emit('ok');
        } else {
          createMessage.warning(res.message);
        }
      })
      .finally(() => {
        confirmLoading.value = false;
      });
  }


  defineExpose({
    add,
    edit,
    submitForm,
  });
</script>

<style lang="less" scoped>
  .antd-modal-form {
    height: 500px !important;
    overflow-y: auto;
    padding: 24px 24px 24px 24px;
  }
</style>
