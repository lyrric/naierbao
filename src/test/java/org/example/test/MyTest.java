package org.example.test;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.http.HttpService;
import org.example.http.MessageService;
import org.example.model.AppointHistory;
import org.example.model.BaseResult;
import org.example.model.Ticket;
import org.example.model.TicketResult;
import org.example.util.ConfigUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Ignore
@Slf4j
public class MyTest {


    @Test
    public void testSendMessage(){
        Ticket ticket1 = new Ticket();
        ticket1.setShopName("成都店");
        ticket1.setStockNum(10);
        ticket1.setAppointmentDate("2024.12.06");
        ticket1.setAppointmentNum(6);
        MessageService.sendRemainTicketMessage("成都店", ticket1);
    }

    @Test
    public void testGetTicket() throws IOException {
        TicketResult ticket = HttpService.getTicket(10);
        System.out.println(JSONObject.toJSONString(ticket));
    }

    @Test
    public void testSaveAppointHistory(){
        AppointHistory appointHistory = new AppointHistory();
        appointHistory.setPhone("137****0001");
        appointHistory.setDate("2024.12.06");
        List<AppointHistory> appointHistories = Collections.singletonList(appointHistory);
        ConfigUtils.saveAppointHistory(appointHistories);
    }
    @Test
    public void testGetAppointHistory(){
        List<AppointHistory> appointHistories = ConfigUtils.getAppointHistories();
        System.out.println(JSONObject.toJSONString(appointHistories));
    }

    @Test
    public void testAppoint() throws IOException, InterruptedException {
        TicketResult result = HttpService.getTicket(10);
        Ticket ticket = result.getData().stream().filter(t->t.getAppointmentDate().equals("2024.12.04")).findFirst().get();
        while (true){
            BaseResult baseResult = HttpService.appoint(ticket, "15682278261");
            log.info("预约结果 {}", JSONObject.toJSONString(baseResult));
            if (baseResult.getSuccess()) {
                break;
            }
            Thread.sleep(100);
        }
        log.info("预约成功");

    }

}
