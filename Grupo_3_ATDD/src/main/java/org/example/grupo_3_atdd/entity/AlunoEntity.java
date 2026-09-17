package org.example.grupo_3_atdd.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade de persistência do Aluno.
 * A regra de negócio (direito a mais 3 cursos) NÃO vive aqui — ela já está
 * testada via TDD em org.example.grupo_3_atdd.domain.Aluno. Esta classe é
 * só o mapeamento JPA/tabela.
 */
@Entity
@Table(name = "aluno")
public class AlunoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CursoEntity> cursos = new ArrayList<>();

    protected AlunoEntity() {
        // exigido pelo JPA
    }

    public AlunoEntity(String nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<CursoEntity> getCursos() {
        return cursos;
    }
}
