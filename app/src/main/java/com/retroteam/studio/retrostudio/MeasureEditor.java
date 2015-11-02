package com.retroteam.studio.retrostudio;

import com.retroteam.studio.retrostudio.MainActivity;
import com.retroteam.studio.retrostudio.NavigationDrawerFragment;
import com.retroteam.studio.util.SystemUiHider;

import android.annotation.TargetApi;

import android.app.ActionBar;
import android.app.Activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;

import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;
import hollowsoft.slidingdrawer.SlidingDrawer;


public class MeasureEditor extends Activity {


    // note data structure will go here.
    public String[] notes = {"A", "B", "C", "D", "E", "F", "G", "A", "B", "C", "D", "E", "F", "G"};

    private boolean pencil = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the note positions from the intent
        Intent intent = getIntent();
        String message = intent.getStringExtra(EditorLandscape.MEASURE_INFO);
        String titlebar = intent.getStringExtra(EditorLandscape.MEASURE_TITLE);

        setContentView(R.layout.activity_trackedit);
        setupActionBar(titlebar); //cannot use this if we have no action bar

        //final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        //toggleHideyBar(); //start immersive mode



        // show a toast with the info
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

//        //dynamically fill up the first row of every line with notes
//        LinearLayout notecontainer = (LinearLayout) findViewById(R.id.notecontainer);
//        //HorizontalScrollView notecontainer = (HorizontalScrollView) findViewById(R.id.content);
//
//        for (int i = 0; i < notes.length; i++) {
//            TextView note = new TextView(getApplicationContext());
//            // use non-deprecated function if 5.0 or above
//            note.setId(i);
//            note.setGravity(1);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                note.setBackground(getDrawable(R.drawable.note_icon_small_alphad));
//            }else {
//                Resources resources = getResources();
//                note.setBackground(resources.getDrawable(R.drawable.note_icon_small_alphad));
//            }
//            note.setText(notes[i]);
//            note.setTextSize(50);
//            note.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    //our implementation of "tap this to play the noise it makes goes here"
//                    //this.image1.playSound();
//                    MediaPlayer mp = MediaPlayer.create(EditorLandscape.this, R.raw.beep);
//                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//
//                        @Override
//                        public void onCompletion(MediaPlayer mp) {
//                            mp.release();
//                        }
//
//                    });
//                    mp.start();
//                }
//            });
//            notecontainer.addView(note);
//        }
    }


    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar(String title) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            ActionBar measure = getActionBar();
            measure.setDisplayHomeAsUpEnabled(true);
            measure.setTitle("Editing Measure " + title);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.measure_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // make this a switch instead
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            // TODO: If Settings has multiple levels, Up should navigate up
            // that hierarchy.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        if (id == R.id.action_penciloff) {
            Toast.makeText(this, "Tell Gabe to implement this!", Toast.LENGTH_SHORT).show();

        }

        if (id == R.id.action_togglefs) {
            toggleHideyBar();
        }

        //toggleHideyBar(); // TODO: something better than this for toggling immersive mode back. For some reason, adding a overflow menu into the editor actionbar and clicking it makes the immersive mode turn off.
        return super.onOptionsItemSelected(item);
    }

    public void toggleHideyBar() {

        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = this.getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i("EditorLandscape", "Turning immersive mode mode off. ");
        } else {
            Log.i("EditorLandscape", "Turning immersive mode mode on.");
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        this.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }


    public void togglePencilTool(View view) {

        com.getbase.floatingactionbutton.FloatingActionButton ptool = (com.getbase.floatingactionbutton.FloatingActionButton) view;

        if(pencil) {
            pencil = false;
            ptool.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_on)));
        } else {
            pencil = true;
            ptool.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_off)));
        }
    }

    public void paintNote(View view) {
        com.getbase.floatingactionbutton.FloatingActionButton ptool = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.pencilTool);
        ImageView iview = (ImageView) view;
        Drawable notestatus = iview.getDrawable();
        if ( notestatus.getConstantState().equals(getResources().getDrawable(R.drawable.note_filled, getTheme()).getConstantState()) ) {
            if (pencil){
                iview.setImageDrawable(getResources().getDrawable(R.drawable.measure_outline, getTheme()));
            }
        } else {
            if (pencil) {
                iview.setImageDrawable(getResources().getDrawable(R.drawable.note_filled, getTheme()));
            }
        }
    }
}
