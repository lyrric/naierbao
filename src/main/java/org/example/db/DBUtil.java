package org.example.db;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.model.AppointHistory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class DBUtil {

    static Connection conn;

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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void insert(AppointHistory appointHistory) {
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
    public static void update(String id, Integer status, Integer type) {
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
}
