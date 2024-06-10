package ir.ac.kut.khanebozorgplayer;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
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
        holder.menu_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(aContext, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            //TODO Fix this shit...
                            /*case R.id.delete:
                                deleteFile(position, v);
                                break;*/
                            default:
                                break;
                        }
                        return false;
                    }
                });
            }
        });
    }

    private void deleteFile(int position, View v) {
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(aFiles.get(position).getID()));
        File file = new File(aFiles.get(position).getPath());
        boolean deleted = file.delete();
        if (deleted) {
            aFiles.remove(position);
            aContext.getContentResolver().delete(contentUri, null, null);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, aFiles.size());
            Snackbar.make(v, "Item Delete Was Successful", Snackbar.LENGTH_LONG).show();
        }
        else {
            Snackbar.make(v, "Item Delete Was Not Successful", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return aFiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{


        TextView file_name;
        ImageView album_art, menu_more;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.audio_filesname);
            album_art = itemView.findViewById(R.id.audio_image);
            menu_more = itemView.findViewById(R.id.menu_more);
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
