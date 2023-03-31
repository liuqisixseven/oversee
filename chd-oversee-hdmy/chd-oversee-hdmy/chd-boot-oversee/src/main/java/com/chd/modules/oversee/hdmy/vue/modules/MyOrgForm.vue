<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="orgId" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="orgId">
              <a-input v-model="model.orgId" placeholder="请输入orgId"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="orgName" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="orgName">
              <a-input v-model="model.orgName" placeholder="请输入orgName"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="orgShortName" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="orgShortName">
              <a-input v-model="model.orgShortName" placeholder="请输入orgShortName"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="zzjs" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="zzjs">
              <a-input v-model="model.zzjs" placeholder="请输入zzjs"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="jtCode" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="jtCode">
              <a-input v-model="model.jtCode" placeholder="请输入jtCode"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="jtCode2" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="jtCode2">
              <a-input v-model="model.jtCode2" placeholder="请输入jtCode2"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="managerId" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="managerId">
              <a-input v-model="model.managerId" placeholder="请输入managerId"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="areaCode" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="areaCode">
              <a-input v-model="model.areaCode" placeholder="请输入areaCode"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="znCode" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="znCode">
              <a-input v-model="model.znCode" placeholder="请输入znCode"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="streetAddress" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="streetAddress">
              <a-input v-model="model.streetAddress" placeholder="请输入streetAddress"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="postCode" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="postCode">
              <a-input v-model="model.postCode" placeholder="请输入postCode"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="国有经济、集体经济、私营经济、有限责任公司、联营经济、股份合作、外商投资、港澳台投资、其他经济" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="jjType">
              <a-input v-model="model.jjType" placeholder="请输入国有经济、集体经济、私营经济、有限责任公司、联营经济、股份合作、外商投资、港澳台投资、其他经济"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="农、林、牧、渔业，采掘业，制造业，电力、煤气及水的生成和供应业，建筑业，地址勘查业、水利管理业，交通运输仓储业及邮电通信业，批发和零售贸易、餐饮业，金融保险业，房地产业，社会服务业，卫生、体育和社会福利业，教育文化艺术及广电业，科学研究和综合技术服务业，国家机关政党机关和社会团体，其他行业" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="hyType">
              <a-input v-model="model.hyType" placeholder="请输入农、林、牧、渔业，采掘业，制造业，电力、煤气及水的生成和供应业，建筑业，地址勘查业、水利管理业，交通运输仓储业及邮电通信业，批发和零售贸易、餐饮业，金融保险业，房地产业，社会服务业，卫生、体育和社会福利业，教育文化艺术及广电业，科学研究和综合技术服务业，国家机关政党机关和社会团体，其他行业"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="area" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="area">
              <a-input-number v-model="model.area" placeholder="请输入area" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="employersNumber" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="employersNumber">
              <a-input-number v-model="model.employersNumber" placeholder="请输入employersNumber" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="gdValue" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="gdValue">
              <a-input-number v-model="model.gdValue" placeholder="请输入gdValue" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="anIncome" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="anIncome">
              <a-input-number v-model="model.anIncome" placeholder="请输入anIncome" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="anProfit" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="anProfit">
              <a-input-number v-model="model.anProfit" placeholder="请输入anProfit" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="mainProducts" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="mainProducts">
              <a-input v-model="model.mainProducts" placeholder="请输入mainProducts"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="生产型企业，经营型企业，基建型企业，发展型企业，电煤供应企业" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="constructionType">
              <a-input v-model="model.constructionType" placeholder="请输入生产型企业，经营型企业，基建型企业，发展型企业，电煤供应企业"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="煤炭业务，发电 业务，化工业务，港口业务，航运业务，营销业务" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="businessType">
              <a-input v-model="model.businessType" placeholder="请输入煤炭业务，发电 业务，化工业务，港口业务，航运业务，营销业务"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="parentOrgId" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="parentOrgId">
              <a-input v-model="model.parentOrgId" placeholder="请输入parentOrgId"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="标识组织节点所在的级别，如一级为公司，二级为部门，三级为处室等，依此类推" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="orgLevel">
              <a-input-number v-model="model.orgLevel" placeholder="请输入标识组织节点所在的级别，如一级为公司，二级为部门，三级为处室等，依此类推" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="upperSupervisorId" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="upperSupervisorId">
              <a-input v-model="model.upperSupervisorId" placeholder="请输入upperSupervisorId"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="upperSupervisorName" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="upperSupervisorName">
              <a-input v-model="model.upperSupervisorName" placeholder="请输入upperSupervisorName"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="displayOrder" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="displayOrder">
              <a-input v-model="model.displayOrder" placeholder="请输入displayOrder"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="description" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="description">
              <a-input v-model="model.description" placeholder="请输入description"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="1－增加，2－删除，3－修改" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="operationCode">
              <a-input-number v-model="model.operationCode" placeholder="请输入1－增加，2－删除，3－修改" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="同步时间(时间格式：yyyy-MM-dd HH:mm:ss" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="synTime">
              <a-input v-model="model.synTime" placeholder="请输入同步时间(时间格式：yyyy-MM-dd HH:mm:ss"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="1－已处理，0－未处理" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="synFlag">
              <a-input-number v-model="model.synFlag" placeholder="请输入1－已处理，0－未处理" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="默认值0，当一次处理成功则仍然为0；处理失败则加1" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="retryTimes">
              <a-input-number v-model="model.retryTimes" placeholder="请输入默认值0，当一次处理成功则仍然为0；处理失败则加1" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="gkOrgId" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="gkOrgId">
              <a-input v-model="model.gkOrgId" placeholder="请输入gkOrgId"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="jsm" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="jsm">
              <a-input v-model="model.jsm" placeholder="请输入jsm"  ></a-input>
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
    name: 'MyOrgForm',
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
           orgId: [
              { required: true, message: '请输入orgId!'},
           ],
           orgName: [
              { required: true, message: '请输入orgName!'},
           ],
           managerId: [
              { required: true, message: '请输入managerId!'},
           ],
           constructionType: [
              { required: true, message: '请输入生产型企业，经营型企业，基建型企业，发展型企业，电煤供应企业!'},
           ],
           businessType: [
              { required: true, message: '请输入煤炭业务，发电 业务，化工业务，港口业务，航运业务，营销业务!'},
           ],
           parentOrgId: [
              { required: true, message: '请输入parentOrgId!'},
           ],
           operationCode: [
              { required: true, message: '请输入1－增加，2－删除，3－修改!'},
           ],
           synTime: [
              { required: true, message: '请输入同步时间(时间格式：yyyy-MM-dd HH:mm:ss!'},
           ],
           synFlag: [
              { required: true, message: '请输入1－已处理，0－未处理!'},
           ],
           retryTimes: [
              { required: true, message: '请输入默认值0，当一次处理成功则仍然为0；处理失败则加1!'},
           ],
        },
        url: {
          add: "/hdmy/myOrg/add",
          edit: "/hdmy/myOrg/edit",
          queryById: "/hdmy/myOrg/queryById"
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