
-- ----------------------------
-- Table structure for rep_demo_employee
-- ----------------------------
DROP TABLE IF EXISTS `oversee_issue`;
CREATE TABLE `oversee_issue` (
     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
     `num` varchar(50) DEFAULT NULL COMMENT '问题编号',
     `title` varchar(300) DEFAULT NULL COMMENT '标题',
     `subtitle` varchar(300) DEFAULT NULL COMMENT '副标题',
     `specific_issues_id` bigint(20) DEFAULT NULL COMMENT '具体问题id 优化具体问题大字段影响搜索 后续可能一个问题对应多个具体问题 则不使用此字段',
     `submit_state` int(4) DEFAULT '0' COMMENT '提交状态 0 草稿 1提交',
     `source` int(4) DEFAULT NULL COMMENT '来源 0 巡视 1 巡查 2 专项检查',
     `is_company_leadership_review` int(4) NOT NULL DEFAULT '0' COMMENT '公司领导审核 -1 不需要 1 需要',
     `headquarters_lead_department_org_id` varchar(50) DEFAULT NULL COMMENT '本部牵头部门id',
     `headquarters_lead_department_manager_user_id` varchar(50) DEFAULT NULL COMMENT '本部牵头部门经办人user_id',
     `responsible_unit_org_id` varchar(50) DEFAULT NULL COMMENT '责任单位id',
     `responsible_lead_department_org_id` varchar(50) DEFAULT NULL COMMENT '责任单位对应的牵头部门id',
     `responsible_lead_department_user_id` varchar(50) DEFAULT NULL COMMENT '责任单位对应的牵头部门经办人id',
     `is_supervise` int(4) NOT NULL DEFAULT '0' COMMENT '是否需要督办 -1 不需要 1 需要',
     `supervisor_org_ids` varchar(50) DEFAULT NULL COMMENT '督办部门ids',
     `supervisor_manager_user_id` varchar(50) DEFAULT NULL COMMENT '督办部门经办人user_id',
     `report_time` datetime DEFAULT NULL COMMENT '上报时间',
     `report_user_id` varchar(50) DEFAULT NULL COMMENT '上报user_id',
     `check_time` datetime DEFAULT NULL COMMENT '检查时间',
     `approval_body` varchar(50) DEFAULT NULL COMMENT '批准主体',
     `issue_category_id` bigint(20) NOT NULL COMMENT '问题大类ID',
     `issue_subcategory_id` bigint(20) NOT NULL COMMENT '问题小类ID',
     `severity` int(4) NOT NULL DEFAULT '0' COMMENT '严重程度 0 普通 1 重要 2严重',
     `is_sign` int(4) NOT NULL DEFAULT '-1' COMMENT '是否需要会签 -1 不需要 1 需要',
     `process_id` varchar(40) DEFAULT NULL COMMENT '流程id',
     `process_def_id` varchar(64) DEFAULT NULL COMMENT '流程定义id',
     `completed_time` datetime DEFAULT NULL COMMENT '完成时间',
     `create_user_id` varchar(50) NOT NULL COMMENT '创建用户id',
     `update_user_id` varchar(50) NOT NULL COMMENT '修改用户id',
     `create_time` datetime NOT NULL COMMENT '创建时间',
     `update_time` datetime NOT NULL COMMENT '更新时间',
     `data_type` int(4) NOT NULL DEFAULT '1' COMMENT '数据状态 -1 无效 1 有效',
     PRIMARY KEY (`id`) USING BTREE,
     KEY `specific_issues_id` (`specific_issues_id`) USING BTREE COMMENT 'specific_issues_id'
) ENGINE=InnoDB COMMENT='问题表';

-- ----------------------------
-- Table structure for rep_demo_employee
-- ----------------------------
DROP TABLE IF EXISTS `issues_supervisor`;
CREATE TABLE `issues_supervisor` (
     `id` bigint(11) NOT NULL AUTO_INCREMENT,
     `issue_id` bigint(20) DEFAULT NULL COMMENT '问题ID',
     `supervisor_org_id` varchar(50) DEFAULT NULL COMMENT '督办部门id',
     `manage_user_id` varchar(50) DEFAULT NULL COMMENT '督办部门负责人user_id',
     `supervisor_user_id` varchar(50) DEFAULT NULL COMMENT '督办部门分管领导user_id',
     `user_id` varchar(50) DEFAULT NULL COMMENT '督办部门经办人user_id',
     `issues_process_type` int(4) DEFAULT "1" COMMENT '督办流程类型 1问题主流程 2 独立子流程',
     `reason` varchar(2000) DEFAULT NULL COMMENT '督办理由',
     `show_type` int(4) NOT NULL DEFAULT '1' COMMENT '显示状态 -1 不显示 1 显示',
     `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序 越小越靠前',
     `create_user_id` varchar(50) NOT NULL COMMENT '创建用户id',
     `update_user_id` varchar(50) NOT NULL COMMENT '修改用户id',
     `create_time` datetime NOT NULL COMMENT '创建时间',
     `update_time` datetime NOT NULL COMMENT '更新时间',
     `data_type` int(4) NOT NULL DEFAULT '1' COMMENT '数据状态 -1 无效 1 有效',
     PRIMARY KEY (`id`) USING BTREE,
     KEY `issue_id` (`issue_id`) USING BTREE COMMENT 'issue_id'
) ENGINE=InnoDB COMMENT='问题督办';

DROP TABLE IF EXISTS `issues_allocation`;
CREATE TABLE `issues_allocation` (
     `id` bigint(11) NOT NULL AUTO_INCREMENT,
     `issue_id` bigint(20) DEFAULT NULL COMMENT '问题ID',
     `responsible_department_org_id` varchar(50) DEFAULT NULL COMMENT '责任单位的责任部门id',
     `manage_user_id` varchar(50) DEFAULT NULL COMMENT '责任单位的责任部门负责人user_id',
     `supervisor_user_id` varchar(50) DEFAULT NULL COMMENT '责任单位的责任部门分管领导user_id',
     `responsible_department_manager_user_id` varchar(50) DEFAULT NULL COMMENT '责任单位的责任部门经办人user_id',
     `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序 越小越靠前',
     `create_user_id` varchar(50) NOT NULL COMMENT '创建用户id',
     `update_user_id` varchar(50) NOT NULL COMMENT '修改用户id',
     `create_time` datetime NOT NULL COMMENT '创建时间',
     `update_time` datetime NOT NULL COMMENT '更新时间',
     `data_type` int(4) NOT NULL DEFAULT '1' COMMENT '数据状态 -1 无效 1 有效',
     PRIMARY KEY (`id`) USING BTREE,
     KEY `issue_id` (`issue_id`) USING BTREE COMMENT 'issue_id'
) ENGINE=InnoDB COMMENT='问题分配';


