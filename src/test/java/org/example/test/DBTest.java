package org.example.test;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.db.AppointHistoryBiz;
import org.example.db.DBUtil;
import org.example.model.AppointHistory;
import org.example.model.Config;
import org.junit.Ignore;
import org.junit.Test;

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
        AppointHistory.setStatus(1);
        AppointHistory.setType(1);
        AppointHistoryBiz.insert(AppointHistory);
    }

    @Test
    public void testDelete(){
       AppointHistoryBiz.deleteById("3434343");
    }

    @Test
    public void testUpdate(){
        AppointHistoryBiz.updateAptHist("123", 2, 2);
    }

    @Test
    public void testQuery(){
        List<AppointHistory> appointHistories = AppointHistoryBiz.selectList(Collections.singletonList("1"), "137****0001", null, null);
        System.out.println(JSONObject.toJSONString(appointHistories));
    }

/*    @Test
    public void testGetConfigs(){
        List<Config> configs = AppointHistoryBiz.getConfigs();
        System.out.println(JSONObject.toJSONString(configs));
    }
    @Test
    public void testInsetConfig(){
        List<Config> configs = DBUtil.getConfigs();
        System.out.println(JSONObject.toJSONString(configs));
        Config config = configs.get(0);
        DBUtil.insertConfig1(config);
    }*/

}
