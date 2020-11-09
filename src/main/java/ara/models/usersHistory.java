package ara.models;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class usersHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int logLevel;
    private String logIp;
    private String logMessage;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    private String userName;


}
