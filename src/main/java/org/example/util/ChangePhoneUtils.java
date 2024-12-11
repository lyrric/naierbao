package org.example.util;

import cn.hutool.core.bean.BeanUtil;
import org.example.http.HttpService;
import org.example.model.CloudAppointHistory;
import org.example.model.Ticket;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ChangePhoneUtils {

    public static void start() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入手机号：");
        String queryPhone =  sc.nextLine().trim();
        List<CloudAppointHistory> cloudAppointHistoryList = HttpService.getActivityAppointment(queryPhone).getData();
        if (cloudAppointHistoryList == null || cloudAppointHistoryList.isEmpty()) {
            System.out.println("当前手机号没有预约记录");
            return;
        }
        cloudAppointHistoryList = cloudAppointHistoryList
                .stream()
                .filter(t -> t.getStatus() == 1).collect(Collectors.toList());
        if (cloudAppointHistoryList.isEmpty()) {
            System.out.println("当前手机号没有有效的预约记录");
            return;
        }
        for (int i = 0; i < cloudAppointHistoryList.size(); i++) {
            CloudAppointHistory cloudAppointHistory = cloudAppointHistoryList.get(i);
            System.out.println(String.format("编号：%s 门店：%s 日期：%s", i + 1, cloudAppointHistory.getShopName(), cloudAppointHistory.getAppointmentDate()));
        }
        System.out.println("请选择编号：");
        int num;
        while (true) {
            try {
                String numStr =  sc.nextLine().trim();
                num = Integer.parseInt(numStr);
                if (num < 1 || num > cloudAppointHistoryList.size()) {
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
        CloudAppointHistory cloudAppointHistory = cloudAppointHistoryList.get(num - 1);
        HttpService.cancelAppointment(cloudAppointHistory.getId());
        System.out.println("取消预约成功");
        System.out.println("开始预约");
        Ticket ticket = buildTicket(cloudAppointHistory);
        HttpService.appoint(ticket, newPhone);
        System.out.println("预约成功");
    }

    public static Ticket buildTicket(CloudAppointHistory cloudAppointHistory){
        Ticket ticket = new Ticket();
        BeanUtil.copyProperties(cloudAppointHistory,ticket);
        ticket.setId(cloudAppointHistory.getTicketId());
        return ticket;
    }
    public static boolean hasMaxAppointment(String phone) throws IOException {
        List<CloudAppointHistory> cloudAppointHistoryList = HttpService.getActivityAppointment(phone).getData();
        if (cloudAppointHistoryList == null || cloudAppointHistoryList.size() < 2) {
            return false;
        }
        return cloudAppointHistoryList.stream().filter(t -> t.getStatus() == 1).count() >= 2;
    }
}
