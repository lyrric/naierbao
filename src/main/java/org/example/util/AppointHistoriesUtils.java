package org.example.util;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.example.model.LocalAppointHistory;

import java.util.Collections;
import java.util.List;

public class AppointHistoriesUtils {


    public static final String APPOINT_HISTORY_PATH = "./AppointHistory.data";


    public static List<LocalAppointHistory> getAppointHistories() {
        if (FileUtil.exist(APPOINT_HISTORY_PATH)) {
            String json = FileUtil.readString(APPOINT_HISTORY_PATH, "UTF-8");
            if (json == null || json.isEmpty()) {
                return Collections.emptyList();
            }
            return JSONObject.parseObject(json, new TypeReference<List<LocalAppointHistory>>(){}.getType());
        }
        return Collections.emptyList();
    }

    public static void saveAppointHistory(List<LocalAppointHistory> appointHistories) {
        String json = JSONObject.toJSONString(appointHistories);
        FileUtil.writeString(json, APPOINT_HISTORY_PATH, "UTF-8");
    }

}
