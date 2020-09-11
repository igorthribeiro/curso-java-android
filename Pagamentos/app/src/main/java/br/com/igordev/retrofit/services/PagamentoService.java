package br.com.igordev.retrofit.services;

import java.util.List;

import br.com.igordev.dominio.Pagamento;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PagamentoService {

    @GET("get-pagamentos/{data}")
    Call<List<Pagamento>> buscaPagamentos(@Path("data") String data);


}
