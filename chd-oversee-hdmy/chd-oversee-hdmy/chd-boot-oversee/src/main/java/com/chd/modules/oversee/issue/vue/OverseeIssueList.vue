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
      <a-button type="primary" icon="download" @click="handleExportXls('oversee_issue')">导出</a-button>
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

    <oversee-issue-modal ref="modalForm" @ok="modalFormOk"></oversee-issue-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import OverseeIssueModal from './modules/OverseeIssueModal'

  export default {
    name: 'OverseeIssueList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      OverseeIssueModal
    },
    data () {
      return {
        description: 'oversee_issue管理页面',
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
            title:'问题编号',
            align:"center",
            dataIndex: 'num'
          },
          {
            title:'标题',
            align:"center",
            dataIndex: 'title'
          },
          {
            title:'副标题',
            align:"center",
            dataIndex: 'subtitle'
          },
          {
            title:'具体问题id 优化具体问题大字段影响搜索 后续可能一个问题对应多个具体问题 则不使用此字段',
            align:"center",
            dataIndex: 'specificIssuesId'
          },
          {
            title:'提交状态 0 草稿 1提交',
            align:"center",
            dataIndex: 'submitState'
          },
          {
            title:'来源 0 巡视 1 巡查 2 专项检查',
            align:"center",
            dataIndex: 'source'
          },
          {
            title:'本部牵头部门id',
            align:"center",
            dataIndex: 'headquartersLeadDepartmentOrgId'
          },
          {
            title:'本部牵头部门经办人user_id',
            align:"center",
            dataIndex: 'headquartersLeadDepartmentManagerUserId'
          },
          {
            title:'责任单位id',
            align:"center",
            dataIndex: 'responsibleUnitOrgId'
          },
          {
            title:'督办部门id',
            align:"center",
            dataIndex: 'supervisorOrgId'
          },
          {
            title:'督办部门经办人user_id',
            align:"center",
            dataIndex: 'supervisorManagerUserId'
          },
          {
            title:'上报时间',
            align:"center",
            dataIndex: 'reportTime',
            customRender:function (text) {
              return !text?"":(text.length>10?text.substr(0,10):text)
            }
          },
          {
            title:'上报user_id',
            align:"center",
            dataIndex: 'reportUserId'
          },
          {
            title:'检查时间',
            align:"center",
            dataIndex: 'checkTime',
            customRender:function (text) {
              return !text?"":(text.length>10?text.substr(0,10):text)
            }
          },
          {
            title:'批准主体',
            align:"center",
            dataIndex: 'approvalBody'
          },
          {
            title:'问题大类ID',
            align:"center",
            dataIndex: 'issueCategoryId'
          },
          {
            title:'问题小类ID',
            align:"center",
            dataIndex: 'issueSubcategoryId'
          },
          {
            title:'严重程度 0 普通 1 重要 2严重',
            align:"center",
            dataIndex: 'severity'
          },
          {
            title:'是否需要督办 -1 不需要 1 需要',
            align:"center",
            dataIndex: 'isSupervise'
          },
          {
            title:'是否需要会签 -1 不需要 1 需要',
            align:"center",
            dataIndex: 'isSign'
          },
          {
            title:'创建用户id',
            align:"center",
            dataIndex: 'createUserId'
          },
          {
            title:'修改用户id',
            align:"center",
            dataIndex: 'updateUserId'
          },
          {
            title:'数据状态 -1 无效 1 有效',
            align:"center",
            dataIndex: 'dataType'
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
          list: "/issue/overseeIssue/list",
          delete: "/issue/overseeIssue/delete",
          deleteBatch: "/issue/overseeIssue/deleteBatch",
          exportXlsUrl: "/issue/overseeIssue/exportXls",
          importExcelUrl: "issue/overseeIssue/importExcel",
          
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
        fieldList.push({type:'string',value:'num',text:'问题编号'})
        fieldList.push({type:'string',value:'title',text:'标题'})
        fieldList.push({type:'string',value:'subtitle',text:'副标题'})
        fieldList.push({type:'int',value:'specificIssuesId',text:'具体问题id 优化具体问题大字段影响搜索 后续可能一个问题对应多个具体问题 则不使用此字段'})
        fieldList.push({type:'int',value:'submitState',text:'提交状态 0 草稿 1提交'})
        fieldList.push({type:'int',value:'source',text:'来源 0 巡视 1 巡查 2 专项检查'})
        fieldList.push({type:'string',value:'headquartersLeadDepartmentOrgId',text:'本部牵头部门id'})
        fieldList.push({type:'string',value:'headquartersLeadDepartmentManagerUserId',text:'本部牵头部门经办人user_id'})
        fieldList.push({type:'string',value:'responsibleUnitOrgId',text:'责任单位id'})
        fieldList.push({type:'string',value:'supervisorOrgId',text:'督办部门id'})
        fieldList.push({type:'string',value:'supervisorManagerUserId',text:'督办部门经办人user_id'})
        fieldList.push({type:'date',value:'reportTime',text:'上报时间'})
        fieldList.push({type:'string',value:'reportUserId',text:'上报user_id'})
        fieldList.push({type:'date',value:'checkTime',text:'检查时间'})
        fieldList.push({type:'string',value:'approvalBody',text:'批准主体'})
        fieldList.push({type:'int',value:'issueCategoryId',text:'问题大类ID'})
        fieldList.push({type:'int',value:'issueSubcategoryId',text:'问题小类ID'})
        fieldList.push({type:'int',value:'severity',text:'严重程度 0 普通 1 重要 2严重'})
        fieldList.push({type:'int',value:'isSupervise',text:'是否需要督办 -1 不需要 1 需要'})
        fieldList.push({type:'int',value:'isSign',text:'是否需要会签 -1 不需要 1 需要'})
        fieldList.push({type:'string',value:'createUserId',text:'创建用户id'})
        fieldList.push({type:'string',value:'updateUserId',text:'修改用户id'})
        fieldList.push({type:'int',value:'dataType',text:'数据状态 -1 无效 1 有效'})
        this.superFieldList = fieldList
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>