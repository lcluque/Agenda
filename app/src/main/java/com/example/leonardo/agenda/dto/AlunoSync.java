package com.example.leonardo.agenda.dto;

import com.example.leonardo.agenda.modelo.Aluno;

import java.util.List;

/**
 * Created by Leonardo on 19/02/2018.
 */

public class AlunoSync {
    private List<Aluno> alunos;
    private String momentoDaUltimaModificacao;

    public String getMomentoDaUltimaModificacao() {
        return momentoDaUltimaModificacao;
    }

    public List<Aluno> getAlunos() {
        return alunos;
    }
    }