-- ----------------------------
-- Table structure for rep_demo_employee
-- ----------------------------
DROP TABLE IF EXISTS `issues_rectification_measure`;
CREATE TABLE `issues_rectification_measure` (
    `id` bigint(11) NOT NULL AUTO_INCREMENT,
    `issue_id` bigint(20) DEFAULT NULL COMMENT '问题ID',
    `issues_allocation_id` bigint(20) DEFAULT NULL COMMENT '问题分配ID，如果一个问题对应多个分配，则此字段必填',
    `rectification_measure_content` text COMMENT '整改措施',
    `rectification_time_limit` datetime DEFAULT NULL COMMENT '整改时限',
    `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序 越小越靠前',
    `create_user_id` varchar(50) NOT NULL COMMENT '创建用户id',
    `update_user_id` varchar(50) NOT NULL COMMENT '修改用户id',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime NOT NULL COMMENT '更新时间',
    `data_type` int(4) NOT NULL DEFAULT '1' COMMENT '数据状态 -1 无效 1 有效',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `issue_id` (`issue_id`) USING BTREE COMMENT 'issue_id'
) ENGINE=InnoDB COMMENT='问题整改';




-- ----------------------------
-- Table structure for rep_demo_employee
-- ----------------------------
DROP TABLE IF EXISTS `specific_issues`;
CREATE TABLE `specific_issues` (
       `id` bigint(11) NOT NULL AUTO_INCREMENT,
       `issue_id` bigint(20) DEFAULT NULL COMMENT '问题ID',
       `specific_issues_content` text COMMENT '具体问题内容',
       `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序 越小越靠前',
       `create_user_id` varchar(50) NOT NULL COMMENT '创建用户id',
       `update_user_id` varchar(50) NOT NULL COMMENT '修改用户id',
       `create_time` datetime NOT NULL COMMENT '创建时间',
       `update_time` datetime NOT NULL COMMENT '更新时间',
       `data_type` int(4) NOT NULL DEFAULT '1' COMMENT '数据状态 -1 无效 1 有效',
       PRIMARY KEY (`id`) USING BTREE,
       KEY `issue_id` (`issue_id`) USING BTREE COMMENT 'issue_id'
) ENGINE=InnoDB COMMENT='具体问题';


