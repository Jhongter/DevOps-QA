package org.example.grupo_3_atdd.dto;

import java.util.List;

public record AlunoResponseDTO(Long id, String nome, boolean temDireitoAMaisCursos, List<CursoResponseDTO> cursos) {
}
