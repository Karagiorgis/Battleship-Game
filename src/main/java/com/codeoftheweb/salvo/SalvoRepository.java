package com.codeoftheweb.salvo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalvoRepository extends JpaRepository<Salvo, Long>{

}