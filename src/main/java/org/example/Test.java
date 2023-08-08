package org.example;

import cn.hutool.core.date.DateUtil;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test {
    public static void main(String[] args) {
        MktActivityUnitDTO dto1 = new MktActivityUnitDTO();
        dto1.setStatus(ActivityUnitStatusEnum.ONLINE.getCode());
        String dateStr1="2023-08-03 11:30:00";
        dto1.setGmtCreated(DateUtil.parse(dateStr1,"yyyy-MM-dd HH:mm:ss"));
        MktActivityUnitDTO dto2 = new MktActivityUnitDTO();
        dto2.setStatus(ActivityUnitStatusEnum.ONLINE.getCode());
        String dateStr2="2023-08-04 11:30:00";
        dto2.setGmtCreated(DateUtil.parse(dateStr2,"yyyy-MM-dd HH:mm:ss"));
        MktActivityUnitDTO dto3 = new MktActivityUnitDTO();
        dto3.setStatus(ActivityUnitStatusEnum.OFFLINE.getCode());
        String dateStr3="2023-08-05 11:30:00";
        dto3.setGmtCreated(DateUtil.parse(dateStr3,"yyyy-MM-dd HH:mm:ss"));
        List<MktActivityUnitDTO> activityUnitDTOS = Stream.of(dto1,dto2,dto3).collect(Collectors.toList());
        // 寻找其中最新的一个上架中的活动单元
        Optional<MktActivityUnitDTO> mktActivityUnitDTO = activityUnitDTOS.stream().filter(unit -> ActivityUnitStatusEnum.ONLINE.getCode().equals(unit.getStatus())).max(Comparator.comparing(MktActivityUnitDTO::getGmtCreated));
        System.out.println(mktActivityUnitDTO);
    }
}
