package br.com.afsilva.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.afsilva.minhasfinancas.model.entity.Lancamento;


public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}
