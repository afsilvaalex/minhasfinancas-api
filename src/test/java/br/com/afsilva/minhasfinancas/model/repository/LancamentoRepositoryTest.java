package br.com.afsilva.minhasfinancas.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.afsilva.minhasfinancas.model.entity.Lancamento;
import br.com.afsilva.minhasfinancas.model.enums.StatusLancamento;
import br.com.afsilva.minhasfinancas.model.enums.TipoLancamento;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("teste")
@DataJpaTest	
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class LancamentoRepositoryTest {

	@Autowired
	LancamentoRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveSalvarUmLancamento() {
		
		Lancamento lancamento = criarLancamento();
		
		lancamento = repository.save(lancamento);
		
		Assertions.assertThat(lancamento.getId()).isNotNull();
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		
		Lancamento lancamento = criarEPersistirUmLancamento();
		
		lancamento = entityManager.find(Lancamento.class, lancamento.getId());
		repository.delete(lancamento);
		
		Lancamento lancamentoInesistente = entityManager.find(Lancamento.class, lancamento.getId());
		
		Assertions.assertThat(lancamentoInesistente).isNull();
		
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		
		Lancamento lancamento = criarEPersistirUmLancamento();
		
		lancamento.setAno(2018);
		lancamento.setDescricao("Teste Atualizar");
		lancamento.setStatus(StatusLancamento.CANCELADO);
		
		repository.save(lancamento);
		
		Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());
		
		Assertions.assertThat(lancamentoAtualizado.getAno()).isEqualTo(2018);
		Assertions.assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("Teste Atualizar");
		Assertions.assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
		
	}
	
	@Test
	public void deveBuscarUmLancamentoPorId() {
		
		Lancamento lancamento = criarEPersistirUmLancamento();
		
		Optional<Lancamento> lancamentoEcontrado = repository.findById(lancamento.getId());
		
		Assertions.assertThat(lancamentoEcontrado.isPresent()).isTrue();
		
		
		
	}
	
	
	private Lancamento criarEPersistirUmLancamento() {
		Lancamento lancamento = criarLancamento();
		lancamento = entityManager.persist(lancamento);
		return lancamento;
	}
	
	public static Lancamento criarLancamento() {
		
		Lancamento lancamento = new Lancamento();
		lancamento.setDescricao("Testes de integraçao");
		lancamento.setMes(3);
		lancamento.setAno(2020);
		lancamento.setValor(BigDecimal.valueOf(100));
		lancamento.setTipo(TipoLancamento.DESPESA);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		lancamento.setDataCadastro(LocalDate.now());
		
		return lancamento;
	

	}
}
