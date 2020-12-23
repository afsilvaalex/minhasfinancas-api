package br.com.afsilva.minhasfinancas.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.afsilva.minhasfinancas.exception.RegraNegocioException;
import br.com.afsilva.minhasfinancas.model.entity.Lancamento;
import br.com.afsilva.minhasfinancas.model.entity.Usuario;
import br.com.afsilva.minhasfinancas.model.enums.StatusLancamento;
import br.com.afsilva.minhasfinancas.model.enums.TipoLancamento;
import br.com.afsilva.minhasfinancas.model.repository.LancamentoRepository;
import br.com.afsilva.minhasfinancas.service.imp.LancamentoServiceImp;



@ExtendWith(SpringExtension.class)
@ActiveProfiles("teste")
public class LancametoServiceTest {
	
	@MockBean
	LancamentoRepository repository;
	
	@SpyBean
	LancamentoServiceImp service; // = new UsuarioServiceImp(repository);
	
	@Test
	public void deveSalvarUmLancamento() {
		
		//cenario
		Lancamento lancamentoASalvar = criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = criarLancamento();
		lancamentoSalvo.setId(1l);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		//execucao
		Lancamento lancamento = service.salvar(lancamentoASalvar);
		
		//verficacao
		Assertions.assertEquals(lancamentoSalvo.getId(), lancamento.getId());
		Assertions.assertEquals(lancamento.getStatus(), StatusLancamento.PENDENTE);
	
	}
	
	@Test
	public void naoDeveSalvaLancamentoQuandoHouverErroDeValidacao() {
		
		//cenario
		Lancamento lancamentoASalvar = criarLancamento();
		
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);
		
		//execucao e verificacao
		
