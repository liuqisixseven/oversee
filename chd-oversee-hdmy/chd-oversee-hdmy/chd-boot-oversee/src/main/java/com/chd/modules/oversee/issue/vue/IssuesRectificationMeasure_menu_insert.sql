-- 注意：该页面对应的前台目录为views/issue文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external)
VALUES ('2022080304583520470', NULL, 'issues_rectification_measure', '/issue/issuesRectificationMeasureList', 'issue/IssuesRectificationMeasureList', NULL, NULL, 0, NULL, '1', 1.00, 0, NULL, 1, 1, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2022-08-03 16:58:47', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022080304583520471', '2022080304583520470', '添加issues_rectification_measure', NULL, NULL, 0, NULL, NULL, 2, 'com.chd.modules.oversee:issues_rectification_measure:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-08-03 16:58:47', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022080304583520472', '2022080304583520470', '编辑issues_rectification_measure', NULL, NULL, 0, NULL, NULL, 2, 'com.chd.modules.oversee:issues_rectification_measure:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-08-03 16:58:47', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022080304583520473', '2022080304583520470', '删除issues_rectification_measure', NULL, NULL, 0, NULL, NULL, 2, 'com.chd.modules.oversee:issues_rectification_measure:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-08-03 16:58:47', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022080304583520474', '2022080304583520470', '批量删除issues_rectification_measure', NULL, NULL, 0, NULL, NULL, 2, 'com.chd.modules.oversee:issues_rectification_measure:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-08-03 16:58:47', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022080304583520475', '2022080304583520470', '导出excel_issues_rectification_measure', NULL, NULL, 0, NULL, NULL, 2, 'com.chd.modules.oversee:issues_rectification_measure:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-08-03 16:58:47', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022080304583520476', '2022080304583520470', '导入excel_issues_rectification_measure', NULL, NULL, 0, NULL, NULL, 2, 'com.chd.modules.oversee:issues_rectification_measure:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-08-03 16:58:47', NULL, NULL, 0, 0, '1', 0);
