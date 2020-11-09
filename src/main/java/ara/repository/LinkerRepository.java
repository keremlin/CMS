package ara.repository;

import ara.models.linker;
import ara.models.workGroup;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkerRepository extends CrudRepository<linker,Long> {

    List<linker> findAll();
    linker findByWorkGroups(workGroup workGroup);
    linker findLinkerById(Long id);
    int removeById(long id);
}
