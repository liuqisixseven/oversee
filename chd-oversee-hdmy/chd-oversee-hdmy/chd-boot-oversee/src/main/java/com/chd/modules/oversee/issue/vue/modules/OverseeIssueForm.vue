<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="问题编号" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="num">
              <a-input v-model="model.num" placeholder="请输入问题编号"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="标题" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="title">
              <a-input v-model="model.title" placeholder="请输入标题"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="副标题" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="subtitle">
              <a-input v-model="model.subtitle" placeholder="请输入副标题"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="具体问题id 优化具体问题大字段影响搜索 后续可能一个问题对应多个具体问题 则不使用此字段" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="specificIssuesId">
              <a-input-number v-model="model.specificIssuesId" placeholder="请输入具体问题id 优化具体问题大字段影响搜索 后续可能一个问题对应多个具体问题 则不使用此字段" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="提交状态 0 草稿 1提交" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="submitState">
              <a-input-number v-model="model.submitState" placeholder="请输入提交状态 0 草稿 1提交" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="来源 0 巡视 1 巡查 2 专项检查" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="source">
              <a-input-number v-model="model.source" placeholder="请输入来源 0 巡视 1 巡查 2 专项检查" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="本部牵头部门id" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="headquartersLeadDepartmentOrgId">
              <a-input v-model="model.headquartersLeadDepartmentOrgId" placeholder="请输入本部牵头部门id"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="本部牵头部门经办人user_id" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="headquartersLeadDepartmentManagerUserId">
              <a-input v-model="model.headquartersLeadDepartmentManagerUserId" placeholder="请输入本部牵头部门经办人user_id"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="责任单位id" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="responsibleUnitOrgId">
              <a-input v-model="model.responsibleUnitOrgId" placeholder="请输入责任单位id"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="督办部门id" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="supervisorOrgId">
              <a-input v-model="model.supervisorOrgId" placeholder="请输入督办部门id"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="督办部门经办人user_id" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="supervisorManagerUserId">
              <a-input v-model="model.supervisorManagerUserId" placeholder="请输入督办部门经办人user_id"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="上报时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="reportTime">
              <j-date placeholder="请选择上报时间" v-model="model.reportTime"  style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="上报user_id" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="reportUserId">
              <a-input v-model="model.reportUserId" placeholder="请输入上报user_id"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="检查时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="checkTime">
              <j-date placeholder="请选择检查时间" v-model="model.checkTime"  style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="批准主体" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="approvalBody">
              <a-input v-model="model.approvalBody" placeholder="请输入批准主体"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="问题大类ID" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="issueCategoryId">
              <a-input-number v-model="model.issueCategoryId" placeholder="请输入问题大类ID" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="问题小类ID" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="issueSubcategoryId">
              <a-input-number v-model="model.issueSubcategoryId" placeholder="请输入问题小类ID" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="严重程度 0 普通 1 重要 2严重" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="severity">
              <a-input-number v-model="model.severity" placeholder="请输入严重程度 0 普通 1 重要 2严重" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="是否需要督办 -1 不需要 1 需要" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isSupervise">
              <a-input-number v-model="model.isSupervise" placeholder="请输入是否需要督办 -1 不需要 1 需要" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="是否需要会签 -1 不需要 1 需要" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isSign">
              <a-input-number v-model="model.isSign" placeholder="请输入是否需要会签 -1 不需要 1 需要" style="width: 100%" />
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
    name: 'OverseeIssueForm',
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
           issueCategoryId: [
              { required: true, message: '请输入问题大类ID!'},
           ],
           issueSubcategoryId: [
              { required: true, message: '请输入问题小类ID!'},
           ],
           severity: [
              { required: true, message: '请输入严重程度 0 普通 1 重要 2严重!'},
           ],
           isSupervise: [
              { required: true, message: '请输入是否需要督办 -1 不需要 1 需要!'},
           ],
           isSign: [
              { required: true, message: '请输入是否需要会签 -1 不需要 1 需要!'},
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
          add: "/issue/overseeIssue/add",
          edit: "/issue/overseeIssue/edit",
          queryById: "/issue/overseeIssue/queryById"
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