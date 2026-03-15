package cn.iocoder.yudao.module.rank.controller.admin.issue.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 榜单期次明细 Response VO")
@Data
public class RankIssueItemRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "排名", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer rankNo;

    @Schema(description = "对象编码", example = "snake")
    private String subjectCode;

    @Schema(description = "对象名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "蛇")
    private String subjectName;

    @Schema(description = "图标地址", example = "https://example.com/snake.png")
    private String iconUrl;

    @Schema(description = "金额或数值", requiredMode = Schema.RequiredMode.REQUIRED, example = "38986742")
    private BigDecimal amountValue;

    @Schema(description = "展示文案", example = "热度爆棚")
    private String displayText;

    @Schema(description = "排序号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer sortOrder;

    @Schema(description = "备注", example = "第一名")
    private String remark;
}
