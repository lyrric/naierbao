package org.example;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.db.DBUtil;
import org.example.http.HttpService;
import org.example.http.MessageService;
import org.example.model.*;
import org.example.util.AppointHistoriesUtils;
import org.example.util.AreaUtil;
import org.example.util.ConfigUtils;
import org.example.util.PhoneUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.hutool.core.date.DatePattern.NORM_DATETIME_PATTERN;

@Slf4j
public class App {


    private Map<String, Config> configMap;

    public void start() {
        init();
        log.info("开始运行");
        while (true) {
            for (Area area : AreaUtil.getAreas()) {
                log.info("开始遍历地区 {}", area.getDictValue());
                List<Area> children = area.getChildren();
                if (children != null && !children.isEmpty()) {
                    for (Area child : children) {
                        log.info("开始查询门店：{}", child.getDictValue());
                        doRun(child.getRemark(), configMap.get(child.getRemark()));
                        log.info("结束查询门店：{}", child.getDictValue());
                    }
                }
                log.info("结束遍历地区 {}", area.getDictValue());
            }
            try {
                Thread.sleep(1000 * 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void doRun(String shopId,Config config) {
        try {
            BaseResult<List<Ticket>> result = HttpService.getTicket(shopId);
            if(!result.getSuccess()){
                log.error("接口返回出错: {}" ,result.getMsg());
                return;
            }
            check(result.getData(),config);
        } catch (Exception e) {
            log.error("未知错误: " ,e);
        }
    }

    public void check(List<Ticket> tickets, Config config) {
        if (tickets == null || tickets.isEmpty()) {
            log.info("没有获取到数据");
            return;
        }
        for (Ticket ticket : tickets) {
            if (ticket.getStockNum() - ticket.getAppointmentNum() > 0) {
                if (appointedCount(ticket) < (config == null ? 1 : config.getMaxCountPerDay())) {
                    log.info("{}有票了", ticket.getShopName());
                    //预约
                    appoint(ticket, config);
                }

            }
        }
    }

    public void init(){
        List<Config> appointHistories = ConfigUtils.getAppointHistories();
        configMap = appointHistories.stream()
                .collect(Collectors.toMap(Config::getShopId, Function.identity()));
        log.info("读取配置文件 {}", JSONObject.toJSONString(configMap));
    }

    public void appoint(Ticket ticket, Config config) {
        long appointedCount = appointedCount(ticket);
        boolean hasMaxAppointment = false;
        for (long i = appointedCount; i < (config == null ? 1 : config.getMaxCountPerDay()); i++) {
            //预约
            String phone = PhoneUtil.generateRandomPhoneNumber();
            try {
                BaseResult<AppointHistory> result = HttpService.appoint(ticket, phone);
                if (!result.getSuccess()) {
                    log.error("预约时发生错误 返回数据 {}", JSONObject.toJSONString(result));
                    break;
                }
                AppointHistory AppointHistory = result.getData();
                AppointHistory.setCreateTime(DateUtil.format(new Date(), NORM_DATETIME_PATTERN));
                AppointHistory.setType(1);
                DBUtil.insert(AppointHistory);
                log.info("预约成功");
                hasMaxAppointment = true;
            } catch (Exception e) {
                log.error("预约时发生错误 ", e);
                break;
            }
        }
        if (hasMaxAppointment) {
            String phones = getPhones(ticket);
            //发送预约成功短信
            if (config != null) {
                MessageService.sendAppointedMessage(ticket.getShopName(), ticket.getAppointmentDate(), phones, config);
            }

        }
    }

    private String getPhones(Ticket ticket){
        return DBUtil.selectList(Collections.singletonList(ticket.getShopId()),null,1, 1).stream()
                .filter(t -> t.getAppointmentDate().equals(ticket.getAppointmentDate()))
                .map(AppointHistory::getPhone)
                .collect(Collectors.joining(","));

    }

    private long appointedCount(Ticket ticket) {
        return DBUtil.selectList(Collections.singletonList(ticket.getShopId()),null,1, 1).stream()
                .filter(t -> t.getAppointmentDate().equals(ticket.getAppointmentDate()))
                .filter(t -> t.getShopId().equals(ticket.getShopId()))
                .count();
    }

}