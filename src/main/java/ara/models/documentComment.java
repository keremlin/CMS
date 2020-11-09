package ara.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
public class documentComment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;


    @NotNull
    private String message;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    private boolean enabled;

    @ManyToOne
    @NotNull
    @JsonIgnore
    private User user;

    @ManyToOne
    @JsonIgnore
    private document document;

}
