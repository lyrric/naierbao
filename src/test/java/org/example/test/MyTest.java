package org.example.test;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.http.HttpService;
import org.example.http.MessageService;
import org.example.model.BaseResult;
import org.example.model.CloudAppointHistory;
import org.example.model.LocalAppointHistory;
import org.example.model.Ticket;
import org.example.util.AppointHistoriesUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
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
        //MessageService.sendRemainTicketMessage("成都店", ticket1,);
    }

    @Test
    public void testGetTicket() throws IOException {
        BaseResult<List<Ticket>> result = HttpService.getTicket(10);
        System.out.println(JSONObject.toJSONString(result));
    }

    @Test
    public void testSaveAppointHistory(){
        LocalAppointHistory localAppointHistory = new LocalAppointHistory();
        localAppointHistory.setPhone("137****0001");
        localAppointHistory.setDate("2024.12.06");
        List<LocalAppointHistory> appointHistories = Collections.singletonList(localAppointHistory);
        AppointHistoriesUtils.saveAppointHistory(appointHistories);
    }
    @Test
    public void testGetAppointHistory(){
        List<LocalAppointHistory> appointHistories = AppointHistoriesUtils.getAppointHistories();
        System.out.println(JSONObject.toJSONString(appointHistories));
    }
    @Test
    public void testGetActivityAppointment() throws IOException {
        BaseResult<List<CloudAppointHistory>> activityAppointment = HttpService.getActivityAppointment("18648831786");
        System.out.println(JSONObject.toJSONString(activityAppointment));
    }
    @Test
    public void testCancelAppointment() throws IOException {
        BaseResult<String> result = HttpService.cancelAppointment("1864227447283732481");
        System.out.println(JSONObject.toJSONString(result));
    }

    @Test
    public void testAppoint() throws IOException, InterruptedException {
        BaseResult<List<Ticket>> result = HttpService.getTicket(10);
        Ticket ticket = result.getData().stream().filter(t->t.getAppointmentDate().equals("2024.12.04")).findFirst().get();
        while (true){
            BaseResult<String> baseResult = HttpService.appoint(ticket, "15682278261");
            log.info("预约结果 {}", JSONObject.toJSONString(baseResult));
            if (baseResult.getSuccess()) {
                break;
            }
            Thread.sleep(100);
        }
        log.info("预约成功");
    }

}
