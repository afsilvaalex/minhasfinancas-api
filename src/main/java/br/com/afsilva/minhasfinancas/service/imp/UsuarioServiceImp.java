package br.com.afsilva.minhasfinancas.service.imp;


import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.afsilva.minhasfinancas.exception.ErroAutenticacao;
import br.com.afsilva.minhasfinancas.exception.RegraNegocioException;
import br.com.afsilva.minhasfinancas.model.entity.Usuario;
import br.com.afsilva.minhasfinancas.model.repository.UsuarioRepository;
import br.com.afsilva.minhasfinancas.service.UsuarioService;

@Service
public class UsuarioServiceImp implements UsuarioService {
	
	private UsuarioRepository repository;

	
	
	public UsuarioServiceImp(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email);
		
		if(!usuario.isPresent()) {
			throw new ErroAutenticacao("Usuário não encontrado.");
		}
		
		if(!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacao("Senha inválida.");
		}
			
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {

		validarEmail(usuario.getEmail());
		
		return repository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = repository.existsByEmail(email);
		if (existe) {
			throw new RegraNegocioException("Já existe um usuário cadastrado com este email");
			
		}
		
	}

	@Override
	public Optional<Usuario> buscarPorId(Long id) {
		
		return repository.findById(id);
	}

}
