package br.com.afsilva.minhasfinancas.model.repository;


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

import br.com.afsilva.minhasfinancas.model.entity.Usuario;

import java.util.Optional;



@ExtendWith(SpringExtension.class)
@ActiveProfiles("teste")
@DataJpaTest	
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {
	
	@Autowired
	UsuarioRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
		
	@Test
	public void deveVerificarAExisttenciaDeUmEmail() {
		
		//Cenario
		Usuario usuario =  criarUsuario();
		entityManager.persist(usuario);
		
		//acao /execucao
		boolean result = repository.existsByEmail("usuario@email.com");
		
		//Verificacao
		Assertions.assertThat(result).isTrue();
			
	}
	
	@Test
	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComOEmail() {
		
		//Cenario

		
		
		//acao
		boolean result = repository.existsByEmail("usuario@email.com");
		
		//verificacao
		Assertions.assertThat(result).isFalse();
		
	}
	
	@Test
	public void devePersistirUmUsuarioNaBaseDeDados() {
		
		//cenario
		Usuario usuario = criarUsuario();
		
		
		//acao
		Usuario usuarioSalvo = repository.save(usuario);
		
		//verificacao
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
		
	}
	
	@Test
	public void deveBuscarUmUsuarioPorEmail() {
		
		//cenario
		Usuario usuario =  criarUsuario();
		entityManager.persist(usuario);
		
		//acao
		Optional<Usuario> result = repository.findByEmail(usuario.getEmail());
		
		//verificacao
		Assertions.assertThat(result.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioAoBuscarUmUsuarioPorEmailQuandoNaoExisteNaBase() {
		
		//cenario

		//acao
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");
		
		//verificacao
		Assertions.assertThat(result.isPresent()).isFalse();
	}
		
	public static Usuario criarUsuario() {
		return new Usuario("usuario", "usuario@email.com", "teste");
	}
	
	
	
}
