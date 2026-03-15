package cn.iocoder.yudao.module.rank.dal.dataobject.issue;

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

import java.math.BigDecimal;

/**
 * 榜单期次明细 DO
 */
@TableName("rank_issue_item")
@KeySequence("rank_issue_item_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankIssueItemDO extends BaseDO {

    @TableId
    private Long id;

    private Long issueId;

    private Integer rankNo;

    private String subjectCode;

    private String subjectName;

    private String iconUrl;

    private BigDecimal amountValue;

    private String displayText;

    private Integer sortOrder;

    private String remark;
}
