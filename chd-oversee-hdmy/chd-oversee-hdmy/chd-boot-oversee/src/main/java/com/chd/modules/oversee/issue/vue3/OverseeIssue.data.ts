import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '问题编号',
    align:"center",
    dataIndex: 'num'
   },
   {
    title: '标题',
    align:"center",
    dataIndex: 'title'
   },
   {
    title: '副标题',
    align:"center",
    dataIndex: 'subtitle'
   },
   {
    title: '具体问题id 优化具体问题大字段影响搜索 后续可能一个问题对应多个具体问题 则不使用此字段',
    align:"center",
    dataIndex: 'specificIssuesId'
   },
   {
    title: '提交状态 0 草稿 1提交',
    align:"center",
    dataIndex: 'submitState'
   },
   {
    title: '来源 0 巡视 1 巡查 2 专项检查',
    align:"center",
    dataIndex: 'source'
   },
   {
    title: '本部牵头部门id',
    align:"center",
    dataIndex: 'headquartersLeadDepartmentOrgId'
   },
   {
    title: '本部牵头部门经办人user_id',
    align:"center",
    dataIndex: 'headquartersLeadDepartmentManagerUserId'
   },
   {
    title: '责任单位id',
    align:"center",
    dataIndex: 'responsibleUnitOrgId'
   },
   {
    title: '督办部门id',
    align:"center",
    dataIndex: 'supervisorOrgId'
   },
   {
    title: '督办部门经办人user_id',
    align:"center",
    dataIndex: 'supervisorManagerUserId'
   },
   {
    title: '上报时间',
    align:"center",
    dataIndex: 'reportTime',
    customRender:({text}) =>{
      return !text?"":(text.length>10?text.substr(0,10):text)
    },
   },
   {
    title: '上报user_id',
    align:"center",
    dataIndex: 'reportUserId'
   },
   {
    title: '检查时间',
    align:"center",
    dataIndex: 'checkTime',
    customRender:({text}) =>{
      return !text?"":(text.length>10?text.substr(0,10):text)
    },
   },
   {
    title: '批准主体',
    align:"center",
    dataIndex: 'approvalBody'
   },
   {
    title: '问题大类ID',
    align:"center",
    dataIndex: 'issueCategoryId'
   },
   {
    title: '问题小类ID',
    align:"center",
    dataIndex: 'issueSubcategoryId'
   },
   {
    title: '严重程度 0 普通 1 重要 2严重',
    align:"center",
    dataIndex: 'severity'
   },
   {
    title: '是否需要督办 -1 不需要 1 需要',
    align:"center",
    dataIndex: 'isSupervise'
   },
   {
    title: '是否需要会签 -1 不需要 1 需要',
    align:"center",
    dataIndex: 'isSign'
   },
   {
    title: '创建用户id',
    align:"center",
    dataIndex: 'createUserId'
   },
   {
    title: '修改用户id',
    align:"center",
    dataIndex: 'updateUserId'
   },
   {
    title: '数据状态 -1 无效 1 有效',
    align:"center",
    dataIndex: 'dataType'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '问题编号',
    field: 'num',
    component: 'Input',
  },
  {
    label: '标题',
    field: 'title',
    component: 'Input',
  },
  {
    label: '副标题',
    field: 'subtitle',
    component: 'Input',
  },
  {
    label: '具体问题id 优化具体问题大字段影响搜索 后续可能一个问题对应多个具体问题 则不使用此字段',
    field: 'specificIssuesId',
    component: 'InputNumber',
  },
  {
    label: '提交状态 0 草稿 1提交',
    field: 'submitState',
    component: 'InputNumber',
  },
  {
    label: '来源 0 巡视 1 巡查 2 专项检查',
    field: 'source',
    component: 'InputNumber',
  },
  {
    label: '本部牵头部门id',
    field: 'headquartersLeadDepartmentOrgId',
    component: 'Input',
  },
  {
    label: '本部牵头部门经办人user_id',
    field: 'headquartersLeadDepartmentManagerUserId',
    component: 'Input',
  },
  {
    label: '责任单位id',
    field: 'responsibleUnitOrgId',
    component: 'Input',
  },
  {
    label: '督办部门id',
    field: 'supervisorOrgId',
    component: 'Input',
  },
  {
    label: '督办部门经办人user_id',
    field: 'supervisorManagerUserId',
    component: 'Input',
  },
  {
    label: '上报时间',
    field: 'reportTime',
    component: 'DatePicker',
  },
  {
    label: '上报user_id',
    field: 'reportUserId',
    component: 'Input',
  },
  {
    label: '检查时间',
    field: 'checkTime',
    component: 'DatePicker',
  },
  {
    label: '批准主体',
    field: 'approvalBody',
    component: 'Input',
  },
  {
    label: '问题大类ID',
    field: 'issueCategoryId',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入问题大类ID!'},
          ];
     },
  },
  {
    label: '问题小类ID',
    field: 'issueSubcategoryId',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入问题小类ID!'},
          ];
     },
  },
  {
    label: '严重程度 0 普通 1 重要 2严重',
    field: 'severity',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入严重程度 0 普通 1 重要 2严重!'},
          ];
     },
  },
  {
    label: '是否需要督办 -1 不需要 1 需要',
    field: 'isSupervise',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入是否需要督办 -1 不需要 1 需要!'},
          ];
     },
  },
  {
    label: '是否需要会签 -1 不需要 1 需要',
    field: 'isSign',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入是否需要会签 -1 不需要 1 需要!'},
          ];
     },
  },
  {
    label: '创建用户id',
    field: 'createUserId',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入创建用户id!'},
          ];
     },
  },
  {
    label: '修改用户id',
    field: 'updateUserId',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入修改用户id!'},
          ];
     },
  },
  {
    label: '数据状态 -1 无效 1 有效',
    field: 'dataType',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入数据状态 -1 无效 1 有效!'},
          ];
     },
  },
	// TODO 主键隐藏字段，目前写死为ID
	{
	  label: '',
	  field: 'id',
	  component: 'Input',
	  show: false
	},
];
