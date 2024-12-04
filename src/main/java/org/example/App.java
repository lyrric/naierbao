package org.example;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.http.HttpService;
import org.example.http.MessageService;
import org.example.model.AppointHistory;
import org.example.model.BaseResult;
import org.example.model.Ticket;
import org.example.model.TicketResult;
import org.example.util.ConfigUtils;
import org.example.util.PhoneUtil;

import java.util.*;

@Slf4j
public class App {

    private long errorCount = 0;
    private long totalCount = 0;


    private List<AppointHistory> appointHistories = new ArrayList<>();

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
            TicketResult ticket = HttpService.getTicket(shopId);
            if(!ticket.getSuccess()){
                log.error("接口返回出错: {}" ,ticket.getMsg());
                errorCount++;
                return;
            }
            check(ticket.getData());
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
                if (!hasAppointed(ticket)) {
                    log.info("{}有票了", ticket.getShopName());
                    //发送消息
                    MessageService.sendRemainTicketMessage(ticket.getShopName(), ticket);
                    //预约
                    appoint(ticket);
                }

            }
        }
    }

    public void init(){
        List<AppointHistory> t = ConfigUtils.getAppointHistories();
        if (!t.isEmpty()) {
            appointHistories = t;
        }
    }
    public void appoint(Ticket ticket) {
        if (hasAppointed(ticket)) {
            log.info("{} {} 已经预约过了 ", ticket.getShopName(), ticket.getAppointmentDate());
            return;
        }
        //预约
        String phone = PhoneUtil.generateRandomPhoneNumber();
        try {
            BaseResult result = HttpService.appoint(ticket, phone);
            if (!result.getSuccess()) {
                log.error("预约时发生错误 返回数据 {}", JSONObject.toJSONString(result));
                return;
            }
            appointHistories.add(new AppointHistory(phone, ticket.getShopId(), ticket.getShopName(), ticket.getAppointmentDate()));
            ConfigUtils.saveAppointHistory(appointHistories);
            //发送预约成功短信
            MessageService.sendAppointedMessage(ticket.getShopName(), ticket.getAppointmentDate(), phone);
        }catch (Exception e){
            log.error("预约时发生错误 ", e);
        }

    }

    private boolean hasAppointed(Ticket ticket) {
        return appointHistories.stream()
                        .filter(t -> t.getDate().equals(ticket.getAppointmentDate()))
                        .anyMatch(t -> t.getShopId().equals(ticket.getShopId()));
    }

}