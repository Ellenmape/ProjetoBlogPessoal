package com.generation.blogpessoal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.generation.blogpessoal.model.Postagem;

//JPA assume a responsabilidade de criar automaticamente o método de criar query no banco de dados
//JpaReporitoy - classe JPA - metodos que vão realizar query no banco de dados
public interface PostagemRepository extends JpaRepository <Postagem, Long >{

}
