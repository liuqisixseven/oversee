import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: 'orgId',
    align:"center",
    dataIndex: 'orgId'
   },
   {
    title: 'orgName',
    align:"center",
    dataIndex: 'orgName'
   },
   {
    title: 'orgShortName',
    align:"center",
    dataIndex: 'orgShortName'
   },
   {
    title: 'zzjs',
    align:"center",
    dataIndex: 'zzjs'
   },
   {
    title: 'jtCode',
    align:"center",
    dataIndex: 'jtCode'
   },
   {
    title: 'jtCode2',
    align:"center",
    dataIndex: 'jtCode2'
   },
   {
    title: 'managerId',
    align:"center",
    dataIndex: 'managerId'
   },
   {
    title: 'areaCode',
    align:"center",
    dataIndex: 'areaCode'
   },
   {
    title: 'znCode',
    align:"center",
    dataIndex: 'znCode'
   },
   {
    title: 'streetAddress',
    align:"center",
    dataIndex: 'streetAddress'
   },
   {
    title: 'postCode',
    align:"center",
    dataIndex: 'postCode'
   },
   {
    title: '国有经济、集体经济、私营经济、有限责任公司、联营经济、股份合作、外商投资、港澳台投资、其他经济',
    align:"center",
    dataIndex: 'jjType'
   },
   {
    title: '农、林、牧、渔业，采掘业，制造业，电力、煤气及水的生成和供应业，建筑业，地址勘查业、水利管理业，交通运输仓储业及邮电通信业，批发和零售贸易、餐饮业，金融保险业，房地产业，社会服务业，卫生、体育和社会福利业，教育文化艺术及广电业，科学研究和综合技术服务业，国家机关政党机关和社会团体，其他行业',
    align:"center",
    dataIndex: 'hyType'
   },
   {
    title: 'area',
    align:"center",
    dataIndex: 'area'
   },
   {
    title: 'employersNumber',
    align:"center",
    dataIndex: 'employersNumber'
   },
   {
    title: 'gdValue',
    align:"center",
    dataIndex: 'gdValue'
   },
   {
    title: 'anIncome',
    align:"center",
    dataIndex: 'anIncome'
   },
   {
    title: 'anProfit',
    align:"center",
    dataIndex: 'anProfit'
   },
   {
    title: 'mainProducts',
    align:"center",
    dataIndex: 'mainProducts'
   },
   {
    title: '生产型企业，经营型企业，基建型企业，发展型企业，电煤供应企业',
    align:"center",
    dataIndex: 'constructionType'
   },
   {
    title: '煤炭业务，发电 业务，化工业务，港口业务，航运业务，营销业务',
    align:"center",
    dataIndex: 'businessType'
   },
   {
    title: 'parentOrgId',
    align:"center",
    dataIndex: 'parentOrgId'
   },
   {
    title: '标识组织节点所在的级别，如一级为公司，二级为部门，三级为处室等，依此类推',
    align:"center",
    dataIndex: 'orgLevel'
   },
   {
    title: 'upperSupervisorId',
    align:"center",
    dataIndex: 'upperSupervisorId'
   },
   {
    title: 'upperSupervisorName',
    align:"center",
    dataIndex: 'upperSupervisorName'
   },
   {
    title: 'displayOrder',
    align:"center",
    dataIndex: 'displayOrder'
   },
   {
    title: 'description',
    align:"center",
    dataIndex: 'description'
   },
   {
    title: '1－增加，2－删除，3－修改',
    align:"center",
    dataIndex: 'operationCode'
   },
   {
    title: '同步时间(时间格式：yyyy-MM-dd HH:mm:ss',
    align:"center",
    dataIndex: 'synTime'
   },
   {
    title: '1－已处理，0－未处理',
    align:"center",
    dataIndex: 'synFlag'
   },
   {
    title: '默认值0，当一次处理成功则仍然为0；处理失败则加1',
    align:"center",
    dataIndex: 'retryTimes'
   },
   {
    title: 'gkOrgId',
    align:"center",
    dataIndex: 'gkOrgId'
   },
   {
    title: 'jsm',
    align:"center",
    dataIndex: 'jsm'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: 'orgId',
    field: 'orgId',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入orgId!'},
          ];
     },
  },
  {
    label: 'orgName',
    field: 'orgName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入orgName!'},
          ];
     },
  },
  {
    label: 'orgShortName',
    field: 'orgShortName',
    component: 'Input',
  },
  {
    label: 'zzjs',
    field: 'zzjs',
    component: 'Input',
  },
  {
    label: 'jtCode',
    field: 'jtCode',
    component: 'Input',
  },
  {
    label: 'jtCode2',
    field: 'jtCode2',
    component: 'Input',
  },
  {
    label: 'managerId',
    field: 'managerId',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入managerId!'},
          ];
     },
  },
  {
    label: 'areaCode',
    field: 'areaCode',
    component: 'Input',
  },
  {
    label: 'znCode',
    field: 'znCode',
    component: 'Input',
  },
  {
    label: 'streetAddress',
    field: 'streetAddress',
    component: 'Input',
  },
  {
    label: 'postCode',
    field: 'postCode',
    component: 'Input',
  },
  {
    label: '国有经济、集体经济、私营经济、有限责任公司、联营经济、股份合作、外商投资、港澳台投资、其他经济',
    field: 'jjType',
    component: 'Input',
  },
  {
    label: '农、林、牧、渔业，采掘业，制造业，电力、煤气及水的生成和供应业，建筑业，地址勘查业、水利管理业，交通运输仓储业及邮电通信业，批发和零售贸易、餐饮业，金融保险业，房地产业，社会服务业，卫生、体育和社会福利业，教育文化艺术及广电业，科学研究和综合技术服务业，国家机关政党机关和社会团体，其他行业',
    field: 'hyType',
    component: 'Input',
  },
  {
    label: 'area',
    field: 'area',
    component: 'InputNumber',
  },
  {
    label: 'employersNumber',
    field: 'employersNumber',
    component: 'InputNumber',
  },
  {
    label: 'gdValue',
    field: 'gdValue',
    component: 'InputNumber',
  },
  {
    label: 'anIncome',
    field: 'anIncome',
    component: 'InputNumber',
  },
  {
    label: 'anProfit',
    field: 'anProfit',
    component: 'InputNumber',
  },
  {
    label: 'mainProducts',
    field: 'mainProducts',
    component: 'Input',
  },
  {
    label: '生产型企业，经营型企业，基建型企业，发展型企业，电煤供应企业',
    field: 'constructionType',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入生产型企业，经营型企业，基建型企业，发展型企业，电煤供应企业!'},
          ];
     },
  },
  {
    label: '煤炭业务，发电 业务，化工业务，港口业务，航运业务，营销业务',
    field: 'businessType',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入煤炭业务，发电 业务，化工业务，港口业务，航运业务，营销业务!'},
          ];
     },
  },
  {
    label: 'parentOrgId',
    field: 'parentOrgId',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入parentOrgId!'},
          ];
     },
  },
  {
    label: '标识组织节点所在的级别，如一级为公司，二级为部门，三级为处室等，依此类推',
    field: 'orgLevel',
    component: 'InputNumber',
  },
  {
    label: 'upperSupervisorId',
    field: 'upperSupervisorId',
    component: 'Input',
  },
  {
    label: 'upperSupervisorName',
    field: 'upperSupervisorName',
    component: 'Input',
  },
  {
    label: 'displayOrder',
    field: 'displayOrder',
    component: 'Input',
  },
  {
    label: 'description',
    field: 'description',
    component: 'Input',
  },
  {
    label: '1－增加，2－删除，3－修改',
    field: 'operationCode',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入1－增加，2－删除，3－修改!'},
          ];
     },
  },
  {
    label: '同步时间(时间格式：yyyy-MM-dd HH:mm:ss',
    field: 'synTime',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入同步时间(时间格式：yyyy-MM-dd HH:mm:ss!'},
          ];
     },
  },
  {
    label: '1－已处理，0－未处理',
    field: 'synFlag',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入1－已处理，0－未处理!'},
          ];
     },
  },
  {
    label: '默认值0，当一次处理成功则仍然为0；处理失败则加1',
    field: 'retryTimes',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入默认值0，当一次处理成功则仍然为0；处理失败则加1!'},
          ];
     },
  },
  {
    label: 'gkOrgId',
    field: 'gkOrgId',
    component: 'Input',
  },
  {
    label: 'jsm',
    field: 'jsm',
    component: 'Input',
  },
	// TODO 主键隐藏字段，目前写死为ID
	{
	  label: '',
	  field: 'id',
	  component: 'Input',
	  show: false
	},
];
