package ara.repository;

import ara.models.usersHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends CrudRepository<usersHistory,Long> {

    List<usersHistory> findAll();
    usersHistory findRoleByUserName(String userName);
}
