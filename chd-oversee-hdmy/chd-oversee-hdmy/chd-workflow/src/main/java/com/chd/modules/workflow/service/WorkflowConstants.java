package com.chd.modules.workflow.service;

public class WorkflowConstants {

    /**
     * 流程状态
     */
    public interface processState{
        final String START="START";//提交
        final String PENDING="PENDING";//审批中
        final String APPROVED="APPROVED";//通过
        final String REJECTED="REJECTED";//拒绝
        final String CANCELED="CANCELED";//撤回
        final String DELETED="DELETED";//删除
        final String COMPLETED="COMPLETED";//完成
    }

    /**
     * 审批节点操作状态变量
     */
    public static final  String taskStatusKey="task_status";
    /**
     * 提交人的变量名称
     */
    public static final String FLOW_SUBMITTER_VAR = "initiator";
    /**
     * 提交人节点名称
     */
    public static final String FLOW_SUBMITTER = "提交人";
    public static final String FLOW_USER_OWNER = "USER_OWNER";//上报者自己
    public static final String FLOW_USER_TASKFROM_USER = "TASKFROM_USER";//审批设置人
    public static final String FLOW_BACK_NODE_KEY = "BACKPOINT_";//设置驳回的节点ID关键字

    public static final String DECIDE_REJECT_NODE_KEY = "DECIDE_REJECT_";//设置是否判断驳回
    public static final String REJECT_TRUE_NODE_KEY = "REJECT_TRUE";//设置驳回的节点ID关键字

    public static String RESPONSIBLE_UNIT_RESPONSIBLE_DEPARTMENT_LIST_KEY = "responsibleUnitResponsibleDepartmentList";
    public static String RESPONSIBLE_UNIT_RESPONSIBLE_DEPARTMENT_KEY = "responsibleUnitResponsibleDepartment";
    public static String SUPERVISOR_LIST_KEY = "supervisorList";
    public static String SUPERVISOR_KEY = "supervisor";
    public static String MANAGE_USER_ID_KEY = "manageUserId";
    public static String USER_ID_KEY = "userId";
    public static String SUPERVISOR_USER_ID_KEY = "supervisorUserId";

    public static String DEPART_ID_KEY = "departId";
    public static String TASK_DEPART_ID_KEY = "departId";

    public static String FORM_MAP_KEY = "form";

    public static String UPDATE_USERID_KEY = "updateUserId";
    public static String TASK_ID_KEY = "taskId";
    public static String IS_SUPERVISE_KEY = "isSupervise";


    public static String SUPERVISOR_ORG_IDS_KEY = "supervisorOrgIds";

    public static String SEVERITY_KEY = "severity";

    public static String RESPONSIBLE_UNIT_ORG_ID_KEY = "responsibleUnitOrgId";


    public static String RESPONSIBLE_LEAD_DEPARTMENT_ORG_ID_KEY = "responsibleLeadDepartmentOrgId";

    public static String SUPERVISE_MAP_DATA_KEY = "::SuperviseMapData";
    public static String USER_TASKS_KEY = "userTasks";

    public static String ISSUES_SUPERVISOR_LIST_KEY = "issuesSupervisorList";

    public static String BIND_RESPONSIBLE_ORG_IDS_KEY = "bindResponsibleOrgIds";

    public static String SUPERVISOR_DATA_KEY = "supervisorData";


    public static String RESPONSIBLE_ORG_IDS_KEY = "responsibleOrgIds";

    public static String RESPONSIBLE_MAIN_ORG_IDS_KEY = "responsibleMainOrgIds";

    public static String RESPONSIBLE_COORDINATION_ORG_IDS_KEY = "responsibleCoordinationOrgIds";



    public interface  ExecutionVariableLocalKey{
        final String TASK_ID="TASK_ID_EXECUTION_VARIABLE_LOCAL_KEY";
        final String PREVIOUS_TASK_ID="PREVIOUS_TASKID_EXECUTION_VARIABLE_LOCAL_KEY";
        final String USER_IDS ="USER_IDS_EXECUTION_VARIABLE_LOCAL_KEY";
        final String PREVIOUS_USER_IDS ="PREVIOUS_USER_ID_EXECUTION_VARIABLE_LOCAL_KEY";
        final String PREVIOUS_TASK_DATA_SRC ="PREVIOUS_TASK_DATA_SRC";
        final String PREVIOUS_TASK_NAMES ="PREVIOUS_TASK_NAMES";

        final String PREVIOUS_TASK_USER_IDS ="PREVIOUS_TASK_USER_IDS";
    }


    /**
     * 流程发起者表单
     */


