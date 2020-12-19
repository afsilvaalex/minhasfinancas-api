package br.com.afsilva.minhasfinancas.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.afsilva.minhasfinancas.model.repository.LancamentoRepository;
import br.com.afsilva.minhasfinancas.service.imp.LancamentoServiceImp;



@ExtendWith(SpringExtension.class)
@ActiveProfiles("teste")
public class LancametoServiceTest {
	
	@MockBean
	LancamentoRepository repository;
	
	@SpyBean
	LancamentoServiceImp service; // = new UsuarioServiceImp(repository);
	
	@Test
	public void deveSalvarUmLancamento() {
		
		
	}


}
