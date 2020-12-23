package br.com.afsilva.minhasfinancas.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.error.ShouldHaveSameSizeAs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.afsilva.minhasfinancas.api.dto.AtualizaStatusDTO;
import br.com.afsilva.minhasfinancas.api.dto.LancamentoDTO;
import br.com.afsilva.minhasfinancas.exception.RegraNegocioException;
import br.com.afsilva.minhasfinancas.model.entity.Lancamento;
import br.com.afsilva.minhasfinancas.model.entity.Usuario;
import br.com.afsilva.minhasfinancas.model.enums.StatusLancamento;
import br.com.afsilva.minhasfinancas.model.enums.TipoLancamento;
import br.com.afsilva.minhasfinancas.service.LancamentoService;
import br.com.afsilva.minhasfinancas.service.UsuarioService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("teste")
@WebMvcTest(controllers = LancamentoController.class)
@AutoConfigureMockMvc
public class LancamentoControllerTest {

	static final String API = "/api/lancamentos";
	static final MediaType JSON = MediaType.APPLICATION_JSON;
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	LancamentoService lancamentoService;
	
	@MockBean
	UsuarioService usuarioService;
	
	@Test
	public void deveSalvarUmLancamento() throws Exception {
		
		//cenario
		LancamentoDTO dto = criarLancamentoDTO();
		Lancamento lancamento = criarLancamento();
		Usuario usuario = criarUsuario();
		
		Mockito.when(lancamentoService.salvar(Mockito.any(Lancamento.class))).thenReturn(lancamento);
		Mockito.when(usuarioService.buscarPorId(usuario.getId())).thenReturn(Optional.of(usuario));
		
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execucao e verificacao
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post(API)
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("id").value(lancamento.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("descricao").value(lancamento.getDescricao()));
		
	}
	
	@Test
	public void deveLancarUmErroAoSalvarUmLancamentoComUmUsuarioNaoCadastrado() throws Exception {
		
		//cenario
		LancamentoDTO dto = criarLancamentoDTO();

		
		Mockito.when(usuarioService.buscarPorId(dto.getUsuario())).thenReturn(Optional.empty());
		
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execucao e verificacao
		
		Mockito.verify(lancamentoService, Mockito.never()).salvar(Mockito.any(Lancamento.class));
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post(API)
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
		
	}
	
