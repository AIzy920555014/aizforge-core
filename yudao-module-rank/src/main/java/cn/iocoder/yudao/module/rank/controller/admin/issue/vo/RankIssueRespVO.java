package cn.iocoder.yudao.module.rank.controller.admin.issue.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 榜单期次 Response VO")
@Data
public class RankIssueRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "榜单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long boardId;

    @Schema(description = "榜单名称", example = "平特热度榜")
    private String boardName;

    @Schema(description = "榜单编码", example = "pt-hot")
    private String boardCode;

    @Schema(description = "期号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026073")
    private String issueNo;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "标题快照", example = "平特热度排行榜")
    private String snapshotTitle;

    @Schema(description = "副标题快照", example = "每日更新")
    private String snapshotSubtitle;

    @Schema(description = "版本号", example = "0")
    private Integer sortVersion;

    @Schema(description = "备注", example = "每日更新")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "榜单明细")
    private List<RankIssueItemRespVO> items;
}
