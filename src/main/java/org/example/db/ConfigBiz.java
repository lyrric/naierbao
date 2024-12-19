package org.example.db;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.example.db.mapper.ConfigMapper;
import org.example.model.entity.Config;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class ConfigBiz {


    public static final LoadingCache<Integer, List<Config>> configsCache = CacheBuilder.newBuilder()
            // 初始化缓存容量
            .initialCapacity(10)
            // 设置缓存过期时间【写入缓存后多久过期】，超过这个时间就淘汰 —— 基于时间进行回收
            .expireAfterWrite(10L, TimeUnit.MINUTES)
            // 创建一个 CacheLoader，重写 load() 方法，以实现"当 get() 时缓存不存在，则调用 load() 方法，放到缓存并返回"的效果
            .build(new CacheLoader<Integer, List<Config>>() {
                // 自动写缓存数据的方法
                @Override
                public List<Config> load(Integer key){
                    return selectList();
                }
            });



    public static void insert(Config config) {
        try {
            List<Config> configs = configsCache.get(1);
            if (configs.stream()
                    .anyMatch(t-> Objects.equals(config.getShopId(),t.getShopId()))) {
                throw new RuntimeException("存在重复门店数据");
            }
        } catch (ExecutionException e) {
            log.error("获取配置失败:", e);
        }
        try( SqlSession session = DBUtil.getSession()) {
            ConfigMapper mapper = session.getMapper(ConfigMapper.class);
            mapper.insert(config);
            session.commit();
            configsCache.refresh(1);
        }

    }

    public static List<Config> selectAll()  {
        try {
            return configsCache.get(1);
        } catch (ExecutionException e) {
            log.error("获取配置失败:", e);
        }
        return Collections.emptyList();
    }

    private static List<Config> selectList()  {
        try(SqlSession session = DBUtil.getSession()) {
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

    public static void deleteById(Integer id){
        try(SqlSession session = DBUtil.getSession()) {
            ConfigMapper mapper = session.getMapper(ConfigMapper.class);
            mapper.update(null, new LambdaUpdateWrapper<>(Config.class)
                    .eq(Config::getId, id)
                    .set(Config::getDeleted, 1));
            session.commit();
            configsCache.refresh(1);
        }

    }
}
