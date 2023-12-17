package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    public OrderMapper orderMapper;

    @Autowired
    public UserMapper userMapper;

    @Autowired
    public OrderDetailMapper orderDetailMapper;


    @Override
    public TurnoverReportVO getTurnoverReport(LocalDate begin, LocalDate end) {
        // 获取时间列表
        List<LocalDate> dateList = getDateList(begin, end);
        // 拼接营业额字符串
        StringBuilder sb = new StringBuilder();
        for(LocalDate time : dateList){
            // 一天的开始时间
            LocalDateTime beginTime = LocalDateTime.of(time, LocalTime.MIN);
            // 一天的结束时间
            LocalDateTime endTime = LocalDateTime.of(time, LocalTime.MAX);
            Integer amount = orderMapper.getTurnoverByDate(beginTime, endTime);
            sb.append(amount==null?0:amount).append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        // 获取列表的字符串
        String dateStr = StringUtils.join(dateList, ",");

        return TurnoverReportVO.builder()
                .dateList(dateStr)
                .turnoverList(sb.toString())
                .build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        // 获取时间列表
        List<LocalDate> dateList = getDateList(begin, end);
        // 拼接新增用户字符串
        StringBuilder newUser = new StringBuilder();
        // 拼接总用户量字符串
        StringBuilder totalUser = new StringBuilder();
        // 遍历
        for(LocalDate time : dateList){
            // 获取一天的开始时间
            LocalDateTime beginTime = LocalDateTime.of(time, LocalTime.MIN);
            // 获取一天的结束时间
            LocalDateTime endTime = LocalDateTime.of(time, LocalTime.MAX);
            // 获取新增用户量
            Integer newUserCnt = userMapper.getUserCntByDate(beginTime, endTime);
            newUser.append(newUserCnt == null?0:newUserCnt).append(",");
            // 获取用户总量
            Integer userCnt = userMapper.getUserCnt(endTime);
            totalUser.append(userCnt == null? 0: userCnt).append(",");
        }
        newUser.deleteCharAt(newUser.length() - 1);
        totalUser.deleteCharAt(totalUser.length() - 1);
        // 封装
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(newUser.toString())
                .totalUserList(totalUser.toString())
                .build();
    }

    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        // 获取时间列表
        List<LocalDate> dateList = getDateList(begin, end);
        // 每日订单数
        List<Integer> orderCountList = new LinkedList<>();
        // 每日有效订单数
        List<Integer> validOrderCountList = new LinkedList<>();
        // 记录订单总数
        int totalOrderCnt = 0;
        // 记录有效订单总数
        int validOrderCnt = 0;
        // 遍历时间
        for(LocalDate time : dateList){
            // 获取一天的开始时间和结束时间
            LocalDateTime beginTime = LocalDateTime.of(time, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(time, LocalTime.MAX);
            // 封装数据
            Map<String, Object> map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            // 获取订单数
            Integer orderCnt = orderMapper.getOrderCntByDateAndStatus(map);
            orderCountList.add(orderCnt);
            totalOrderCnt += orderCnt;
            // 获取有效订单(已完成的订单)数
            map.put("status", Orders.COMPLETED);
            Integer completedOrderCnt = orderMapper.getOrderCntByDateAndStatus(map);
            validOrderCountList.add(completedOrderCnt);
            validOrderCnt += completedOrderCnt;
        }
        // 计算订单完成率
        Double orderCompletionRate = totalOrderCnt == 0 ? 0.0 : validOrderCnt/ (double) totalOrderCnt;
        // 封装数据
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCnt)
                .validOrderCount(validOrderCnt)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        // 获取开始时间
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        // 获取结束时间
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        // 调用
        List<GoodsSalesDTO> salesByDate = orderDetailMapper.getSalesByDate(beginTime, endTime);
        // 封装数据
        StringBuilder salesName = new StringBuilder();
        StringBuilder salesNumber = new StringBuilder();
        for(GoodsSalesDTO goods : salesByDate){
            salesName.append(goods.getName()).append(",");
            salesNumber.append(goods.getNumber()).append(",");
        }
        salesName.deleteCharAt(salesName.length() - 1);
        salesNumber.deleteCharAt(salesNumber.length() - 1);
        return SalesTop10ReportVO.builder()
                .nameList(salesName.toString())
                .numberList(salesNumber.toString())
                .build();
    }

    @Override
    public void export() {

    }

    /**
     * 根绝开始时间和结束时间获取时间列表
     * @param begin 开始时间
     * @param end 结束时间
     * @return 时间列表
     */
    public List<LocalDate> getDateList(LocalDate begin, LocalDate end){
        List<LocalDate> list = new LinkedList<>();
        // 添加开始时间
        list.add(begin);
        // 遍历添加，直到到end
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            list.add(begin);
        }
        return list;
    }
}
