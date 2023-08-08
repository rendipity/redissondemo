package org.example;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 活动单元状态定义
 * @author xiehuaqiao
 */

@Getter
@AllArgsConstructor
public enum ActivityUnitStatusEnum {

    DEBUGGING(2,"debugging", "待联调"),
    WAIT_DEBUG(2,"wait_debug", "待联调"),
    WAIT_ONLINE(3, "wait_online", "待上架"),
    ONLINE(5,"online", "已上架"),
    OFFLINE(8,"offline", "已下架"),
    FINISH(9,"finish", "已结束")
    ;

    private final Integer id;
    private final String code;
    private final String desc;

    public static ActivityUnitStatusEnum getByStatusCode(String statusCode) {
        for (ActivityUnitStatusEnum statusEnum : ActivityUnitStatusEnum.values()) {
            if (StrUtil.equals(statusCode, statusEnum.getCode())) {
                return statusEnum;
            }
        }
        return null;
    }
}
