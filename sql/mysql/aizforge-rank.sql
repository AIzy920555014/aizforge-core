CREATE TABLE IF NOT EXISTS `rank_board` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `tenant_id` bigint NOT NULL DEFAULT '1' COMMENT '租户编号',
  `name` varchar(50) NOT NULL COMMENT '榜单名称',
  `code` varchar(64) NOT NULL COMMENT '榜单编码',
  `title` varchar(100) NOT NULL COMMENT '页面标题',
  `subtitle` varchar(100) DEFAULT NULL COMMENT '页面副标题',
  `board_type` varchar(32) NOT NULL DEFAULT 'ranking' COMMENT '榜单类型',
  `theme_code` varchar(32) NOT NULL DEFAULT 'light-default' COMMENT '主题编码',
  `show_rank_no` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否显示排名',
  `show_issue_no` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否显示期号',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_rank_board_tenant_id` (`tenant_id`),
  UNIQUE KEY `uk_rank_board_code` (`code`)
) COMMENT='排行榜定义表';

CREATE TABLE IF NOT EXISTS `rank_issue` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `tenant_id` bigint NOT NULL DEFAULT '1' COMMENT '租户编号',
  `board_id` bigint NOT NULL COMMENT '榜单编号',
  `issue_no` varchar(32) NOT NULL COMMENT '期号',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态',
  `snapshot_title` varchar(100) DEFAULT NULL COMMENT '标题快照',
  `snapshot_subtitle` varchar(100) DEFAULT NULL COMMENT '副标题快照',
  `sort_version` int NOT NULL DEFAULT '0' COMMENT '版本号',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_rank_issue_tenant_id` (`tenant_id`),
  UNIQUE KEY `uk_rank_issue_board_issue` (`board_id`, `issue_no`)
) COMMENT='排行榜期次表';

CREATE TABLE IF NOT EXISTS `rank_issue_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `tenant_id` bigint NOT NULL DEFAULT '1' COMMENT '租户编号',
  `issue_id` bigint NOT NULL COMMENT '期次编号',
  `rank_no` int NOT NULL COMMENT '排名',
  `subject_code` varchar(64) DEFAULT NULL COMMENT '对象编码',
  `subject_name` varchar(50) NOT NULL COMMENT '对象名称',
  `icon_url` varchar(255) DEFAULT NULL COMMENT '图标地址',
  `amount_value` decimal(18,2) NOT NULL COMMENT '金额或数值',
  `display_text` varchar(100) DEFAULT NULL COMMENT '展示文案',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序号',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_rank_issue_item_tenant_id` (`tenant_id`),
  KEY `idx_rank_issue_item_issue_id` (`issue_id`)
) COMMENT='排行榜期次明细表';

DELETE FROM `system_role_menu` WHERE `menu_id` BETWEEN 3000 AND 3019;
DELETE FROM `system_menu` WHERE `id` BETWEEN 3000 AND 3019;

INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES
(3000, '排行榜模板', '', 1, 60, 0, '/rank', 'ep:histogram', '', '', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(3001, '榜单定义', '', 2, 1, 3000, 'board', 'ep:collection', 'rank/board/index', 'RankBoard', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(3002, '榜单期次', '', 2, 2, 3000, 'issue', 'ep:data-analysis', 'rank/issue/index', 'RankIssue', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(3003, '榜单查询', 'rank:board:query', 3, 1, 3001, '', '', '', '', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(3004, '榜单新增', 'rank:board:create', 3, 2, 3001, '', '', '', '', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(3005, '榜单修改', 'rank:board:update', 3, 3, 3001, '', '', '', '', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(3006, '榜单删除', 'rank:board:delete', 3, 4, 3001, '', '', '', '', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(3007, '期次查询', 'rank:issue:query', 3, 1, 3002, '', '', '', '', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(3008, '期次新增', 'rank:issue:create', 3, 2, 3002, '', '', '', '', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(3009, '期次修改', 'rank:issue:update', 3, 3, 3002, '', '', '', '', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(3010, '期次删除', 'rank:issue:delete', 3, 4, 3002, '', '', '', '', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0');

INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) VALUES
(1, 3000, 'admin', NOW(), 'admin', NOW(), b'0', 1),
(1, 3001, 'admin', NOW(), 'admin', NOW(), b'0', 1),
(1, 3002, 'admin', NOW(), 'admin', NOW(), b'0', 1),
(1, 3003, 'admin', NOW(), 'admin', NOW(), b'0', 1),
(1, 3004, 'admin', NOW(), 'admin', NOW(), b'0', 1),
(1, 3005, 'admin', NOW(), 'admin', NOW(), b'0', 1),
(1, 3006, 'admin', NOW(), 'admin', NOW(), b'0', 1),
(1, 3007, 'admin', NOW(), 'admin', NOW(), b'0', 1),
(1, 3008, 'admin', NOW(), 'admin', NOW(), b'0', 1),
(1, 3009, 'admin', NOW(), 'admin', NOW(), b'0', 1),
(1, 3010, 'admin', NOW(), 'admin', NOW(), b'0', 1);
