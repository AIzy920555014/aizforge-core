package cn.iocoder.yudao.module.rank.controller.app.board.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 App - 榜单展示 Response VO")
@Data
public class AppRankBoardRespVO {

    @Schema(description = "榜单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long boardId;

    @Schema(description = "榜单名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "平特热度榜")
    private String boardName;

    @Schema(description = "榜单编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "pt-hot")
    private String boardCode;

    @Schema(description = "页面标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "平特热度排行榜")
    private String title;

    @Schema(description = "页面副标题", example = "每日更新")
    private String subtitle;

    @Schema(description = "期号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026073")
    private String issueNo;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "主题编码", example = "light-default")
    private String themeCode;

    @Schema(description = "是否显示排名", example = "true")
    private Boolean showRankNo;

    @Schema(description = "是否显示期号", example = "true")
    private Boolean showIssueNo;

    @Schema(description = "榜单明细")
    private List<Item> items;

    @Schema(description = "用户 App - 榜单明细")
    @Data
    public static class Item {
        @Schema(description = "排名", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer rankNo;

        @Schema(description = "对象编码", example = "snake")
        private String subjectCode;

        @Schema(description = "对象名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "蛇")
        private String subjectName;

        @Schema(description = "图标地址", example = "https://example.com/snake.png")
        private String iconUrl;

        @Schema(description = "金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "38986742")
        private BigDecimal amountValue;

        @Schema(description = "展示文案", example = "热度爆棚")
        private String displayText;

        @Schema(description = "排序号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer sortOrder;
    }
}
