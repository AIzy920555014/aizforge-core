package cn.iocoder.yudao.module.rank.service.issue;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.rank.controller.admin.issue.vo.RankIssuePageReqVO;
import cn.iocoder.yudao.module.rank.controller.admin.issue.vo.RankIssueSaveReqVO;
import cn.iocoder.yudao.module.rank.controller.app.board.vo.AppRankBoardRespVO;
import cn.iocoder.yudao.module.rank.dal.dataobject.issue.RankIssueDO;
import cn.iocoder.yudao.module.rank.dal.dataobject.issue.RankIssueItemDO;

import java.util.List;

public interface RankIssueService {

    Long createRankIssue(RankIssueSaveReqVO createReqVO);

    void updateRankIssue(RankIssueSaveReqVO updateReqVO);

    void deleteRankIssue(Long id);

    RankIssueDO getRankIssue(Long id);

    List<RankIssueItemDO> getRankIssueItems(Long issueId);

    PageResult<RankIssueDO> getRankIssuePage(RankIssuePageReqVO pageReqVO);

    void copyLastIssue(Long issueId);

    void publishIssue(Long issueId);

    void offlineIssue(Long issueId);

    AppRankBoardRespVO getPreview(Long issueId);

    AppRankBoardRespVO getPublicCurrent(String boardCode);

    AppRankBoardRespVO getPublicHistory(String boardCode, String issueNo);
}
