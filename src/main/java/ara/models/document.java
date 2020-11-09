package ara.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;


@Data
@Entity

public class document {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @ManyToOne
    private hierarchy hierarchy;

    @NotNull
    private String title;

    private String description;

    private boolean enabled;

    private String cssClass;

    private int securityLevel;

    @Column(nullable = true,columnDefinition = "int default 0")
    private int reviewStep;

    @Column(nullable = true,columnDefinition = "int default 1")
    private boolean allowComment ;

    @Column(nullable = true,columnDefinition = "int default 0")
    private boolean direct;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date createDate;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date editDate;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date destroyDate;

    @Lob
    @NotNull
    private String documentContent;

    @ManyToOne
    @JoinColumn(name="OWNER_ID")
    @JsonIgnore
    private User owner;

    @OneToMany(mappedBy = "document" ,cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Collection<documentFile> documentFile;

    @ManyToMany
    @JsonIgnore
    private Collection<workGroup> workGroups;

    @OneToOne
    @JsonIgnore
    private picThumb picThumb;

    @OneToMany(mappedBy = "document",cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Collection<documentComment> documentComments;

}
