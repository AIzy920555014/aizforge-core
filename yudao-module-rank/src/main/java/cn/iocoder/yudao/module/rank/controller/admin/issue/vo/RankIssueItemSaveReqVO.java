package cn.iocoder.yudao.module.rank.controller.admin.issue.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 榜单期次明细新增/修改 Request VO")
@Data
public class RankIssueItemSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "排名", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "排名不能为空")
    private Integer rankNo;

    @Schema(description = "对象编码", example = "snake")
    @Size(max = 64, message = "对象编码长度不能超过 64 个字符")
    private String subjectCode;

    @Schema(description = "对象名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "蛇")
    @NotBlank(message = "对象名称不能为空")
    @Size(max = 50, message = "对象名称长度不能超过 50 个字符")
    private String subjectName;

    @Schema(description = "图标地址", example = "https://example.com/snake.png")
    @Size(max = 255, message = "图标地址长度不能超过 255 个字符")
    private String iconUrl;

    @Schema(description = "金额或数值", requiredMode = Schema.RequiredMode.REQUIRED, example = "38986742")
    @NotNull(message = "金额不能为空")
    private BigDecimal amountValue;

    @Schema(description = "展示文案", example = "热度爆棚")
    @Size(max = 100, message = "展示文案长度不能超过 100 个字符")
    private String displayText;

    @Schema(description = "排序号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "排序号不能为空")
    private Integer sortOrder;

    @Schema(description = "备注", example = "第一名")
    @Size(max = 255, message = "备注长度不能超过 255 个字符")
    private String remark;
}
