package ara.models;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
public class picThumb {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotNull
    private String path;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    private boolean enabled;

}
