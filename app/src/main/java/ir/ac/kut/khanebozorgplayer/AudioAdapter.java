package ir.ac.kut.khanebozorgplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
}
