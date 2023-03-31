-- 注意：该页面对应的前台目录为views/issue文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2022100511452350120', NULL, 'accountability_handling', '/issue/accountabilityHandlingList', 'issue/AccountabilityHandlingList', NULL, NULL, 0, NULL, '1', 1.00, 0, NULL, 1, 1, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2022-10-05 11:45:12', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022100511452360121', '2022100511452350120', '添加accountability_handling', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.oversee:accountability_handling:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-10-05 11:45:12', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022100511452360122', '2022100511452350120', '编辑accountability_handling', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.oversee:accountability_handling:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-10-05 11:45:12', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022100511452360123', '2022100511452350120', '删除accountability_handling', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.oversee:accountability_handling:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-10-05 11:45:12', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022100511452360124', '2022100511452350120', '批量删除accountability_handling', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.oversee:accountability_handling:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-10-05 11:45:12', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022100511452360125', '2022100511452350120', '导出excel_accountability_handling', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.oversee:accountability_handling:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-10-05 11:45:12', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022100511452360126', '2022100511452350120', '导入excel_accountability_handling', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.oversee:accountability_handling:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-10-05 11:45:12', NULL, NULL, 0, 0, '1', 0);