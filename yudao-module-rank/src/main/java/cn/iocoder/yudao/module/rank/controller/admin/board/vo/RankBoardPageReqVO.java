package cn.iocoder.yudao.module.rank.controller.admin.board.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 榜单定义分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class RankBoardPageReqVO extends PageParam {

    @Schema(description = "榜单名称，模糊匹配", example = "平特热度榜")
    private String name;

    @Schema(description = "榜单编码，模糊匹配", example = "pt-hot")
    private String code;

    @Schema(description = "状态", example = "0")
    private Integer status;
}
