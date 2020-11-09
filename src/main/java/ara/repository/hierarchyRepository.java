package ara.repository;

import ara.models.hierarchy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface hierarchyRepository extends CrudRepository<hierarchy,Long> {

    List<hierarchy> findAll();
    hierarchy findHierarchyById(Long id);
    List<hierarchy> findHierarchyByLinker(ara.models.linker linker);
    int removeById(long id);
    
}
