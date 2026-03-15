package cn.iocoder.yudao.module.rank.service.issue;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.rank.controller.admin.issue.vo.RankIssueItemSaveReqVO;
import cn.iocoder.yudao.module.rank.controller.admin.issue.vo.RankIssuePageReqVO;
import cn.iocoder.yudao.module.rank.controller.admin.issue.vo.RankIssueSaveReqVO;
import cn.iocoder.yudao.module.rank.controller.app.board.vo.AppRankBoardRespVO;
import cn.iocoder.yudao.module.rank.dal.dataobject.board.RankBoardDO;
import cn.iocoder.yudao.module.rank.dal.dataobject.issue.RankIssueDO;
import cn.iocoder.yudao.module.rank.dal.dataobject.issue.RankIssueItemDO;
import cn.iocoder.yudao.module.rank.dal.mysql.board.RankBoardMapper;
import cn.iocoder.yudao.module.rank.dal.mysql.issue.RankIssueItemMapper;
import cn.iocoder.yudao.module.rank.dal.mysql.issue.RankIssueMapper;
import cn.iocoder.yudao.module.rank.enums.RankIssueStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.rank.enums.ErrorCodeConstants.BOARD_NOT_EXISTS;
import static cn.iocoder.yudao.module.rank.enums.ErrorCodeConstants.ISSUE_ITEM_EMPTY;
import static cn.iocoder.yudao.module.rank.enums.ErrorCodeConstants.ISSUE_LAST_NOT_EXISTS;
import static cn.iocoder.yudao.module.rank.enums.ErrorCodeConstants.ISSUE_NOT_EXISTS;
import static cn.iocoder.yudao.module.rank.enums.ErrorCodeConstants.ISSUE_NO_EXISTS;
import static cn.iocoder.yudao.module.rank.enums.ErrorCodeConstants.ISSUE_PUBLIC_NOT_EXISTS;
import static cn.iocoder.yudao.module.rank.enums.ErrorCodeConstants.ISSUE_RANK_DUPLICATE;

@Service
@Validated
public class RankIssueServiceImpl implements RankIssueService {

