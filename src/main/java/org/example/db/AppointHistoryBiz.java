package org.example.db;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.example.db.mapper.AppointHistoryMapper;
import org.example.model.entity.AppointHistory;
import org.example.util.AppointHistoriesUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AppointHistoryBiz {

    public static final LoadingCache<String, Long> countCache = CacheBuilder.newBuilder()
            // 初始化缓存容量
            .initialCapacity(10)
            // 设置缓存过期时间【写入缓存后多久过期】，超过这个时间就淘汰 —— 基于时间进行回收
            .expireAfterWrite(10L, TimeUnit.MINUTES)
            // 创建一个 CacheLoader，重写 load() 方法，以实现"当 get() 时缓存不存在，则调用 load() 方法，放到缓存并返回"的效果
            .build(new CacheLoader<String, Long>() {
                // 自动写缓存数据的方法
                @Override
                public Long load(String key){
                    log.info("调用 load() 方法, key 为：" + key);
                    String[] array = key.split(",");
                    String shopId = array[0];
                    String date = array[1];
                    return AppointHistoryBiz.selectCount(shopId, date);
                }
            });

    public static void insert(AppointHistory appointHistory) {
        try( SqlSession session = DBUtil.getSession()) {
            AppointHistoryMapper mapper = session.getMapper(AppointHistoryMapper.class);
            mapper.insert(appointHistory);
            session.commit();
        }catch (Exception e){
            log.error("保存失败");
            AppointHistoriesUtils.saveAppointHistory(appointHistory);
        }
        countCache.refresh(appointHistory.getShopId() + "," + appointHistory.getAppointmentDate());
    }

    public static long countByShopIdAndDate(String shopId, String date){
        try {
            return countCache.get(shopId + "," + date);
        } catch (ExecutionException e) {
            log.error("获取数量时错误",e);
        }
        return 0;
    }

    public static List<AppointHistory> selectList(List<String> shopIds, String phone, Integer status, Integer type)  {
        try( SqlSession session = DBUtil.getSession()) {
            AppointHistoryMapper mapper = session.getMapper(AppointHistoryMapper.class);
            return mapper.selectList(new LambdaQueryWrapper<AppointHistory>()
                    .in(shopIds != null && !shopIds.isEmpty(), AppointHistory::getShopId, shopIds)
                    .eq(StringUtils.isNotBlank(phone), AppointHistory::getPhone, phone)
                    .eq(status != null, AppointHistory::getStatus, status)
                    .eq(type != null, AppointHistory::getType, type)
                    .ge(AppointHistory::getAppointmentDate, DateUtil.format(new Date(), "yyyy.MM.dd"))
                    .orderBy(true,true,AppointHistory::getAppointmentDate)
                    .last(" limit 100"));
        }
    }

    private static long selectCount(String shopId, String date)  {
        try( SqlSession session = DBUtil.getSession()) {
            AppointHistoryMapper mapper = session.getMapper(AppointHistoryMapper.class);
            return mapper.selectCount(new LambdaQueryWrapper<AppointHistory>()
                    .eq(AppointHistory::getShopId, shopId)
                    .eq(AppointHistory::getStatus, 1)
                    .eq(AppointHistory::getType, 1)
                    .eq(AppointHistory::getAppointmentDate,date));
        }
    }
    public static void updateAptHist(String id, Integer status, Integer type) {
        try( SqlSession session = DBUtil.getSession()) {
            AppointHistoryMapper mapper = session.getMapper(AppointHistoryMapper.class);
            AppointHistory appointHistory = mapper.selectById(id);
            mapper.update(new LambdaUpdateWrapper<AppointHistory>()
                    .eq(AppointHistory::getId, id)
                    .set(AppointHistory::getStatus, status)
                    .set(AppointHistory::getType, type));
            session.commit();
            countCache.refresh(appointHistory.getShopId() + "," + appointHistory.getAppointmentDate());
        }


    }
    public static void deleteById(String id){
        try( SqlSession session = DBUtil.getSession()) {
            AppointHistoryMapper mapper = session.getMapper(AppointHistoryMapper.class);
            AppointHistory appointHistory = mapper.selectById(id);
            mapper.deleteById(id);
            session.commit();
            countCache.refresh(appointHistory.getShopId() + "," + appointHistory.getAppointmentDate());
        }

    }
}
