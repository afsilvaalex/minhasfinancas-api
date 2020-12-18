package br.com.afsilva.minhasfinancas.service;

import java.util.Optional;

import br.com.afsilva.minhasfinancas.model.entity.Usuario;

public interface UsuarioService {

	Usuario autenticar(String email, String senha);
	
	Usuario salvarUsuario(Usuario usuario);
	
	void validarEmail(String email);
	
	Optional<Usuario> buscarPorId(Long id);
	
	
	
	
	
}
