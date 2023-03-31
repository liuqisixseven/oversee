<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('my_org')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <!-- 高级查询区域 -->
      <j-super-query :fieldList="superFieldList" ref="superQueryModal" @handleSuperQuery="handleSuperQuery"></j-super-query>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        :scroll="{x:true}"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        class="j-table-force-nowrap"
        @change="handleTableChange">

        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
        <template slot="imgSlot" slot-scope="text,record">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
          <img v-else :src="getImgView(text)" :preview="record.id" height="25px" alt="" style="max-width:80px;font-size: 12px;font-style: italic;"/>
        </template>
        <template slot="fileSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
          <a-button
            v-else
            :ghost="true"
            type="primary"
            icon="download"
            size="small"
            @click="downloadFile(text)">
            下载
          </a-button>
        </template>

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item>
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>

    <my-org-modal ref="modalForm" @ok="modalFormOk"></my-org-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import MyOrgModal from './modules/MyOrgModal'

  export default {
    name: 'MyOrgList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      MyOrgModal
    },
    data () {
      return {
        description: 'my_org管理页面',
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
          },
          {
            title:'orgId',
            align:"center",
            dataIndex: 'orgId'
          },
          {
            title:'orgName',
            align:"center",
            dataIndex: 'orgName'
          },
          {
            title:'orgShortName',
            align:"center",
            dataIndex: 'orgShortName'
          },
          {
            title:'zzjs',
            align:"center",
            dataIndex: 'zzjs'
          },
          {
            title:'jtCode',
            align:"center",
            dataIndex: 'jtCode'
          },
          {
            title:'jtCode2',
            align:"center",
            dataIndex: 'jtCode2'
          },
          {
            title:'managerId',
            align:"center",
            dataIndex: 'managerId'
          },
          {
            title:'areaCode',
            align:"center",
            dataIndex: 'areaCode'
          },
          {
            title:'znCode',
            align:"center",
            dataIndex: 'znCode'
          },
          {
            title:'streetAddress',
            align:"center",
            dataIndex: 'streetAddress'
          },
          {
            title:'postCode',
            align:"center",
            dataIndex: 'postCode'
          },
          {
            title:'国有经济、集体经济、私营经济、有限责任公司、联营经济、股份合作、外商投资、港澳台投资、其他经济',
            align:"center",
            dataIndex: 'jjType'
          },
          {
            title:'农、林、牧、渔业，采掘业，制造业，电力、煤气及水的生成和供应业，建筑业，地址勘查业、水利管理业，交通运输仓储业及邮电通信业，批发和零售贸易、餐饮业，金融保险业，房地产业，社会服务业，卫生、体育和社会福利业，教育文化艺术及广电业，科学研究和综合技术服务业，国家机关政党机关和社会团体，其他行业',
            align:"center",
            dataIndex: 'hyType'
          },
          {
            title:'area',
            align:"center",
            dataIndex: 'area'
          },
          {
            title:'employersNumber',
            align:"center",
            dataIndex: 'employersNumber'
          },
          {
            title:'gdValue',
            align:"center",
            dataIndex: 'gdValue'
          },
          {
            title:'anIncome',
            align:"center",
            dataIndex: 'anIncome'
          },
          {
            title:'anProfit',
            align:"center",
            dataIndex: 'anProfit'
          },
          {
            title:'mainProducts',
            align:"center",
            dataIndex: 'mainProducts'
          },
          {
            title:'生产型企业，经营型企业，基建型企业，发展型企业，电煤供应企业',
            align:"center",
            dataIndex: 'constructionType'
          },
          {
            title:'煤炭业务，发电 业务，化工业务，港口业务，航运业务，营销业务',
            align:"center",
            dataIndex: 'businessType'
          },
          {
            title:'parentOrgId',
            align:"center",
            dataIndex: 'parentOrgId'
          },
          {
            title:'标识组织节点所在的级别，如一级为公司，二级为部门，三级为处室等，依此类推',
            align:"center",
            dataIndex: 'orgLevel'
          },
          {
            title:'upperSupervisorId',
            align:"center",
            dataIndex: 'upperSupervisorId'
          },
          {
            title:'upperSupervisorName',
            align:"center",
            dataIndex: 'upperSupervisorName'
          },
          {
            title:'displayOrder',
            align:"center",
            dataIndex: 'displayOrder'
          },
          {
            title:'description',
            align:"center",
            dataIndex: 'description'
          },
          {
            title:'1－增加，2－删除，3－修改',
            align:"center",
            dataIndex: 'operationCode'
          },
          {
            title:'同步时间(时间格式：yyyy-MM-dd HH:mm:ss',
            align:"center",
            dataIndex: 'synTime'
          },
          {
            title:'1－已处理，0－未处理',
            align:"center",
            dataIndex: 'synFlag'
          },
          {
            title:'默认值0，当一次处理成功则仍然为0；处理失败则加1',
            align:"center",
            dataIndex: 'retryTimes'
          },
          {
            title:'gkOrgId',
            align:"center",
            dataIndex: 'gkOrgId'
          },
          {
            title:'jsm',
            align:"center",
            dataIndex: 'jsm'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: "/hdmy/myOrg/list",
          delete: "/hdmy/myOrg/delete",
          deleteBatch: "/hdmy/myOrg/deleteBatch",
          exportXlsUrl: "/hdmy/myOrg/exportXls",
          importExcelUrl: "hdmy/myOrg/importExcel",
          
        },
        dictOptions:{},
        superFieldList:[],
      }
    },
    created() {
    this.getSuperFieldList();
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
    },
    methods: {
      initDictConfig(){
      },
      getSuperFieldList(){
        let fieldList=[];
        fieldList.push({type:'string',value:'orgId',text:'orgId'})
        fieldList.push({type:'string',value:'orgName',text:'orgName'})
        fieldList.push({type:'string',value:'orgShortName',text:'orgShortName'})
        fieldList.push({type:'string',value:'zzjs',text:'zzjs'})
        fieldList.push({type:'string',value:'jtCode',text:'jtCode'})
        fieldList.push({type:'string',value:'jtCode2',text:'jtCode2'})
        fieldList.push({type:'string',value:'managerId',text:'managerId'})
        fieldList.push({type:'string',value:'areaCode',text:'areaCode'})
        fieldList.push({type:'string',value:'znCode',text:'znCode'})
        fieldList.push({type:'string',value:'streetAddress',text:'streetAddress'})
        fieldList.push({type:'string',value:'postCode',text:'postCode'})
        fieldList.push({type:'string',value:'jjType',text:'国有经济、集体经济、私营经济、有限责任公司、联营经济、股份合作、外商投资、港澳台投资、其他经济'})
        fieldList.push({type:'string',value:'hyType',text:'农、林、牧、渔业，采掘业，制造业，电力、煤气及水的生成和供应业，建筑业，地址勘查业、水利管理业，交通运输仓储业及邮电通信业，批发和零售贸易、餐饮业，金融保险业，房地产业，社会服务业，卫生、体育和社会福利业，教育文化艺术及广电业，科学研究和综合技术服务业，国家机关政党机关和社会团体，其他行业'})
        fieldList.push({type:'number',value:'area',text:'area'})
        fieldList.push({type:'number',value:'employersNumber',text:'employersNumber'})
        fieldList.push({type:'number',value:'gdValue',text:'gdValue'})
        fieldList.push({type:'number',value:'anIncome',text:'anIncome'})
        fieldList.push({type:'number',value:'anProfit',text:'anProfit'})
        fieldList.push({type:'string',value:'mainProducts',text:'mainProducts'})
        fieldList.push({type:'string',value:'constructionType',text:'生产型企业，经营型企业，基建型企业，发展型企业，电煤供应企业'})
        fieldList.push({type:'string',value:'businessType',text:'煤炭业务，发电 业务，化工业务，港口业务，航运业务，营销业务'})
        fieldList.push({type:'string',value:'parentOrgId',text:'parentOrgId'})
        fieldList.push({type:'number',value:'orgLevel',text:'标识组织节点所在的级别，如一级为公司，二级为部门，三级为处室等，依此类推'})
        fieldList.push({type:'string',value:'upperSupervisorId',text:'upperSupervisorId'})
        fieldList.push({type:'string',value:'upperSupervisorName',text:'upperSupervisorName'})
        fieldList.push({type:'string',value:'displayOrder',text:'displayOrder'})
        fieldList.push({type:'string',value:'description',text:'description'})
        fieldList.push({type:'number',value:'operationCode',text:'1－增加，2－删除，3－修改'})
        fieldList.push({type:'string',value:'synTime',text:'同步时间(时间格式：yyyy-MM-dd HH:mm:ss'})
        fieldList.push({type:'number',value:'synFlag',text:'1－已处理，0－未处理'})
        fieldList.push({type:'number',value:'retryTimes',text:'默认值0，当一次处理成功则仍然为0；处理失败则加1'})
        fieldList.push({type:'string',value:'gkOrgId',text:'gkOrgId'})
        fieldList.push({type:'string',value:'jsm',text:'jsm'})
        this.superFieldList = fieldList
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>