package com.example.igordev.financeiro;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.igordev.financeiro.model.Categoria;
import com.example.igordev.financeiro.util.FormatarPeriodo;
import com.example.igordev.financeiro.util.ListaCores;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    private PieChart pieChart;
    private ArrayList categorias;
    private FormatarPeriodo periodo;
    private String[] meses;
    private TextView textViewTotal;
    private TextView textViewCategoria;
    private ImageView imageViewIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        pieChart = (PieChart) findViewById(R.id.pieChart);

        meses = getResources().getStringArray(R.array.meses);

        pieChart.setEntryLabelTextSize(10f);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setNoDataText(getResources().getString(R.string.empty_graph));


        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);

        periodo = (FormatarPeriodo) getIntent().getSerializableExtra("periodo");
        categorias = getIntent().getParcelableArrayListExtra("categorias");

        if (categorias != null && categorias.size() > 0) {


            List<PieEntry> pieEntries = new ArrayList<>();
            for (Object c : categorias) {
                Categoria categoria = (Categoria) c;
                pieEntries.add(new PieEntry((float) categoria.getTotalCategoria(periodo),categoria.getNome()));
            }

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String dataInicial = df.format(periodo.getDataInicial().getTime());
            String dataFinal = df.format(periodo.getDataFinal().getTime());
            SpannableString spannableString = new SpannableString(
                    String.format(getResources().getString(R.string.center_graph),
                            meses[periodo.getDataInicial().get(Calendar.MONTH)],
                            dataInicial,
                            dataFinal)
            );

            final int inicioMes = 0;
            final int fimMes = spannableString.length() - 23;
            final int inicioData = spannableString.length() - 23;
            final int fimData = spannableString.length();

//formata Nome Mês
            spannableString.setSpan(new StyleSpan(Typeface.ITALIC), inicioMes, fimMes, 0);
            spannableString.setSpan(new ForegroundColorSpan(
                    ContextCompat.getColor(getActivity(), R.color.colorAccent)), inicioMes, fimMes, 0);

//Formata Data
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), inicioData, fimData, 0);
            spannableString.setSpan(new RelativeSizeSpan(.8f), inicioData, fimData, 0);
            spannableString.setSpan(new ForegroundColorSpan(
                    ContextCompat.getColor(getActivity(),  R.color.colorPrimary)), inicioData, fimData, 0);
            pieChart.setCenterText(spannableString);


            pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    atualizaCategoria((int)h.getX(), periodo);
                }

                @Override
                public void onNothingSelected() {

                }
            });

            PieDataSet pieDataSet = new PieDataSet(pieEntries, null);
            pieDataSet.setColors(ListaCores.getColors());
            pieDataSet.setSliceSpace(3f);
            pieDataSet.setSelectionShift(5f);


            Legend legend = pieChart.getLegend();
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            legend.setWordWrapEnabled(true);
            legend.setDrawInside(false);
            legend.setFormSize(10f);
            legend.setFormToTextSpace(2f);
            legend.setXEntrySpace(20f);
            legend.setYEntrySpace(0f);
            legend.setYOffset(0f);
            legend.setTextSize(12f);

            PieData pieData = new PieData(pieDataSet);
            pieData.setValueFormatter(new PercentFormatter());
            pieData.setValueTextSize(12f);
            pieChart.setData(pieData);
        }
        pieChart.invalidate();
        pieChart.animateY(getResources().getInteger(R.integer.velocidade_animacao_graph));
        atualizaCategoria(0, periodo);
    }

    private AppCompatActivity getActivity() {
        return this;
    }

    private void atualizaCategoria(int codigoCategoria, FormatarPeriodo periodo) {
        textViewTotal = (TextView) findViewById(R.id.textViewTotal);
        textViewCategoria = (TextView) findViewById(R.id.textViewCategoria);
        imageViewIcon = (ImageView) findViewById(R.id.imageViewIcon);

        if (categorias.size() > 0) {
            Categoria categoria = (Categoria) categorias.get(codigoCategoria);
            imageViewIcon.setImageResource(categoria.getImagem());
            textViewTotal.setText(DecimalFormat.getCurrencyInstance().format(categoria.getTotalCategoria(periodo)));
            char signal = (categoria.getTipoLancamento() == 'R' ? '+' : '-');
            textViewCategoria.setText(String.format(getResources().getString(R.string.total_categoria), signal + "$ " + categoria.getNome()));
        } else {
            textViewTotal.setText(null);
            textViewCategoria.setText(null);
            imageViewIcon.setImageDrawable(null);
        }
    }
}
