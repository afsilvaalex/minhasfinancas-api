package br.com.afsilva.minhasfinancas.service;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.afsilva.minhasfinancas.exception.ErroAutenticacao;
import br.com.afsilva.minhasfinancas.exception.RegraNegocioException;
import br.com.afsilva.minhasfinancas.model.entity.Usuario;
import br.com.afsilva.minhasfinancas.model.repository.UsuarioRepository;
import br.com.afsilva.minhasfinancas.service.imp.UsuarioServiceImp;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("teste")
public class UsuarioServiceTest {

//	UsuarioRepository repository = Mockito.mock(UsuarioRepository.class) ;
	@MockBean
	UsuarioRepository repository;

	@SpyBean
	UsuarioServiceImp service; // = new UsuarioServiceImp(repository);

	@BeforeEach
	public void SetUp() {

		// repository = Mockito.mock(UsuarioRepository.class);
		// service = new UsuarioServiceImp(repository);

	}

	@Test
	public void deveSalvarUmUsuario() {
		// cenario
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		
		Usuario usuario = criarUsuario();
		usuario.setId(1l);

		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

		// acao
		Usuario usuarioSalvo = service.salvarUsuario(usuario);

		usuario.setId(2l);

		// verificacao
		Assertions.assertEquals(usuarioSalvo.getId(), usuario.getId());

	}

	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {

		// Cenario
		Usuario usuario = criarUsuario();
		usuario.setId(1l);
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(usuario.getEmail());

		// Acao

		Assertions.assertThrows(RegraNegocioException.class, () -> service.salvarUsuario(usuario));
		Mockito.verify(repository, Mockito.never()).save(usuario);

	}

	@Test
	public void deveAutenticarUmUsuarioComSucesso() {

		// Cenario
		Usuario usuario = criarUsuario();
		Mockito.when(repository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));

		// Acao

		service.autenticar(usuario.getEmail(), usuario.getSenha());

		// verificacao

	}


	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {

		// Cenario

		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

		// acao
		RuntimeException runTimeException = Assertions.assertThrows(ErroAutenticacao.class,
				() -> service.autenticar(" ", " "));

		// verificacao
		Assertions.assertTrue(runTimeException.getMessage().contains("Usuário não encontrado."));
	}

	@Test
	public void deveLancarErroQuandoSenhaEstiverInvalida() {

		// Cenario

		Usuario usuario = criarUsuario();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

		// acao
		RuntimeException runTimeException = Assertions.assertThrows(ErroAutenticacao.class,
				() -> service.autenticar(usuario.getEmail(), " "));

		// verificacao
		Assertions.assertTrue(runTimeException.getMessage().contains("Senha inválida."));
	}

	@Test
	public void deveValidarEmail() {

		// cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

		// acao
		service.validarEmail("usario@email.com");

	}

	@Test
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		// Cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

		// acao
		Assertions.assertThrows(RegraNegocioException.class, () -> service.validarEmail("usuario@email.com"));

	}
	
	@Test
	public void deveObterUmUsuarioPorID() {
		
		//cenario
		Long id = 1l;
		Usuario usuario = criarUsuario();
		usuario.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(usuario));
		
		//execucao
		Optional<Usuario> usuarioPorId = service.buscarPorId(id);
		
		//verificacao
		
		Assertions.assertTrue(usuarioPorId.isPresent());
			
	}
	

	private Usuario criarUsuario() {
		Usuario usuario = new Usuario("Alexandre", "usuario@email.com", "password");
		return usuario;
	}
}
