import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '用户id',
    align: "center",
    dataIndex: 'userId'
  },
  {
    title: 'userName',
    align: "center",
    dataIndex: 'userName'
  },
  {
    title: '地址',
    align: "center",
    dataIndex: 'address'
  },
  {
    title: '密码',
    align: "center",
    dataIndex: 'password'
  },
  {
    title: 'promptQuestion',
    align: "center",
    dataIndex: 'promptQuestion'
  },
  {
    title: 'promptAnswer',
    align: "center",
    dataIndex: 'promptAnswer'
  },
  {
    title: 'sex',
    align: "center",
    dataIndex: 'sex'
  },
  {
    title: 'employerNumber',
    align: "center",
    dataIndex: 'employerNumber'
  },
  {
    title: 'telNumber',
    align: "center",
    dataIndex: 'telNumber'
  },
  {
    title: 'mobile',
    align: "center",
    dataIndex: 'mobile'
  },
  {
    title: 'fasNumber',
    align: "center",
    dataIndex: 'fasNumber'
  },
  {
    title: 'mail',
    align: "center",
    dataIndex: 'mail'
  },
  {
    title: 'titile',
    align: "center",
    dataIndex: 'titile'
  },
  {
    title: '1－可用，0－不可用',
    align: "center",
    dataIndex: 'enable'
  },
  {
    title: 'orgId',
    align: "center",
    dataIndex: 'orgId'
  },
  {
    title: '1-正职，2-副职，3-员工',
    align: "center",
    dataIndex: 'orgDuty'
  },
  {
    title: '员工在部门的排序（3位编码，排序最优先为000）',
    align: "center",
    dataIndex: 'displayOrder'
  },
  {
    title: '用户属于其他部门ID，如果存在多个部门，以“，”分隔',
    align: "center",
    dataIndex: 'otherOrgId'
  },
  {
    title: '1－增加，2－删除，3－修改，4—修改密码',
    align: "center",
    dataIndex: 'operationCode'
  },
  {
    title: '格式为YYYY-MM-DD hh:mm:ss',
    align: "center",
    dataIndex: 'synTime'
  },
  {
    title: '1-已处理，0-未处理',
    align: "center",
    dataIndex: 'synFlag'
  },
  {
    title: '默认值为0，当一次处理成功则仍然为0；当处理失败时累计加1',
    align: "center",
    dataIndex: 'retryTime'
  },
];

//查询数据
export const searchFormSchema: FormSchema[] = [
];

//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '用户id',
    field: 'userId',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
      return [
              { required: true, message: '请输入用户id!'},
             ];
    },
  },
  {
    label: 'userName',
    field: 'userName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
      return [
              { required: true, message: '请输入userName!'},
             ];
    },
  },
  {
    label: '地址',
    field: 'address',
    component: 'Input',
  },
  {
    label: '密码',
    field: 'password',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
      return [
              { required: true, message: '请输入密码!'},
             ];
    },
  },
  {
    label: 'promptQuestion',
    field: 'promptQuestion',
    component: 'Input',
  },
  {
    label: 'promptAnswer',
    field: 'promptAnswer',
    component: 'Input',
  },
  {
    label: 'sex',
    field: 'sex',
    component: 'Input',
  },
  {
    label: 'employerNumber',
    field: 'employerNumber',
    component: 'Input',
  },
  {
    label: 'telNumber',
    field: 'telNumber',
    component: 'Input',
  },
  {
    label: 'mobile',
    field: 'mobile',
    component: 'Input',
  },
  {
    label: 'fasNumber',
    field: 'fasNumber',
    component: 'Input',
  },
  {
    label: 'mail',
    field: 'mail',
    component: 'Input',
  },
  {
    label: 'titile',
    field: 'titile',
    component: 'Input',
  },
  {
    label: '1－可用，0－不可用',
    field: 'enable',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
      return [
              { required: true, message: '请输入1－可用，0－不可用!'},
             ];
    },
  },
  {
    label: 'orgId',
    field: 'orgId',
    component: 'Input',
  },
  {
    label: '1-正职，2-副职，3-员工',
    field: 'orgDuty',
    component: 'Input',
  },
  {
    label: '员工在部门的排序（3位编码，排序最优先为000）',
    field: 'displayOrder',
    component: 'Input',
  },
  {
    label: '用户属于其他部门ID，如果存在多个部门，以“，”分隔',
    field: 'otherOrgId',
    component: 'Input',
  },
  {
    label: '1－增加，2－删除，3－修改，4—修改密码',
    field: 'operationCode',
    component: 'InputNumber',
  },
  {
    label: '格式为YYYY-MM-DD hh:mm:ss',
    field: 'synTime',
    component: 'Input',
  },
  {
    label: '1-已处理，0-未处理',
    field: 'synFlag',
    component: 'InputNumber',
  },
  {
    label: '默认值为0，当一次处理成功则仍然为0；当处理失败时累计加1',
    field: 'retryTime',
    component: 'InputNumber',
  },
	// TODO 主键隐藏字段，目前写死为ID
  {
    label: '',
    field: 'id',
    component: 'Input',
    show: false,
  },
];
