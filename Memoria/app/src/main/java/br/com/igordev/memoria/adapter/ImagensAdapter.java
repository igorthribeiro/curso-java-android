package br.com.igordev.memoria.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import br.com.igordev.memoria.R;


public class ImagensAdapter extends BaseAdapter {

    //Contexto
    private Context context;
    //Vetor de imagens
    private List<String> imagens;
    //gerenciador de ativos (assets) do projeto
    AssetManager assetManager;


    public ImagensAdapter(Context context, List<String> imagens) {
        this.context = context;
        this.imagens = imagens;
        this.assetManager = context.getAssets();
    }

    @Override
    public int getCount() {
        return imagens.size();
    }

    @Override
    public Object getItem(int position) {
        return imagens.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //inflar a view criada no passo anterior
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_imagens, parent, false);
        //localiza o componente da imagem dentro da view
        ImageView imageView = view.findViewById(R.id.imageView);

        try {
            //abrir o arquivo da imagem a partir da nome
            InputStream stream = assetManager.open("img/" + imagens.get(position));
            Drawable imagem = Drawable.createFromStream(stream, imagens.get(position));
            //modifica dinamicamente a imagem contida no adapter
            imageView.setImageDrawable(imagem);
            //vamos fazer um backup do nome imagem na propriedade TAG do imageView
            //isso será necessário para podermos esconder e reexibir a imagem
            imageView.setTag(imagens.get(position));

            //extra: acessibilidade
            imageView.setContentDescription(context.getResources().getString(R.string.wildcard));
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Erro ao recupear imagens: " + e.getMessage());
        }

        return view;
    }
}
