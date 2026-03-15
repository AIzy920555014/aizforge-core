package cn.iocoder.yudao.module.rank.dal.mysql.board;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.rank.controller.admin.board.vo.RankBoardPageReqVO;
import cn.iocoder.yudao.module.rank.dal.dataobject.board.RankBoardDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RankBoardMapper extends BaseMapperX<RankBoardDO> {

    default PageResult<RankBoardDO> selectPage(RankBoardPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<RankBoardDO>()
                .likeIfPresent(RankBoardDO::getName, reqVO.getName())
                .likeIfPresent(RankBoardDO::getCode, reqVO.getCode())
                .eqIfPresent(RankBoardDO::getStatus, reqVO.getStatus())
                .orderByDesc(RankBoardDO::getId));
    }

    default RankBoardDO selectByCode(String code) {
        return selectOne(RankBoardDO::getCode, code);
    }

    default List<RankBoardDO> selectEnabledList() {
        return selectList(new LambdaQueryWrapperX<RankBoardDO>()
                .eq(RankBoardDO::getStatus, 0)
                .orderByDesc(RankBoardDO::getId));
    }
}