	@Test
	public void deveAtualizarUmLancamento() throws Exception {
		
		//cenario
		LancamentoDTO dto = criarLancamentoDTO();
		Lancamento lancamento = criarLancamento();
		Usuario usuario = criarUsuario();
		dto.setId(lancamento.getId());
		dto.setDescricao("Testes Unitário Atualizar Lancamento");
		dto.setAno(2018);
		
		Mockito.when(lancamentoService.obterPorId(dto.getId())).thenReturn(Optional.of(lancamento));
		Mockito.when(usuarioService.buscarPorId(usuario.getId())).thenReturn(Optional.of(usuario));
		Mockito.when(lancamentoService.atualizar(Mockito.any(Lancamento.class))).thenReturn(lancamento);

		
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execucao e verificacao
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.put(API.concat("/" + dto.getId()))
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("descricao").value(dto.getDescricao()))
			.andExpect(MockMvcResultMatchers.jsonPath("ano").value(dto.getAno()));
		
	}
	
	@Test
	public void deveLancarErroAoAtualizarUmLancamentoNaoCadastrado() throws Exception {
		
		//cenario
		LancamentoDTO dto = criarLancamentoDTO();

		Usuario usuario = criarUsuario();
		dto.setId(1l);
		dto.setDescricao("Testes Unitário Atualizar Lancamento");
		dto.setAno(2018);
		
		Mockito.when(lancamentoService.obterPorId(dto.getId())).thenReturn(Optional.empty());
		Mockito.when(usuarioService.buscarPorId(usuario.getId())).thenReturn(Optional.of(usuario));
	
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execucao e verificacao
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.put(API.concat("/" + dto.getId()))
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		
		Mockito.verify(lancamentoService, Mockito.never()).atualizar(Mockito.any(Lancamento.class));
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	public void deveLancarErroAoAtualizarUmLancamentoEOUsuarioNaoEstaCadastrado() throws Exception {
		
		//cenario
		LancamentoDTO dto = criarLancamentoDTO();
		Lancamento lancamento = criarLancamento();

		Usuario usuario = criarUsuario();
		dto.setId(1l);
		dto.setDescricao("Testes Unitário Atualizar Lancamento");
		dto.setAno(2018);
		
		Mockito.when(lancamentoService.obterPorId(dto.getId())).thenReturn(Optional.of(lancamento));
		Mockito.when(usuarioService.buscarPorId(dto.getUsuario())).thenReturn(Optional.empty());
	
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execucao e verificacao
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.put(API.concat("/" + dto.getId()))
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		
		Mockito.verify(lancamentoService, Mockito.never()).atualizar(Mockito.any(Lancamento.class));
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	public void deveAtualizarOStatusDeUmLancamento() throws Exception {
		
		//cenario
		AtualizaStatusDTO statusDto = new AtualizaStatusDTO(StatusLancamento.CANCELADO.toString());
		
		Lancamento lancamento = criarLancamento();

		
		Mockito.when(lancamentoService.obterPorId(lancamento.getId())).thenReturn(Optional.of(lancamento));
		Mockito.when(lancamentoService.atualizar(Mockito.any(Lancamento.class))).thenReturn(lancamento);
		
		String json = new ObjectMapper().writeValueAsString(statusDto);
		
		//execucao e verificacao
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.put(API.concat("/" + lancamento.getId() + "/atualiza-status" ))
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("status").value(statusDto.getStatus()));
		
	}
	
	@Test
	public void deveLancarErroAoAtualizarOStatusDeUmLancamentoNaoCadastrado() throws Exception {
		
		//cenario
		AtualizaStatusDTO statusDto = new AtualizaStatusDTO(StatusLancamento.CANCELADO.toString());
		
		
		Mockito.when(lancamentoService.obterPorId(1l)).thenReturn(Optional.empty());

		
		String json = new ObjectMapper().writeValueAsString(statusDto);
		
		//execucao e verificacao
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.put(API.concat("/1/atualiza-status" ))
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		
		Mockito.verify(lancamentoService, Mockito.never()).atualizar(Mockito.any(Lancamento.class));
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	public void deveLancarErroAoAtualizarOStatusDeUmLancamentoNoRepositorio() throws Exception {
		
		//cenario
		AtualizaStatusDTO statusDto = new AtualizaStatusDTO(StatusLancamento.CANCELADO.toString());
		
		Lancamento lancamento = criarLancamento();

		
		Mockito.when(lancamentoService.obterPorId(lancamento.getId())).thenReturn(Optional.of(lancamento));
		Mockito.when( lancamentoService.atualizar(Mockito.any(Lancamento.class))).thenThrow(RegraNegocioException.class);

		
		String json = new ObjectMapper().writeValueAsString(statusDto);
		
		//execucao e verificacao
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.put(API.concat("/"+ lancamento.getId() + "/atualiza-status" ))
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		
		Mockito.verify(lancamentoService, Mockito.never()).atualizar(Mockito.any(Lancamento.class));
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	public void deveDeletarUmLancamento() throws Exception{
		
		//cenario
		LancamentoDTO dto = criarLancamentoDTO();
		Lancamento lancamento = criarLancamento();;
		dto.setId(lancamento.getId());
		
		Mockito.when(lancamentoService.obterPorId(dto.getId())).thenReturn(Optional.of(lancamento));
		Mockito.doNothing().when(lancamentoService).deletar(lancamento);	
		
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execucao e verificacao
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.delete(API.concat("/" + dto.getId()))
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isNoContent());
	
		
	
	
	
	}
	
	@Test
	public void deveLancarUmErroAoTentarDeletarUmLancamentoNaoCadastrado() throws Exception{

		
		//cenario
		LancamentoDTO dto = criarLancamentoDTO();

		Usuario usuario = criarUsuario();
		dto.setId(1l);
		
		Mockito.when(lancamentoService.obterPorId(dto.getId())).thenReturn(Optional.empty());
		Mockito.when(usuarioService.buscarPorId(usuario.getId())).thenReturn(Optional.of(usuario));
	
		
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execucao e verificacao
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.delete(API.concat("/" + dto.getId()))
													.accept(JSON)
													.contentType(JSON)
													.content(json);
	
		Mockito.verify(lancamentoService, Mockito.never()).deletar(Mockito.any(Lancamento.class));
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
	
		
	
	
	
	}
	
	@Test
	public void deveBuscarLancamentoComDescricaoMesAno() throws Exception{
		
		//cenario
		Long idUsuario = 1l;
		String descricao = "Teste de busca Lancamento";
		Integer mes =  1;
		Integer ano = 2020;
		
		Lancamento lancamentoBusca = criarLancamento();
		Lancamento lancamentoRetorno = criarLancamento();
		lancamentoRetorno.setId(2l);
		
		List<Lancamento> listaLancamentos = new ArrayList<Lancamento>();
		
		
		listaLancamentos.add(lancamentoBusca);
	 	listaLancamentos.add(lancamentoRetorno);

		String json = new ObjectMapper().writeValueAsString(listaLancamentos);
		
		Usuario usuario = criarUsuario();
	
		Mockito.when(usuarioService.buscarPorId(idUsuario)).thenReturn(Optional.of(usuario));
		Mockito.when(lancamentoService.buscar(Mockito.any(Lancamento.class))).thenReturn(listaLancamentos);
		
	//execucao e verificacao
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.get(API.concat("?usuario=" +
																		idUsuario +
																		"&descricao=" +
																		descricao +
																		"&mes=" +
																		mes +
																		"&ano=" +
																		ano))
													.contentType(JSON)
													.accept(JSON);

		mvc
		.perform(request)
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers.content().string(json));
	//	.andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)));
	
		
	}
	
	
	
	@Test
	public void deveEnviarErroAoBuscarLancamentoComDescricaoMesAnoENaoEncontrarOUsuario() throws Exception{
		
		//cenario
		Long idUsuario = 1l;
		String descricao = "Teste de busca Lancamento";
		Integer mes =  1;
		Integer ano = 2020;
		
		
		Usuario usuario = criarUsuario();
	
		Mockito.when(usuarioService.buscarPorId(idUsuario)).thenReturn(Optional.empty());
		
	//execucao e verificacao
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.get(API.concat("?usuario=" +
																		idUsuario +
																		"&descricao=" +
																		descricao +
																		"&mes=" +
																		mes +
																		"&ano=" +
																		ano))
													.contentType(JSON)
													.accept(JSON);

		mvc
		.perform(request)
		.andExpect(MockMvcResultMatchers.status().isBadRequest());
		
		Mockito.verify(lancamentoService, Mockito.never()).buscar(Mockito.any(Lancamento.class));
		
	}
	
	public static Lancamento criarLancamento() {
		
		Lancamento lancamento = new Lancamento();
		lancamento.setId(1l);
		lancamento.setDescricao("Teste Unitario do Controller Lancamento");
		lancamento.setMes(3);
		lancamento.setAno(2020);
		lancamento.setValor(BigDecimal.valueOf(100));
		lancamento.setTipo(TipoLancamento.DESPESA);
		lancamento.setStatus(StatusLancamento.PENDENTE);
	//	lancamento.setDataCadastro(LocalDate.now());
		Usuario usuario = new Usuario("Alexandre", "user@email.com");
		lancamento.setUsuario(usuario);		
		
		return lancamento;
	

	}
	
	public static LancamentoDTO criarLancamentoDTO() {
		
		LancamentoDTO dto = new LancamentoDTO();
		dto.setDescricao("Teste Unitario do Controller Lancamento");
		dto.setMes(1);
		dto.setAno(2020);
		dto.setValor(BigDecimal.valueOf(1000));
		dto.setUsuario(1l);
		dto.setTipo(TipoLancamento.RECEITA.toString());
		dto.setStatus(StatusLancamento.PENDENTE.toString());
		
		return dto;
	}
	
	private Usuario criarUsuario() {
		
		Usuario usuario = new Usuario();
		usuario.setId(1l);
		usuario.setNome("Teste Unitario");
		usuario.setEmail("user@email.com");
		usuario.setSenha("password");
		return usuario;
		
		
	}
}
