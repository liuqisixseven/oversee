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
      <a-button type="primary" icon="download" @click="handleExportXls('my_user')">导出</a-button>
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

    <my-user-modal ref="modalForm" @ok="modalFormOk"></my-user-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import MyUserModal from './modules/MyUserModal'

  export default {
    name: 'MyUserList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      MyUserModal
    },
    data () {
      return {
        description: 'my_user管理页面',
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
            title:'用户id',
            align:"center",
            dataIndex: 'userId'
          },
          {
            title:'userName',
            align:"center",
            dataIndex: 'userName'
          },
          {
            title:'地址',
            align:"center",
            dataIndex: 'address'
          },
          {
            title:'密码',
            align:"center",
            dataIndex: 'password'
          },
          {
            title:'promptQuestion',
            align:"center",
            dataIndex: 'promptQuestion'
          },
          {
            title:'promptAnswer',
            align:"center",
            dataIndex: 'promptAnswer'
          },
          {
            title:'sex',
            align:"center",
            dataIndex: 'sex'
          },
          {
            title:'employerNumber',
            align:"center",
            dataIndex: 'employerNumber'
          },
          {
            title:'telNumber',
            align:"center",
            dataIndex: 'telNumber'
          },
          {
            title:'mobile',
            align:"center",
            dataIndex: 'mobile'
          },
          {
            title:'fasNumber',
            align:"center",
            dataIndex: 'fasNumber'
          },
          {
            title:'mail',
            align:"center",
            dataIndex: 'mail'
          },
          {
            title:'titile',
            align:"center",
            dataIndex: 'titile'
          },
          {
            title:'1－可用，0－不可用',
            align:"center",
            dataIndex: 'enable'
          },
          {
            title:'orgId',
            align:"center",
            dataIndex: 'orgId'
          },
          {
            title:'1-正职，2-副职，3-员工',
            align:"center",
            dataIndex: 'orgDuty'
          },
          {
            title:'员工在部门的排序（3位编码，排序最优先为000）',
            align:"center",
            dataIndex: 'displayOrder'
          },
          {
            title:'用户属于其他部门ID，如果存在多个部门，以“，”分隔',
            align:"center",
            dataIndex: 'otherOrgId'
          },
          {
            title:'1－增加，2－删除，3－修改，4—修改密码',
            align:"center",
            dataIndex: 'operationCode'
          },
          {
            title:'格式为YYYY-MM-DD hh:mm:ss',
            align:"center",
            dataIndex: 'synTime'
          },
          {
            title:'1-已处理，0-未处理',
            align:"center",
            dataIndex: 'synFlag'
          },
          {
            title:'默认值为0，当一次处理成功则仍然为0；当处理失败时累计加1',
            align:"center",
            dataIndex: 'retryTime'
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
          list: "/hdmy/myUser/list",
          delete: "/hdmy/myUser/delete",
          deleteBatch: "/hdmy/myUser/deleteBatch",
          exportXlsUrl: "/hdmy/myUser/exportXls",
          importExcelUrl: "hdmy/myUser/importExcel",
          
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
        fieldList.push({type:'string',value:'userId',text:'用户id'})
        fieldList.push({type:'string',value:'userName',text:'userName'})
        fieldList.push({type:'string',value:'address',text:'地址'})
        fieldList.push({type:'string',value:'password',text:'密码'})
        fieldList.push({type:'string',value:'promptQuestion',text:'promptQuestion'})
        fieldList.push({type:'string',value:'promptAnswer',text:'promptAnswer'})
        fieldList.push({type:'string',value:'sex',text:'sex'})
        fieldList.push({type:'string',value:'employerNumber',text:'employerNumber'})
        fieldList.push({type:'string',value:'telNumber',text:'telNumber'})
        fieldList.push({type:'string',value:'mobile',text:'mobile'})
        fieldList.push({type:'string',value:'fasNumber',text:'fasNumber'})
        fieldList.push({type:'string',value:'mail',text:'mail'})
        fieldList.push({type:'string',value:'titile',text:'titile'})
        fieldList.push({type:'int',value:'enable',text:'1－可用，0－不可用'})
        fieldList.push({type:'string',value:'orgId',text:'orgId'})
        fieldList.push({type:'string',value:'orgDuty',text:'1-正职，2-副职，3-员工'})
        fieldList.push({type:'string',value:'displayOrder',text:'员工在部门的排序（3位编码，排序最优先为000）'})
        fieldList.push({type:'string',value:'otherOrgId',text:'用户属于其他部门ID，如果存在多个部门，以“，”分隔'})
        fieldList.push({type:'int',value:'operationCode',text:'1－增加，2－删除，3－修改，4—修改密码'})
        fieldList.push({type:'string',value:'synTime',text:'格式为YYYY-MM-DD hh:mm:ss'})
        fieldList.push({type:'int',value:'synFlag',text:'1-已处理，0-未处理'})
        fieldList.push({type:'int',value:'retryTime',text:'默认值为0，当一次处理成功则仍然为0；当处理失败时累计加1'})
        this.superFieldList = fieldList
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>