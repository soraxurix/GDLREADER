package com.example.gdlreader;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyviewHolder> {

    Context context;
    ArrayList<Libro> list;

    public MyAdapter(Context context, ArrayList<Libro> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_libro,parent, false);
        return  new MyviewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {

        Libro libro = list.get(position);
        holder.TituloLibro.setText(libro.getTitulo());
        holder.AutorLibro.setText(libro.getAutor());
        holder.GeneroLibro.setText(libro.getGenero());
        Glide.with(holder.imagen.getContext()).load(libro.getUrl()).into(holder.imagen);
        String Libroid =String.valueOf(libro.getNoLibro()) ;
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), InformacionLibro.class);
                intent.putExtra("keyLibro",Libroid);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyviewHolder extends  RecyclerView.ViewHolder{
        TextView TituloLibro, AutorLibro, GeneroLibro;
        ImageView imagen;
        public CardView cardView;
        public MyviewHolder(@NonNull View itemView) {
            super(itemView);
            TituloLibro = itemView.findViewById(R.id.textviewTituloLibro);
            AutorLibro = itemView.findViewById(R.id.textviewAutorLibro);
            GeneroLibro = itemView.findViewById(R.id.textviewGeneroLibro);
            imagen = itemView.findViewById(R.id.imageviewPortadaLibro);
            cardView =itemView.findViewById(R.id.cardview);
        }
    }

   /* int getId(int position){
        return list.get(position).ID;
    }*/
}
