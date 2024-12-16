package org.example.db;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.model.AppointHistory;
import org.example.model.Config;
import org.jdbi.v3.core.Jdbi;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class DBUtil {

    static Connection conn;
    static Jdbi jdbi;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            //SQLite 数据库文件
            String dbFile;
            if (FileUtil.exist("./naierbao.db")) {
                dbFile = "./naierbao.db";
            }else if(FileUtil.exist("d://naierbao.db")){
                dbFile = "d://naierbao.db";
            }else{
                throw new RuntimeException("数据库文件不存在");
            }

            String url = "jdbc:sqlite:" + dbFile;
            conn = DriverManager.getConnection(url);
            jdbi = Jdbi.create(conn);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void insertAptHist(AppointHistory appointHistory) {
        String sql = "INSERT INTO main.appoint_history ( id, shopId, shopName, phone, appointmentDate, ticketId, ticketName, status, lineType, isPunished, isDeleted, createTime, type) " +
                String.format("VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s,'%s',%s);"
                        , appointHistory.getId(), appointHistory.getShopId(), appointHistory.getShopName(), appointHistory.getPhone(), appointHistory.getAppointmentDate(), appointHistory.getTicketId(),
                        appointHistory.getTicketName(), appointHistory.getStatus(), appointHistory.getLineType(), appointHistory.getIsPunished() ? 1 : 0, appointHistory.getIsDeleted(), appointHistory.getCreateTime(), appointHistory.getType());
        log.info(sql);
        try {
            Statement stat = conn.createStatement();
            stat.executeUpdate(sql);
            stat.close();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    public static void updateAptHist(String id, Integer status, Integer type) {
        String sql = "update main.appoint_history set status =  " + status + ", type = " + type + " where id = '" + id + "'";
        log.info(sql);
        try {
            Statement stat = conn.createStatement();
            stat.executeUpdate(sql);
            stat.close();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static List<AppointHistory> selectList(List<String> shopIds, String phone, Integer status, Integer type) {
        String sql = "select * from main.appoint_history where 1 = 1 ";
        if (!shopIds.isEmpty()) {
            String condition = String.join("','", shopIds);
            sql = sql + " and shopId in ('" + condition + "')";
        }
        if (phone != null && phone.length() > 0) {
            sql = sql + " and phone = '" + phone + "'";
        }
        if (status != null) {
            sql = sql + " and status = " + status;
        }
        if (type != null) {
            sql = sql + " and type = " + type;
        }
        sql = sql + " and appointmentDate >= '" + getToday() + "'";
        log.info(sql);
        try {
            List<AppointHistory> result = new ArrayList<>();
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                AppointHistory appointHistory = AppointHistory.builder()
                        .id(rs.getString("id"))
                        .shopId(rs.getString("shopId"))
                        .shopName(rs.getString("shopName"))
                        .phone(rs.getString("phone"))
                        .appointmentDate(rs.getString("appointmentDate"))
                        .ticketId(rs.getString("ticketId"))
                        .ticketName(rs.getString("ticketName"))
                        .status(rs.getInt("status"))
                        .lineType(rs.getInt("lineType"))
                        .isPunished(rs.getInt("isPunished") == 1)
                        .isDeleted(rs.getInt("isDeleted"))
                        .createTime(rs.getString("createTime"))
                        .type(rs.getInt("type"))
                        .build();
                result.add(appointHistory);
            }
            rs.close();
            statement.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getToday(){
        Date now = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(now);
    }

    public static List<Config> getConfigs(){
        String sql = "select * from config where deleted = 0";
        log.info(sql);
        try {
            List<Config> result = new ArrayList<>();
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                Config config = Config.builder()
                        .id(rs.getInt("id"))
                        .shopId(rs.getString("shopId"))
                        .areaName(rs.getString("areaName"))
                        .shopName(rs.getString("shopName"))
                        .xianyuNickname(rs.getString("xianyuNickname"))
                        .phone(rs.getString("phone"))
                        .remark(rs.getString("remark"))
                        .maxCountPerDay(rs.getInt("maxCountPerDay"))
                        .type(rs.getInt("type"))
                        .data(rs.getString("data"))
                        .success(rs.getInt("success") == 1)
                        .deleted(rs.getInt("deleted") == 1)
                        .build();
                List<String> spts = new ArrayList<>();
                if (StringUtils.isNotBlank(rs.getString("spts"))) {
                    spts = Arrays.stream(rs.getString("spts").split(",")).collect(Collectors.toList());
                }
                config.setSpts(spts);
                result.add(config);
            }
            rs.close();
            statement.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertConfig(Config config) {
        String sql = "INSERT INTO 'main'.'config'('areaName', 'shopId', 'shopName', 'xianyuNickname', 'phone', 'remark', 'spts', 'maxCountPerDay', 'type', 'data', 'success', 'deleted') " +
                String.format(" VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', %s, %s, '%s', %s, %s);"
                        , config.getAreaName(),config.getShopId(), config.getShopName(), config.getXianyuNickname(), config.getPhone(), config.getRemark(), String.join(",", config.getSpts()),
                        config.getMaxCountPerDay(), config.getType(),config.getSuccess(),config.getDeleted());
        log.info(sql);
        try {
            Statement stat = conn.createStatement();
            stat.executeUpdate(sql);
            stat.close();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void insertConfig1(Config config) {
        try {
            jdbi.withHandle(handle -> {
                handle.execute("INSERT INTO 'main'.'config'('areaName', 'shopId', 'shopName', 'xianyuNickname', 'phone', 'remark', 'spts', 'maxCountPerDay', 'type', 'data', 'success', 'deleted') " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        config.getAreaName(), config.getShopId(), config.getShopName(), config.getXianyuNickname(), config.getPhone(), config.getRemark(),
                        config.getSpts() == null ? "" : String.join(",", config.getSpts()), config.getMaxCountPerDay(), config.getType(), config.getSuccess(), config.getDeleted());
                return null;
            });
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
