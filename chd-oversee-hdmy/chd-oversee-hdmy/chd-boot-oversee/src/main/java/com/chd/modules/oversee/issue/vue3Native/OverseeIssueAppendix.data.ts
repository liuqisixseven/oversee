import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '附件路径地址',
    align: "center",
    dataIndex: 'appendixPath'
  },
  {
    title: '附件类型 1 问题录入 2问题分配 3整改措施',
    align: "center",
    dataIndex: 'type'
  },
  {
    title: '文件类型 0 无 1图片 2pdf 此字段不一定启用',
    align: "center",
    dataIndex: 'fileType'
  },
  {
    title: '问题ID',
    align: "center",
    dataIndex: 'issueId'
  },
  {
    title: '排序 越小越靠前',
    align: "center",
    dataIndex: 'sort'
  },
  {
    title: '创建用户id',
    align: "center",
    dataIndex: 'createUserId'
  },
  {
    title: '修改用户id',
    align: "center",
    dataIndex: 'updateUserId'
  },
  {
    title: '数据状态 -1 无效 1 有效',
    align: "center",
    dataIndex: 'dataType'
  },
];

//查询数据
export const searchFormSchema: FormSchema[] = [
];

//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '附件路径地址',
    field: 'appendixPath',
    component: 'Input',
  },
  {
    label: '附件类型 1 问题录入 2问题分配 3整改措施',
    field: 'type',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
      return [
              { required: true, message: '请输入附件类型 1 问题录入 2问题分配 3整改措施!'},
             ];
    },
  },
  {
    label: '文件类型 0 无 1图片 2pdf 此字段不一定启用',
    field: 'fileType',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
      return [
              { required: true, message: '请输入文件类型 0 无 1图片 2pdf 此字段不一定启用!'},
             ];
    },
  },
  {
    label: '问题ID',
    field: 'issueId',
    component: 'InputNumber',
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
    show: false,
  },
];
