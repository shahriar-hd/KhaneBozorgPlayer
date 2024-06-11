package ir.ac.kut.khanebozorgplayer;

import static ir.ac.kut.khanebozorgplayer.MainActivity.audioFiles;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;

public class AlbumDetails extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView albumArt, backButtonAlbumDetails;
    TextView albumeNameBar;
    String albumName;
    ArrayList<AudioFiles> albumSongs = new ArrayList<>();
    AlbumDetailesAdapter albumDetailesAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        if (!albumSongs.isEmpty()){
            albumDetailesAdapter = new AlbumDetailesAdapter(this, albumSongs);
            recyclerView.setAdapter(albumDetailesAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        recyclerView = findViewById(R.id.album_details_recycler_view);
        albumArt = findViewById(R.id.album_art);
        albumName = getIntent().getStringExtra("albumName");
        albumeNameBar = findViewById(R.id.albumname);
        albumeNameBar.setText(albumName);
        backButtonAlbumDetails = findViewById(R.id.back_botton_album_details);
        int j = 0;
        for (int i = 0; i < audioFiles.size(); i++) {
            if (audioFiles.get(i).getAlbum() == null)
                i++;
            else if (albumName.equals(audioFiles.get(i).getAlbum())) {
                albumSongs.add(j, audioFiles.get(i));
                j++;
            }
        }
        if (!albumSongs.isEmpty()) {

            byte[] image;
            try {
                image = getAlbumArt(albumSongs.get(0).getPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (image != null) {
                Glide.with(this).load(image).into(albumArt);
            } else {
                Glide.with(this).load(R.drawable.defualt).into(albumArt);
            }
        }
        backButtonAlbumDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlbumDetails.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private byte[] getAlbumArt(String uri) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return  art;
    }
}