package br.com.alura.forum.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.alura.forum.model.Topico;

public interface TopicoRepository extends JpaRepository<Topico, Long> {
	
  @Query("SELECT t FROM Topico t WHERE t.curso.nome = :nomeCurso")
  List<Topico> carregarPorNomeDoCurso(@Param("nomeCurso") String nomeCurso); // JPQL

  Page<Topico> findByCursoNome(String nomeCurso, Pageable paginacao);

  List<Topico> findByCursoNome(String nomeCurso);  // filtro pelo relacionamento (Curso.nome)



}