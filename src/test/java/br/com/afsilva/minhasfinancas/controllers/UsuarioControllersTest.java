package br.com.afsilva.minhasfinancas.controllers;

import java.math.BigDecimal;
import java.util.Optional;


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

import br.com.afsilva.minhasfinancas.api.dto.UsuarioDTO;
import br.com.afsilva.minhasfinancas.exception.ErroAutenticacao;
import br.com.afsilva.minhasfinancas.exception.RegraNegocioException;
import br.com.afsilva.minhasfinancas.model.entity.Usuario;
import br.com.afsilva.minhasfinancas.service.LancamentoService;
import br.com.afsilva.minhasfinancas.service.UsuarioService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("teste")
@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc
public class UsuarioControllersTest {
	
	static final String API = "/api/usuarios";
	static final MediaType JSON = MediaType.APPLICATION_JSON;
	
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	UsuarioService service;
	
	@MockBean
	LancamentoService lancamentoService;
	
	
	@Test
	public void deveAutenticarUmUsuario() throws Exception{
		
		//cenario
		UsuarioDTO dto = criarUsuarioDto();
		Usuario usuario = criarUsuario();
		
		Mockito.when(service.autenticar(dto.getEmail(), dto.getSenha())).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execucao e verificacao
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post(API.concat("/autenticar"))
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		
		
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
			.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
	}
	
	
	@Test
	public void deveRetornarBadRquestAoObterErroDeAutenticacao() throws Exception{
		
		//cenario
		UsuarioDTO dto = criarUsuarioDto();
		
		Mockito.when(service.autenticar(dto.getEmail(), dto.getSenha())).thenThrow(ErroAutenticacao.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execucao e verificacao
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post(API.concat("/autenticar"))
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		
		
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
	
	}
	
	
	@Test
	public void deveCriarUmNovoUsuario() throws Exception{
		
		//cenario
		UsuarioDTO dto = criarUsuarioDto();
		Usuario usuario = criarUsuario();

		Mockito.when( service.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);
		
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
			.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
			.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
	}
	
	@Test
	public void deveRetornarBadRequestAoTentarCriarUmUsuarioInvalido() throws Exception{
		
		//cenario
		UsuarioDTO dto = criarUsuarioDto();

		Mockito.when( service.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execucao e verificacao
		
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
	public void deveObterSaldo() throws Exception{
		
		//cenario
		
		Usuario usuario = criarUsuario();
		
		Mockito.when(service.buscarPorId(usuario.getId())).thenReturn(Optional.of(usuario));
		
		Mockito.when(lancamentoService.obterSaldoPorTipoLancamentoEUsuario(usuario.getId()))
					.thenReturn(BigDecimal.valueOf(1000));
		
		//execucao e verificacao
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(API.concat("/" + usuario.getId() + "/saldo"))
				.accept(JSON); 

		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.content().string("1000"));
	}
	
	@Test
	public void deveRetornarStatusNotFoundAoObterSaldoComUmUsuarioNaoEncontrado() throws Exception{
		
		//cenario
		Long idUsuario = 1l;	

		Mockito.when(service.buscarPorId(idUsuario)).thenReturn(Optional.empty());

		
		//execucao e verificacao
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(API.concat("/" + idUsuario + "/saldo"))
				.accept(JSON); 

		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isNotFound());

	}
	
	private UsuarioDTO criarUsuarioDto() {
		
		UsuarioDTO usuarioDto = new UsuarioDTO();
		usuarioDto.setEmail("user@email.com");
		usuarioDto.setSenha("password");
		
		return usuarioDto;
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
