package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private TestRestTemplate testRestTemplate;
	
	//start zera a tabela de usuario e a partir do momento que apaga tudo cadastra um
	@BeforeAll
	void start() {
		usuarioRepository.deleteAll();
		
		//garante que nosso banco de dados esteja limpo e tenha um primeiro usuário (root)
		usuarioService.cadastrarUsuario(new Usuario(0L, "root", "root@root", "rootroot", ""));
		
	}
	
	@Test //avisa que é um teste
	@DisplayName("Deve cadastrar um novo usuario") //como eu quero que aparareça no display do relatório de teste
	public void DeveCriarUmNovoUsuario(){
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(
		//o id é 0 por conta do construtor de usuario que pede o valor de id, como é auto incremento deixamos não informado, como 0
		new Usuario(0L, "Thiago", "thiago@email.com", "12345678", ""));
		
		//equivale ao send
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange(
		"/usuario/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());

	}

}
