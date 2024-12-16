package org.example.test;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.db.DBUtil;
import org.example.http.HttpService;
import org.example.model.AppointHistory;
import org.example.model.BaseResult;
import org.example.model.Ticket;
import org.example.util.AppointHistoriesUtils;
import org.junit.Ignore;
import org.junit.Test;

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Ignore
@Slf4j
public class DBTest {


    @Test
    public void testInsert(){
        AppointHistory AppointHistory = new AppointHistory();
        AppointHistory.setId("3434343");
        AppointHistory.setPhone("137****0001");
        AppointHistory.setAppointmentDate("2024.12.06");
        AppointHistory.setStatus(1);
        AppointHistory.setShopId("1");
        AppointHistory.setShopName("成都");
        AppointHistory.setTicketId("成都");
        AppointHistory.setTicketName("成都");
        AppointHistory.setLineType(1);
        AppointHistory.setIsDeleted(1);
        AppointHistory.setIsPunished(false);
        DBUtil.insert(AppointHistory);
    }

    @Test
    public void testUpdate(){
        DBUtil.update("123", 2, 2);
    }

    @Test
    public void testQuery(){
        List<AppointHistory> appointHistories = DBUtil.selectList(Collections.singletonList("1"), "137****0001", null, null);
        System.out.println(JSONObject.toJSONString(appointHistories));
    }

}
