package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * @author 龙
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    public OrderMapper orderMapper;

    @Autowired
    public UserMapper userMapper;

    @Autowired
    private WorkspaceService workspaceService;

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

    public void export(HttpServletResponse response) {
        //查询获取数据
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        //查询概览数据
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(LocalDateTime.of(begin,
                LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));

        //拿到模版文件并通过输入流创建一个新的excel文件
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("moban/运营数据报表模板.xlsx");
        try{
            XSSFWorkbook excel = new XSSFWorkbook(inputStream);
            //填充数据
            XSSFSheet sheet = excel.getSheet("Sheet1");

            //填充数据--时间
            sheet.getRow(1).getCell(1).setCellValue("时间：" + begin + "至" + end);
            // 创建样式
            CellStyle centeredStyle = excel.createCellStyle();
            centeredStyle.setAlignment(HorizontalAlignment.CENTER);
            sheet.getRow(1).getCell(1).setCellStyle(centeredStyle);

            //获得第4行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());

            //获得第5行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

            //填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(i);
                //查询某一天的营业数据
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));

                //获得某一行
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }


            //传输excel文件到客户端下载
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);
            //关闭资源
            outputStream.close();
            excel.close();
        }catch (IOException e){
            e.printStackTrace();
        }
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
