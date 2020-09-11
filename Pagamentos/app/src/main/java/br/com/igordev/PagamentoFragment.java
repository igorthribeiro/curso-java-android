package br.com.igordev;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.List;

import br.com.igordev.adapter.ListaPagamentoAdapter;
import br.com.igordev.dominio.Pagamento;
import br.com.igordev.retrofit.RetrofitConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PagamentoFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pagamentos, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.fabHoje).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listaPagamento("todos");
            }
        });

        view.findViewById(R.id.fabTodos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listaPagamento("hoje");
            }
        });



    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void listaPagamento(String data) {
        final Call<List<Pagamento>> pagamentoCall = new RetrofitConfig().getPagamentoService().buscaPagamentos(data);
        pagamentoCall.enqueue(new Callback<List<Pagamento>>() {
            @Override
            public void onResponse(Call<List<Pagamento>> call, Response<List<Pagamento>> response) {
                List<Pagamento> pagamentos = response.body();
                ListView listViewPagamentos = getActivity().findViewById(R.id.listViewPagamentos);
                listViewPagamentos.setAdapter(new ListaPagamentoAdapter(getActivity(), pagamentos));
            }

            @Override
            public void onFailure(Call<List<Pagamento>> call, Throwable t) {
                Log.e(getClass().getSimpleName(), "Erro no serviço de pagamentos: " + t.getMessage());
                Toast.makeText(getActivity(), R.string.erro_servico, Toast.LENGTH_LONG).show();
            }
        });

    }


}