    public interface  taskStatus{
        final String APPROVED="1";//通过
        final String REJECTED="2";//拒绝
        final String DELEGATE="3";//委派
        final String TURN="4";//转办
    }

    public interface TASK_FORMID{
        final String SELECT_TASK_USER="selectTaskUser";
        final String DEPTS_SELECT="deptsSelect";
        final String DELEGATE="delegate";
        final String TEXT_AND_TIME="textAndTime";
        final String PIN_NUMBER="PinNumber";

        final String ISSUE_APPROVED="IssueApproved";  // 问题审核表单 通过表示问题审核通过
        final String PROBLEM_STATUS_MODI_FICATION="problemStatusModification";
        final String FINISH_ISSUE_PROCESS="finishIssueProcess";

        final String DISTRIBUTION_SUPERVISION_DEPARTMENT="DistributionSupervisionDepartment";
        /**
         * 发起者表单（驳回表单）
         */
        String PROCESS_OWNER_FORM="processOwnerForm";
    }

    //executor-经办人,manager-负责人,supervisor-分管领导，secretary-书记，specialist-业务员
    public interface DEPART_ROLE{
        final String MANAGER="MANAGER";//负责人
        final String SUPERVISOR="SUPERVISOR";//分管领导
        final String EXECUTOR="EXECUTOR";//经办人
        final String SECRETARY="SECRETARY";//书记
        final String SPECIALIST="SPECIALIST";//业务员
        final String LAUNCHUSER="LAUNCHUSER";//发起人
    }

    public interface DEPART_SOURCE{
        final String INITIATOR="INITIATOR";//发起部门 或者本部牵头部门

        final String LAUNCH="LAUNCH";//TODO 发起部门被占用 暂用这个 用在发起督办等流程
        final String RESPONSIBLE_HANDLE="RESPONSIBLE_HANDLE";//责任单位牵头部门
        final String SUPERVISOR="SUPERVISOR";//督办部门
        final String RESPONSIBLE_EXECUTE="RESPONSIBLE_EXECUTE";//责任单位责任部门
    }


    public  enum  CommentTypeEnum {
        FQ("发起"),
        SP("通过"),
        JJ("拒绝"),
        BH("驳回"),
        CH("撤回"),
        ZC("暂存"),
        QS("签收"),
        WP("委派"),
        BS("打回发起者"),
        ZH("知会"),
        ZY("转阅"),
        YY("已阅"),
        ZB("转办"),
        QJQ("前加签"),
        HJQ("后加签"),
        XTZX("系统执行"),
        TJ("提交"),
        CXTJ("重新提交"),
        SPJS("审批结束"),
        LCZZ("流程终止"),
        SQ("授权"),
        CFTG("重复跳过"),
        XT("协同"),
        PS("评审");
        private String name;//名称

        /**
         * 通过type获取Msg
         *
         * @param type
         * @return
         * @Description:
         */
        public static String getEnumMsgByType(String type) {
            for (CommentTypeEnum e : CommentTypeEnum.values()) {
                if (e.toString().equals(type)) {
                    return e.name;
                }
            }
            return "";
        }
        private CommentTypeEnum(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }


    /**
     *
     */
    public interface NOTICE_HANDLE{
        String OVERSEE_PROCESS_NOTICE="overseeProcessNotice";//OverseeProcessNotice
    }

    /**
     * 通知参数KEY
     */
    public interface NOTICE_PARAM_KEY{
        String MESSAGE_TYPE="messageType";
        String BIZ_ID="bizId";
        String DATA="data";
        String TIME="time";
        String USER="user";
        String TASK="task";
    }

    /**
     * 通知消息类型
     */
    public interface NOTICE_MESSAGE_TYPE{
        /** 流程完成通知 **/
        String PROCESS_COMPLETED="PROCESS_COMPLETED";
        /** 任务表单 **/
        String TASK_FORM_DATA="TASK_FORM_DATA";

        String NEXT_TASK_LIST="NEXT_TASK_LIST";
    }

    public interface PIN_NUMBER_LIST_ITEM_NAME{
        final String ACCOUNTABILITY_HANDLING_LIST="accountabilityHandlingList";//问责处理
        final String RECTIFY_VIOLATIONS_LIST="rectifyViolationsList";//纠偏违规
        final String IMPROVE_REGULATIONS_LIST="improveRegulationsList";//完善规章
        final String RECOVER_FUNDS_LIST="recoverFundsList";//收回资金
    }

    public interface TEXT_AND_TIME_LIST_ITEM_NAME{
        final String ISSUES_RECTIFICATION_MEASURE_LIST="issuesRectificationMeasureList";//问责处理

    }


}
