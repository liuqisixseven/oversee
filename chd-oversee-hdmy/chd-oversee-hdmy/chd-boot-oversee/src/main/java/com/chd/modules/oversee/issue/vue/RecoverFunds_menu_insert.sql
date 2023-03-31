-- 注意：该页面对应的前台目录为views/issue文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2022100511463200330', NULL, 'recover_funds', '/issue/recoverFundsList', 'issue/RecoverFundsList', NULL, NULL, 0, NULL, '1', 1.00, 0, NULL, 1, 1, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2022-10-05 11:46:33', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022100511463200331', '2022100511463200330', '添加recover_funds', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.oversee:recover_funds:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-10-05 11:46:33', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022100511463200332', '2022100511463200330', '编辑recover_funds', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.oversee:recover_funds:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-10-05 11:46:33', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022100511463200333', '2022100511463200330', '删除recover_funds', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.oversee:recover_funds:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-10-05 11:46:33', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022100511463200334', '2022100511463200330', '批量删除recover_funds', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.oversee:recover_funds:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-10-05 11:46:33', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022100511463200335', '2022100511463200330', '导出excel_recover_funds', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.oversee:recover_funds:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-10-05 11:46:33', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2022100511463200336', '2022100511463200330', '导入excel_recover_funds', NULL, NULL, 0, NULL, NULL, 2, 'org.jeecg.modules.oversee:recover_funds:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2022-10-05 11:46:33', NULL, NULL, 0, 0, '1', 0);