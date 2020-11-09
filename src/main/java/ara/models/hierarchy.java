package ara.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;



import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class hierarchy {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private String icon;

    @JsonIgnore
    @ManyToOne
    private linker linker;

    
}