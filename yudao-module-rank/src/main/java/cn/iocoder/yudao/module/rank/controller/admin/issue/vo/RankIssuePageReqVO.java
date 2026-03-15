package cn.iocoder.yudao.module.rank.controller.admin.issue.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 榜单期次分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class RankIssuePageReqVO extends PageParam {

    @Schema(description = "榜单编号", example = "1")
    private Long boardId;

    @Schema(description = "期号，模糊匹配", example = "2026073")
    private String issueNo;

    @Schema(description = "状态", example = "1")
    private Integer status;
}
