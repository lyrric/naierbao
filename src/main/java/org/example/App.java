package org.example;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.http.HttpService;
import org.example.http.MessageService;
import org.example.model.Config;
import org.example.model.LocalAppointHistory;
import org.example.model.BaseResult;
import org.example.model.Ticket;
import org.example.util.AppointHistoriesUtils;
import org.example.util.ConfigUtils;
import org.example.util.PhoneUtil;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class App {

    private long errorCount = 0;
    private long totalCount = 0;


    private List<LocalAppointHistory> appointHistories = new ArrayList<>();
    private Config config;

    public void start() {
        init();
        log.info("开始运行");
        while (true) {
            doRun(Ticket.SHOP_HUANQIU);
            try {
                Thread.sleep(1000 * 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            totalCount++;
            if (totalCount % 100 == 0) {
                log.info("已尝试次数: {} 异常次数: {}", totalCount, errorCount);
            }
        }
    }

    public void doRun(int shopId) {
        try {
            BaseResult<List<Ticket>> result = HttpService.getTicket(shopId);
            if(!result.getSuccess()){
                log.error("接口返回出错: {}" ,result.getMsg());
                errorCount++;
                return;
            }
            check(result.getData());
        } catch (Exception e) {
            log.error("未知错误: {}" ,e.getMessage());
            errorCount++;
        }
    }
    public void check(List<Ticket> tickets) {
        if (tickets == null || tickets.isEmpty()) {
            log.info("没有获取到数据");
            return;
        }
        for (Ticket ticket : tickets) {
            if (ticket.getStockNum() - ticket.getAppointmentNum() > 0) {
                if (appointedCount(ticket) < config.getMaxCountPerDay()) {
                    log.info("{}有票了", ticket.getShopName());
                    //发送消息
                    //MessageService.sendRemainTicketMessage(ticket.getShopName(), ticket, config);
                    //预约
                    appoint(ticket);
                }

            }
        }
    }

    public void init(){
        List<LocalAppointHistory> t = AppointHistoriesUtils.getAppointHistories();
        if (!t.isEmpty()) {
            appointHistories = t;
        }
        config = ConfigUtils.getAppointHistories();
    }
    public void appoint(Ticket ticket) {
        long appointedCount = appointedCount(ticket);
        if (appointedCount >= config.getMaxCountPerDay()) {
            log.info("{} {} 已经预约过了 ", ticket.getShopName(), ticket.getAppointmentDate());
            return;
        }
        for (long i = appointedCount; i < config.getMaxCountPerDay(); i++) {
            //预约
            String phone = PhoneUtil.generateRandomPhoneNumber();
            try {
                BaseResult<String> result = HttpService.appoint(ticket, phone);
                if (!result.getSuccess()) {
                    log.error("预约时发生错误 返回数据 {}", JSONObject.toJSONString(result));
                    return;
                }
                appointHistories.add(new LocalAppointHistory(phone, ticket.getShopId(), ticket.getShopName(), ticket.getAppointmentDate()));
                AppointHistoriesUtils.saveAppointHistory(appointHistories);
                String phones = getPhones(ticket);
                //发送预约成功短信
                MessageService.sendAppointedMessage(ticket.getShopName(), ticket.getAppointmentDate(), phones, config);
            }catch (Exception e){
                log.error("预约时发生错误 ", e);
                return;
            }
        }


    }

    private String getPhones(Ticket ticket){
        return appointHistories.stream()
                .filter(t -> t.getDate().equals(ticket.getAppointmentDate()))
                .filter(t -> t.getShopId().equals(ticket.getShopId()))
                .map(LocalAppointHistory::getPhone)
                .collect(Collectors.joining("，"));
    }

    private long appointedCount(Ticket ticket) {
        return appointHistories.stream()
                .filter(t -> t.getDate().equals(ticket.getAppointmentDate()))
                .filter(t -> t.getShopId().equals(ticket.getShopId()))
                .count();
    }

}