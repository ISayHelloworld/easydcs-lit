package main.entity;

import lombok.Data;

@Data
public class FlightManagerAccount {
    private String account;
    private String password;
    private String authority;
    private String company;
}
