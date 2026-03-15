package cn.iocoder.yudao.module.rank.service.board;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.rank.controller.admin.board.vo.RankBoardPageReqVO;
import cn.iocoder.yudao.module.rank.controller.admin.board.vo.RankBoardSaveReqVO;
import cn.iocoder.yudao.module.rank.dal.dataobject.board.RankBoardDO;
import cn.iocoder.yudao.module.rank.dal.mysql.board.RankBoardMapper;
import cn.iocoder.yudao.module.rank.dal.mysql.issue.RankIssueMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.rank.enums.ErrorCodeConstants.BOARD_CODE_EXISTS;
import static cn.iocoder.yudao.module.rank.enums.ErrorCodeConstants.BOARD_DELETE_DENIED;
import static cn.iocoder.yudao.module.rank.enums.ErrorCodeConstants.BOARD_NOT_EXISTS;

@Service
@Validated
public class RankBoardServiceImpl implements RankBoardService {

    @Resource
    private RankBoardMapper rankBoardMapper;
    @Resource
    private RankIssueMapper rankIssueMapper;

    @Override
    public Long createRankBoard(RankBoardSaveReqVO createReqVO) {
        validateBoardCodeUnique(null, createReqVO.getCode());
        RankBoardDO rankBoard = BeanUtils.toBean(createReqVO, RankBoardDO.class);
        rankBoardMapper.insert(rankBoard);
        return rankBoard.getId();
    }

    @Override
    public void updateRankBoard(RankBoardSaveReqVO updateReqVO) {
        validateRankBoardExists(updateReqVO.getId());
        validateBoardCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        RankBoardDO updateObj = BeanUtils.toBean(updateReqVO, RankBoardDO.class);
        rankBoardMapper.updateById(updateObj);
    }

    @Override
    public void deleteRankBoard(Long id) {
        validateRankBoardExists(id);
        if (rankIssueMapper.selectCountByBoardId(id) > 0) {
            throw exception(BOARD_DELETE_DENIED);
        }
        rankBoardMapper.deleteById(id);
    }

    @Override
    public RankBoardDO getRankBoard(Long id) {
        return rankBoardMapper.selectById(id);
    }

    @Override
    public RankBoardDO getRankBoardByCode(String code) {
        return rankBoardMapper.selectByCode(code);
    }

    @Override
    public PageResult<RankBoardDO> getRankBoardPage(RankBoardPageReqVO pageReqVO) {
        return rankBoardMapper.selectPage(pageReqVO);
    }

    @Override
    public List<RankBoardDO> getRankBoardSimpleList() {
        return rankBoardMapper.selectEnabledList();
    }

    private void validateRankBoardExists(Long id) {
        if (rankBoardMapper.selectById(id) == null) {
            throw exception(BOARD_NOT_EXISTS);
        }
    }

    private void validateBoardCodeUnique(Long id, String code) {
        RankBoardDO board = rankBoardMapper.selectByCode(code);
        if (board == null) {
            return;
        }
        if (id == null || !board.getId().equals(id)) {
            throw exception(BOARD_CODE_EXISTS);
        }
    }
}
