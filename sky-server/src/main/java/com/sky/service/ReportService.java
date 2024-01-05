package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

/**
 * @author 龙
 */
public interface ReportService {

    /**
     *
     * @param begin 开始时间
     * @param end 结束时间
     * @return 营业额数据
     */
    TurnoverReportVO getTurnoverReport(LocalDate begin,LocalDate end);

    /**
     *
     * @param begin 开始时间
     * @param end 结束时间
     * @return 统计出的用户数据
     */
    UserReportVO getUserStatistics(LocalDate begin,LocalDate end);

    /**
     *
     * @param begin 开始时间
     * @param end 结束时间
     * @return 统计出的订单数据
     */
    OrderReportVO getOrdersStatistics(LocalDate begin,LocalDate end);

    /**
     *
     * @param begin 开始时间
     * @param end 结束时间
     * @return 销量top10
     */
    SalesTop10ReportVO getSalesTop10(LocalDate begin,LocalDate end);

    /**
     * 导出Excel表
     */
    void export(HttpServletResponse response);
}
