package org.example.util;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.example.model.AppointHistory;

import java.util.Collections;
import java.util.List;

public class AppointHistoriesUtils {


    public static final String APPOINT_HISTORY_PATH = "d://AppointHistory.data";


    /*public static List<AppointHistory> getAppointHistories() {
        if (FileUtil.exist(APPOINT_HISTORY_PATH)) {
            String json = FileUtil.readString(APPOINT_HISTORY_PATH, "UTF-8");
            if (json == null || json.isEmpty()) {
                return Collections.emptyList();
            }
            json = "[" + json + "]";
            return JSONObject.parseObject(json, new TypeReference<List<AppointHistory>>(){}.getType());
        }
        return Collections.emptyList();
    }
*/

    public static void saveAppointHistory(AppointHistory AppointHistory) {
        String json = JSONObject.toJSONString(AppointHistory);
        FileUtil.appendString(json, APPOINT_HISTORY_PATH, "UTF-8");

    }

}
