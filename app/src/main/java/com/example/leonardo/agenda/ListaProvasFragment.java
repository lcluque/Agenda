package com.example.leonardo.agenda;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.leonardo.agenda.modelo.Prova;

import java.util.Arrays;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Leonardo on 29/01/2018.
 */

public class ListaProvasFragment extends android.support.v4.app.Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_provas,container,false);

        ListView lista = (ListView) view.findViewById(R.id.provas_lista);

        List<String> topicosPort = Arrays.asList("Sujeito","Objeto direto","Objeto indireto");
        Prova provaPortugues = new Prova("Portugues","25/01/2018",topicosPort);

        List<String> topicosMat = Arrays.asList("Equacoes de segundo grau","Trigonometria");
        Prova provaMatematica = new Prova ("Matematica","27/01/2018",topicosMat);

        List<Prova> provas = Arrays.asList(provaPortugues,provaMatematica);

        ArrayAdapter<Prova> adapter = new ArrayAdapter<Prova>(getContext(), android.R.layout.simple_list_item_1,provas);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Prova prova = (Prova) parent.getItemAtPosition(position);
                Toast.makeText(getContext(), "Clicou na prova de " + prova, Toast.LENGTH_SHORT).show();
                ProvasActivity provasActivity = (ProvasActivity) getActivity();
                provasActivity.selecionaProva(prova);
            }
        });

        return view;
    }
}
