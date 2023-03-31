<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="用户id" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="userId">
              <a-input v-model="model.userId" placeholder="请输入用户id"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="userName" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="userName">
              <a-input v-model="model.userName" placeholder="请输入userName"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="地址" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="address">
              <a-input v-model="model.address" placeholder="请输入地址"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="密码" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="password">
              <a-input v-model="model.password" placeholder="请输入密码"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="promptQuestion" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="promptQuestion">
              <a-input v-model="model.promptQuestion" placeholder="请输入promptQuestion"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="promptAnswer" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="promptAnswer">
              <a-input v-model="model.promptAnswer" placeholder="请输入promptAnswer"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="sex" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="sex">
              <a-input v-model="model.sex" placeholder="请输入sex"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="employerNumber" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="employerNumber">
              <a-input v-model="model.employerNumber" placeholder="请输入employerNumber"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="telNumber" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="telNumber">
              <a-input v-model="model.telNumber" placeholder="请输入telNumber"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="mobile" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="mobile">
              <a-input v-model="model.mobile" placeholder="请输入mobile"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="fasNumber" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="fasNumber">
              <a-input v-model="model.fasNumber" placeholder="请输入fasNumber"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="mail" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="mail">
              <a-input v-model="model.mail" placeholder="请输入mail"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="titile" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="titile">
              <a-input v-model="model.titile" placeholder="请输入titile"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="1－可用，0－不可用" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="enable">
              <a-input-number v-model="model.enable" placeholder="请输入1－可用，0－不可用" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="orgId" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="orgId">
              <a-input v-model="model.orgId" placeholder="请输入orgId"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="1-正职，2-副职，3-员工" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="orgDuty">
              <a-input v-model="model.orgDuty" placeholder="请输入1-正职，2-副职，3-员工"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="员工在部门的排序（3位编码，排序最优先为000）" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="displayOrder">
              <a-input v-model="model.displayOrder" placeholder="请输入员工在部门的排序（3位编码，排序最优先为000）"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="用户属于其他部门ID，如果存在多个部门，以“，”分隔" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="otherOrgId">
              <a-input v-model="model.otherOrgId" placeholder="请输入用户属于其他部门ID，如果存在多个部门，以“，”分隔"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="1－增加，2－删除，3－修改，4—修改密码" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="operationCode">
              <a-input-number v-model="model.operationCode" placeholder="请输入1－增加，2－删除，3－修改，4—修改密码" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="格式为YYYY-MM-DD hh:mm:ss" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="synTime">
              <a-input v-model="model.synTime" placeholder="请输入格式为YYYY-MM-DD hh:mm:ss"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="1-已处理，0-未处理" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="synFlag">
              <a-input-number v-model="model.synFlag" placeholder="请输入1-已处理，0-未处理" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="默认值为0，当一次处理成功则仍然为0；当处理失败时累计加1" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="retryTime">
              <a-input-number v-model="model.retryTime" placeholder="请输入默认值为0，当一次处理成功则仍然为0；当处理失败时累计加1" style="width: 100%" />
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </j-form-container>
  </a-spin>
</template>

<script>

  import { httpAction, getAction } from '@/api/manage'
  import { validateDuplicateValue } from '@/utils/util'

  export default {
    name: 'MyUserForm',
    components: {
    },
    props: {
      //表单禁用
      disabled: {
        type: Boolean,
        default: false,
        required: false
      }
    },
    data () {
      return {
        model:{
         },
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        confirmLoading: false,
        validatorRules: {
           userId: [
              { required: true, message: '请输入用户id!'},
           ],
           userName: [
              { required: true, message: '请输入userName!'},
           ],
           password: [
              { required: true, message: '请输入密码!'},
           ],
           enable: [
              { required: true, message: '请输入1－可用，0－不可用!'},
           ],
        },
        url: {
          add: "/hdmy/myUser/add",
          edit: "/hdmy/myUser/edit",
          queryById: "/hdmy/myUser/queryById"
        }
      }
    },
    computed: {
      formDisabled(){
        return this.disabled
      },
    },
    created () {
       //备份model原始值
      this.modelDefault = JSON.parse(JSON.stringify(this.model));
    },
    methods: {
      add () {
        this.edit(this.modelDefault);
      },
      edit (record) {
        this.model = Object.assign({}, record);
        this.visible = true;
      },
      submitForm () {
        const that = this;
        // 触发表单验证
        this.$refs.form.validate(valid => {
          if (valid) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
               method = 'put';
            }
            httpAction(httpurl,this.model,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
            })
          }
         
        })
      },
    }
  }
</script>