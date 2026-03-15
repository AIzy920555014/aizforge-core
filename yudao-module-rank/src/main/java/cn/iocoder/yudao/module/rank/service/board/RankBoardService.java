package cn.iocoder.yudao.module.rank.service.board;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.rank.controller.admin.board.vo.RankBoardPageReqVO;
import cn.iocoder.yudao.module.rank.controller.admin.board.vo.RankBoardSaveReqVO;
import cn.iocoder.yudao.module.rank.dal.dataobject.board.RankBoardDO;

import java.util.List;

public interface RankBoardService {

    Long createRankBoard(RankBoardSaveReqVO createReqVO);

    void updateRankBoard(RankBoardSaveReqVO updateReqVO);

    void deleteRankBoard(Long id);

    RankBoardDO getRankBoard(Long id);

    RankBoardDO getRankBoardByCode(String code);

    PageResult<RankBoardDO> getRankBoardPage(RankBoardPageReqVO pageReqVO);

    List<RankBoardDO> getRankBoardSimpleList();
}
