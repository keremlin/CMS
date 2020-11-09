package ara.models;

import lombok.Data;

import java.util.Date;

@Data
public class vmStatus {
    public vmStatus(){
        this.dateTime=new Date();
    }
    private int id;
    private String status;
    private int code;
    private String message;
    private Date dateTime;
}
