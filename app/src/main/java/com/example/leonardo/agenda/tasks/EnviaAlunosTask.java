package com.example.leonardo.agenda.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.leonardo.agenda.WebClient;
import com.example.leonardo.agenda.converter.AlunoConverter;
import com.example.leonardo.agenda.dao.AlunoDAO;
import com.example.leonardo.agenda.modelo.Aluno;

import java.util.List;

/**
 * Created by Leonardo on 26/01/2018.
 */

public class EnviaAlunosTask extends AsyncTask<Object,Object,String> {

    private Context context;
    private ProgressDialog dialog;

    public EnviaAlunosTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        dialog = ProgressDialog.show(context,"Aguarde","Enviando Dados...",true,true);
    }

    @Override
    protected String doInBackground(Object... params) {
        AlunoDAO dao = new AlunoDAO(context);
        List<Aluno> alunos = dao.buscaAlunos();
        dao.close();
        AlunoConverter conversor = new AlunoConverter();
        String json = conversor.converteParaJSON(alunos);
        WebClient client = new WebClient();
        String resposta = client.post(json);

        return resposta;
    }

    @Override
    protected void onPostExecute(String resposta) {
        dialog.dismiss();
        Toast.makeText(context, resposta, Toast.LENGTH_SHORT).show();
    }
}
