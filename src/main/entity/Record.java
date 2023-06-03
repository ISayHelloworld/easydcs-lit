package main.entity;

import lombok.Data;

@Data
public class Record {
    private String account;

    // insert, update,
    private String event;

    // 被操作用户信息
    private String name;
    private String company;
    private String flightNumber;
    private String flightDate;
    private String ticketNumber;

    private String content;

    private String time;
}