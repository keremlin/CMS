package ara.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
public class documentFile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String path;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    private boolean enabled;

    @ManyToOne()
    @JsonIgnore
    private ara.models.document document;
}
