package ir.ac.kut.khanebozorgplayer;

import android.content.Context;
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

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyHolder> {
    private Context aContext;
    private ArrayList<AudioFiles> albumList;
    View view;
    public AlbumAdapter(Context aContext, ArrayList<AudioFiles> albumList){
        this.aContext = aContext;
        this.albumList = albumList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(aContext).inflate(R.layout.album_items, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.albumName.setText(albumList.get(position).getAlbum());
        byte[] image = new byte[0];
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
            albumImage = itemview.findViewById(R.id.album_image);
            albumName = itemview.findViewById(R.id.album_name);
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
