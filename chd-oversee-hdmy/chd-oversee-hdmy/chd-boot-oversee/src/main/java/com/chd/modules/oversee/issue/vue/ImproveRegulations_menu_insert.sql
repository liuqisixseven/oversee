-- 注意：该页面对应的前台目录为views/issue文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2022100511464550140', NULL, 'improve_regulations', '/issue/improveRegulationsList', 'issue/ImproveRegulationsList', NULL, NULL, 0, NULL, '1', 1.00, 0, NULL, 1, 1, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2022-10-05 11:46:14', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022100511464550141', '2022100511464550140', '添加improve_regulations', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.oversee:improve_regulations:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-10-05 11:46:14', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022100511464550142', '2022100511464550140', '编辑improve_regulations', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.oversee:improve_regulations:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-10-05 11:46:14', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022100511464550143', '2022100511464550140', '删除improve_regulations', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.oversee:improve_regulations:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-10-05 11:46:14', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022100511464550144', '2022100511464550140', '批量删除improve_regulations', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.oversee:improve_regulations:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-10-05 11:46:14', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022100511464550145', '2022100511464550140', '导出excel_improve_regulations', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.oversee:improve_regulations:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-10-05 11:46:14', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022100511464550146', '2022100511464550140', '导入excel_improve_regulations', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.oversee:improve_regulations:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-10-05 11:46:14', NULL, NULL, 0, 0, '1', 0);