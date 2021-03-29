package agenda.igordev.com.br.agenda.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DialogDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {


    private EditText editText;


    public void setEditText(EditText editText) {
        this.editText = editText;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        return new DatePickerDialog(getActivity(), this,
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, dayOfMonth, 0, 0);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        editText.setText(df.format(c.getTime()));
    }
}
