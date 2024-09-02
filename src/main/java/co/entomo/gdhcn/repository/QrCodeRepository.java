package co.entomo.gdhcn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.entomo.gdhcn.entity.QrCode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
/**
 *
 *  @author Uday Matta
 *  @organization entomo Labs
 * Repository interface for managing {@link QrCode} entities.
 * Extends {@link JpaRepository} to provide basic CRUD operations.
 */
public interface QrCodeRepository extends JpaRepository<QrCode, String> {
    /**
     * Finds a {@link QrCode} entity by its ID and passcode.
     *
     * @param id       the ID of the {@link QrCode}.
     * @param passCode the passcode associated with the {@link QrCode}.
     * @return an {@link Optional} containing the found {@link QrCode}, or {@code Optional.empty()} if no matching entity is found.
     */
    Optional<QrCode> findByIdAndPassCode(String id, String passCode);

    //Optional<QrCode> findByManifestIdAndPassCodeAndIsAccessed(String manifestId, String passCode, Boolean accessed);

    Optional<QrCode> findByManifestId(String manifestId);
}
