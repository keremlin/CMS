package ara.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Data
@NoArgsConstructor
public class olap {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne
    private olapIndex olapIndexer;

    private int indexer;

    private String d1;
    private String d2;
    private String d3;
    private String d4;
    private String d5;
    private String d6;
    private String d7;
    private String d8;
    private String d9;
    private String d10;
    private String d11;
    private String d12;
    private String d13;
    private String d14;
    private String d15;
    private String d16;
    private String d17;
    private String d18;
    private String d19;
    private String d20;
    private String d21;
    private String d22;
}