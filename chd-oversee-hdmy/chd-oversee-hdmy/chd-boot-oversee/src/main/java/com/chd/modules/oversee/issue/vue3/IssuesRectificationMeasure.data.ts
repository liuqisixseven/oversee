import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '问题ID',
    align:"center",
    dataIndex: 'issueId'
   },
   {
    title: '问题分配ID，如果一个问题对应多个分配，则此字段必填',
    align:"center",
    dataIndex: 'issuesAllocationId'
   },
   {
    title: '整改措施',
    align:"center",
    dataIndex: 'rectificationMeasureContent'
   },
   {
    title: '整改时限',
    align:"center",
    dataIndex: 'rectificationTimeLimit',
    customRender:({text}) =>{
      return !text?"":(text.length>10?text.substr(0,10):text)
    },
   },
   {
    title: '排序 越小越靠前',
    align:"center",
    dataIndex: 'sort'
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
    label: '问题ID',
    field: 'issueId',
    component: 'InputNumber',
  },
  {
    label: '问题分配ID，如果一个问题对应多个分配，则此字段必填',
    field: 'issuesAllocationId',
    component: 'InputNumber',
  },
  {
    label: '整改措施',
    field: 'rectificationMeasureContent',
    component: 'InputTextArea',
  },
  {
    label: '整改时限',
    field: 'rectificationTimeLimit',
    component: 'DatePicker',
  },
  {
    label: '排序 越小越靠前',
    field: 'sort',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入排序 越小越靠前!'},
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
