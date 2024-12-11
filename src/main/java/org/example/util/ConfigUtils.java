package org.example.util;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import org.example.model.Config;

public class ConfigUtils {

    public static Config getAppointHistories() {
        String path = "./config.data";
        if (FileUtil.exist(path)) {
            String json = FileUtil.readString(path, "UTF-8");
            if (json == null || json.isEmpty()) {
                return new Config();
            }
            return JSONObject.parseObject(json, Config.class);
        }
        return new Config();
    }
}
