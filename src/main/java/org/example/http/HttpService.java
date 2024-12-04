package org.example.http;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.example.model.BaseResult;
import org.example.model.Ticket;
import org.example.model.TicketResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class HttpService {

    public static final int TICKET_ID = 23603;

    public static TicketResult getTicket(Integer shopId) throws IOException {
        String url = String.format("https://reserve.neobiochina.com/api/neobio-activity/activityAppointment/appointment/"
                + shopId + "?ticketId=" + 23603);
        HttpGet get = new HttpGet(url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(get);
        HttpEntity httpEntity = response.getEntity();
        String json = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
        return JSONObject.parseObject(json, TicketResult.class);
    }

    public static BaseResult appoint(Ticket ticket, String phone) {
        String url = "https://reserve.neobiochina.com/api/neobio-activity/activityAppointment/save";
        Map<String, Object> body = new HashMap<>();
        body.put("adultNum", 1);
        body.put("appointmentDate", ticket.getAppointmentDate());
        body.put("childNum", 1);
        body.put("phone", phone);
        body.put("shopId", ticket.getShopId());
        body.put("shopName", ticket.getShopName());
        body.put("ticketId", TICKET_ID);
        body.put("ticketName", "一周岁以下宝宝免费体验票");
        String result = HttpUtil.post(url, JSONObject.toJSONString(body));
        return JSONObject.parseObject(result, BaseResult.class);
    }

    public static void sendMessage(String spt, String content, String summary){
        Map<String, Object> body = new HashMap<>();
        body.put("content", content);
        body.put("summary", summary);
        body.put("contentType", 2);
        body.put("spt", spt);
        String json = HttpUtil.post("https://wxpusher.zjiecode.com/api/send/message/simple-push", JSONObject.toJSONString(body));
        log.info("发型消息结果: {}", json);
    }


}
