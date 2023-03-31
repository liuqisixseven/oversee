<template>
  <a-spin :spinning="confirmLoading">
    <a-form ref="formRef" class="antd-modal-form" :labelCol="labelCol" :wrapperCol="wrapperCol">
      <a-row>
        <a-col :span="24">
          <a-form-item label="问题编号" v-bind="validateInfos.num">
            <a-input v-model:value="formData.num" placeholder="请输入问题编号" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="标题" v-bind="validateInfos.title">
            <a-input v-model:value="formData.title" placeholder="请输入标题" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="副标题" v-bind="validateInfos.subtitle">
            <a-input v-model:value="formData.subtitle" placeholder="请输入副标题" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="具体问题id 优化具体问题大字段影响搜索 后续可能一个问题对应多个具体问题 则不使用此字段" v-bind="validateInfos.specificIssuesId">
	          <a-input-number v-model:value="formData.specificIssuesId" placeholder="请输入具体问题id 优化具体问题大字段影响搜索 后续可能一个问题对应多个具体问题 则不使用此字段" style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="提交状态 0 草稿 1提交" v-bind="validateInfos.submitState">
	          <a-input-number v-model:value="formData.submitState" placeholder="请输入提交状态 0 草稿 1提交" style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="来源 0 巡视 1 巡查 2 专项检查" v-bind="validateInfos.source">
	          <a-input-number v-model:value="formData.source" placeholder="请输入来源 0 巡视 1 巡查 2 专项检查" style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="本部牵头部门id" v-bind="validateInfos.headquartersLeadDepartmentOrgId">
            <a-input v-model:value="formData.headquartersLeadDepartmentOrgId" placeholder="请输入本部牵头部门id" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="本部牵头部门经办人user_id" v-bind="validateInfos.headquartersLeadDepartmentManagerUserId">
            <a-input v-model:value="formData.headquartersLeadDepartmentManagerUserId" placeholder="请输入本部牵头部门经办人user_id" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="责任单位id" v-bind="validateInfos.responsibleUnitOrgId">
            <a-input v-model:value="formData.responsibleUnitOrgId" placeholder="请输入责任单位id" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="督办部门id" v-bind="validateInfos.supervisorOrgId">
            <a-input v-model:value="formData.supervisorOrgId" placeholder="请输入督办部门id" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="督办部门经办人user_id" v-bind="validateInfos.supervisorManagerUserId">
            <a-input v-model:value="formData.supervisorManagerUserId" placeholder="请输入督办部门经办人user_id" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="上报时间" v-bind="validateInfos.reportTime">
		        <a-date-picker placeholder="请选择上报时间" v-model:value="formData.reportTime" value-format="YYYY-MM-DD"  style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="上报user_id" v-bind="validateInfos.reportUserId">
            <a-input v-model:value="formData.reportUserId" placeholder="请输入上报user_id" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="检查时间" v-bind="validateInfos.checkTime">
		        <a-date-picker placeholder="请选择检查时间" v-model:value="formData.checkTime" value-format="YYYY-MM-DD"  style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="批准主体" v-bind="validateInfos.approvalBody">
            <a-input v-model:value="formData.approvalBody" placeholder="请输入批准主体" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="问题大类ID" v-bind="validateInfos.issueCategoryId">
	          <a-input-number v-model:value="formData.issueCategoryId" placeholder="请输入问题大类ID" style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="问题小类ID" v-bind="validateInfos.issueSubcategoryId">
	          <a-input-number v-model:value="formData.issueSubcategoryId" placeholder="请输入问题小类ID" style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="严重程度 0 普通 1 重要 2严重" v-bind="validateInfos.severity">
	          <a-input-number v-model:value="formData.severity" placeholder="请输入严重程度 0 普通 1 重要 2严重" style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="是否需要督办 -1 不需要 1 需要" v-bind="validateInfos.isSupervise">
	          <a-input-number v-model:value="formData.isSupervise" placeholder="请输入是否需要督办 -1 不需要 1 需要" style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="是否需要会签 -1 不需要 1 需要" v-bind="validateInfos.isSign">
	          <a-input-number v-model:value="formData.isSign" placeholder="请输入是否需要会签 -1 不需要 1 需要" style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="创建用户id" v-bind="validateInfos.createUserId">
            <a-input v-model:value="formData.createUserId" placeholder="请输入创建用户id" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="修改用户id" v-bind="validateInfos.updateUserId">
            <a-input v-model:value="formData.updateUserId" placeholder="请输入修改用户id" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="数据状态 -1 无效 1 有效" v-bind="validateInfos.dataType">
	          <a-input-number v-model:value="formData.dataType" placeholder="请输入数据状态 -1 无效 1 有效" style="width: 100%" :disabled="disabled"/>
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
  import { saveOrUpdate } from '../OverseeIssue.api';
  import { Form } from 'ant-design-vue';
  
  const props = defineProps({
    disabled: { type: Boolean, default: false },
  });
  const formRef = ref();
  const useForm = Form.useForm;
  const emit = defineEmits(['register', 'ok']);
  const formData = reactive<Record<string, any>>({
    id: '',
    num: '',   
    id: '',
    title: '',   
    id: '',
    subtitle: '',   
    id: '',
    specificIssuesId: undefined,
    id: '',
    submitState: undefined,
    id: '',
    source: undefined,
    id: '',
    headquartersLeadDepartmentOrgId: '',   
    id: '',
    headquartersLeadDepartmentManagerUserId: '',   
    id: '',
    responsibleUnitOrgId: '',   
    id: '',
    supervisorOrgId: '',   
    id: '',
    supervisorManagerUserId: '',   
    id: '',
    reportTime: '',   
    id: '',
    reportUserId: '',   
    id: '',
    checkTime: '',   
    id: '',
    approvalBody: '',   
    id: '',
    issueCategoryId: undefined,
    id: '',
    issueSubcategoryId: undefined,
    id: '',
    severity: undefined,
    id: '',
    isSupervise: undefined,
    id: '',
    isSign: undefined,
    id: '',
    createUserId: '',   
    id: '',
    updateUserId: '',   
    id: '',
    dataType: undefined,
  });
  const { createMessage } = useMessage();
  const labelCol = ref<any>({ xs: { span: 24 }, sm: { span: 5 } });
  const wrapperCol = ref<any>({ xs: { span: 24 }, sm: { span: 16 } });
  const confirmLoading = ref<boolean>(false);
  //表单验证
  const validatorRules = {
    issueCategoryId: [{ required: true, message: '请输入问题大类ID!'},],
    issueSubcategoryId: [{ required: true, message: '请输入问题小类ID!'},],
    severity: [{ required: true, message: '请输入严重程度 0 普通 1 重要 2严重!'},],
    isSupervise: [{ required: true, message: '请输入是否需要督办 -1 不需要 1 需要!'},],
    isSign: [{ required: true, message: '请输入是否需要会签 -1 不需要 1 需要!'},],
    createUserId: [{ required: true, message: '请输入创建用户id!'},],
    updateUserId: [{ required: true, message: '请输入修改用户id!'},],
    dataType: [{ required: true, message: '请输入数据状态 -1 无效 1 有效!'},],
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
