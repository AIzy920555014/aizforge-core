package cn.iocoder.yudao.module.rank.dal.dataobject.board;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 榜单定义 DO
 */
@TableName("rank_board")
@KeySequence("rank_board_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankBoardDO extends BaseDO {

    @TableId
    private Long id;

    private String name;

    private String code;

    private String title;

    private String subtitle;

    private String boardType;

    private String themeCode;

    private Boolean showRankNo;

    private Boolean showIssueNo;

    private Integer status;

    private String remark;
}
