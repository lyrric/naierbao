package org.example.model;

import lombok.Data;

import java.util.List;

@Data
public class TicketResult {

    private Boolean success;

    private Integer code;

    private String msg;

    private List<Ticket> data;

}
