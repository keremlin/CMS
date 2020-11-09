package ara.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
public class olapIndex { 
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = CascadeType.REMOVE)
    private Collection<olap> olap;
    
    private byte type;
    private String title;
    private String dateRange;
    private String dateRangeEnd;
    private String dateCreated;

    
}