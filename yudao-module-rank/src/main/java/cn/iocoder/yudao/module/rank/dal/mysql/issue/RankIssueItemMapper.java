package cn.iocoder.yudao.module.rank.dal.mysql.issue;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.rank.dal.dataobject.issue.RankIssueItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RankIssueItemMapper extends BaseMapperX<RankIssueItemDO> {

    default List<RankIssueItemDO> selectListByIssueId(Long issueId) {
        return selectList(new LambdaQueryWrapperX<RankIssueItemDO>()
                .eq(RankIssueItemDO::getIssueId, issueId)
                .orderByAsc(RankIssueItemDO::getSortOrder)
                .orderByAsc(RankIssueItemDO::getRankNo)
                .orderByAsc(RankIssueItemDO::getId));
    }

    default void deleteByIssueId(Long issueId) {
        delete(new LambdaQueryWrapperX<RankIssueItemDO>()
                .eq(RankIssueItemDO::getIssueId, issueId));
    }
}
