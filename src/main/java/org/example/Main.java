package org.example;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Main {


    public static void main(String[] args) {
        if (args.length > 0) {
            if ("ui".equals(args[0])) {
                //UiMain.show();
            }else{
                new App().start();
            }
        }else{
            ChangePhoneUi.show();
        }
    }

}