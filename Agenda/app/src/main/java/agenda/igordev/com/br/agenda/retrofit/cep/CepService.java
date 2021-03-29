package agenda.igordev.com.br.agenda.retrofit.cep;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CepService {
    @GET("{cep}/json")
    Call<Cep> buscaCep(@Path("cep") String cep);
}
