package cn.iocoder.yudao.module.rank.controller.admin.board.vo;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 榜单定义新增/修改 Request VO")
@Data
public class RankBoardSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "榜单名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "平特热度榜")
    @NotBlank(message = "榜单名称不能为空")
    @Size(max = 50, message = "榜单名称长度不能超过 50 个字符")
    private String name;

    @Schema(description = "榜单编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "pt-hot")
    @NotBlank(message = "榜单编码不能为空")
    @Size(max = 64, message = "榜单编码长度不能超过 64 个字符")
    private String code;

    @Schema(description = "页面标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "平特热度排行榜")
    @NotBlank(message = "页面标题不能为空")
    @Size(max = 100, message = "页面标题长度不能超过 100 个字符")
    private String title;

    @Schema(description = "页面副标题", example = "每日更新")
    @Size(max = 100, message = "页面副标题长度不能超过 100 个字符")
    private String subtitle;

    @Schema(description = "榜单类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "ranking")
    @NotBlank(message = "榜单类型不能为空")
    private String boardType;

    @Schema(description = "主题编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "light-default")
    @NotBlank(message = "主题编码不能为空")
    private String themeCode;

    @Schema(description = "是否显示排名", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "是否显示排名不能为空")
    private Boolean showRankNo;

    @Schema(description = "是否显示期号", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "是否显示期号不能为空")
    private Boolean showIssueNo;

    @Schema(description = "状态，参见 CommonStatusEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "状态不能为空")
    private Integer status = CommonStatusEnum.ENABLE.getStatus();

    @Schema(description = "备注", example = "榜单模板")
    @Size(max = 255, message = "备注长度不能超过 255 个字符")
    private String remark;
}
