package com.chd.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseConstant {

    public static String HEADQUARTERS_ORG_NAME = "华电煤业集团有限公司";

//    华电煤业集团有限公司
    public static String HEADQUARTERS_ORG_ID = "HQ";


    //煤业区域分公司
    public static String FGS_ORG_ID = "FGS";

    //    项目公司
    public static String XMGS_ORG_ID = "XMGS";

    //    华电区域公司
    public static String QYGS_ID = "QYGS";

    //  参股公司
    public static String CGGS_ORG_ID = "CGGS";

    //  集团安生部
    public static String JT_SCB_ORG_ID = "JT_SCB";

    //  集团财务管理部
    public static String JTCWGL_ORG_ID = "JTCWGL";

    //  集团审计
    public static String JT_SJ_ORG_ID = "JT_SJ";

    //  财务公司
    public static String JR_ORG_ID = "JR";

    //  开发用户组
    public static String DEVORG_ID = "DEVORG";

    public static String OVERSEE_ISSUE_ID_DATA_PREFIX = "oversee::issue::id::";

    public static String OVERSEE_ISSUE_MAP_ID_DATA_PREFIX = "oversee::issue::map::id::";

    public static final String OVERSEE_ISSUE_PROCESS_ID = "overseeIssueProcessId";
    public static final String OVERSEE_ISSUE_PROCESS_DEF_ID = "overseeIssueProcessDefId";

    public static final String MY_ORG_RESPONSIBLE_UNIT_ORG_ID_LIST_MAP_KEY = "my::org::responsible::unit::org::id::list::map";

    public static final String MY_ORG_RESPONSIBLE_UNIT_ORG_ID_LIST_KEY = "my::org::responsible::unit::org::id::list";

    public static final String MY_ORG_DATE_SRC_KEY = "my::org::date::src";


    public static final String PROCESS_CATEGORY_ISSUES = "ISSUES";
    public static final String PROCESS_CATEGORY_issues = "issues";
    public static final String PROCESS_CATEGORY_ISSUES_test = "test";
    public static final String PROCESS_CATEGORY_ISSUES_TEST = "TEST";
    public static final List<String> PROCESS_CATEGORY_ISSUES_LIST= new ArrayList<>();
    static {
        PROCESS_CATEGORY_ISSUES_LIST.add(PROCESS_CATEGORY_ISSUES);
        PROCESS_CATEGORY_ISSUES_LIST.add(PROCESS_CATEGORY_issues);
        PROCESS_CATEGORY_ISSUES_LIST.add(PROCESS_CATEGORY_ISSUES_test);
        PROCESS_CATEGORY_ISSUES_LIST.add(PROCESS_CATEGORY_ISSUES_TEST);
    }
    public static final String PROCESS_CATEGORY_NO_RECTIFICATION = "NoRectification";
    public static final String PROCESS_INITIATE_SUPERVISION = "InitiateSupervision";

    public static final String PROCESS_CATEGORY_ISSUES_KEY = "processCategoryIssues";
    public static final String PROCESS_OLD_ISSUES_KEY = "processOldIssues";
    public static final String DEFAULT_USER = "admin";

    public static final String ISSUES_SUPERVISOR_ID_KEY = "issuesSupervisorId";

    public static final String MANAGER_TITLE_DICT_KEY = "manager_title";

    public static final String IS_SEND_TODO_DICT_KEY = "is_send_todo";
    public static final String TITLE_SET_DICT_KEY = "title_set";
    public static final String TASK_KEY_DICT_KEY = "task_key";
    public static final String SUPERINTEND_MSG_TIP = "superintend_msg_tip";

    public static final String COMMON_OPINIONS_DICT_KEY = "common_opinions";

    public static final String SYSTEM_NAME_DICT_KEY = "system_name";

    public static final String ISSUE_SOURCE_DICT_KEY = "issue_source";

    public static final String ISSUE_SUBMIT_STATE_DICT_KEY = "issue_submit_state";

    public static final String ISSUE_SEVERITY_DICT_KEY = "issue_severity";

    public static final String COMPLETED_TIMEOUT_DICT_KEY = "completed_timeout";

    //销号模板编码
    public static final String CANCELLATION_FORM_TEMPLATE_KEY = "CANCELLATION_FORM_TEMPLATE";

    public static final String REASON_CANCELLATION_EDIT_TASKID_REDIS_KEY = "reasonCancellation::edit::taskId::";
    public static final String OVERSEE_ISSUE_ROLE_EDIT_ISSUEID_ROLETYPE_DATAID_REDIS_KEY = "overseeIssueRole::edit::issueId::roleType::dataId::";

    public static final String OVERSEE_ISSUE_TODO_EDIT_TASK_ID_KEY = "overseeIssueTodo::edit::taskId::";


    public static final String PROCESS_STAGE_KEY = "processStage";

    public static String OVERSEE_ISSUE_CATEGORY_EDIT_CATEGORY_NAME_PREFIX = "oversee::issue::category::issue::edit::category::name::";

}
