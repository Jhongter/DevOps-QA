package org.example.grupo_3_atdd.repository;

import org.example.grupo_3_atdd.entity.AlunoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<AlunoEntity, Long> {
}
