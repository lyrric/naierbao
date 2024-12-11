package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.util.ChangePhoneUtils;

import java.io.IOException;

@Slf4j
public class Main {


    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            if ("ui".equals(args[0])) {
                UiMain.main();
            }else{
                ChangePhoneUtils.start();
            }

        }else{
            new App().start();
        }
    }

}