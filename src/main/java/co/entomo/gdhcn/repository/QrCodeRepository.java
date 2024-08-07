package co.entomo.gdhcn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.entomo.gdhcn.entity.QrCode;

import java.util.Optional;

public interface QrCodeRepository extends JpaRepository<QrCode, String> {
    Optional<QrCode> findByIdAndPassCode(String id, String passCode);
}