    @Resource
    private RankBoardMapper rankBoardMapper;
    @Resource
    private RankIssueMapper rankIssueMapper;
    @Resource
    private RankIssueItemMapper rankIssueItemMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRankIssue(RankIssueSaveReqVO createReqVO) {
        RankBoardDO board = validateBoardExists(createReqVO.getBoardId());
        validateIssueNoUnique(null, createReqVO.getBoardId(), createReqVO.getIssueNo());
        validateItems(createReqVO.getItems());

        RankIssueDO issue = BeanUtils.toBean(createReqVO, RankIssueDO.class);
        issue.setStatus(RankIssueStatusEnum.DRAFT.getStatus());
        issue.setPublishTime(null);
        issue.setSnapshotTitle(board.getTitle());
        issue.setSnapshotSubtitle(board.getSubtitle());
        issue.setSortVersion(0);
        rankIssueMapper.insert(issue);
        replaceIssueItems(issue.getId(), createReqVO.getItems());
        return issue.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRankIssue(RankIssueSaveReqVO updateReqVO) {
        RankIssueDO issue = validateIssueExists(updateReqVO.getId());
        validateBoardExists(updateReqVO.getBoardId());
        validateIssueNoUnique(updateReqVO.getId(), updateReqVO.getBoardId(), updateReqVO.getIssueNo());
        validateItems(updateReqVO.getItems());

        RankIssueDO updateObj = BeanUtils.toBean(updateReqVO, RankIssueDO.class);
        updateObj.setStatus(issue.getStatus());
        updateObj.setPublishTime(issue.getPublishTime());
        updateObj.setSnapshotTitle(issue.getSnapshotTitle());
        updateObj.setSnapshotSubtitle(issue.getSnapshotSubtitle());
        updateObj.setSortVersion(issue.getSortVersion() == null ? 0 : issue.getSortVersion() + 1);
        rankIssueMapper.updateById(updateObj);
        replaceIssueItems(updateReqVO.getId(), updateReqVO.getItems());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRankIssue(Long id) {
        validateIssueExists(id);
        rankIssueItemMapper.deleteByIssueId(id);
        rankIssueMapper.deleteById(id);
    }

    @Override
    public RankIssueDO getRankIssue(Long id) {
        return rankIssueMapper.selectById(id);
    }

    @Override
    public List<RankIssueItemDO> getRankIssueItems(Long issueId) {
        return rankIssueItemMapper.selectListByIssueId(issueId);
    }

    @Override
    public PageResult<RankIssueDO> getRankIssuePage(RankIssuePageReqVO pageReqVO) {
        return rankIssueMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyLastIssue(Long issueId) {
        RankIssueDO currentIssue = validateIssueExists(issueId);
        RankIssueDO lastIssue = rankIssueMapper.selectLatestByBoardId(currentIssue.getBoardId(), currentIssue.getId());
        if (lastIssue == null) {
            throw exception(ISSUE_LAST_NOT_EXISTS);
        }
        List<RankIssueItemDO> lastItems = rankIssueItemMapper.selectListByIssueId(lastIssue.getId());
        if (CollUtil.isEmpty(lastItems)) {
            throw exception(ISSUE_LAST_NOT_EXISTS);
        }
        rankIssueItemMapper.deleteByIssueId(issueId);
        for (RankIssueItemDO item : lastItems) {
            item.setId(null);
            item.setIssueId(issueId);
            item.clean();
        }
        rankIssueItemMapper.insertBatch(lastItems);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishIssue(Long issueId) {
        RankIssueDO issue = validateIssueExists(issueId);
        RankBoardDO board = validateBoardExists(issue.getBoardId());
        List<RankIssueItemDO> items = rankIssueItemMapper.selectListByIssueId(issueId);
        if (CollUtil.isEmpty(items)) {
            throw exception(ISSUE_ITEM_EMPTY);
        }

        RankIssueDO updateObj = new RankIssueDO();
        updateObj.setId(issueId);
        updateObj.setStatus(RankIssueStatusEnum.PUBLISHED.getStatus());
        updateObj.setPublishTime(LocalDateTime.now());
        updateObj.setSnapshotTitle(board.getTitle());
        updateObj.setSnapshotSubtitle(board.getSubtitle());
        updateObj.setSortVersion(issue.getSortVersion() == null ? 1 : issue.getSortVersion() + 1);
        rankIssueMapper.updateById(updateObj);

        List<RankIssueDO> publishedIssues = rankIssueMapper.selectList(new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<RankIssueDO>()
                .eq(RankIssueDO::getBoardId, issue.getBoardId())
                .eq(RankIssueDO::getStatus, RankIssueStatusEnum.PUBLISHED.getStatus())
                .ne(RankIssueDO::getId, issueId));
        for (RankIssueDO publishedIssue : publishedIssues) {
            RankIssueDO offline = new RankIssueDO();
            offline.setId(publishedIssue.getId());
            offline.setStatus(RankIssueStatusEnum.OFFLINE.getStatus());
            rankIssueMapper.updateById(offline);
        }
    }

    @Override
    public void offlineIssue(Long issueId) {
        validateIssueExists(issueId);
        RankIssueDO updateObj = new RankIssueDO();
        updateObj.setId(issueId);
        updateObj.setStatus(RankIssueStatusEnum.OFFLINE.getStatus());
        rankIssueMapper.updateById(updateObj);
    }

    @Override
    public AppRankBoardRespVO getPreview(Long issueId) {
        RankIssueDO issue = validateIssueExists(issueId);
        RankBoardDO board = validateBoardExists(issue.getBoardId());
        return buildPublicResp(board, issue, rankIssueItemMapper.selectListByIssueId(issueId));
    }

    @Override
    public AppRankBoardRespVO getPublicCurrent(String boardCode) {
        RankBoardDO board = validateBoardExists(boardCode);
        RankIssueDO issue = rankIssueMapper.selectLatestPublishedByBoardId(board.getId());
        if (issue == null) {
            throw exception(ISSUE_PUBLIC_NOT_EXISTS);
        }
        return buildPublicResp(board, issue, rankIssueItemMapper.selectListByIssueId(issue.getId()));
    }

    @Override
    public AppRankBoardRespVO getPublicHistory(String boardCode, String issueNo) {
        RankBoardDO board = validateBoardExists(boardCode);
        RankIssueDO issue = rankIssueMapper.selectPublicHistoryByBoardIdAndIssueNo(board.getId(), issueNo);
        if (issue == null) {
            throw exception(ISSUE_PUBLIC_NOT_EXISTS);
        }
        return buildPublicResp(board, issue, rankIssueItemMapper.selectListByIssueId(issue.getId()));
    }

    private RankBoardDO validateBoardExists(Long boardId) {
        RankBoardDO board = rankBoardMapper.selectById(boardId);
        if (board == null) {
            throw exception(BOARD_NOT_EXISTS);
        }
        return board;
    }

    private RankBoardDO validateBoardExists(String boardCode) {
        RankBoardDO board = rankBoardMapper.selectByCode(boardCode);
        if (board == null) {
            throw exception(BOARD_NOT_EXISTS);
        }
        return board;
    }

    private RankIssueDO validateIssueExists(Long issueId) {
        RankIssueDO issue = rankIssueMapper.selectById(issueId);
        if (issue == null) {
            throw exception(ISSUE_NOT_EXISTS);
        }
        return issue;
    }

    private void validateIssueNoUnique(Long issueId, Long boardId, String issueNo) {
        RankIssueDO issue = rankIssueMapper.selectByBoardIdAndIssueNo(boardId, issueNo);
        if (issue == null) {
            return;
        }
        if (issueId == null || !issue.getId().equals(issueId)) {
            throw exception(ISSUE_NO_EXISTS);
        }
    }

    private void validateItems(List<RankIssueItemSaveReqVO> items) {
        if (CollUtil.isEmpty(items)) {
            throw exception(ISSUE_ITEM_EMPTY);
        }
        Set<Integer> ranks = new HashSet<>();
        for (RankIssueItemSaveReqVO item : items) {
            if (!ranks.add(item.getRankNo())) {
                throw exception(ISSUE_RANK_DUPLICATE);
            }
        }
    }

    private void replaceIssueItems(Long issueId, List<RankIssueItemSaveReqVO> items) {
        rankIssueItemMapper.deleteByIssueId(issueId);
        List<RankIssueItemDO> itemDOs = BeanUtils.toBean(items, RankIssueItemDO.class);
        for (RankIssueItemDO itemDO : itemDOs) {
            itemDO.setId(null);
            itemDO.setIssueId(issueId);
            itemDO.clean();
        }
        rankIssueItemMapper.insertBatch(itemDOs);
    }

    private AppRankBoardRespVO buildPublicResp(RankBoardDO board, RankIssueDO issue, List<RankIssueItemDO> items) {
        if (CollUtil.isEmpty(items)) {
            throw exception(ISSUE_PUBLIC_NOT_EXISTS);
        }
        AppRankBoardRespVO respVO = new AppRankBoardRespVO();
        respVO.setBoardId(board.getId());
        respVO.setBoardName(board.getName());
        respVO.setBoardCode(board.getCode());
        respVO.setTitle(StrUtil.blankToDefault(issue.getSnapshotTitle(), board.getTitle()));
        respVO.setSubtitle(StrUtil.blankToDefault(issue.getSnapshotSubtitle(), board.getSubtitle()));
        respVO.setIssueNo(issue.getIssueNo());
        respVO.setPublishTime(issue.getPublishTime());
        respVO.setThemeCode(board.getThemeCode());
        respVO.setShowRankNo(board.getShowRankNo());
        respVO.setShowIssueNo(board.getShowIssueNo());
        respVO.setItems(BeanUtils.toBean(items, AppRankBoardRespVO.Item.class));
        return respVO;
    }
}
