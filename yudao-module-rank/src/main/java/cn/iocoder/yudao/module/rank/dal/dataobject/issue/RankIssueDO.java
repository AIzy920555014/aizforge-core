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

import java.time.LocalDateTime;

/**
 * 榜单期次 DO
 */
@TableName("rank_issue")
@KeySequence("rank_issue_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankIssueDO extends BaseDO {

    @TableId
    private Long id;

    private Long boardId;

    private String issueNo;

    private LocalDateTime publishTime;

    private Integer status;

    private String snapshotTitle;

    private String snapshotSubtitle;

    private Integer sortVersion;

    private String remark;
}
