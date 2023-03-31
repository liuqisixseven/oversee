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
      <a-button type="primary" icon="download" @click="handleExportXls('recover_funds')">导出</a-button>
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

    <recover-funds-modal ref="modalForm" @ok="modalFormOk"></recover-funds-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import RecoverFundsModal from './modules/RecoverFundsModal'

  export default {
    name: 'RecoverFundsList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      RecoverFundsModal
    },
    data () {
      return {
        description: 'recover_funds管理页面',
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
            title:'追缴违规违纪资金(万元)',
            align:"center",
            dataIndex: 'recoveryIllegalDisciplinaryFundsSrc'
          },
          {
            title:'追缴违规违纪资金(万元)',
            align:"center",
            dataIndex: 'recoveryIllegalDisciplinaryFunds'
          },
          {
            title:'直接挽回或避免经济损失(万元)',
            align:"center",
            dataIndex: 'recoverDamagesSrc'
          },
          {
            title:'直接挽回或避免经济损失(万元)',
            align:"center",
            dataIndex: 'recoverDamages'
          },
          {
            title:'问题ID',
            align:"center",
            dataIndex: 'issueId'
          },
          {
            title:'责任单位的责任部门id',
            align:"center",
            dataIndex: 'orgId'
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
          list: "/issue/recoverFunds/list",
          delete: "/issue/recoverFunds/delete",
          deleteBatch: "/issue/recoverFunds/deleteBatch",
          exportXlsUrl: "/issue/recoverFunds/exportXls",
          importExcelUrl: "issue/recoverFunds/importExcel",
          
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
        fieldList.push({type:'string',value:'recoveryIllegalDisciplinaryFundsSrc',text:'追缴违规违纪资金(万元)'})
        fieldList.push({type:'number',value:'recoveryIllegalDisciplinaryFunds',text:'追缴违规违纪资金(万元)'})
        fieldList.push({type:'string',value:'recoverDamagesSrc',text:'直接挽回或避免经济损失(万元)'})
        fieldList.push({type:'number',value:'recoverDamages',text:'直接挽回或避免经济损失(万元)'})
        fieldList.push({type:'int',value:'issueId',text:'问题ID'})
        fieldList.push({type:'string',value:'orgId',text:'责任单位的责任部门id'})
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