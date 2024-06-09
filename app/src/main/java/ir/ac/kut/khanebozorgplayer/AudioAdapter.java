package ir.ac.kut.khanebozorgplayer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.MyViewHolder> {

    private Context aContext;
    private ArrayList<AudioFiles> aFiles;

    public AudioAdapter(Context aContext, ArrayList<AudioFiles> aFiles) {
        this.aContext = aContext;
        this.aFiles = aFiles;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(aContext).inflate(R.layout.audio_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.file_name.setText(aFiles.get(position).getTitle());
        byte[] image = new byte[0];
        try {
            image = getAlbumArt(aFiles.get(position).getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (image != null){
            Glide.with(aContext).asBitmap().load(image).into(holder.album_art);
        }
        else {
            Glide.with(aContext).load(R.drawable.defualt).into(holder.album_art);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(aContext, PlayerActivity.class);
                intent.putExtra("position", position);
                aContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return aFiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{


        TextView file_name;
        ImageView album_art;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.audio_filesname);
            album_art = itemView.findViewById(R.id.audio_image);
        }
    }

    private byte[] getAlbumArt(String uri) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return  art;
    }
}
