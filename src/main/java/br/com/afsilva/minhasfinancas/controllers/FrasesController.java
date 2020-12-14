package br.com.afsilva.minhasfinancas.controllers;


import java.util.Arrays;
import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/frases")
public class FrasesController {
	
	private List<String> frases = Arrays.asList("frase 0", "frase1", "Frase 2", "Frase4", "Frase5", "frase6");
	
	
	
	@GetMapping
	public ResponseEntity<String> getFrase() {
		return ResponseEntity.ok("Não sou dono do mundo");
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<String> recupera(@PathVariable("id") int id) {
		try {
			return ResponseEntity.ok(frases.get(id));
			
		}catch (IndexOutOfBoundsException e){
			return ResponseEntity.notFound().build();
			
		}
		
	}
	
	@PostMapping("/nova{frase}")
	public ResponseEntity<String> postNewFrase(@PathVariable("frase") String str){
		
		frases.add(str);
		
		return ResponseEntity.ok("nova frase incluida");
		
	}
	
	

}
