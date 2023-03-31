<template>
  <a-spin :spinning="confirmLoading">
    <a-form ref="formRef" class="antd-modal-form" :labelCol="labelCol" :wrapperCol="wrapperCol">
      <a-row>
        <a-col :span="24">
          <a-form-item label="问题ID" v-bind="validateInfos.issueId">
	          <a-input-number v-model:value="formData.issueId" placeholder="请输入问题ID" style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="责任单位的责任部门id" v-bind="validateInfos.responsibleDepartmentOrgId">
            <a-input v-model:value="formData.responsibleDepartmentOrgId" placeholder="请输入责任单位的责任部门id" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="责任单位的责任部门经办人user_id" v-bind="validateInfos.responsibleDepartmentManagerUserId">
            <a-input v-model:value="formData.responsibleDepartmentManagerUserId" placeholder="请输入责任单位的责任部门经办人user_id" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="排序 越小越靠前" v-bind="validateInfos.sort">
	          <a-input-number v-model:value="formData.sort" placeholder="请输入排序 越小越靠前" style="width: 100%" :disabled="disabled"/>
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
  import { saveOrUpdate } from '../IssuesAllocation.api';
  import { Form } from 'ant-design-vue';
  
  const props = defineProps({
    disabled: { type: Boolean, default: false },
  });
  const formRef = ref();
  const useForm = Form.useForm;
  const emit = defineEmits(['register', 'ok']);
  const formData = reactive<Record<string, any>>({
    id: '',
    issueId: undefined,
    id: '',
    responsibleDepartmentOrgId: '',   
    id: '',
    responsibleDepartmentManagerUserId: '',   
    id: '',
    sort: undefined,
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
    sort: [{ required: true, message: '请输入排序 越小越靠前!'},],
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
