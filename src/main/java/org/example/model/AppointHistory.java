package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointHistory {

    private String phone;

    private Integer shopId;

    private String shopName;

    private String date;
}
