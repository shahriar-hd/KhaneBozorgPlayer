package ir.ac.kut.khanebozorgplayer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
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
import java.util.Objects;

public class AlbumDetailesAdapter extends RecyclerView.Adapter<AlbumDetailesAdapter.MyHolder> {
    private Context aContext;
    private ArrayList<AudioFiles> albumList;
    View view;
    public AlbumDetailesAdapter(Context aContext, ArrayList<AudioFiles> albumList){
        this.aContext = aContext;
        this.albumList = albumList;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.albumName.setText(albumList.get(position).getTitle());
        byte[] image;
        try {
            image = getAlbumArt(albumList.get(position).getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (image != null){
            Glide.with(aContext).asBitmap().load(image).into(holder.albumImage);
        }
        else {
            Glide.with(aContext).load(R.drawable.defualt).into(holder.albumImage);
        }

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        ImageView albumImage;
        TextView albumName;


        public MyHolder(@NonNull View itemview){
            super(Objects.requireNonNull(itemview));
            albumImage = itemview.findViewById(R.id.audio_image);
            albumName = itemview.findViewById(R.id.audio_filesname);

        }
    }
    @NonNull
    @Override
    public AlbumDetailesAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(aContext).inflate(R.layout.audio_items, parent, false);
        return new AlbumDetailesAdapter.MyHolder(view);
    }

    private byte[] getAlbumArt(String uri) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return  art;
    }
}
