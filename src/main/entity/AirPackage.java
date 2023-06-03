package main.entity;

import lombok.Data;

@Data
public class AirPackage implements Cloneable {
    private String company;
    private String flightNumber;

    private String flightDate;

    private String ticketNumber;
    private int type;
    private int num;
    private String number;
    private int weight;
    private String toLocation;
    private boolean isPrint = false;

    @Override
    public AirPackage clone() throws CloneNotSupportedException {
        AirPackage obj = (AirPackage) super.clone();
        obj.setType(this.type);
        obj.setNum(this.num);
        obj.setNumber(this.number);
        obj.setWeight(this.weight);
        obj.setToLocation(this.toLocation);
        obj.isPrint = this.isPrint;
        return obj;
    }
}
