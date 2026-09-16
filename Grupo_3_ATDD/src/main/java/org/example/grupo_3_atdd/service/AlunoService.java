package org.example.grupo_3_atdd.service;

import org.example.grupo_3_atdd.domain.Aluno;
import org.example.grupo_3_atdd.domain.Curso;
import org.example.grupo_3_atdd.dto.AlunoRequestDTO;
import org.example.grupo_3_atdd.dto.AlunoResponseDTO;
import org.example.grupo_3_atdd.dto.CursoRequestDTO;
import org.example.grupo_3_atdd.dto.CursoResponseDTO;
import org.example.grupo_3_atdd.dto.FinalizarCursoRequestDTO;
import org.example.grupo_3_atdd.entity.AlunoEntity;
import org.example.grupo_3_atdd.entity.CursoEntity;
import org.example.grupo_3_atdd.repository.AlunoRepository;
import org.example.grupo_3_atdd.repository.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Camada de aplicação. Faz a ponte entre as entidades JPA (persistência) e o
 * modelo de domínio já testado via TDD (org.example.grupo_3_atdd.domain),
 * evitando duplicar a regra "média >= 7,0 em curso finalizado dá direito a
 * mais 3 cursos".
 */
@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final CursoRepository cursoRepository;

    public AlunoService(AlunoRepository alunoRepository, CursoRepository cursoRepository) {
        this.alunoRepository = alunoRepository;
        this.cursoRepository = cursoRepository;
    }

    public AlunoResponseDTO criarAluno(AlunoRequestDTO dto) {
        AlunoEntity salvo = alunoRepository.save(new AlunoEntity(dto.nome()));
        return toResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public List<AlunoResponseDTO> listarAlunos() {
        return alunoRepository.findAll().stream().map(this::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public AlunoResponseDTO buscarAluno(Long id) {
        return toResponseDTO(buscarAlunoEntity(id));
    }

    public CursoResponseDTO criarCurso(Long alunoId, CursoRequestDTO dto) {
        AlunoEntity alunoEntity = buscarAlunoEntity(alunoId);
        CursoEntity salvo = cursoRepository.save(new CursoEntity(dto.nome(), alunoEntity));
        return toResponseDTO(salvo);
    }

    @Transactional
    public CursoResponseDTO finalizarCurso(Long alunoId, Long cursoId, FinalizarCursoRequestDTO dto) {
        AlunoEntity alunoEntity = buscarAlunoEntity(alunoId);
        CursoEntity cursoEntity = cursoRepository.findById(cursoId)
                .filter(c -> c.getAluno().getId().equals(alunoEntity.getId()))
                .orElseThrow(() -> new NoSuchElementException(
                        "Curso " + cursoId + " não encontrado para o aluno " + alunoId));

        // Usa o método de domínio testado em AlunoTest (curso.finalizar) para
        // validar a operação antes de persistir o resultado.
        Curso cursoDominio = new Curso(cursoEntity.getNome());
        Aluno alunoDominio = new Aluno(alunoEntity.getNome());
        cursoDominio.finalizar(alunoDominio, dto.media());

        cursoEntity.setFinalizado(cursoDominio.isFinalizado());
        cursoEntity.setMedia(cursoDominio.getMedia());
        cursoRepository.save(cursoEntity);

        return toResponseDTO(cursoEntity);
    }

    @Transactional(readOnly = true)
    public boolean temDireitoAMaisCursos(Long alunoId) {
        return calculaDireitoAMaisCursos(buscarAlunoEntity(alunoId));
    }

    private AlunoEntity buscarAlunoEntity(Long id) {
        return alunoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Aluno não encontrado: " + id));
    }

    private AlunoResponseDTO toResponseDTO(AlunoEntity entity) {
        List<CursoResponseDTO> cursos = entity.getCursos().stream().map(this::toResponseDTO).toList();
        return new AlunoResponseDTO(entity.getId(), entity.getNome(), calculaDireitoAMaisCursos(entity), cursos);
    }

    private CursoResponseDTO toResponseDTO(CursoEntity entity) {
        return new CursoResponseDTO(entity.getId(), entity.getNome(), entity.isFinalizado(), entity.getMedia());
    }

    /**
     * Reconstrói o objeto de domínio Aluno a partir das entidades JPA e
     * reaplica a regra já coberta pelos 4 cenários de AlunoTest, em vez de
     * reescrever a condição "media >= 7.0" aqui.
     */
    private boolean calculaDireitoAMaisCursos(AlunoEntity entity) {
        Aluno alunoDominio = new Aluno(entity.getNome());
        for (CursoEntity cursoEntity : entity.getCursos()) {
            if (cursoEntity.isFinalizado() && cursoEntity.getMedia() != null) {
                Curso cursoDominio = new Curso(cursoEntity.getNome());
                cursoDominio.finalizar(alunoDominio, cursoEntity.getMedia());
            }
        }
        return alunoDominio.temDireitoAMaisCursos();
    }
}
