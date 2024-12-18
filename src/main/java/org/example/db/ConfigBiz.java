package org.example.db;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.example.db.mapper.ConfigMapper;
import org.example.model.entity.Config;

import java.util.List;

@Slf4j

public class ConfigBiz {


    public static void insert(Config config) {
        try( SqlSession session = DBUtil.getSession()) {
            ConfigMapper mapper = session.getMapper(ConfigMapper.class);
            mapper.insert(config);
            session.commit();
        }

    }

    public static List<Config> selectList()  {
        try( SqlSession session = DBUtil.getSession()) {
            ConfigMapper mapper = session.getMapper(ConfigMapper.class);
            return mapper.selectList(new LambdaQueryWrapper<Config>()
                    .eq(Config::getDeleted, 0));
        }
    }

    public static void update(Config config) {
        try( SqlSession session = DBUtil.getSession()) {
            ConfigMapper mapper = session.getMapper(ConfigMapper.class);
            mapper.updateById(config);
            session.commit();
        }

    }
    public static void deleteById(String id){
        try( SqlSession session = DBUtil.getSession()) {
            ConfigMapper mapper = session.getMapper(ConfigMapper.class);
            mapper.update(null, new LambdaUpdateWrapper<>(Config.class)
                    .eq(Config::getId, id)
                    .set(Config::getDeleted, 1));
            session.commit();
        }

    }
}
