package org.example.http;

import lombok.extern.slf4j.Slf4j;
import org.example.model.Config;
import org.example.model.Ticket;

import java.util.List;

@Slf4j
public class MessageService {

    public static void sendRemainTicketMessage(String shopName, Ticket ticket, Config config) {
        StringBuilder message = new StringBuilder();
        message.append(String.format("<p>门店：%s，日期：%s，剩余%s张!</p>", ticket.getShopName(), ticket.getAppointmentDate(), ticket.getStockNum() - ticket.getAppointmentNum()));
        String summary = String.format("%s有票了", shopName);
        log.info("发送消息 {}", message);
        for (String spt : config.getSpts()) {
            HttpService.sendMessage(spt, message.toString(), summary);
        }
    }

    public static void sendAppointedMessage(String shopName, String date, String phones,Config config) {
        try {
            String content = String.format("<p>门店：%s，日期：%s，预约手机号：%s，预约成功！</p>", shopName, date, phones);
            String summary = String.format("%s抢票成功", shopName);
            log.info("发送消息 {}", content);
            for (String spt : config.getSpts()) {
                HttpService.sendMessage(spt, content, summary);
            }
        }catch (Exception e){
            log.error("发送预约成功消息失败", e);
        }

    }
}
