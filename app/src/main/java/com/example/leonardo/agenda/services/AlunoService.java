package com.example.leonardo.agenda.services;

import com.example.leonardo.agenda.dto.AlunoSync;
import com.example.leonardo.agenda.modelo.Aluno;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Leonardo on 06/02/2018.
 */

public interface AlunoService {

    @POST("aluno")
    Call<Void> insere(@Body Aluno aluno);

    @GET("aluno")
    Call<AlunoSync> lista();

    @DELETE("aluno/{id}")
    Call<Void> deleta(@Path("id")String id);
}
