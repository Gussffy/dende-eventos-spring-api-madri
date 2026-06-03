package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByOrganizadorIdOrderById(Long organizadorId);

    @Query("SELECT e FROM Evento e WHERE e.ativo = true AND e.dataFinal > :agora ORDER BY e.dataInicio, e.nome")
    List<Evento> findAtivos(@Param("agora") LocalDateTime agora);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Evento e " +
           "WHERE e.organizadorId = :organizadorId AND (e.ativo = true OR (e.dataInicio <= :agora AND e.dataFinal > :agora))")
    boolean existsAtivosOuEmExecucao(@Param("organizadorId") Long organizadorId, @Param("agora") LocalDateTime agora);
}
