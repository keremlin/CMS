package ara.repository;

import ara.models.olap;
import ara.models.olapIndex;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface olapIndexRepository extends CrudRepository<olapIndex,Long> {
    List<olapIndex> findAll();
    int removeOlapIndexById(long id);
    olapIndex findOlapIndexById(long id);
}
