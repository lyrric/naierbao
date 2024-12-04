package org.example.http;

import lombok.extern.slf4j.Slf4j;
import org.example.model.Ticket;

import java.util.List;

@Slf4j
public class MessageService {

    private static final String SPT_WXD = "SPT_1";
    private static final String SPT_CLQ = "SPT_2";

    public static void sendRemainTicketMessage(String shopName, Ticket ticket) {
        StringBuilder message = new StringBuilder();
        message.append(String.format("<p>门店：%s，日期：%s，剩余%s张!</p>", ticket.getShopName(), ticket.getAppointmentDate(), ticket.getStockNum() - ticket.getAppointmentNum()));
        String summary = String.format("%s有票了", shopName);
        log.info("发送消息 {}", message);
        HttpService.sendMessage(SPT_WXD, message.toString(), summary);
        HttpService.sendMessage(SPT_CLQ, message.toString(), summary);
    }

    public static void sendAppointedMessage(String shopName, String date, String phone) {
        try {
            String content = String.format("<p>门店：%s，日期：%s，预约手机号：%s，预约成功！</p>", shopName, date, phone);
            String summary = String.format("%s抢票成功", shopName);
            log.info("发送消息 {}", content);
            HttpService.sendMessage(SPT_WXD, content, summary);
            HttpService.sendMessage(SPT_CLQ, content, summary);
        }catch (Exception e){
            log.error("发送预约成功消息失败", e);
        }

    }
}
