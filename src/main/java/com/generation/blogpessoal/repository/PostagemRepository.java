package com.generation.blogpessoal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.generation.blogpessoal.model.Postagem;

//JPA assume a responsabilidade de criar automaticamente o método de criar query no banco de dados
//JpaReporitoy - classe JPA - metodos que vão realizar query no banco de dados
public interface PostagemRepository extends JpaRepository <Postagem, Long >{
	public List <Postagem> findAllByTituloContainingIgnoreCase(@Param("titulo")String titulo);

}
