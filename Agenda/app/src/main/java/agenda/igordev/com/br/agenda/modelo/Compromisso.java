package agenda.igordev.com.br.agenda.modelo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import br.com.igordev.eminjectlib.dao.Entity;

@DatabaseTable
public class Compromisso implements Entity {

    @DatabaseField(generatedId = true)  private Long id;
    @DatabaseField private String textoCurso;
    @DatabaseField private String textoLongo;
    @DatabaseField private Date data;
    @DatabaseField private String hora;

    /*

    - Chamar um datePicker - Agendar data alarme;
    - EditText com a hora do alarme;
    - Agendar o alarme ao salvar (ALARM_SERVICE)
    - No onReceiver do BroadCast, enviar o SMS (texto curto)

    */

    public Compromisso() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTextoCurso() {
        return textoCurso;
    }

    public void setTextoCurso(String textoCurso) {
        this.textoCurso = textoCurso;
    }

    public String getTextoLongo() {
        return textoLongo;
    }

    public void setTextoLongo(String textoLongo) {
        this.textoLongo = textoLongo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
