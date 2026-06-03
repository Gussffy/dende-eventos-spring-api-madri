package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.enums.StatusIngresso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngressoRepository extends JpaRepository<Ingresso, Long> {

    List<Ingresso> findByUsuarioIdOrderByDataCompraDesc(Long usuarioId);

    List<Ingresso> findByEventoIdOrderByDataCompraDesc(Long eventoId);

    boolean existsByUsuarioIdAndEventoIdAndStatus(Long usuarioId, Long eventoId, StatusIngresso status);

    @Query("SELECT COUNT(i) FROM Ingresso i WHERE i.eventoId = :eventoId AND i.status NOT IN ('CANCELADO', 'REEMBOLSADO')")
    int countIngressosValidosPorEvento(@Param("eventoId") Long eventoId);
}
