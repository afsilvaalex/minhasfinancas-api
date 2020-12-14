package br.com.afsilva.minhasfinancas.service;

import br.com.afsilva.minhasfinancas.model.entity.Usuario;

public interface UsuarioService {

	Usuario autenticar(String email, String senha);
	
	Usuario salvarUsuario(Usuario usuario);
	
	void validarEmail(String email);
	
	
	
	
	
}
