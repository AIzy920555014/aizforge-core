-- AIzyForge 智企云枢品牌与演示数据清理
-- 可重复执行；执行前必须完成数据库备份。

DROP PROCEDURE IF EXISTS aizyforge_clean_text_column;
DELIMITER $$
CREATE PROCEDURE aizyforge_clean_text_column(IN p_table VARCHAR(64), IN p_column VARCHAR(64))
BEGIN
    SET @aizyforge_clean_sql = CONCAT(
        'UPDATE `', p_table, '` SET `', p_column, '` = ',
        'REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(`', p_column, '`, ',
        '''芋道源码'', ''智企云枢''), ',
        '''芋艿'', ''系统用户''), ',
        '''芋道'', ''智企云枢''), ',
        '''test.yudao.iocoder.cn'', ''aixy99.site''), ',
        '''iocoder.cn'', ''aixy99.site''), ',
        '''http://aixy99.site'', ''https://aixy99.site'') ',
        'WHERE `', p_column, '` REGEXP ''芋道|芋艿|iocoder\\.cn'''
    );
    PREPARE aizyforge_clean_stmt FROM @aizyforge_clean_sql;
    EXECUTE aizyforge_clean_stmt;
    DEALLOCATE PREPARE aizyforge_clean_stmt;
END$$
DELIMITER ;

START TRANSACTION;

UPDATE system_tenant
SET name = '智企云枢',
    contact_name = '系统管理员',
    contact_mobile = '',
    websites = 'aixy99.site',
    updater = '1',
    update_time = NOW()
WHERE id = 1;

UPDATE system_tenant
SET name = CONCAT('已停用租户-', id),
    contact_name = '',
    contact_mobile = '',
    websites = '',
    status = 1,
    deleted = b'1',
    updater = '1',
    update_time = NOW()
WHERE id <> 1;

UPDATE system_users
SET nickname = '系统管理员',
    email = '',
    mobile = '',
    avatar = NULL,
    remark = '系统管理员',
    login_ip = '',
    login_date = NULL,
    updater = '1',
    update_time = NOW()
WHERE id = 1 AND username = 'admin';

UPDATE system_users
SET username = CONCAT('disabled_', id),
    password = '',
    nickname = '已停用用户',
    email = '',
    mobile = '',
    avatar = NULL,
    remark = '',
    status = 1,
    login_ip = '',
    login_date = NULL,
    deleted = b'1',
    updater = '1',
    update_time = NOW()
WHERE id <> 1;

DELETE FROM system_oauth2_access_token;
DELETE FROM system_oauth2_refresh_token;
DELETE FROM system_oauth2_approve WHERE user_id <> 1;
DELETE FROM system_oauth2_code WHERE user_id <> 1;

UPDATE system_dept
SET name = '智企云枢',
    phone = '',
    email = '',
    updater = '1',
    update_time = NOW()
WHERE id = 100;

UPDATE system_menu
SET name = '已停用菜单',
    path = '',
    status = 1,
    visible = b'0',
    deleted = b'1',
    updater = '1',
    update_time = NOW()
WHERE id IN (1254, 2159, 2160);

UPDATE system_oauth2_client
SET name = '智企云枢',
    logo = 'https://aixy99.site/brand-mark.svg',
    description = '智企云枢默认客户端',
    redirect_uris = '[\"https://aixy99.site\"]',
    updater = '1',
    update_time = NOW()
WHERE id = 1 AND client_id = 'default';

UPDATE system_oauth2_client
SET client_id = CONCAT('disabled_', id),
    secret = '',
    name = '已停用客户端',
    logo = '',
    description = '',
    redirect_uris = '[]',
    status = 1,
    deleted = b'1',
    updater = '1',
    update_time = NOW()
WHERE id <> 1;

UPDATE system_notice
SET title = '已停用公告',
    content = '',
    status = 1,
    deleted = b'1',
    updater = '1',
    update_time = NOW();

DELETE FROM system_notify_message;

UPDATE infra_file_config
SET name = CONCAT('已停用文件配置-', id),
    remark = '',
    master = b'0',
    config = '{}',
    deleted = b'1',
    updater = '1',
    update_time = NOW()
WHERE id <> 4;

UPDATE infra_file_config
SET name = '数据库存储',
    remark = '智企云枢默认文件存储',
    master = b'1',
    config = '{\"@class\":\"cn.iocoder.yudao.module.infra.framework.file.core.client.db.DBFileClientConfig\",\"domain\":\"https://aixy99.site\"}',
    deleted = b'0',
    updater = '1',
    update_time = NOW()
WHERE id = 4;

-- 清理既有业务演示数据中的旧品牌文字和官方远程资源域名。
CALL aizyforge_clean_text_column('ai_chat_role', 'avatar');
CALL aizyforge_clean_text_column('ai_image', 'pic_url');
CALL aizyforge_clean_text_column('ai_image', 'options');
CALL aizyforge_clean_text_column('ai_music', 'image_url');
CALL aizyforge_clean_text_column('ai_music', 'audio_url');
CALL aizyforge_clean_text_column('ai_music', 'video_url');
CALL aizyforge_clean_text_column('bpm_process_definition_info', 'icon');
CALL aizyforge_clean_text_column('bpm_process_definition_info', 'simple_model');
CALL aizyforge_clean_text_column('crm_follow_up_record', 'pic_urls');
CALL aizyforge_clean_text_column('erp_stock_in', 'file_url');
CALL aizyforge_clean_text_column('member_address', 'name');
CALL aizyforge_clean_text_column('member_address', 'detail_address');
CALL aizyforge_clean_text_column('member_user', 'nickname');
CALL aizyforge_clean_text_column('member_user', 'avatar');
CALL aizyforge_clean_text_column('product_category', 'pic_url');
CALL aizyforge_clean_text_column('product_comment', 'user_avatar');
CALL aizyforge_clean_text_column('product_comment', 'sku_pic_url');
CALL aizyforge_clean_text_column('product_comment', 'pic_urls');
CALL aizyforge_clean_text_column('product_sku', 'pic_url');
CALL aizyforge_clean_text_column('product_spu', 'description');
CALL aizyforge_clean_text_column('product_spu', 'pic_url');
CALL aizyforge_clean_text_column('product_spu', 'slider_pic_urls');
CALL aizyforge_clean_text_column('promotion_article', 'title');
CALL aizyforge_clean_text_column('promotion_banner', 'url');
CALL aizyforge_clean_text_column('promotion_combination_record', 'pic_url');
CALL aizyforge_clean_text_column('promotion_combination_record', 'avatar');
CALL aizyforge_clean_text_column('promotion_diy_page', 'property');
CALL aizyforge_clean_text_column('promotion_diy_template', 'property');
CALL aizyforge_clean_text_column('promotion_kefu_conversation', 'last_message_content');
CALL aizyforge_clean_text_column('promotion_kefu_message', 'content');
CALL aizyforge_clean_text_column('report_go_view_project', 'pic_url');
CALL aizyforge_clean_text_column('system_mail_template', 'nickname');
CALL aizyforge_clean_text_column('trade_after_sale', 'pic_url');
CALL aizyforge_clean_text_column('trade_delivery_pick_up_store', 'logo');
CALL aizyforge_clean_text_column('trade_order', 'receiver_name');
CALL aizyforge_clean_text_column('trade_order', 'receiver_detail_address');
CALL aizyforge_clean_text_column('trade_order_item', 'pic_url');

COMMIT;

DROP PROCEDURE IF EXISTS aizyforge_clean_text_column;
