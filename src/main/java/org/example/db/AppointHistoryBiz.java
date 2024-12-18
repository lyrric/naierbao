package org.example.db;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.example.db.mapper.AppointHistoryMapper;
import org.example.model.entity.AppointHistory;
import org.example.util.AppointHistoriesUtils;

import java.util.Date;
import java.util.List;

@Slf4j

public class AppointHistoryBiz {


    public static void insert(AppointHistory appointHistory) {
        try( SqlSession session = DBUtil.getSession()) {
            AppointHistoryMapper mapper = session.getMapper(AppointHistoryMapper.class);
            mapper.insert(appointHistory);
            session.commit();
        }catch (Exception e){
            log.error("保存失败");
            AppointHistoriesUtils.saveAppointHistory(appointHistory);
        }

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
                    .orderBy(true,true,AppointHistory::getCreateTime)
                    .last(" limit 100"));
        }

    }
    public static void updateAptHist(String id, Integer status, Integer type) {
        try( SqlSession session = DBUtil.getSession()) {
            AppointHistoryMapper mapper = session.getMapper(AppointHistoryMapper.class);
            mapper.update(new LambdaUpdateWrapper<AppointHistory>()
                    .eq(AppointHistory::getId, id)
                    .set(AppointHistory::getStatus, status)
                    .set(AppointHistory::getType, type));
            session.commit();
        }

    }
    public static void deleteById(String id){
        try( SqlSession session = DBUtil.getSession()) {
            AppointHistoryMapper mapper = session.getMapper(AppointHistoryMapper.class);
            mapper.deleteById(id);
            session.commit();
        }

    }
}
