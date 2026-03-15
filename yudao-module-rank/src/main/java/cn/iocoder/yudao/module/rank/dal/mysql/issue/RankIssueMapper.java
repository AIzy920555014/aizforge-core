package cn.iocoder.yudao.module.rank.dal.mysql.issue;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.rank.controller.admin.issue.vo.RankIssuePageReqVO;
import cn.iocoder.yudao.module.rank.dal.dataobject.issue.RankIssueDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RankIssueMapper extends BaseMapperX<RankIssueDO> {

    default PageResult<RankIssueDO> selectPage(RankIssuePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<RankIssueDO>()
                .eqIfPresent(RankIssueDO::getBoardId, reqVO.getBoardId())
                .likeIfPresent(RankIssueDO::getIssueNo, reqVO.getIssueNo())
                .eqIfPresent(RankIssueDO::getStatus, reqVO.getStatus())
                .orderByDesc(RankIssueDO::getPublishTime)
                .orderByDesc(RankIssueDO::getId));
    }

    default RankIssueDO selectByBoardIdAndIssueNo(Long boardId, String issueNo) {
        return selectOne(new LambdaQueryWrapperX<RankIssueDO>()
                .eq(RankIssueDO::getBoardId, boardId)
                .eq(RankIssueDO::getIssueNo, issueNo)
                .last("LIMIT 1"));
    }

    default RankIssueDO selectLatestByBoardId(Long boardId, Long excludeId) {
        return selectOne(new LambdaQueryWrapperX<RankIssueDO>()
                .eq(RankIssueDO::getBoardId, boardId)
                .neIfPresent(RankIssueDO::getId, excludeId)
                .orderByDesc(RankIssueDO::getPublishTime)
                .orderByDesc(RankIssueDO::getId)
                .last("LIMIT 1"));
    }

    default RankIssueDO selectLatestPublishedByBoardId(Long boardId) {
        return selectOne(new LambdaQueryWrapperX<RankIssueDO>()
                .eq(RankIssueDO::getBoardId, boardId)
                .eq(RankIssueDO::getStatus, 1)
                .orderByDesc(RankIssueDO::getPublishTime)
                .orderByDesc(RankIssueDO::getId)
                .last("LIMIT 1"));
    }

    default RankIssueDO selectPublicHistoryByBoardIdAndIssueNo(Long boardId, String issueNo) {
        return selectOne(new LambdaQueryWrapperX<RankIssueDO>()
                .eq(RankIssueDO::getBoardId, boardId)
                .eq(RankIssueDO::getIssueNo, issueNo)
                .ne(RankIssueDO::getStatus, 0)
                .orderByDesc(RankIssueDO::getId)
                .last("LIMIT 1"));
    }

    default Long selectCountByBoardId(Long boardId) {
        return selectCount(RankIssueDO::getBoardId, boardId);
    }
}
