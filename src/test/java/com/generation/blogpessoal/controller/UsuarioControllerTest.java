package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

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
		usuarioService.cadastrarUsuario(new Usuario(0L, "root", "root@root.com", "rootroot", ""));
		
	}
	
	@Test //avisa que é um teste
	@DisplayName("Cadastrar um novo usuário") //como eu quero que aparareça no display do relatório de teste
	public void deveCriarUmNovoUsuario(){
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(
				new Usuario(0L, "Thiago", "thiago@email.com", "12345678", ""));
		////o id é 0 por conta do construtor de usuario que pede o valor de id, como é auto incremento deixamos não informado, como 0
		
		//equivale ao send
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange(
		"/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());

	}
	
	@Test
	@DisplayName("Não deve permitir duplicação de usuário") 
	public void nãoDeveDuplicarUsuario() {
		usuarioService.cadastrarUsuario(new Usuario(
				0L, "Antonio Fagundes", "antonio@email.com.br", "13456278", ""));
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L, "Antonio Fagundes", "antonio@email.com.br", "13456278", ""));
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
	
	}
	@Test
	@DisplayName("Atualizar um usuário")
	public void deveAtealizarUmUsuario() {
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(
				0L, "Giovana Saraiva", "giovana@email.com.br", "giovana123", ""));
				
				Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(),
						"Giovana Rodrigues Saraiva", "giovana@email.com.br", "giovana123", "");
				
				HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);
				
				ResponseEntity<Usuario> corpoResposta = testRestTemplate.withBasicAuth("root@root.com", "rootroot").exchange(
						"/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);
				
				assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
						
	}

	
	@Test
	@DisplayName("Listar todos os usuários")
	public void deveMostrarTodosUsuarios() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Jolene Dog", "jolene@email.com.br", "jolene123", ""));
		
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Shenzi Maria", "shenzimaria@email", "shenzimaria123", "")
				);
		
		ResponseEntity<String> resposta = testRestTemplate.withBasicAuth("root@root.com", "rootroot").exchange("/usuarios/all", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	
}
}
