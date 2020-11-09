package ara.repository;

import ara.models.olap;
import ara.models.olapIndex;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface olapRepository extends CrudRepository<olap,Long> {
    int removeByOlapIndexer(olapIndex olapIndexer);
    List<olap> findAllByOlapIndexerOrderByIndexerAsc(olapIndex olapIndexer);
    List<olap> findAllByOlapIndexerAndD1IsInOrderByIndexerAsc(olapIndex olapIndexer,List<String> d1);
    olap findByOlapIndexerAndIndexer(olapIndex olapIndexer,int indexer);
    List<olap> findAll();
    
}