		Assertions.assertThrows(RegraNegocioException.class, () -> service.salvar(lancamentoASalvar));
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
		
	}
	
	
	@Test
	public void deveAtualizarUmLancamento() {
		
		//cenario
		
		Lancamento lancamentoSalvo = criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.CANCELADO); 
		
		Mockito.doNothing().when(service).validar(lancamentoSalvo);	
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		
		//execucao
		service.atualizar(lancamentoSalvo);
		
		//verficacao
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		
		//cenario
		Lancamento lancamento = criarLancamento();
		lancamento.setId(null);

		
		//execucao e verificacao
		
		Assertions.assertThrows(NullPointerException.class, () -> service.atualizar(lancamento));
		Mockito.verify(repository, Mockito.never()).save(lancamento);
		
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		
		//cenario
		Lancamento lancamento = criarLancamento();
		lancamento.setId(1l);
		
		//execucao
		service.deletar(lancamento);
		
		//verificacao
		Mockito.verify(repository).delete(lancamento);
		
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
				
		//cenario
		Lancamento lancamento = criarLancamento();
		lancamento.setId(null);
		
		//execucao
		Assertions.assertThrows(NullPointerException.class, () -> service.deletar(lancamento));

		
		//verificacao
		Mockito.verify(repository, Mockito.never()).delete(lancamento);
		
	}
	
	@Test
	public void deveFiltrarLancamentos() {
		//cenario
		Lancamento lancamento = criarLancamento();
		lancamento.setId(1l);
		
		List<Lancamento> lista = Arrays.asList(lancamento);
		
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
		
		//execucao
		List<Lancamento> resultado = service.buscar(lancamento);
		
		//verificacao		
		Assertions.assertEquals(lista, resultado);
		Assertions.assertNotNull(resultado);
		//Assertions.assertTrue(resultado.size() == 1);

		
	}
	
	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
	
		//cenario
		Lancamento lancamento = criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.CANCELADO;
		
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
		
		//Execucao
		
		service.atualizarStatus(lancamento, novoStatus);
		
		Assertions.assertEquals(lancamento.getStatus(), novoStatus);
		
		Mockito.verify(service).atualizar(lancamento);
		
	}
	
	@Test
	public void deveObterUmLancamentoPorID() {
		
		//cenario
		Long id = 1l;
		
		Lancamento lancamento = criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		//execucao
		Optional<Lancamento> lancamentoPorId = service.obterPorId(id);
		
		//verificacao
		
	
		Assertions.assertTrue(lancamentoPorId.isPresent());
	}
	
	@Test
	public void deveRetornarVazioQuandoOLancamentoNaoExiste() {
		
		//cenario
		Long id = 1l;		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		//execucao
		Optional<Lancamento> lancamentoPorId = service.obterPorId(id);
		
		//verificacao
		
		Assertions.assertFalse(lancamentoPorId.isPresent());

	
	}
	
	@Test
	public void deveValidarLancamento() {
		// cenario
		Lancamento lancamento = criarLancamento();
		
		service.validar(lancamento);
		
		
	}
	
	@Test
	public void deveLancarErrosAoValidarLancamento() {
		
		//cenario
		Lancamento lancamento = new Lancamento();
			
		//acao e verificacao
		
		//teste descricao nula
		Throwable erro =  Assertions.assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
		Assertions.assertTrue(erro.getMessage().contains("Informe uma Descrição válida."));
		
		//teste descricao em branco
		lancamento.setDescricao("");
		erro =  Assertions.assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
		Assertions.assertTrue(erro.getMessage().contains("Informe uma Descrição válida."));
		
		
		lancamento.setDescricao("Descricao testes");
		
		//teste Mes nulo
		erro =  Assertions.assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
		Assertions.assertTrue(erro.getMessage().contains("Informe um Mês válido."));
		
		//teste Mes < 1
		lancamento.setMes(0);
		erro =  Assertions.assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
		Assertions.assertTrue(erro.getMessage().contains("Informe um Mês válido."));	
		
		//teste Mes > 12
		lancamento.setMes(13);
		erro =  Assertions.assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
		Assertions.assertTrue(erro.getMessage().contains("Informe um Mês válido."));
		
		
		lancamento.setMes(1);
		
		
		//teste ano nulo
		
		erro =  Assertions.assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
		Assertions.assertTrue(erro.getMessage().contains("Informe um Ano válido."));
		
		//teste ano maior de 4 digitos
		lancamento.setAno(12345);
		erro =  Assertions.assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
		Assertions.assertTrue(erro.getMessage().contains("Informe um Ano válido."));
	
	
		lancamento.setAno(2020);
		
		
		//teste usuario nulo
		erro =  Assertions.assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
		Assertions.assertTrue(erro.getMessage().contains("Informe um Usuário válido."));
		
		
		Usuario usuario = new Usuario("Alexandre", "user@email.com");
		lancamento.setUsuario(usuario);		
		
		//teste valor nulo
		erro =  Assertions.assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
		Assertions.assertTrue(erro.getMessage().contains("Informe um Valor válido."));
		
		//tete valor < 1
		lancamento.setValor(BigDecimal.ZERO);
		erro =  Assertions.assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
		Assertions.assertTrue(erro.getMessage().contains("Informe um Valor válido."));
		
		
		lancamento.setValor(BigDecimal.valueOf(100));
		
		//teste tipo nulo
		erro =  Assertions.assertThrows(RegraNegocioException.class, () -> service.validar(lancamento));
		Assertions.assertTrue(erro.getMessage().contains("Informe um Tipo de lancamento."));
		
		
	}
	
	@Test
	public void deveObterSaldoDeUmUsuario() {

		Long idUsuario = 1l;
		
		Mockito.when(repository.obterSaldoPorTipoLancamentoEUsuario(idUsuario, TipoLancamento.DESPESA)).thenReturn(BigDecimal.valueOf(100));
		Mockito.when(repository.obterSaldoPorTipoLancamentoEUsuario(idUsuario, TipoLancamento.RECEITA)).thenReturn(BigDecimal.valueOf(200));
		
		BigDecimal saldo = service.obterSaldoPorTipoLancamentoEUsuario(idUsuario);
		
		Assertions.assertEquals(saldo, BigDecimal.valueOf(100));
		
	}
	
	@Test
	public void deveObterSaldoDeUmUsuarioEZerar() {

		Long idUsuario = 1l;
		
		Mockito.when(repository.obterSaldoPorTipoLancamentoEUsuario(idUsuario, TipoLancamento.DESPESA)).thenReturn(null);
		Mockito.when(repository.obterSaldoPorTipoLancamentoEUsuario(idUsuario, TipoLancamento.RECEITA)).thenReturn(null);
		
		BigDecimal saldo = service.obterSaldoPorTipoLancamentoEUsuario(idUsuario);
		
		Assertions.assertEquals(saldo, BigDecimal.valueOf(0));
		
	}
	
	public static Lancamento criarLancamento() {
		
		Lancamento lancamento = new Lancamento();
		lancamento.setId(1l);
		lancamento.setDescricao("Testes de Servico");
		lancamento.setMes(3);
		lancamento.setAno(2020);
		lancamento.setValor(BigDecimal.valueOf(100));
		lancamento.setTipo(TipoLancamento.DESPESA);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		lancamento.setDataCadastro(LocalDate.now());
		Usuario usuario = new Usuario("Alexandre", "user@email.com");
		lancamento.setUsuario(usuario);		
		
		return lancamento;
	

	}


}
