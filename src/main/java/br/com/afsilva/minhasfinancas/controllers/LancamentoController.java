package br.com.afsilva.minhasfinancas.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.afsilva.minhasfinancas.api.dto.AtualizaStatusDTO;
import br.com.afsilva.minhasfinancas.api.dto.LancamentoDTO;
import br.com.afsilva.minhasfinancas.exception.RegraNegocioException;
import br.com.afsilva.minhasfinancas.model.entity.Lancamento;
import br.com.afsilva.minhasfinancas.model.entity.Usuario;
import br.com.afsilva.minhasfinancas.model.enums.StatusLancamento;
import br.com.afsilva.minhasfinancas.model.enums.TipoLancamento;
import br.com.afsilva.minhasfinancas.service.LancamentoService;
import br.com.afsilva.minhasfinancas.service.UsuarioService;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoController {

	private LancamentoService service;
	private UsuarioService usuarioService;
	
	private LancamentoController(LancamentoService service, UsuarioService usuarioService) {
		this.service = service;
		this.usuarioService = usuarioService;
	}
	
	@PostMapping
	public ResponseEntity<Object> salvar(@RequestBody LancamentoDTO dto) {
		
		try {
			Lancamento lancamento = converter(dto);
			lancamento = service.salvar(lancamento);
			return  new ResponseEntity<Object>(lancamento, HttpStatus.CREATED);
			
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	
	@PutMapping("{id}")
	public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
		
		return service.obterPorId(id).map(entity -> {
			try {
				Lancamento lancamento = converter(dto);
				lancamento.setId(entity.getId());
				service.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}

		}).orElseGet( () -> 
			new ResponseEntity("Lancamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id) {
		return service.obterPorId(id).map(lancamento -> {
			service.deletar(lancamento);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}).orElseGet(() ->
				new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}
	
	@GetMapping
	public ResponseEntity buscar(
			@RequestParam(value = "descricao", required = false) String descricao,
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "ano", required = false) Integer ano,
			@RequestParam("usuario") Long idUsuario) {
		
		Lancamento lancamentoFiltro = new Lancamento();
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setMes(mes);
		lancamentoFiltro.setAno(ano);

		
		Optional<Usuario> usuario = usuarioService.buscarPorId(idUsuario);
		if (!usuario.isPresent()) {
			return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado para o Id informado");
		}else {
			lancamentoFiltro.setUsuario(usuario.get());
		}
		
		List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
		return ResponseEntity.ok(lancamentos);
		
	}
	
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizaStatus(@PathVariable("id") Long id,  @RequestBody AtualizaStatusDTO dto) {
		
		return service.obterPorId(id).map( lancamento -> {
			StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
			if (statusSelecionado == null) {
				ResponseEntity.badRequest().body("Não foi possível atualizar o status do lançamento, envie um status válido");
			}
			
			try {
				lancamento.setStatus(statusSelecionado);
				service.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}

		}).orElseGet(() ->
		new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}
	
	private Lancamento converter(LancamentoDTO dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		lancamento.setId(dto.getId());
		
		if (dto.getTipo() != null) {
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		}
		
		if (dto.getStatus() != null) {
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		}

		
		
		Usuario usuario = usuarioService
			.buscarPorId(dto.getUsuario())
			.orElseThrow(() -> new RegraNegocioException("Usuário para o Id informado."));
		lancamento.setUsuario(usuario);
		return lancamento;
	}
}
