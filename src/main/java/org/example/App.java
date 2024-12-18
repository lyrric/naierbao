package org.example;

import com.alibaba.fastjson.JSONObject;
import javafx.application.Platform;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;
import org.example.db.AppointHistoryBiz;
import org.example.db.ConfigBiz;
import org.example.db.RunInfoBiz;
import org.example.http.HttpService;
import org.example.http.MessageService;
import org.example.model.Area;
import org.example.model.BaseResult;
import org.example.model.Ticket;
import org.example.model.entity.AppointHistory;
import org.example.model.entity.Config;
import org.example.util.AreaUtil;
import org.example.util.PhoneUtil;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class App {

    private AtomicBoolean run = new AtomicBoolean(false);

    private final ExecutorService queryService = Executors.newFixedThreadPool(10);

    private final Label statusLabel;
    private long runCount = 0;
    private final AtomicLong successCount = new AtomicLong(0);

    public App(Label statusLabel) {
        this.statusLabel = statusLabel;
    }
    private void doStart() {
        new Thread(()->{
            while (true) {
                runCount++;
                if (!run.get()) {
                    return;
                }
                RunInfoBiz.updateLastRunTime();
                setLabelText("已运行次数: " + runCount + " 成功次数：" + successCount.get());
                for (Area area : AreaUtil.getAreas()) {
                    log.debug("开始遍历地区 {}", area.getDictValue());
                    List<Area> children = area.getChildren();
                    if (children != null && !children.isEmpty()) {
                        for (Area child : children) {
                            queryService.execute(()->{
                                doRun(child.getRemark(), getConfigMap().get(child.getRemark()));
                            });
                        }
                    }
                    log.debug("结束遍历地区 {}", area.getDictValue());
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }

    private void doRun(String shopId, Config Config) {
        try {
            BaseResult<List<Ticket>> result = HttpService.getTicket(shopId);
            if(!result.getSuccess()){
                log.error("接口返回出错: {}" ,result.getMsg());
                return;
            }
            check(result.getData(), Config);
        } catch (Exception e) {
            log.error("未知错误: " ,e);
        }
    }

    private void check(List<Ticket> tickets, Config Config) {
        if (tickets == null || tickets.isEmpty()) {
            log.debug("没有获取到数据");
            return;
        }
        for (Ticket ticket : tickets) {
            if (ticket.getStockNum() - ticket.getAppointmentNum() > 0) {
                if (appointedCount(ticket) < (Config == null ? 2 : Config.getMaxCountPerDay())) {
                    log.info("{}有票了", ticket.getShopName());
                    //预约
                    new Thread(()->{
                        appoint(ticket, Config);}).start();
                }

            }
        }
    }

    private void appoint(Ticket ticket, Config Config) {
        long appointedCount = appointedCount(ticket);
        boolean hasMaxAppointment = false;
        for (long i = appointedCount; i < (Config == null ? 2 : Config.getMaxCountPerDay()); i++) {
            //预约
            String phone = PhoneUtil.generateRandomPhoneNumber();
            try {
                BaseResult<AppointHistory> result = HttpService.appoint(ticket, phone);
                if (!result.getSuccess()) {
                    log.error("预约时发生错误 返回数据 {}", JSONObject.toJSONString(result));
                    break;
                }
                AppointHistory AppointHistory = result.getData();
                AppointHistory.setCreateTime(new Date());
                AppointHistory.setType(1);
                AppointHistoryBiz.insert(AppointHistory);
                log.info("预约成功");
                successCount.incrementAndGet();
                hasMaxAppointment = true;
            } catch (Exception e) {
                log.error("预约时发生错误 ", e);
                break;
            }
        }
        if (hasMaxAppointment) {
            String phones = getPhones(ticket);
            //发送预约成功短信
            if (Config != null) {
                Optional<Area> optionalArea = AreaUtil.getArea(Config.getShopId());
                MessageService.sendAppointedMessage(optionalArea.map(Area::getDictValue).orElse(""), ticket.getShopName(), ticket.getAppointmentDate(), phones, Config);
            }
        }
    }

    private String getPhones(Ticket ticket){
        return AppointHistoryBiz.selectList(Collections.singletonList(ticket.getShopId()),null,1, 1).stream()
                .filter(t -> t.getAppointmentDate().equals(ticket.getAppointmentDate()))
                .map(AppointHistory::getPhone)
                .collect(Collectors.joining(","));

    }

    private long appointedCount(Ticket ticket) {
        return AppointHistoryBiz.selectList(Collections.singletonList(ticket.getShopId()),null,1, 1).stream()
                .filter(t -> t.getAppointmentDate().equals(ticket.getAppointmentDate()))
                .filter(t -> t.getShopId().equals(ticket.getShopId()))
                .count();
    }


    private Map<String, Config> getConfigMap(){
        return ConfigBiz.selectList()
                .stream()
                .collect(Collectors.toMap(Config::getShopId, Function.identity()));
    }

    private void setLabelText(String text) {
        if (run.get()) {
            // 使用Platform.runLater在FX线程上更新Label
            Platform.runLater(() -> {
                statusLabel.setText(text);
            });
        }

    }
    public void start(){
        if (!run.get()) {
            run.set(true);
            doStart();
        }
    }

    public void stop(){
        run.set(false);
    }

    public boolean getStatus(){
        return run.get();
    }
}