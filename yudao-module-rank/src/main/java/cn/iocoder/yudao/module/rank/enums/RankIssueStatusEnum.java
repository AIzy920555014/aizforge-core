package cn.iocoder.yudao.module.rank.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 榜单期次状态
 */
@Getter
@AllArgsConstructor
public enum RankIssueStatusEnum {

    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布"),
    OFFLINE(2, "已下线");

    private final Integer status;
    private final String name;
}
