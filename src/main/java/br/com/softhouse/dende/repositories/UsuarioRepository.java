package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByIdAndTipoUsuario(Long id, String tipoUsuario);

    Optional<Usuario> findByEmailAndTipoUsuario(String email, String tipoUsuario);

    boolean existsByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u WHERE u.id = :id AND u.tipoUsuario = 'COMUM'")
    boolean existsByIdAndTipoComum(Long id);
}
