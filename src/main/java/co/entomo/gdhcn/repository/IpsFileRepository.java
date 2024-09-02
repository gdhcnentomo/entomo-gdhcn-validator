package co.entomo.gdhcn.repository;/**
 * @author Uday Matta
 */

import co.entomo.gdhcn.entity.IpsFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @organization Entomo Labs
 */
public interface IpsFileRepository extends JpaRepository<IpsFile, String> {

 Optional<IpsFile> findByManifestId(String manifestId);
}
