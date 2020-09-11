package br.com.igordev.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;

import br.com.igordev.retrofit.services.PagamentoService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConfig {

    private final Retrofit retrofit;

    public RetrofitConfig() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new DateDeserializer())
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.33:8080/pagamento-service/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public PagamentoService getPagamentoService() {
        return this.retrofit.create(PagamentoService.class);
    }


}
