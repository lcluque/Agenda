package com.example.leonardo.agenda;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.leonardo.agenda.adapter.AlunosAdapter;
import com.example.leonardo.agenda.dao.AlunoDAO;
import com.example.leonardo.agenda.dto.AlunoSync;
import com.example.leonardo.agenda.modelo.Aluno;
import com.example.leonardo.agenda.retrofit.RetrofitInicializador;
import com.example.leonardo.agenda.tasks.EnviaAlunosTask;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ListaAlunosActivity extends AppCompatActivity {

    private ListView listaAlunos;
    private SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos);

        listaAlunos = (ListView) findViewById(R.id.Lista_Alunos);
        swipe = (SwipeRefreshLayout)findViewById(R.id.swipe_lista_aluno);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                buscaAlunos();
            }
        });


        registerForContextMenu(listaAlunos);

        listaAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> lista, View item, int position, long id) {
             Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(position);
             Intent intent = new Intent(ListaAlunosActivity.this,FormularioActivity.class);
             intent.putExtra("aluno",aluno);
             startActivity(intent);
            }
        });

        Button novoAluno = (Button) findViewById(R.id.lista_novo);
        novoAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                startActivity(intent);
            }
        });
        buscaAlunos();
    }

    private void CarregaLista() {
        AlunoDAO dao = new AlunoDAO(this);
        List<Aluno> alunos = dao.buscaAlunos();
        for (Aluno aluno:
                alunos) {
            Log.i("Id do aluno", String.valueOf(aluno.getId()));
            
        }
        dao.close();
        AlunosAdapter adapter = new AlunosAdapter(this, alunos);
        listaAlunos.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CarregaLista();
    }

    private void buscaAlunos() {
        Call<AlunoSync> call = new RetrofitInicializador().getAlunoService().lista();
        call.enqueue(new Callback<AlunoSync>() {
            @Override
            public void onResponse(Call<AlunoSync> call, Response<AlunoSync> response) {
                AlunoSync alunoSync = response.body();
                AlunoDAO dao = new AlunoDAO(ListaAlunosActivity.this);
                dao.sincroniza(alunoSync.getAlunos());
                dao.close();
                CarregaLista();
                swipe.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<AlunoSync> call, Throwable t) {
                Log.e("onFailure chamado", t.getMessage() );
                swipe.setRefreshing(false);
            }
        });
    }


    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista_alunos,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_enviar_notas:
                new EnviaAlunosTask(this).execute();
                break;
            case R.id.menu_baixar_provas:
                Intent intent = new Intent(this,ProvasActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_mapa:
                Intent vaiParaMapa = new Intent(this,MapsActivity.class);
                startActivity(vaiParaMapa);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,final ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        final Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(info.position);

        MenuItem itemLigar = menu.add("Ligar");
        itemLigar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(ActivityCompat.checkSelfPermission(ListaAlunosActivity.this,
                        Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ListaAlunosActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},123);
                } else {
                    Intent intentLigar = new Intent(Intent.ACTION_CALL);
                    intentLigar.setData(Uri.parse("tel:" + aluno.getTelefone()));
                    startActivity(intentLigar);
                }
                return false;
            }
        });

        MenuItem itemSMS = menu.add("Enviar SMS");
        Intent intentSMS = new Intent(Intent.ACTION_VIEW);
        intentSMS.setData(Uri.parse("sms:" + aluno.getTelefone()));
        itemSMS.setIntent(intentSMS);

        MenuItem itemMapa = menu.add("Visualizar no mapa");
        Intent intentMapa = new Intent(Intent.ACTION_VIEW);
        intentMapa.setData(Uri.parse("geo:0,0?q=" + aluno.getEndereco()));
        itemMapa.setIntent(intentMapa);

        MenuItem itemSite = menu.add("Visitar Site");
        Intent intenSize = new Intent(Intent.ACTION_VIEW);
        String site = aluno.getSite();
        if (!site.startsWith("http://")){
            site = "http://" + site;
        }
        intenSize.setData(Uri.parse(site));
        itemSite.setIntent(intenSize);

        MenuItem deletar = menu.add("Deletar");
       deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
           @Override
           public boolean onMenuItemClick(MenuItem menuItem) {

               Call<Void> call = new RetrofitInicializador().getAlunoService().deleta(aluno.getId());
               call.enqueue(new Callback<Void>() {
                   @Override
                   public void onResponse(Call<Void> call, Response<Void> response) {
                       AlunoDAO dao = new AlunoDAO(ListaAlunosActivity.this);
                       dao.deleta(aluno);
                       dao.close();
                       CarregaLista();
                   }

                   @Override
                   public void onFailure(Call<Void> call, Throwable t) {
                       Toast.makeText(ListaAlunosActivity.this, "Falha ao deletar", Toast.LENGTH_SHORT).show();
                   }
               });
                return false;
           }
       });

    }
}
