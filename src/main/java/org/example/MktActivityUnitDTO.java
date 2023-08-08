package org.example;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 活动单元实体
 */
@Data
public class MktActivityUnitDTO implements Serializable {

    /**
     * 活动单元ID
     */
    private Long id;

    /**
     * 活动单元编码
     */
    private String code;

    /**
     * 活动单元名称
     */
    private String name;


    private String type;

    /**
     * 活动计划code
     */
    private String actPlanCode;

    /**
     * 活动说明
     */
    private String activityDesc;

    /**
     * 活动单元状态
     * @see ActivityUnitStatusEnum
     */
    private String status;

    /**
     * 活动开始时间
     */
    private Date startTime;

    /**
     * 活动结束时间
     */
    private Date endTime;

    /**
     * 修改人
     */
    private String modifiedBy;

    private Date gmtCreated;
    private String tenantId;
    private Date gmtModified;
    private Integer isDeleted;
    private String ext;
}
