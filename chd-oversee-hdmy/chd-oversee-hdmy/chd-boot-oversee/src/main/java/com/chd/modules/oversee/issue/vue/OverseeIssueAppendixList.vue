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
      <a-button type="primary" icon="download" @click="handleExportXls('oversee_issue_appendix')">导出</a-button>
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

    <oversee-issue-appendix-modal ref="modalForm" @ok="modalFormOk"></oversee-issue-appendix-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import OverseeIssueAppendixModal from './modules/OverseeIssueAppendixModal'

  export default {
    name: 'OverseeIssueAppendixList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      OverseeIssueAppendixModal
    },
    data () {
      return {
        description: 'oversee_issue_appendix管理页面',
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
            title:'附件路径地址',
            align:"center",
            dataIndex: 'appendixPath'
          },
          {
            title:'附件类型 1 问题录入 2问题分配 3整改措施',
            align:"center",
            dataIndex: 'type'
          },
          {
            title:'文件类型 0 无 1图片 2pdf 此字段不一定启用',
            align:"center",
            dataIndex: 'fileType'
          },
          {
            title:'问题ID',
            align:"center",
            dataIndex: 'issueId'
          },
          {
            title:'排序 越小越靠前',
            align:"center",
            dataIndex: 'sort'
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
          list: "/issue/overseeIssueAppendix/list",
          delete: "/issue/overseeIssueAppendix/delete",
          deleteBatch: "/issue/overseeIssueAppendix/deleteBatch",
          exportXlsUrl: "/issue/overseeIssueAppendix/exportXls",
          importExcelUrl: "issue/overseeIssueAppendix/importExcel",
          
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
        fieldList.push({type:'string',value:'appendixPath',text:'附件路径地址'})
        fieldList.push({type:'int',value:'type',text:'附件类型 1 问题录入 2问题分配 3整改措施'})
        fieldList.push({type:'int',value:'fileType',text:'文件类型 0 无 1图片 2pdf 此字段不一定启用'})
        fieldList.push({type:'int',value:'issueId',text:'问题ID'})
        fieldList.push({type:'int',value:'sort',text:'排序 越小越靠前'})
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