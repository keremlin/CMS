package ara.repository;


import ara.models.User;
import ara.models.document;
import ara.models.documentComment;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface documentCommentRepository extends CrudRepository<documentComment,Integer> {
List<documentComment> findByDocumentAndEnabledOrderByCreateDateDesc(document Document, boolean enabled);
List<documentComment> findByDocumentOrderByCreateDateDesc(document Document);
documentComment findDocumentCommentById(int id);
int countAllByUser(User user);
int countAllByDocumentIsInAndEnabledAndCreateDateGreaterThan(List<document> documents, boolean enabled, Date date);
}
