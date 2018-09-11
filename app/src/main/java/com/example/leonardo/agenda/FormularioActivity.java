package com.example.leonardo.agenda;

import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.leonardo.agenda.dao.AlunoDAO;
import com.example.leonardo.agenda.modelo.Aluno;
import com.example.leonardo.agenda.retrofit.RetrofitInicializador;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormularioActivity extends AppCompatActivity {

    public static final int CODIGO_CAMERA = 567;
    private FormularioHelper helper;
    private String caminhoFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);
        helper = new FormularioHelper(this);
        Intent intent = getIntent();
        Aluno aluno = (Aluno) intent.getSerializableExtra("aluno");
        if(aluno != null){
            helper.preencheFormulario(aluno);
        }
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); StrictMode.setVmPolicy(builder.build());

        Button botaofoto = (Button) findViewById(R.id.formulario_btnFoto);
        botaofoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                caminhoFoto = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
                File arquivoFoto = new File(caminhoFoto);
                Uri fotoURI = FileProvider.getUriForFile(FormularioActivity.this,
                        "br.eti.cvm.agenda.fileprovider",
                        arquivoFoto);
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, fotoURI);
                if (intentCamera.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intentCamera, CODIGO_CAMERA);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //abrir a foto que a gente tirou
        if(resultCode == RESULT_OK) {

            if (requestCode == CODIGO_CAMERA) {
               helper.carregaImagem(caminhoFoto);

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_formulario,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_formulario_ok:
                Aluno aluno = helper.pegaAluno();

                AlunoDAO dao = new AlunoDAO(this);
                if (aluno.getId() != null) {
                    dao.altera(aluno);
                } else {
                    dao.insere(aluno);
                }
                dao.close();

                Call call = new RetrofitInicializador().getAlunoService().insere(aluno);

                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        Log.i("onResponse", "requisicao com sucesso");
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        Log.e("onFailure", "requisicao falhou");
                    }
                });

                Toast.makeText(FormularioActivity.this,"Dados inseridos",Toast.LENGTH_SHORT).show();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    public class GenericFileProvider extends FileProvider{

    }
}
