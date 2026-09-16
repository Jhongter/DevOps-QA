package org.example.grupo_3_atdd.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.grupo_3_atdd.dto.AlunoRequestDTO;
import org.example.grupo_3_atdd.dto.AlunoResponseDTO;
import org.example.grupo_3_atdd.dto.CursoRequestDTO;
import org.example.grupo_3_atdd.dto.CursoResponseDTO;
import org.example.grupo_3_atdd.dto.FinalizarCursoRequestDTO;
import org.example.grupo_3_atdd.service.AlunoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alunos")
@Tag(name = "Alunos", description = "US: aluno com curso finalizado e média >= 7,0 tem direito a mais 3 cursos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @PostMapping
    @Operation(summary = "Cria um novo aluno")
    public ResponseEntity<AlunoResponseDTO> criar(@RequestBody AlunoRequestDTO dto) {
        return ResponseEntity.ok(alunoService.criarAluno(dto));
    }

    @GetMapping
    @Operation(summary = "Lista todos os alunos cadastrados")
    public ResponseEntity<List<AlunoResponseDTO>> listar() {
        return ResponseEntity.ok(alunoService.listarAlunos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um aluno pelo id, com seus cursos")
    public ResponseEntity<AlunoResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(alunoService.buscarAluno(id));
    }

    @PostMapping("/{id}/cursos")
    @Operation(summary = "Matricula o aluno em um novo curso (ainda não finalizado)")
    public ResponseEntity<CursoResponseDTO> criarCurso(@PathVariable Long id, @RequestBody CursoRequestDTO dto) {
        return ResponseEntity.ok(alunoService.criarCurso(id, dto));
    }

    @PostMapping("/{id}/cursos/{cursoId}/finalizar")
    @Operation(summary = "Finaliza um curso do aluno, lançando a média final")
    public ResponseEntity<CursoResponseDTO> finalizarCurso(
            @PathVariable Long id,
            @PathVariable Long cursoId,
            @RequestBody FinalizarCursoRequestDTO dto) {
        return ResponseEntity.ok(alunoService.finalizarCurso(id, cursoId, dto));
    }

    @GetMapping("/{id}/direito-bonus")
    @Operation(summary = "Verifica se o aluno tem direito a mais 3 cursos")
    public ResponseEntity<Boolean> temDireitoBonus(@PathVariable Long id) {
        return ResponseEntity.ok(alunoService.temDireitoAMaisCursos(id));
    }
}
