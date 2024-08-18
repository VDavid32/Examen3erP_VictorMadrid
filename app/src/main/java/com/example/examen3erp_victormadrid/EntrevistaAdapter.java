package com.example.examen3erp_victormadrid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.examen3erp_victormadrid.Entrevista;
import com.example.examen3erp_victormadrid.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class EntrevistaAdapter extends RecyclerView.Adapter<EntrevistaAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Entrevista> arrayList;
    private OnItemClickListener onItemClickListener;
    private MediaPlayer mediaPlayer;

    public EntrevistaAdapter(Context context, ArrayList<Entrevista> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.entrevistas_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.descripcion.setText(arrayList.get(position).getDescripcion());
        holder.periodista.setText(arrayList.get(position).getPeriodista());
        holder.fecha.setText(arrayList.get(position).getFecha().toString());

        // Cargar imagen en el ImageView
        byte[] imagenBytes = arrayList.get(position).getImagen();
        Bitmap imagenBitmap = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);
        holder.imagen.setImageBitmap(imagenBitmap);

        holder.audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reproducir el audio aquí
                byte[] audioBytes = arrayList.get(position).getAudio();
                File tempFile = new File(context.getCacheDir(), "temp_audio.mp3");
                try {
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    fos.write(audioBytes);
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(tempFile.getAbsolutePath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(arrayList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView descripcion, periodista, fecha;
        ImageView imagen;
        ImageButton audio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            descripcion = itemView.findViewById(R.id.list_item_descripcion);
            periodista = itemView.findViewById(R.id.list_item_periodista);
            fecha = itemView.findViewById(R.id.list_item_fecha);
            imagen = itemView.findViewById(R.id.list_item_imagen);
            audio = itemView.findViewById(R.id.list_item_audio);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(Entrevista entrevista);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}