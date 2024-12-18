package org.example.http;

import lombok.extern.slf4j.Slf4j;
import org.example.model.entity.Config;

@Slf4j
public class MessageService {


    public static void sendAppointedMessage(String area, String shopName, String date, String phones, Config Config) {
        try {
            String content = String.format("<p>门店：%s，日期：%s，预约手机号：%s，预约成功！</p>", area + "-" + shopName, date, phones);

            content = content + String.format("备注：%s </p>",Config.getRemark());
            String summary = String.format("%s 抢票成功", area + "-" + shopName);
            log.info("发送消息 {}", content);
            for (String spt : Config.getSptsList()) {
                HttpService.sendMessage(spt, content, summary);
            }
        } catch (Exception e) {
            log.error("发送预约成功消息失败", e);
        }

    }
}
