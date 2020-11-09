package ara.repository;


import ara.models.documentFile;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface documentFileRepository extends CrudRepository<documentFile,Long> {

    public ArrayList<documentFile> findAllByDocument(ara.models.document id);
    public ara.models.documentFile findDocumentFileById(Long id);

}
