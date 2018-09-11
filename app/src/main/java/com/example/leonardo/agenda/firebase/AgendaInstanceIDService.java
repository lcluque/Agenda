package com.example.leonardo.agenda.firebase;

import android.util.Log;

import com.example.leonardo.agenda.retrofit.RetrofitInicializador;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Leonardo on 27/02/2018.
 */

public class AgendaInstanceIDService extends FirebaseInstanceIdService{

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token firebase", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        enviaTokenParaServidor(refreshedToken);
    }

    private void enviaTokenParaServidor(final String token) {

        Call<Void> call = new RetrofitInicializador().getDispositivoService().enviaToken(token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i("Token enviado", token);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Token falhou",t.getMessage());
            }
        });

    }


}
