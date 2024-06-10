package ir.ac.kut.khanebozorgplayer;

import static ir.ac.kut.khanebozorgplayer.MainActivity.audioFiles;
import static ir.ac.kut.khanebozorgplayer.MainActivity.repat_bool;
import static ir.ac.kut.khanebozorgplayer.MainActivity.shuffle_bool;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


//TODO : Menu in player activity, back button action, delete in more option menu.
public class PlayerActivity extends AppCompatActivity {

    TextView song_name, artist_name, duration_played, duration_total;
    ImageView cover_art, back_button, shuffle_button, reapeat_button;
    FloatingActionButton play_button, next_botton, previos_button;
    SeekBar seekBar;
    int position = -1;
    static ArrayList<AudioFiles> listSongs = new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Thread playThread, prevThread, nextThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player);
        initViews();
        getIntentMethod();
        metaDate(uri);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {

                    int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(currentPosition);
                    duration_played.setText(formattedTime(currentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });
        shuffle_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shuffle_bool) {
                    shuffle_bool = false;
                    shuffle_button.setImageResource(R.drawable.icon_shuffle_off);
                } else {
                    shuffle_bool = true;
                    shuffle_button.setImageResource(R.drawable.icon_shuffle);
                }
            }
        });
        reapeat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repat_bool) {
                    repat_bool = false;
                    reapeat_button.setImageResource(R.drawable.icon_repeat_off);
                } else {
                    repat_bool = true;
                    reapeat_button.setImageResource(R.drawable.icon_repeat);
                }
            }
        });
    }


    @Override
    protected void onResume() {
        playThreadBotton();
        prevThreadBotton();
        nextThreadBotton();
        super.onResume();
    }

    private void nextThreadBotton() {
        nextThread = new Thread(){
            @Override
            public void run() {
                super.run();
                next_botton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBottonClick();
                    }
                });
            }
        };
        nextThread.start();
    }

    private void nextBottonClick() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if (shuffle_bool && !repat_bool) {
            position = (int) (Math.random() * listSongs.size());
        } else if (!shuffle_bool && !repat_bool) {
            if (position == listSongs.size() - 1) {
                position = 0;
            } else {
                position++;
            }
        }
        uri = Uri.parse(listSongs.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        metaDate(uri);
        seekBar.setMax(mediaPlayer.getDuration() / 1000);
        play_button.setImageResource(R.drawable.icon_pause);
    }

    private void prevThreadBotton() {
        prevThread = new Thread(){
            @Override
            public void run() {
                super.run();
                previos_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevBottonClick();
                    }
                });
            }
        };
        prevThread.start();
    }

    private void prevBottonClick() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if (shuffle_bool && !repat_bool) {
            position = (int) (Math.random() * listSongs.size());
        } else if (!shuffle_bool && !repat_bool) {
            if (position == 0) {
                position = listSongs.size() - 1;
            } else {
                position--;
            }
        }
        uri = Uri.parse(listSongs.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        metaDate(uri);
        seekBar.setMax(mediaPlayer.getDuration() / 1000);
        play_button.setImageResource(R.drawable.icon_pause);
    }

    private void playThreadBotton() {
        playThread = new Thread(){
            @Override
            public void run() {
                super.run();
                play_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseBotton();
                    }
                });
            }
        };
        playThread.start();
    }

    private void playPauseBotton() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            play_button.setImageResource(R.drawable.icon_play);
        } else {
            play_button.setImageResource(R.drawable.icon_pause);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
        }
    }

    private String formattedTime(int aCurrentPosition) {
        String totalout = "";
        String totalNew = "";
        String seconds = String.valueOf(aCurrentPosition % 60);
        String minutes = String.valueOf(aCurrentPosition / 60);
        totalout = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;
        if (seconds.length() == 1) {
            return totalNew;
        } else {
            return totalout;
        }
    }

    private void getIntentMethod() {
        position = getIntent().getIntExtra("position", -1);
        listSongs = audioFiles;
        if (listSongs != null) {
            play_button.setImageResource(R.drawable.icon_pause);
            uri = Uri.parse(listSongs.get(position).getPath());
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();

        }
        else {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }
        seekBar.setMax(mediaPlayer.getDuration() / 1000);
        metaDate(uri);

    }

    private void initViews() {
        song_name = findViewById(R.id.song_name);
        artist_name = findViewById(R.id.song_artist);
        cover_art = findViewById(R.id.cover_art);
        next_botton = findViewById(R.id.next);
        previos_button = findViewById(R.id.previous);
        back_button = findViewById(R.id.back_botton);
        shuffle_button = findViewById(R.id.shuffle);
        reapeat_button = findViewById(R.id.repeat);
        play_button = findViewById(R.id.play_puase);
        seekBar = findViewById(R.id.seek_bar);
        duration_played = findViewById(R.id.duration_played);
        duration_total = findViewById(R.id.duration);


    }

    private void metaDate(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal = Integer.parseInt(listSongs.get(position).getDuration());
        duration_total.setText(formattedTime(durationTotal / 1000));
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;

        RelativeLayout relativeLayout2 = findViewById(R.id.relative_layout_botton);
        if (art != null) {
            Glide.with(this).asBitmap().load(art).into(cover_art);
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if (swatch != null) {

                        // TODO : change background color of shuffle and reapet button, change play,next,prev button icon
                        RelativeLayout relativeLayout = findViewById(R.id.player_container);
                        GradientDrawable gradientDrawableBackGround = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), swatch.getRgb()});
                        relativeLayout.setBackground(gradientDrawableBackGround);
                        song_name.setTextColor(swatch.getBodyTextColor());
                        artist_name.setTextColor(swatch.getTitleTextColor());
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getBodyTextColor(), 0x00000000});
                        relativeLayout2.setBackgroundDrawable(gradientDrawable);
                        ColorStateList colorStateList = ColorStateList.valueOf(swatch.getRgb());
                        play_button.setBackgroundTintList(colorStateList);
                        next_botton.setBackgroundTintList(colorStateList);
                        previos_button.setBackgroundTintList(colorStateList);
                    }
                    else {
                        RelativeLayout relativeLayout = findViewById(R.id.player_container);
                        GradientDrawable gradientDrawableBackGround = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0xff000000, 0xff000000});
                        relativeLayout.setBackground(gradientDrawableBackGround);
                        song_name.setTextColor(getColor(R.color.text2));
                        artist_name.setTextColor(getColor(R.color.text3));
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getBodyTextColor(), 0x00000000});
                        relativeLayout2.setBackgroundDrawable(gradientDrawable);
                        ColorStateList colorStateList = ColorStateList.valueOf(swatch.getRgb());
                        play_button.setBackgroundTintList(colorStateList);
                        next_botton.setBackgroundTintList(colorStateList);
                        previos_button.setBackgroundTintList(colorStateList);
                    }
                }
            });
            } else {
            Glide.with(this).load(R.drawable.defualt).into(cover_art);
            RelativeLayout relativeLayout = findViewById(R.id.player_container);
            relativeLayout.setBackgroundColor(getColor(R.color.background1));
            song_name.setTextColor(getColor(R.color.text2));
            artist_name.setTextColor(getColor(R.color.text3));
            GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{getColor(R.color.background3), 0x00000000});
            relativeLayout2.setBackgroundDrawable(gradientDrawable);
            ColorStateList colorStateList = ColorStateList.valueOf(getColor(R.color.barColor1));
            play_button.setBackgroundTintList(colorStateList);
            next_botton.setBackgroundTintList(colorStateList);
            previos_button.setBackgroundTintList(colorStateList);
        }
        song_name.setText(listSongs.get(position).getTitle());
        artist_name.setText(listSongs.get(position).getArtist());
    }
}