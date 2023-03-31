-- 注意：该页面对应的前台目录为views/issue文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external)
VALUES ('2022080304594450210', NULL, 'oversee_issue_appendix', '/issue/overseeIssueAppendixList', 'issue/OverseeIssueAppendixList', NULL, NULL, 0, NULL, '1', 1.00, 0, NULL, 1, 1, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2022-08-03 16:59:21', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022080304594450211', '2022080304594450210', '添加oversee_issue_appendix', NULL, NULL, 0, NULL, NULL, 2, 'com.chd.modules.oversee:oversee_issue_appendix:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-08-03 16:59:21', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022080304594450212', '2022080304594450210', '编辑oversee_issue_appendix', NULL, NULL, 0, NULL, NULL, 2, 'com.chd.modules.oversee:oversee_issue_appendix:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-08-03 16:59:21', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022080304594450213', '2022080304594450210', '删除oversee_issue_appendix', NULL, NULL, 0, NULL, NULL, 2, 'com.chd.modules.oversee:oversee_issue_appendix:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-08-03 16:59:21', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022080304594450214', '2022080304594450210', '批量删除oversee_issue_appendix', NULL, NULL, 0, NULL, NULL, 2, 'com.chd.modules.oversee:oversee_issue_appendix:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-08-03 16:59:21', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022080304594450215', '2022080304594450210', '导出excel_oversee_issue_appendix', NULL, NULL, 0, NULL, NULL, 2, 'com.chd.modules.oversee:oversee_issue_appendix:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-08-03 16:59:21', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022080304594450216', '2022080304594450210', '导入excel_oversee_issue_appendix', NULL, NULL, 0, NULL, NULL, 2, 'com.chd.modules.oversee:oversee_issue_appendix:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-08-03 16:59:21', NULL, NULL, 0, 0, '1', 0);
