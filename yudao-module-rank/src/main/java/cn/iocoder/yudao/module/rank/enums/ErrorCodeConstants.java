package cn.iocoder.yudao.module.rank.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * rank 错误码定义
 *
 * rank 模块，使用 1-080-000-000 段
 */
public interface ErrorCodeConstants {

    ErrorCode BOARD_NOT_EXISTS = new ErrorCode(1_080_000_000, "榜单不存在");
    ErrorCode BOARD_CODE_EXISTS = new ErrorCode(1_080_000_001, "榜单编码已存在");
    ErrorCode BOARD_DELETE_DENIED = new ErrorCode(1_080_000_002, "榜单下已存在期次数据，不能删除");

    ErrorCode ISSUE_NOT_EXISTS = new ErrorCode(1_080_001_000, "期次不存在");
    ErrorCode ISSUE_NO_EXISTS = new ErrorCode(1_080_001_001, "同一榜单下期号已存在");
    ErrorCode ISSUE_ITEM_EMPTY = new ErrorCode(1_080_001_002, "榜单明细不能为空");
    ErrorCode ISSUE_RANK_DUPLICATE = new ErrorCode(1_080_001_003, "榜单排名不能重复");
    ErrorCode ISSUE_LAST_NOT_EXISTS = new ErrorCode(1_080_001_004, "没有可复制的上一期数据");
    ErrorCode ISSUE_PUBLIC_NOT_EXISTS = new ErrorCode(1_080_001_005, "未找到可展示的榜单内容");
}
