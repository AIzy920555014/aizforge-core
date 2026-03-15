package cn.iocoder.yudao.module.rank.controller.admin.board.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 榜单定义 Response VO")
@Data
public class RankBoardRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "榜单名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "平特热度榜")
    private String name;

    @Schema(description = "榜单编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "pt-hot")
    private String code;

    @Schema(description = "页面标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "平特热度排行榜")
    private String title;

    @Schema(description = "页面副标题", example = "每日更新")
    private String subtitle;

    @Schema(description = "榜单类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "ranking")
    private String boardType;

    @Schema(description = "主题编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "light-default")
    private String themeCode;

    @Schema(description = "是否显示排名", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean showRankNo;

    @Schema(description = "是否显示期号", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean showIssueNo;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer status;

    @Schema(description = "备注", example = "榜单模板")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;
}
