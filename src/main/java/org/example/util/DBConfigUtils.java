package org.example.util;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import org.example.model.DBConfig;

public class DBConfigUtils {


    public static DBConfig getDBConfig() {
        String path = "d://DBConfig.data";
        if (FileUtil.exist(path)) {
            String json = FileUtil.readString(path, "UTF-8");
            if (json == null || json.isEmpty()) {
                throw new RuntimeException("没有获取到配置");
            }
            return JSONObject.parseObject(json, DBConfig.class);
        }
        throw new RuntimeException("没有获取到配置");
    }
}
