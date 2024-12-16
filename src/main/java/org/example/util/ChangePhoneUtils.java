package org.example.util;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.http.HttpService;
import org.example.model.BaseResult;
import org.example.model.AppointHistory;
import org.example.model.Ticket;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


@Slf4j
public class ChangePhoneUtils {

    public static void main(String[] args) throws IOException {
        start();
    }

    public static void start() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入手机号：");
        String queryPhone =  sc.nextLine().trim();
        List<AppointHistory> appointHistoryList = HttpService.getActivityAppointment(queryPhone).getData();
        if (appointHistoryList == null || appointHistoryList.isEmpty()) {
            System.out.println("当前手机号没有预约记录");
            return;
        }
        appointHistoryList = appointHistoryList
                .stream()
                .filter(t -> t.getStatus() == 1).collect(Collectors.toList());
        if (appointHistoryList.isEmpty()) {
            System.out.println("当前手机号没有有效的预约记录");
            return;
        }
        for (int i = 0; i < appointHistoryList.size(); i++) {
            AppointHistory appointHistory = appointHistoryList.get(i);
            System.out.println(String.format("编号：%s 门店：%s 日期：%s", i + 1, appointHistory.getShopName(), appointHistory.getAppointmentDate()));
        }
        System.out.println("请选择编号：");
        int num;
        while (true) {
            try {
                String numStr =  sc.nextLine().trim();
                num = Integer.parseInt(numStr);
                if (num < 1 || num > appointHistoryList.size()) {
                    System.out.println("输入错误，请重新输入：");
                    continue;
                }
                break;
            }catch (Exception e){
                System.out.println("输入错误，请重新输入：");
            }
        }
        String newPhone;
        while (true) {
            System.out.println("请输入新的手机号码：");
            newPhone =  sc.nextLine().trim();
            if (hasMaxAppointment(newPhone)) {
                //只能同时预约两个
                System.out.println("当前手机号已经同时预约了两个，请换一个手机号！");
            }else{
                break;
            }
        }
        System.out.println("开始取消预约");
        AppointHistory appointHistory = appointHistoryList.get(num - 1);
        HttpService.cancelAppointment(appointHistory.getId());
        System.out.println("取消预约成功");
        System.out.println("开始预约");
        Ticket ticket = buildTicket(appointHistory);
        HttpService.appoint(ticket, newPhone);
        System.out.println("预约成功");
    }

    public static AppointHistory change(AppointHistory appointHistory, String newPhone) {
        try {
            if (hasMaxAppointment(newPhone)) {
                //只能同时预约两个
                throw new RuntimeException("当前手机号已经同时预约了两个，请换一个手机号！");
            }
            log.info("开始取消预约");
            HttpService.cancelAppointment(appointHistory.getId());
            log.info("取消预约成功");
            log.info("开始预约");
            Ticket ticket = buildTicket(appointHistory);
            BaseResult<AppointHistory> result = HttpService.appoint(ticket, newPhone);
            log.info("预约成功");
            return result.getData();
        }catch (IOException e){
            throw new RuntimeException(e);
        }

    }

    public static Ticket buildTicket(AppointHistory appointHistory){
        Ticket ticket = new Ticket();
        BeanUtil.copyProperties(appointHistory,ticket);
        ticket.setId(appointHistory.getTicketId());
        return ticket;
    }
    public static boolean hasMaxAppointment(String phone) throws IOException {
        List<AppointHistory> appointHistoryList = HttpService.getActivityAppointment(phone).getData();
        if (appointHistoryList == null || appointHistoryList.size() < 2) {
            return false;
        }
        return appointHistoryList.stream().filter(t -> t.getStatus() == 1).count() >= 2;
    }
}
