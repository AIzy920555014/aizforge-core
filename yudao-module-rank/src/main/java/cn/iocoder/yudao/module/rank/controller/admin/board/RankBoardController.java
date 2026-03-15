package cn.iocoder.yudao.module.rank.controller.admin.board;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.rank.controller.admin.board.vo.RankBoardPageReqVO;
import cn.iocoder.yudao.module.rank.controller.admin.board.vo.RankBoardRespVO;
import cn.iocoder.yudao.module.rank.controller.admin.board.vo.RankBoardSaveReqVO;
import cn.iocoder.yudao.module.rank.dal.dataobject.board.RankBoardDO;
import cn.iocoder.yudao.module.rank.service.board.RankBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 榜单定义")
@RestController
@RequestMapping("/rank/board")
@Validated
public class RankBoardController {

    @Resource
    private RankBoardService rankBoardService;

    @PostMapping("/create")
    @Operation(summary = "创建榜单定义")
    @PreAuthorize("@ss.hasPermission('rank:board:create')")
    public CommonResult<Long> createRankBoard(@Valid @RequestBody RankBoardSaveReqVO createReqVO) {
        return success(rankBoardService.createRankBoard(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改榜单定义")
    @PreAuthorize("@ss.hasPermission('rank:board:update')")
    public CommonResult<Boolean> updateRankBoard(@Valid @RequestBody RankBoardSaveReqVO updateReqVO) {
        rankBoardService.updateRankBoard(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除榜单定义")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('rank:board:delete')")
    public CommonResult<Boolean> deleteRankBoard(@RequestParam("id") Long id) {
        rankBoardService.deleteRankBoard(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得榜单定义")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('rank:board:query')")
    public CommonResult<RankBoardRespVO> getRankBoard(@RequestParam("id") Long id) {
        RankBoardDO board = rankBoardService.getRankBoard(id);
        return success(BeanUtils.toBean(board, RankBoardRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得榜单定义分页")
    @PreAuthorize("@ss.hasPermission('rank:board:query')")
    public CommonResult<PageResult<RankBoardRespVO>> getRankBoardPage(@Valid RankBoardPageReqVO pageReqVO) {
        PageResult<RankBoardDO> pageResult = rankBoardService.getRankBoardPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, RankBoardRespVO.class));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得启用中的榜单简单列表")
    @PreAuthorize("@ss.hasPermission('rank:board:query')")
    public CommonResult<List<RankBoardRespVO>> getRankBoardSimpleList() {
        return success(BeanUtils.toBean(rankBoardService.getRankBoardSimpleList(), RankBoardRespVO.class));
    }
}
