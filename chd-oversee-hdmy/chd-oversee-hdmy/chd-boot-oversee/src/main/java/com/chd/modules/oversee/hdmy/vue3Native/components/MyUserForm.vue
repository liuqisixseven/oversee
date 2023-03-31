<template>
  <a-spin :spinning="confirmLoading">
    <a-form ref="formRef" class="antd-modal-form" :labelCol="labelCol" :wrapperCol="wrapperCol">
      <a-row>
        <a-col :span="24">
          <a-form-item label="用户id" v-bind="validateInfos.userId">
            <a-input v-model:value="formData.userId" placeholder="请输入用户id" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="userName" v-bind="validateInfos.userName">
            <a-input v-model:value="formData.userName" placeholder="请输入userName" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="地址" v-bind="validateInfos.address">
            <a-input v-model:value="formData.address" placeholder="请输入地址" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="密码" v-bind="validateInfos.password">
            <a-input v-model:value="formData.password" placeholder="请输入密码" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="promptQuestion" v-bind="validateInfos.promptQuestion">
            <a-input v-model:value="formData.promptQuestion" placeholder="请输入promptQuestion" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="promptAnswer" v-bind="validateInfos.promptAnswer">
            <a-input v-model:value="formData.promptAnswer" placeholder="请输入promptAnswer" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="sex" v-bind="validateInfos.sex">
            <a-input v-model:value="formData.sex" placeholder="请输入sex" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="employerNumber" v-bind="validateInfos.employerNumber">
            <a-input v-model:value="formData.employerNumber" placeholder="请输入employerNumber" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="telNumber" v-bind="validateInfos.telNumber">
            <a-input v-model:value="formData.telNumber" placeholder="请输入telNumber" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="mobile" v-bind="validateInfos.mobile">
            <a-input v-model:value="formData.mobile" placeholder="请输入mobile" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="fasNumber" v-bind="validateInfos.fasNumber">
            <a-input v-model:value="formData.fasNumber" placeholder="请输入fasNumber" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="mail" v-bind="validateInfos.mail">
            <a-input v-model:value="formData.mail" placeholder="请输入mail" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="titile" v-bind="validateInfos.titile">
            <a-input v-model:value="formData.titile" placeholder="请输入titile" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="1－可用，0－不可用" v-bind="validateInfos.enable">
	          <a-input-number v-model:value="formData.enable" placeholder="请输入1－可用，0－不可用" style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="orgId" v-bind="validateInfos.orgId">
            <a-input v-model:value="formData.orgId" placeholder="请输入orgId" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="1-正职，2-副职，3-员工" v-bind="validateInfos.orgDuty">
            <a-input v-model:value="formData.orgDuty" placeholder="请输入1-正职，2-副职，3-员工" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="员工在部门的排序（3位编码，排序最优先为000）" v-bind="validateInfos.displayOrder">
            <a-input v-model:value="formData.displayOrder" placeholder="请输入员工在部门的排序（3位编码，排序最优先为000）" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="用户属于其他部门ID，如果存在多个部门，以“，”分隔" v-bind="validateInfos.otherOrgId">
            <a-input v-model:value="formData.otherOrgId" placeholder="请输入用户属于其他部门ID，如果存在多个部门，以“，”分隔" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="1－增加，2－删除，3－修改，4—修改密码" v-bind="validateInfos.operationCode">
	          <a-input-number v-model:value="formData.operationCode" placeholder="请输入1－增加，2－删除，3－修改，4—修改密码" style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="格式为YYYY-MM-DD hh:mm:ss" v-bind="validateInfos.synTime">
            <a-input v-model:value="formData.synTime" placeholder="请输入格式为YYYY-MM-DD hh:mm:ss" :disabled="disabled"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="1-已处理，0-未处理" v-bind="validateInfos.synFlag">
	          <a-input-number v-model:value="formData.synFlag" placeholder="请输入1-已处理，0-未处理" style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="默认值为0，当一次处理成功则仍然为0；当处理失败时累计加1" v-bind="validateInfos.retryTime">
	          <a-input-number v-model:value="formData.retryTime" placeholder="请输入默认值为0，当一次处理成功则仍然为0；当处理失败时累计加1" style="width: 100%" :disabled="disabled"/>
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
  import { saveOrUpdate } from '../MyUser.api';
  import { Form } from 'ant-design-vue';
  
  const props = defineProps({
    disabled: { type: Boolean, default: false },
  });
  const formRef = ref();
  const useForm = Form.useForm;
  const emit = defineEmits(['register', 'ok']);
  const formData = reactive<Record<string, any>>({
    id: '',
    userId: '',   
    id: '',
    userName: '',   
    id: '',
    address: '',   
    id: '',
    password: '',   
    id: '',
    promptQuestion: '',   
    id: '',
    promptAnswer: '',   
    id: '',
    sex: '',   
    id: '',
    employerNumber: '',   
    id: '',
    telNumber: '',   
    id: '',
    mobile: '',   
    id: '',
    fasNumber: '',   
    id: '',
    mail: '',   
    id: '',
    titile: '',   
    id: '',
    enable: undefined,
    id: '',
    orgId: '',   
    id: '',
    orgDuty: '',   
    id: '',
    displayOrder: '',   
    id: '',
    otherOrgId: '',   
    id: '',
    operationCode: undefined,
    id: '',
    synTime: '',   
    id: '',
    synFlag: undefined,
    id: '',
    retryTime: undefined,
  });
  const { createMessage } = useMessage();
  const labelCol = ref<any>({ xs: { span: 24 }, sm: { span: 5 } });
  const wrapperCol = ref<any>({ xs: { span: 24 }, sm: { span: 16 } });
  const confirmLoading = ref<boolean>(false);
  //表单验证
  const validatorRules = {
    userId: [{ required: true, message: '请输入用户id!'},],
    userName: [{ required: true, message: '请输入userName!'},],
    password: [{ required: true, message: '请输入密码!'},],
    enable: [{ required: true, message: '请输入1－可用，0－不可用!'},],
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
