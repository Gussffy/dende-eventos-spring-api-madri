package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    Optional<Empresa> findByOrganizadorId(Long organizadorId);

    Optional<Empresa> findByCnpj(String cnpj);

    boolean existsByCnpj(String cnpj);

    boolean existsByOrganizadorId(Long organizadorId);
}
