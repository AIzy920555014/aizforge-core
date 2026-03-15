package cn.iocoder.yudao.module.rank.controller.admin.issue;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.rank.controller.admin.issue.vo.RankIssueItemRespVO;
import cn.iocoder.yudao.module.rank.controller.admin.issue.vo.RankIssuePageReqVO;
import cn.iocoder.yudao.module.rank.controller.admin.issue.vo.RankIssueRespVO;
import cn.iocoder.yudao.module.rank.controller.admin.issue.vo.RankIssueSaveReqVO;
import cn.iocoder.yudao.module.rank.controller.app.board.vo.AppRankBoardRespVO;
import cn.iocoder.yudao.module.rank.dal.dataobject.board.RankBoardDO;
import cn.iocoder.yudao.module.rank.dal.dataobject.issue.RankIssueDO;
import cn.iocoder.yudao.module.rank.dal.dataobject.issue.RankIssueItemDO;
import cn.iocoder.yudao.module.rank.service.board.RankBoardService;
import cn.iocoder.yudao.module.rank.service.issue.RankIssueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 榜单期次")
@RestController
@RequestMapping("/rank/issue")
@Validated
public class RankIssueController {

    @Resource
    private RankIssueService rankIssueService;
    @Resource
    private RankBoardService rankBoardService;

    @PostMapping("/create")
    @Operation(summary = "创建榜单期次")
    @PreAuthorize("@ss.hasPermission('rank:issue:create')")
    public CommonResult<Long> createRankIssue(@Valid @RequestBody RankIssueSaveReqVO createReqVO) {
        return success(rankIssueService.createRankIssue(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改榜单期次")
    @PreAuthorize("@ss.hasPermission('rank:issue:update')")
    public CommonResult<Boolean> updateRankIssue(@Valid @RequestBody RankIssueSaveReqVO updateReqVO) {
        rankIssueService.updateRankIssue(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除榜单期次")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('rank:issue:delete')")
    public CommonResult<Boolean> deleteRankIssue(@RequestParam("id") Long id) {
        rankIssueService.deleteRankIssue(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得榜单期次详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('rank:issue:query')")
    public CommonResult<RankIssueRespVO> getRankIssue(@RequestParam("id") Long id) {
        return success(buildResp(rankIssueService.getRankIssue(id)));
    }

    @GetMapping("/page")
    @Operation(summary = "获得榜单期次分页")
    @PreAuthorize("@ss.hasPermission('rank:issue:query')")
    public CommonResult<PageResult<RankIssueRespVO>> getRankIssuePage(@Valid RankIssuePageReqVO pageReqVO) {
        PageResult<RankIssueDO> pageResult = rankIssueService.getRankIssuePage(pageReqVO);
        PageResult<RankIssueRespVO> respPage = BeanUtils.toBean(pageResult, RankIssueRespVO.class);
        for (RankIssueRespVO item : respPage.getList()) {
            fillBoardInfo(item);
        }
        return success(respPage);
    }

    @PostMapping("/{id}/copy-last")
    @Operation(summary = "复制上一期榜单数据")
    @PreAuthorize("@ss.hasPermission('rank:issue:update')")
    public CommonResult<Boolean> copyLastIssue(@PathVariable("id") Long id) {
        rankIssueService.copyLastIssue(id);
        return success(true);
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "发布当前期")
    @PreAuthorize("@ss.hasPermission('rank:issue:update')")
    public CommonResult<Boolean> publishIssue(@PathVariable("id") Long id) {
        rankIssueService.publishIssue(id);
        return success(true);
    }

    @PostMapping("/{id}/offline")
    @Operation(summary = "下线当前期")
    @PreAuthorize("@ss.hasPermission('rank:issue:update')")
    public CommonResult<Boolean> offlineIssue(@PathVariable("id") Long id) {
        rankIssueService.offlineIssue(id);
        return success(true);
    }

    @GetMapping("/{id}/preview")
    @Operation(summary = "预览当前期")
    @PreAuthorize("@ss.hasPermission('rank:issue:query')")
    public CommonResult<AppRankBoardRespVO> previewIssue(@PathVariable("id") Long id) {
        return success(rankIssueService.getPreview(id));
    }

    private RankIssueRespVO buildResp(RankIssueDO issue) {
        RankIssueRespVO respVO = BeanUtils.toBean(issue, RankIssueRespVO.class);
        List<RankIssueItemDO> items = rankIssueService.getRankIssueItems(issue.getId());
        respVO.setItems(BeanUtils.toBean(items, RankIssueItemRespVO.class));
        fillBoardInfo(respVO);
        return respVO;
    }

    private void fillBoardInfo(RankIssueRespVO respVO) {
        RankBoardDO board = rankBoardService.getRankBoard(respVO.getBoardId());
        if (board == null) {
            return;
        }
        respVO.setBoardName(board.getName());
        respVO.setBoardCode(board.getCode());
    }
}
