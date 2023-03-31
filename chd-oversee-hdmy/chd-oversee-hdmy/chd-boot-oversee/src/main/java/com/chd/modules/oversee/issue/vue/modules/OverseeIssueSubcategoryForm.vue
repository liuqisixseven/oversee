<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="name">
              <a-input v-model="model.name" placeholder="请输入名称"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="问题大类ID" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="issueCategoryId">
              <a-input-number v-model="model.issueCategoryId" placeholder="请输入问题大类ID" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="排序 越小越靠前" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="sort">
              <a-input-number v-model="model.sort" placeholder="请输入排序 越小越靠前" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="创建用户id" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="createUserId">
              <a-input v-model="model.createUserId" placeholder="请输入创建用户id"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="修改用户id" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="updateUserId">
              <a-input v-model="model.updateUserId" placeholder="请输入修改用户id"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="数据状态 -1 无效 1 有效" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="dataType">
              <a-input-number v-model="model.dataType" placeholder="请输入数据状态 -1 无效 1 有效" style="width: 100%" />
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
    name: 'OverseeIssueSubcategoryForm',
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
           name: [
              { required: true, message: '请输入名称!'},
           ],
           issueCategoryId: [
              { required: true, message: '请输入问题大类ID!'},
           ],
           sort: [
              { required: true, message: '请输入排序 越小越靠前!'},
           ],
           createUserId: [
              { required: true, message: '请输入创建用户id!'},
           ],
           updateUserId: [
              { required: true, message: '请输入修改用户id!'},
           ],
           dataType: [
              { required: true, message: '请输入数据状态 -1 无效 1 有效!'},
           ],
        },
        url: {
          add: "/issue/overseeIssueSubcategory/add",
          edit: "/issue/overseeIssueSubcategory/edit",
          queryById: "/issue/overseeIssueSubcategory/queryById"
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