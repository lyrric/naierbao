package org.example.util;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Config;

import java.util.Collections;
import java.util.List;

@Slf4j
public class ConfigUtils {

    public static List<Config> getAppointHistories() {
        String path = "./config.data";
        if (FileUtil.exist(path)) {
            String json = FileUtil.readString(path, "UTF-8");
            if (json == null || json.isEmpty()) {
                throw new RuntimeException("没有获取到配置");
            }
            return JSONObject.parseArray(json, Config.class);
        }
        throw new RuntimeException("没有获取到配置");
    }
}
