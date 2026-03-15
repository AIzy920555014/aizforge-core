package cn.iocoder.yudao.module.rank.controller.app.board;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.module.rank.controller.app.board.vo.AppRankBoardRespVO;
import cn.iocoder.yudao.module.rank.service.issue.RankIssueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 榜单展示")
@RestController
@RequestMapping("/rank/public/board")
@Validated
@TenantIgnore
public class AppRankBoardController {

    @Resource
    private RankIssueService rankIssueService;

    @GetMapping("/{boardCode}")
    @Operation(summary = "获得榜单当前已发布内容")
    @Parameter(name = "boardCode", description = "榜单编码", required = true, example = "pt-hot")
    @PermitAll
    public CommonResult<AppRankBoardRespVO> getCurrentBoard(@PathVariable("boardCode") String boardCode) {
        return success(rankIssueService.getPublicCurrent(boardCode));
    }

    @GetMapping("/{boardCode}/{issueNo}")
    @Operation(summary = "获得榜单指定期次内容")
    @Parameter(name = "boardCode", description = "榜单编码", required = true, example = "pt-hot")
    @Parameter(name = "issueNo", description = "期号", required = true, example = "2026073")
    @PermitAll
    public CommonResult<AppRankBoardRespVO> getHistoryBoard(@PathVariable("boardCode") String boardCode,
                                                            @PathVariable("issueNo") String issueNo) {
        return success(rankIssueService.getPublicHistory(boardCode, issueNo));
    }
}
