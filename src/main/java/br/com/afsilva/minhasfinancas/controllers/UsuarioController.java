package br.com.afsilva.minhasfinancas.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.afsilva.minhasfinancas.api.dto.UsuarioDTO;
import br.com.afsilva.minhasfinancas.exception.ErroAutenticacao;
import br.com.afsilva.minhasfinancas.exception.RegraNegocioException;
import br.com.afsilva.minhasfinancas.model.entity.Usuario;
import br.com.afsilva.minhasfinancas.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
	
	private UsuarioService service;
	
	public UsuarioController(UsuarioService service) {
		
		this.service = service;
	}
	
	@PostMapping("/autenticar")
	public ResponseEntity autenticar(@RequestBody UsuarioDTO dto) {
		
		try {
			Usuario usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
			return ResponseEntity.ok(usuarioAutenticado);
			
		} catch (ErroAutenticacao e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping
	public ResponseEntity salvar(@RequestBody UsuarioDTO dto) {
		
		Usuario usuario  = new Usuario(dto.getNome(), dto.getEmail(), dto.getSenha());
		
		try {
			Usuario usuarioSalvo = service.salvarUsuario(usuario);
			return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
			
		} catch(RegraNegocioException e ){
			
			return ResponseEntity.badRequest().body(e.getMessage());
			
		}
	}
	
	
}