CREATE TABLE `oversee_issue_specific` (
      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
      `issue_id` bigint(20) NOT NULL COMMENT '问题表主键',
      `user_id` varchar(50) NOT NULL COMMENT '用户id',
      `org_id` varchar(50) DEFAULT NULL COMMENT '部门id',
      `specific_type` int(11) DEFAULT '0' COMMENT '类型',
      `content` longblob COMMENT '内容',
      `create_by` varchar(40) NOT NULL COMMENT '创建人',
      `create_time` datetime NOT NULL COMMENT '创建时间',
      `update_by` varchar(40) NOT NULL COMMENT '更新人',
      `update_time` datetime NOT NULL COMMENT '更新时间',
      `expect_correct_time` datetime DEFAULT NULL COMMENT '预计整改完成时间',
      `files` varchar(200) DEFAULT NULL COMMENT '附件',
      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='上报问题内容';



-- ----------------------------
-- Table structure for rep_demo_employee
-- ----------------------------
DROP TABLE IF EXISTS `oversee_issue_appendix`;
CREATE TABLE `oversee_issue_appendix` (
      `id` bigint(11) NOT NULL AUTO_INCREMENT,
      `appendix_path` varchar(300) DEFAULT NULL COMMENT '附件路径地址',
      `type` int(4) NOT NULL DEFAULT '1' COMMENT '附件类型 1 问题录入 2问题分配 3整改措施',
      `file_type` int(4) NOT NULL DEFAULT '0' COMMENT '文件类型 0 无 1图片 2pdf 此字段不一定启用',
      `issue_id` bigint(20) DEFAULT NULL COMMENT '问题ID',
      `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序 越小越靠前',
      `create_user_id` varchar(50) NOT NULL COMMENT '创建用户id',
      `update_user_id` varchar(50) NOT NULL COMMENT '修改用户id',
      `create_time` datetime NOT NULL COMMENT '创建时间',
      `update_time` datetime NOT NULL COMMENT '更新时间',
      `data_type` int(4) NOT NULL DEFAULT '1' COMMENT '数据状态 -1 无效 1 有效',
      PRIMARY KEY (`id`) USING BTREE,
      KEY `issue_id_appendix_path` (`issue_id`,`appendix_path`) USING BTREE COMMENT 'issue_id_appendix_path',
      KEY `issue_id_type_appendix_path` (`issue_id`,`type`,`appendix_path`) USING BTREE COMMENT 'issue_id_type_appendix_path'
) ENGINE=InnoDB COMMENT='问题附件';


-- ----------------------------
-- Table structure for rep_demo_employee
-- ----------------------------
DROP TABLE IF EXISTS `oversee_issue_category`;
CREATE TABLE `oversee_issue_category` (
      `id` bigint(20) NOT NULL AUTO_INCREMENT,
      `name` varchar(50) NOT NULL COMMENT '名称',
      `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序 越小越靠前',
      `create_user_id` varchar(50) NOT NULL COMMENT '创建用户id',
      `update_user_id` varchar(50) NOT NULL COMMENT '修改用户id',
      `create_time` datetime NOT NULL COMMENT '创建时间',
      `update_time` datetime NOT NULL COMMENT '更新时间',
      `data_type` int(4) NOT NULL DEFAULT '1' COMMENT '数据状态 -1 无效 1 有效',
      PRIMARY KEY (`id`) USING BTREE,
      KEY `sort` (`sort`) USING BTREE COMMENT 'sort'
) ENGINE=InnoDB COMMENT='问题大类';



-- ----------------------------
-- Table structure for rep_demo_employee
-- ----------------------------
DROP TABLE IF EXISTS `oversee_issue_subcategory`;
CREATE TABLE `oversee_issue_subcategory` (
     `id` bigint(20) NOT NULL AUTO_INCREMENT,
     `name` varchar(50) NOT NULL COMMENT '名称',
     `issue_category_id` bigint(20) NOT NULL COMMENT '问题大类ID',
     `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序 越小越靠前',
     `create_user_id` varchar(50) NOT NULL COMMENT '创建用户id',
     `update_user_id` varchar(50) NOT NULL COMMENT '修改用户id',
     `create_time` datetime NOT NULL COMMENT '创建时间',
     `update_time` datetime NOT NULL COMMENT '更新时间',
     `data_type` int(4) NOT NULL DEFAULT '1' COMMENT '数据状态 -1 无效 1 有效',
     PRIMARY KEY (`id`) USING BTREE,
     KEY `issue_category_id_sort` (`issue_category_id`,`sort`) USING BTREE COMMENT 'issue_category_id_sort'
) ENGINE=InnoDB COMMENT='问题小类';








CREATE TABLE `my_user` (
   `user_id` varchar(50) NOT NULL collate utf8mb4_bin COMMENT '用户id',
   `user_name` varchar(100) NOT NULL COMMENT '',
   `address` varchar(300) DEFAULT NULL COMMENT '地址',
   `password` varchar(300) NOT NULL COMMENT '密码',
   `prompt_question` varchar(200) DEFAULT NULL COMMENT '',


   `prompt_answer` varchar(200) DEFAULT NULL COMMENT '',
   `sex` varchar(2) DEFAULT NULL COMMENT '',
   `employer_number` varchar(20) DEFAULT NULL COMMENT '',
   `tel_number` varchar(20) DEFAULT NULL COMMENT '',
   `mobile` CHAR(11) DEFAULT NULL COMMENT '',

   `fas_number` varchar(20) DEFAULT NULL COMMENT '',
   `mail` varchar(50) DEFAULT NULL COMMENT '',
   `titile` varchar(100) DEFAULT NULL COMMENT '',
   `enable` int(4) NOT NULL COMMENT '1－可用，0－不可用',
   `create_time` CHAR(10) DEFAULT NULL COMMENT '',

   `org_id` varchar(50) DEFAULT NULL COMMENT '',
   `org_duty` varchar(50) DEFAULT NULL COMMENT '1-正职，2-副职，3-员工',
   `display_order` varchar(300) DEFAULT NULL COMMENT '员工在部门的排序（3位编码，排序最优先为000）',
   `other_org_id` varchar(200) DEFAULT NULL COMMENT '用户属于其他部门ID，如果存在多个部门，以“，”分隔',
   `operation_code` int(4) DEFAULT NULL COMMENT '1－增加，2－删除，3－修改，4—修改密码',

   `syn_time` varchar(19) DEFAULT NULL COMMENT '格式为YYYY-MM-DD hh:mm:ss',
   `syn_flag` int(4) DEFAULT NULL COMMENT '1-已处理，0-未处理',
   `retry_time` int(4) DEFAULT NULL COMMENT '默认值为0，当一次处理成功则仍然为0；当处理失败时累计加1',
   PRIMARY KEY (`user_id`)
) ENGINE=InnoDB COMMENT='华电煤业用户表';


CREATE TABLE `my_org` (
  `org_id` varchar(50) NOT NULL collate utf8mb4_bin COMMENT '',
  `org_name` varchar(200) NOT NULL COMMENT '',
  `org_short_name` varchar(50) DEFAULT NULL COMMENT '',
  `zzjs` varchar(50) DEFAULT NULL COMMENT '',
  `jt_code` varchar(30) DEFAULT NULL COMMENT '',

  `jt_code2` varchar(30) DEFAULT NULL COMMENT '',
  `manager_id` varchar(50) NOT NULL COMMENT '',
  `area_code` varchar(300) DEFAULT NULL COMMENT '',
  `zn_code` varchar(50) DEFAULT NULL COMMENT '',
  `street_address` varchar(500) DEFAULT NULL COMMENT '',

  `post_code` CHAR(6) DEFAULT NULL COMMENT '',
  `jj_type` varchar(100) DEFAULT NULL COMMENT '国有经济、集体经济、私营经济、有限责任公司、联营经济、股份合作、外商投资、港澳台投资、其他经济',
  `hy_type` varchar(100) DEFAULT NULL COMMENT '农、林、牧、渔业，采掘业，制造业，电力、煤气及水的生成和供应业，建筑业，地址勘查业、水利管理业，交通运输仓储业及邮电通信业，批发和零售贸易、餐饮业，金融保险业，房地产业，社会服务业，卫生、体育和社会福利业，教育文化艺术及广电业，科学研究和综合技术服务业，国家机关政党机关和社会团体，其他行业',
  `create_time` CHAR(20) DEFAULT NULL COMMENT '',
  `area` decimal(10,4) DEFAULT NULL COMMENT '',

  `employers_number` decimal(16,4) DEFAULT NULL COMMENT '',
  `gd_value` decimal(16,4) DEFAULT NULL COMMENT '',
  `an_income` decimal(16,4) DEFAULT NULL COMMENT '',
  `an_profit` decimal(16,4) DEFAULT NULL COMMENT '',
  `main_products` varchar(500) DEFAULT NULL COMMENT '',

  `construction_type` varchar(100) NOT NULL COMMENT '生产型企业，经营型企业，基建型企业，发展型企业，电煤供应企业',
  `business_type` varchar(100) NOT NULL COMMENT '煤炭业务，发电 业务，化工业务，港口业务，航运业务，营销业务',
  `parent_org_id` varchar(50) NOT NULL COMMENT '',
  `org_level` decimal(16) DEFAULT NULL COMMENT '标识组织节点所在的级别，如一级为公司，二级为部门，三级为处室等，依此类推',
  `upper_supervisor_id` varchar(50) DEFAULT NULL COMMENT '',

  `upper_supervisor_name` varchar(100) DEFAULT NULL COMMENT '',
  `display_order` CHAR(3) DEFAULT NULL COMMENT '',
  `description` varchar(200) DEFAULT NULL COMMENT '',
  `operation_code` decimal(16) NOT NULL COMMENT '1－增加，2－删除，3－修改',
  `syn_time` CHAR(19) NOT NULL COMMENT '同步时间(时间格式：yyyy-MM-dd HH:mm:ss',

  `syn_flag` decimal(16) NOT NULL COMMENT '1－已处理，0－未处理',
  `retry_times` decimal(16)  NOT NULL COMMENT '默认值0，当一次处理成功则仍然为0；处理失败则加1',
  `gk_org_id` varchar(200) DEFAULT NULL COMMENT '',
  `jsm` varchar(50) DEFAULT NULL COMMENT '',
  PRIMARY KEY (`org_id`)
) ENGINE=InnoDB COMMENT='华电组织架构表';



ALTER TABLE sys_user ADD`hdmy_user_id` varchar(60) DEFAULT NULL COMMENT 'my_user表user_id';

ALTER TABLE sys_user ADD `hdmy_org_id` varchar(60) DEFAULT NULL COMMENT 'my_org表org_id';

ALTER TABLE sys_depart ADD `hdmy_org_id` varchar(60) DEFAULT NULL COMMENT 'my_org表org_id';
ALTER TABLE sys_depart ADD `upper_supervisor_id` varchar(50) DEFAULT NULL COMMENT '分管领导id';
ALTER TABLE sys_depart ADD `upper_supervisor_name` varchar(100) DEFAULT NULL COMMENT '分管领导名称';

ALTER TABLE oversee_issue ADD `completed_time` datetime DEFAULT NULL COMMENT '完成时间';
ALTER TABLE oversee_issue ADD  `process_id` varchar(40) DEFAULT NULL COMMENT '流程id';
ALTER TABLE oversee_issue ADD  `process_def_id` varchar(64) DEFAULT NULL COMMENT '流程定义id';


ALTER TABLE issues_allocation ADD `manage_user_id` varchar(50) DEFAULT NULL COMMENT '责任单位的责任部门负责人user_id';
ALTER TABLE issues_allocation ADD `supervisor_user_id` varchar(50) DEFAULT NULL COMMENT '责任单位的责任部门分管领导user_id';


ALTER TABLE oss_file ADD `relative_path` varchar(1000) DEFAULT NULL COMMENT '文件相对地址';


-- ----------------------------
-- Table structure for rep_demo_employee
-- ----------------------------
DROP TABLE IF EXISTS `process_classification`;
CREATE TABLE `process_classification` (
      `id` bigint(11) NOT NULL AUTO_INCREMENT,
      `name` varchar(50) DEFAULT NULL COMMENT '流程绑定名称',
      `value` varchar(50) COMMENT '流程分类值',
      `type` int(4) NOT NULL DEFAULT '0' COMMENT '流程类别 默认是1问题上报 1问题上报流程',
      `subcategory` varchar(50) COMMENT '子类别 问题上报流程默认绑定来源',
      `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序 越小越靠前',
      `create_user_id` varchar(50) NOT NULL COMMENT '创建用户id',
      `update_user_id` varchar(50) NOT NULL COMMENT '修改用户id',
      `create_time` datetime NOT NULL COMMENT '创建时间',
      `update_time` datetime NOT NULL COMMENT '更新时间',
      `data_type` int(4) NOT NULL DEFAULT '1' COMMENT '数据状态 -1 无效 1 有效',
      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB COMMENT='流程分类对应';


CREATE TABLE `oversee_user_signature` (
      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
      `user_id` varchar(50) NOT NULL COMMENT '用户id',
      `signature_data` longblob COMMENT '签名数据',
      `create_by` varchar(40) NOT NULL COMMENT '创建人',
      `create_time` datetime NOT NULL COMMENT '创建时间',
      `update_by` varchar(40) NOT NULL COMMENT '更新人',
      `update_time` datetime NOT NULL COMMENT '更新时间',
      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户签名';



-- ----------------------------
-- Table structure for rep_demo_employee
-- ----------------------------
-- DROP TABLE IF EXISTS `morphological_categories`;
CREATE TABLE `morphological_categories` (
      `id` bigint(20) NOT NULL AUTO_INCREMENT,
      `name` varchar(50) NOT NULL COMMENT '名称',
      `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序 越小越靠前',
      `create_user_id` varchar(50) NOT NULL COMMENT '创建用户id',
      `update_user_id` varchar(50) NOT NULL COMMENT '修改用户id',
      `create_time` datetime NOT NULL COMMENT '创建时间',
      `update_time` datetime NOT NULL COMMENT '更新时间',
      `data_type` int(4) NOT NULL DEFAULT '1' COMMENT '数据状态 -1 无效 1 有效',
      PRIMARY KEY (`id`) USING BTREE,
      KEY `sort` (`sort`) USING BTREE COMMENT 'sort'
) ENGINE=InnoDB COMMENT='形态大类';

-- ----------------------------
-- Table structure for rep_demo_employee
-- ----------------------------
-- DROP TABLE IF EXISTS `morphological_subclass`;
CREATE TABLE `morphological_subclass` (
      `id` bigint(20) NOT NULL AUTO_INCREMENT,
      `name` varchar(50) NOT NULL COMMENT '名称',
      `morphological_categories_id` bigint(20) NOT NULL COMMENT '形态大类ID',
      `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序 越小越靠前',
      `create_user_id` varchar(50) NOT NULL COMMENT '创建用户id',
      `update_user_id` varchar(50) NOT NULL COMMENT '修改用户id',
      `create_time` datetime NOT NULL COMMENT '创建时间',
      `update_time` datetime NOT NULL COMMENT '更新时间',
      `data_type` int(4) NOT NULL DEFAULT '1' COMMENT '数据状态 -1 无效 1 有效',
      PRIMARY KEY (`id`) USING BTREE,
      KEY `morphological_categories_id_sort` (`morphological_categories_id`,`sort`) USING BTREE COMMENT 'morphological_categories_id_sort'
) ENGINE=InnoDB COMMENT='形态小类';








ALTER TABLE oversee_issue_specific ADD   `expect_correct_time` datetime DEFAULT NULL COMMENT '预计整改完成时间';
ALTER TABLE oversee_issue_specific ADD   `files` varchar(200) DEFAULT NULL COMMENT '附件';

ALTER TABLE issues_supervisor ADD `issues_process_type` int(4) DEFAULT "1" COMMENT '督办流程类型 1问题主流程 2 独立子流程';
ALTER TABLE issues_supervisor ADD `reason` varchar(2000) DEFAULT NULL COMMENT '督办理由';






CREATE TABLE `accountability_handling` (
       `id` bigint(20) NOT NULL AUTO_INCREMENT,
       `user_id` varchar(50) NOT NULL COMMENT '用户id',
       `task_id` varchar(50) NOT NULL COMMENT 'task_id',
       `uuid` varchar(50) NOT NULL COMMENT 'uuid',
       `morphological_categories_id` bigint(20) DEFAULT NULL COMMENT '形态大类',
       `morphological_subclass_id` bigint(20) DEFAULT NULL COMMENT '形态小类',
       `accountability_date` datetime DEFAULT NULL COMMENT '问责日期',
       `accountable_subject` varchar(50) DEFAULT NULL COMMENT '问责主体',
       `tissue_processing` int(4) DEFAULT NULL COMMENT '是否组织处理 -1 不是 1是',
       `file_a_case` int(4) DEFAULT NULL COMMENT '是否立案 -1 不是 1是',
       `transfer_of_justice` int(4) DEFAULT NULL COMMENT '是否移交司法 -1 不是 1是',
       `issue_id` bigint(20) NOT NULL COMMENT '问题ID',
       `org_id` varchar(50) NOT NULL COMMENT '责任单位的责任部门id',
       `create_user_id` varchar(50) NOT NULL COMMENT '创建用户id',
       `update_user_id` varchar(50) NOT NULL COMMENT '修改用户id',
       `create_time` datetime NOT NULL COMMENT '创建时间',
       `update_time` datetime NOT NULL COMMENT '更新时间',
       `data_type` int(4) NOT NULL DEFAULT '1' COMMENT '数据状态 -1 无效 1 有效',
       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB COMMENT='问责处理';


CREATE TABLE `rectify_violations` (
      `id` bigint(20) NOT NULL AUTO_INCREMENT,
      `user_id` varchar(50) NOT NULL COMMENT '用户id',
      `task_id` varchar(50) NOT NULL COMMENT 'task_id',
      `uuid` varchar(50) NOT NULL COMMENT 'uuid',
      `original_post` varchar(50) DEFAULT NULL COMMENT '原岗位',
      `new_post` varchar(50) DEFAULT NULL COMMENT '新岗位',
      `correction_method` int(5) DEFAULT NULL COMMENT '纠偏方式',
      `issue_id` bigint(20) NOT NULL COMMENT '问题ID',
      `org_id` varchar(50) NOT NULL COMMENT '责任单位的责任部门id',
      `create_user_id` varchar(50) NOT NULL COMMENT '创建用户id',
      `update_user_id` varchar(50) NOT NULL COMMENT '修改用户id',
      `create_time` datetime NOT NULL COMMENT '创建时间',
      `update_time` datetime NOT NULL COMMENT '更新时间',
      `data_type` int(4) NOT NULL DEFAULT '1' COMMENT '数据状态 -1 无效 1 有效',
      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB COMMENT='纠偏违规';


CREATE TABLE `improve_regulations` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `task_id` varchar(50) NOT NULL COMMENT 'task_id',
   `uuid` varchar(50) NOT NULL COMMENT 'uuid',
   `institution_name` varchar(100) NOT NULL COMMENT '制度名称',
   `document_no` varchar(100) DEFAULT NULL COMMENT '文号',
   `perfect_way` int(5) DEFAULT NULL COMMENT '完善方式',
   `date` datetime DEFAULT NULL COMMENT '日期',
   `issue_id` bigint(20) NOT NULL COMMENT '问题ID',
   `org_id` varchar(50) NOT NULL COMMENT '责任单位的责任部门id',
   `create_user_id` varchar(50) NOT NULL COMMENT '创建用户id',
   `update_user_id` varchar(50) NOT NULL COMMENT '修改用户id',
   `create_time` datetime NOT NULL COMMENT '创建时间',
   `update_time` datetime NOT NULL COMMENT '更新时间',
   `data_type` int(4) NOT NULL DEFAULT '1' COMMENT '数据状态 -1 无效 1 有效',
   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB COMMENT='完善规章';


CREATE TABLE `recover_funds` (
     `id` bigint(20) NOT NULL AUTO_INCREMENT,
     `task_id` varchar(50) NOT NULL COMMENT 'task_id',
     `uuid` varchar(50) NOT NULL COMMENT 'uuid',
     `recovery_illegal_disciplinary_funds` varchar(100) DEFAULT NULL COMMENT '追缴违规违纪资金(万元)',
     `recovery_illegal_disciplinary_funds_number` decimal(16,6) DEFAULT NULL COMMENT '追缴违规违纪资金(万元)',
     `recover_damages` varchar(100) DEFAULT NULL COMMENT '直接挽回或避免经济损失(万元)',
     `recover_damages_number` decimal(16,6) DEFAULT NULL COMMENT '直接挽回或避免经济损失(万元)',
     `issue_id` bigint(20) NOT NULL COMMENT '问题ID',
     `org_id` varchar(50) NOT NULL COMMENT '责任单位的责任部门id',
     `create_user_id` varchar(50) NOT NULL COMMENT '创建用户id',
     `update_user_id` varchar(50) NOT NULL COMMENT '修改用户id',
     `create_time` datetime NOT NULL COMMENT '创建时间',
     `update_time` datetime NOT NULL COMMENT '更新时间',
     `data_type` int(4) NOT NULL DEFAULT '1' COMMENT '数据状态 -1 无效 1 有效',
     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB COMMENT='收回资金';

CREATE TABLE `reason_cancellation` (
     `id` bigint(20) NOT NULL AUTO_INCREMENT,
     `task_id` varchar(50) NOT NULL COMMENT 'task_id',
     `reason` text DEFAULT NULL COMMENT '销号理由',
     `issue_id` bigint(20) NOT NULL COMMENT '问题ID',
     `org_id` varchar(50) NOT NULL COMMENT '责任单位的责任部门id',
     `create_user_id` varchar(50) NOT NULL COMMENT '创建用户id',
     `update_user_id` varchar(50) NOT NULL COMMENT '修改用户id',
     `create_time` datetime NOT NULL COMMENT '创建时间',
     `update_time` datetime NOT NULL COMMENT '更新时间',
     `data_type` int(4) NOT NULL DEFAULT '1' COMMENT '数据状态 -1 无效 1 有效',
     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB COMMENT='销号理由';



INSERT INTO `chd_workflow_candidate_group`
(`id`, `name`, `source`, `role`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`)
VALUES ('LAUNCH_MANAGER', '发起部门负责人(主流程)', 'LAUNCH', 'MANAGER', '', NOW(), '系统', NOW(), '系统');

INSERT INTO `chd_workflow_candidate_group`
(`id`, `name`, `source`, `role`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`)
VALUES ('LAUNCH_SUPERVISOR', '发起部门分管领导(主流程)', 'LAUNCH', 'SUPERVISOR', '', NOW(), '系统', NOW(), '系统');


ALTER TABLE sys_permission ADD `required_permissions` tinyint(1) DEFAULT '1' COMMENT '是否需要授权:    1:是   0:不是';

ALTER TABLE chd_workflow_process ADD `next_user_task` varchar(255) DEFAULT NULL COMMENT '下一个审批节点';
ALTER TABLE chd_workflow_process ADD  `next_task_time` datetime DEFAULT NULL COMMENT '下一个审批节点时间';

ALTER TABLE improve_regulations ADD `document_no` varchar(100) DEFAULT NULL COMMENT '文号';

ALTER TABLE issues_supervisor ADD `show_type` int(4) NOT NULL DEFAULT '1' COMMENT '显示状态 -1 不显示 1 显示';

CREATE TABLE `oversee_issue_file` (
      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
      `issue_id` bigint(20) NOT NULL COMMENT '问题ID',
      `user_id` varchar(50) NOT NULL COMMENT '用户id',
      `specific_type` int(11) DEFAULT '0' COMMENT '归属业务类型:0-问题上报，1-整改措施',
      `task_id` varchar(50) NOT NULL COMMENT '任务id',
      `files` varchar(200) DEFAULT NULL COMMENT '附件',
      `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
      `create_time` datetime NOT NULL COMMENT '创建时间',
      `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
      `update_time` datetime NOT NULL COMMENT '更新时间',
      `data_type` int(4) NOT NULL DEFAULT '1' COMMENT '数据状态 -1 无效 1 有效',
      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='问题上传文件记录';


CREATE TABLE `oversee_issue_role` (
      `id` bigint(20) NOT NULL AUTO_INCREMENT,
      `issue_id` bigint(20) NOT NULL COMMENT '问题ID',
      `data_id` varchar(50) NOT NULL COMMENT '角色对应数据id',
      `role_type` int(4) NOT NULL COMMENT '角色类型 1 用户 2 部门',
      `source` varchar(40) DEFAULT NULL COMMENT '来源 暂不使用',
      `role` varchar(100) DEFAULT NULL COMMENT '角色 暂不使用 :EXECUTOR-经办人,SECRETARY-书记,SPECIALIST-业务员',
      `create_user_id` varchar(50) NOT NULL COMMENT '创建用户id',
      `update_user_id` varchar(50) NOT NULL COMMENT '修改用户id',
      `create_time` datetime NOT NULL COMMENT '创建时间',
      `update_time` datetime NOT NULL COMMENT '更新时间',
      `data_type` int(4) NOT NULL DEFAULT '1' COMMENT '数据状态 -1 无效 1 有效',
      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB COMMENT='问题角色表';







ALTER TABLE issues_rectification_measure ADD `task_id` varchar(50) DEFAULT NULL COMMENT 'task_id';
ALTER TABLE issues_rectification_measure ADD `uuid` varchar(50) DEFAULT NULL COMMENT 'uuid';
ALTER TABLE issues_rectification_measure ADD `org_id` varchar(50) NOT NULL COMMENT '责任单位的责任部门id';

ALTER TABLE oversee_issue_appendix ADD `data_id` varchar(50) DEFAULT NULL COMMENT '数据id，根据type录入不同值 type=3时录入部门id';
ALTER TABLE oversee_issue_specific ADD `issues_rectification_measure_id` bigint(20) DEFAULT NULL COMMENT 'issues_rectification_measure表id';

ALTER TABLE oversee_issue ADD `related_issue_ids` varchar(200) DEFAULT NULL COMMENT '关联问题ids';

ALTER TABLE my_org ADD `path` varchar(300) DEFAULT NULL COMMENT '组织结构路径';

ALTER TABLE sys_depart ADD `path` varchar(300) DEFAULT NULL COMMENT '组织结构路径';


CREATE TABLE `oversee_issue_todo` (
      `id` bigint(20) NOT NULL AUTO_INCREMENT,
      `issue_id` bigint(20) NOT NULL COMMENT '问题ID',
      `task_id` varchar(50) NOT NULL COMMENT '任务id',
      `user_id` varchar(50) NOT NULL COMMENT '用户id',
      `depart_id` varchar(50) DEFAULT NULL COMMENT '部门id',
      `send_status` int(4) DEFAULT NULL COMMENT '发送状态 -1待办未发送 1待办已发送 2已办未处理 3已办已发送',
      `send_time` datetime DEFAULT NULL COMMENT '发送时间',
      `role` varchar(100) DEFAULT NULL COMMENT '角色 暂不使用 :EXECUTOR-经办人,SECRETARY-书记,SPECIALIST-业务员',
      `create_user_id` varchar(50) NOT NULL COMMENT '创建用户id',
      `update_user_id` varchar(50) NOT NULL COMMENT '修改用户id',
      `create_time` datetime NOT NULL COMMENT '创建时间',
      `update_time` datetime NOT NULL COMMENT '更新时间',
      `data_type` int(4) NOT NULL DEFAULT '1' COMMENT '数据状态 -1 无效 1 有效',
      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB COMMENT='问题待办表';

ALTER TABLE oversee_issue_todo ADD `previous_user_ids` varchar(500) DEFAULT NULL COMMENT '上一步任务集合userIds';



CREATE TABLE `common_opinions` (
      `id` bigint(20) NOT NULL AUTO_INCREMENT,
      `user_id` varchar(50) NOT NULL COMMENT '用户id',
      `type` int(4) NOT NULL DEFAULT '1' COMMENT '类型 1流程常用意见',
      `sort` int(11) NOT NULL DEFAULT '100' COMMENT '排序 越小越靠前',
      `value` varchar(500) NOT NULL COMMENT '内容',
      `create_user_id` varchar(50) NOT NULL COMMENT '创建用户id',
      `update_user_id` varchar(50) NOT NULL COMMENT '修改用户id',
      `create_time` datetime NOT NULL COMMENT '创建时间',
      `update_time` datetime NOT NULL COMMENT '更新时间',
      `data_type` int(4) NOT NULL DEFAULT '1' COMMENT '数据状态 -1 无效 1 有效',
      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB COMMENT='常用意见';

CREATE TABLE `system_template` (
       `id` bigint(11) NOT NULL AUTO_INCREMENT,
       `name` varchar(300) DEFAULT NULL COMMENT '描述',
       `appendix_path` varchar(300) DEFAULT NULL COMMENT '附件路径地址',
       `type_code` varchar(30) DEFAULT NULL COMMENT '附件类型编码',
       `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序 越小越靠前',
       `create_user_id` varchar(50) NOT NULL COMMENT '创建用户id',
       `update_user_id` varchar(50) NOT NULL COMMENT '修改用户id',
       `create_time` datetime NOT NULL COMMENT '创建时间',
       `update_time` datetime NOT NULL COMMENT '更新时间',
       `data_type` int(4) NOT NULL DEFAULT '1' COMMENT '数据状态 -1 无效 1 有效',
       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB COMMENT='系统模版';


INSERT INTO `sys_dict`(`id`, `dict_name`, `dict_code`, `description`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `type`) VALUES ('1587499786121003010', '系统名称', 'system_name', '系统名称', 0, 'admin', '2022-11-02 01:40:18', NULL, NULL, 0);
INSERT INTO `sys_dict_item`(`id`, `dict_id`, `item_text`, `item_value`, `description`, `sort_order`, `status`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES ('1587866844423782401', '1587499786121003010', '系统名称', 'main_top_title', '顶部导航系统名称', 1, 1, 'admin', '2022-11-03 01:58:52', NULL, NULL);
INSERT INTO `sys_dict_item`(`id`, `dict_id`, `item_text`, `item_value`, `description`, `sort_order`, `status`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES ('1587866682838220801', '1587499786121003010', '系统名称2', 'home_item_2_title', '系统导航页第二项名称', 1, 1, 'admin', '2022-11-03 01:58:13', NULL, NULL);
INSERT INTO `sys_dict_item`(`id`, `dict_id`, `item_text`, `item_value`, `description`, `sort_order`, `status`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES ('1587866581063434241', '1587499786121003010', '系统名称', 'home_item_1_title', '系统导航页第一项名称', 1, 1, 'admin', '2022-11-03 01:57:49', NULL, NULL);
INSERT INTO `sys_dict_item`(`id`, `dict_id`, `item_text`, `item_value`, `description`, `sort_order`, `status`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES ('1587866450729631746', '1587499786121003010', '系统名称', 'home_top_title', '系统导航页头部名称', 1, 1, 'admin', '2022-11-03 01:57:18', NULL, NULL);
INSERT INTO `sys_dict_item`(`id`, `dict_id`, `item_text`, `item_value`, `description`, `sort_order`, `status`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES ('1587866312716058626', '1587499786121003010', '系统名称', 'sign_right_title', '登录页面右侧系统名称', 1, 1, 'admin', '2022-11-03 01:56:45', NULL, NULL);
INSERT INTO `sys_dict_item`(`id`, `dict_id`, `item_text`, `item_value`, `description`, `sort_order`, `status`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES ('1587500218650214402', '1587499786121003010', '监督检查发现问题整改信息系统', 'sign_left_title', '登录页面左侧', 1, 1, 'admin', '2022-11-02 01:42:01', NULL, NULL);


ALTER TABLE sys_announcement ADD `depart_ids` text COMMENT '指定部门';
ALTER TABLE sys_announcement_send ADD `depart_id` varchar(50) DEFAULT NULL COMMENT '部门id';

ALTER TABLE oversee_issue_appendix ADD `task_id` varchar(50) DEFAULT NULL COMMENT 'task_id';

ALTER TABLE oss_file ADD `issue_ids` varchar(1000) DEFAULT NULL COMMENT '关联问题id列表，以逗号分隔';
ALTER TABLE oss_file ADD `type` int(2) DEFAULT NULL COMMENT '数据类型，1表示问题归档类型';

DROP TABLE IF EXISTS `oversee_work_move`;
CREATE TABLE `oversee_work_move` (
      `id` bigint(11) NOT NULL AUTO_INCREMENT,
      `from_user_id` varchar(50) NOT NULL COMMENT '交接用户id',
      `to_user_id` varchar(50) NOT NULL COMMENT '被交接用户id',
      `create_user_id` varchar(50) NOT NULL COMMENT '创建用户id',
      `status` int(2) NOT NULL COMMENT '状态，1为有效',
      `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB COMMENT='工作交接，定时任务扫描';


ALTER TABLE oversee_issue ADD `responsible_lead_department_user_id` varchar(50) DEFAULT NULL COMMENT '责任单位对应的牵头部门经办人id';


-- 设置字段区分大小写
ALTER TABLE sys_user MODIFY COLUMN hdmy_user_id VARCHAR(60) BINARY;
ALTER TABLE sys_user MODIFY COLUMN username VARCHAR(100) BINARY;
ALTER TABLE sys_user MODIFY COLUMN id VARCHAR(60) BINARY;
--



ALTER TABLE issues_allocation ADD `department_type` int(4) DEFAULT NULL COMMENT '责任部门类型 1 配合部门 2 主责任部门';

ALTER TABLE issues_supervisor ADD `bind_responsible_org_ids` varchar(1000) DEFAULT NULL COMMENT '督办部门绑定的责任部门ids';

ALTER TABLE oversee_issue ADD  `is_document` int(2) DEFAULT 0 COMMENT '问题整改完成归档标志，0未归档，1归档完成';
ALTER TABLE oss_file ADD `responsible_unit_org_name` VARCHAR(100) DEFAULT NULL COMMENT '所属单位';

--  修改chd_workflow_process表 text 字段为text


CREATE TABLE `my_org_settings` (
          `org_id` varchar(50) NOT NULL collate utf8mb4_bin COMMENT '',
          `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序 越小越靠前',
          `is_show` int(4) DEFAULT NULL COMMENT '是否展示 -1 不展示 1 展示',
          `create_user_id` varchar(50) NOT NULL COMMENT '创建用户id',
          `update_user_id` varchar(50) NOT NULL COMMENT '修改用户id',
          `create_time` datetime NOT NULL COMMENT '创建时间',
          `update_time` datetime NOT NULL COMMENT '更新时间',
          `data_type` int(4) NOT NULL DEFAULT '1' COMMENT '数据状态 -1 无效 1 有效',
          PRIMARY KEY (`org_id`)
) ENGINE=InnoDB COMMENT='华电组织架构设置表';


ALTER TABLE oss_file ADD `responsible_unit_org_id` VARCHAR(50) DEFAULT NULL COMMENT '所属单位id';

CREATE TABLE `oss_file_issue` (
       `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
       `oss_file_id` varchar(32) NOT NULL COMMENT 'oss_file id',
       `issue_id` bigint(20) DEFAULT NULL COMMENT '问题ID',
       `create_user_id` varchar(50) NOT NULL COMMENT '创建用户id',
       `update_user_id` varchar(50) NOT NULL COMMENT '修改用户id',
       `create_time` datetime NOT NULL COMMENT '创建时间',
       `update_time` datetime NOT NULL COMMENT '更新时间',
       `data_type` int(4) NOT NULL DEFAULT '1' COMMENT '数据状态 -1 无效 1 有效',
       PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='oss file issue 和文件关联表';



CREATE TABLE `kafka_issues_send_recording` (
      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
      `issue_id` bigint(20) NOT NULL COMMENT '问题ID',
      `send_status`int(4) DEFAULT NULL COMMENT '推送状态 -1 未推送 1 已推送',
      `create_user_id` varchar(50) NOT NULL COMMENT '创建用户id',
      `update_user_id` varchar(50) NOT NULL COMMENT '修改用户id',
      `create_time` datetime NOT NULL COMMENT '创建时间',
      `update_time` datetime NOT NULL COMMENT '更新时间',
      `data_type` int(4) NOT NULL DEFAULT '1' COMMENT '数据状态 -1 无效 1 有效',
      PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='kafka推送问题记录表';
