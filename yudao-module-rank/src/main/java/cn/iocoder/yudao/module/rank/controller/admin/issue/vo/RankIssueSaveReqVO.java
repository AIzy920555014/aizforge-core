package cn.iocoder.yudao.module.rank.controller.admin.issue.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 榜单期次新增/修改 Request VO")
@Data
public class RankIssueSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "榜单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "榜单编号不能为空")
    private Long boardId;

    @Schema(description = "期号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026073")
    @NotBlank(message = "期号不能为空")
    @Size(max = 32, message = "期号长度不能超过 32 个字符")
    private String issueNo;

    @Schema(description = "备注", example = "每日更新")
    @Size(max = 255, message = "备注长度不能超过 255 个字符")
    private String remark;

    @Schema(description = "榜单明细", requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    @NotEmpty(message = "榜单明细不能为空")
    private List<RankIssueItemSaveReqVO> items;
}
