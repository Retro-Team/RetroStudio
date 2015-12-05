/*
 * Retro Studio Copyright 2015 Retro Team
 * This file is part of Retro Studio.
 *
 * Retro Studio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Retro Studio is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Retro Studio.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.retroteam.studio.retrostudio;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import android.annotation.TargetApi;

import android.app.ActionBar;
import android.app.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;

import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.retroteam.studio.retrostudio.pcm.*;

public class EditorLandscape extends Activity {

    /**
     * The info that gets passed into the MeasureEditor activity.
     */
    public final static String MEASURE_INFO = "com.retroteam.studio.MEASUREINFO";

    public final static String MEASURE_TITLE = "com.retroteam.studio.MEASURETITLE";

    public final static String MEASURE = "com.retroteam.studio.MEASURE";

    public final static String MEASURE_TRACK = "com.retroteam.studio.MEASURE_TRACK";

    private double PROJECT_TEMPO = 60.0;

    private String PROJECT_TITLE = "";

    private int TS_BEATS = 4;

    private int TS_NOTES = 4;

    private Project theproject = new Project(new TimeSignature(TS_BEATS, TS_NOTES));

    private Point displaysize = new Point();

    private ThreadedPlayback audioplayer;

    private int trackReloadCounter = 0;

    private Menu actionMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_editor_landscape);

        //get display size
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(displaysize);

        //check where we're coming from, if we have an intent
        if (getIntent().getStringExtra("SourceActivity") != null && getIntent() != null) {
            Intent intent = getIntent();
            if (intent.getStringExtra("SourceActivity").equals("MainActivity")) {
                ProjectWrapper projwrapper = (ProjectWrapper) intent.getSerializableExtra("project");
                if(projwrapper != null){
                    // We are loading the project from a file.
                    PROJECT_TITLE = projwrapper.title;
                    PROJECT_TEMPO = projwrapper.tempo;
                    TS_BEATS = projwrapper.tSig.beats();
                    TS_NOTES = projwrapper.tSig.notes();
                    theproject = projwrapper.project;
                    ArrayList<MeasureTagGroup> mTagList = projwrapper.mTagList;
                    //redraw project
                    redrawMe(theproject);
                    //re-apply measure tags
                    for(int m = 0; m < mTagList.size(); m++){
                        ImageView measure = (ImageView) findMeasureByCoords(mTagList.get(m).row, mTagList.get(m).column);
                        measure.setTag(R.id.TAG_ROW, mTagList.get(m).row);
                        measure.setTag(R.id.TAG_COLUMN, mTagList.get(m).column);
                        measure.setTag(R.bool.TAG_HASNOTES, mTagList.get(m).hasNotes);
                        measure.setTag(R.id.TAG_GUISNAP, mTagList.get(m).guiSNAP);
                        measure.setTag(R.id.TAG_FILLED_NOTES, mTagList.get(m).filledNotes);
                        if(mTagList.get(m).hasNotes){
                            measure.setImageResource(R.drawable.measure_new_filled);
                        }
                    }
                    Toast.makeText(this, "Opened project "+PROJECT_TITLE, Toast.LENGTH_SHORT).show();
                }else {
                    // We're making a new project or coming from the main menu
                    Toast.makeText(this, "Created new project.", Toast.LENGTH_SHORT).show();
                    TS_BEATS = intent.getIntExtra("TimeSigBeats", 4);
                    TS_NOTES = intent.getIntExtra("TimeSigNotes", 4);
                    theproject = new Project(new TimeSignature(TS_BEATS, TS_NOTES));
                }
            }
        }

        // Put the project title in the actionbar.
        if(PROJECT_TITLE != ""){
            setupActionBar("'"+PROJECT_TITLE+"' Project Editor (" + Integer.toString(TS_BEATS) + "/" + Integer.toString(TS_NOTES) + " Time)");
        }else{
            setupActionBar("Project Editor (" + Integer.toString(TS_BEATS) + "/" + Integer.toString(TS_NOTES) + " Time)");
        }

        //add buttons to the floating action menu
        FloatingActionsMenu menuAddTrack = (FloatingActionsMenu) findViewById(R.id.add_track_menu);
        FloatingActionButton addSineTrack = new FloatingActionButton(getBaseContext());
        addSineTrack.setTitle("Add Sine Wave Track");
        addSineTrack.setIcon(R.drawable.sine_wave);
        addSineTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SineWave wave = new SineWave();
                theproject.addTrack(wave);
                addTrack(R.drawable.sine_wave, false);
            }
        });
        FloatingActionButton addSquareTrack = new FloatingActionButton(getBaseContext());
        addSquareTrack.setTitle("Add Square Wave Track");
        addSquareTrack.setIcon(R.drawable.square_wave);
        addSquareTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SquareWave wave = new SquareWave();
                theproject.addTrack(wave);
                addTrack(R.drawable.square_wave, false);
            }
        });
        FloatingActionButton addNoiseTrack = new FloatingActionButton(getBaseContext());
        addNoiseTrack.setTitle("Add Noise Wave Track");
        addNoiseTrack.setIcon(R.drawable.noise_wave);
        addNoiseTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoiseWave wave = new NoiseWave();
                theproject.addTrack(wave);
                addTrack(R.drawable.noise_wave, false);
            }
        });

        FloatingActionButton addSawTrack = new FloatingActionButton(getBaseContext());
        addSawTrack.setTitle("Add Saw Wave Track");
        addSawTrack.setIcon(R.drawable.saw_wave);
        addSawTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SawtoothWave wave = new SawtoothWave();
                theproject.addTrack(wave);
                addTrack(R.drawable.saw_wave, false);
            }
        });

        FloatingActionButton addInvSawTrack = new FloatingActionButton(getBaseContext());
        addInvSawTrack.setTitle("Add Inverse Saw Wave Track");
        addInvSawTrack.setIcon(R.drawable.inv_saw_wave);
        addInvSawTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InverseSawtoothWave wave = new InverseSawtoothWave();
                theproject.addTrack(wave);
                addTrack(R.drawable.inv_saw_wave, false);
            }
        });

        FloatingActionButton addTriangleTrack = new FloatingActionButton(getBaseContext());
        addTriangleTrack.setTitle("Add Triangle Wave Track");
        addTriangleTrack.setIcon(R.drawable.triangle_wave);
        addTriangleTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TriangleWave wave = new TriangleWave();
                theproject.addTrack(wave);
                addTrack(R.drawable.triangle_wave, false);
            }
        });

        menuAddTrack.addButton(addSineTrack);
        menuAddTrack.addButton(addSquareTrack);
        menuAddTrack.addButton(addNoiseTrack);
        menuAddTrack.addButton(addSawTrack);
        menuAddTrack.addButton(addInvSawTrack);
        menuAddTrack.addButton(addTriangleTrack);

        // Set the project tempo.
        theproject.tempo(PROJECT_TEMPO);
    }


    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar(String title) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            ActionBar editorbar = getActionBar();
            editorbar.setDisplayHomeAsUpEnabled(true);
            editorbar.setTitle(title);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editor_activity_actions, menu);
        actionMenu = menu; //save menu reference to modify it later
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        if (id == R.id.action_play) {
            if(!(theproject.size() == 0) && theproject.track(0).measure(0) != null) {


                theproject.tempo(PROJECT_TEMPO);

                new Thread(new Runnable() {
                    public void run() {
                        ThreadedPlayback audioplayer2 = new ThreadedPlayback(theproject.export());
                        audioplayer = audioplayer2;
                        audioplayer.play();
                    }
                }).start();
                Toast.makeText(this, "Playing...", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Add a track and measure first!", Toast.LENGTH_SHORT).show();
            }
        }

        if (id == R.id.action_exportaswav) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();

            final View save_dialog = inflater.inflate(R.layout.save_dialog, null);
            final EditText sd = (EditText) save_dialog.findViewById(R.id.projectTitleField);
            sd.setText(PROJECT_TITLE);

            builder.setTitle("Export as Unsigned 8-bit WAV")
                    .setMessage("Enter the filename.")
                    .setView(save_dialog)
                    .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            exportAsWav(theproject, sd.getText().toString());

                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog dialog = builder.create();

            dialog.show();

        }

        if (id == R.id.action_togglefs) {
            toggleHideyBar();
        }

        if (id == R.id.action_tempo){
            tempoPrompt();
        }

        if (id == R.id.action_save){
            if(PROJECT_TITLE == ""){
                saveProject();
            }else {
                saveProject(PROJECT_TITLE);
            }
        }

        if (id == R.id.action_saveas){
            saveProject();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * How to handle a result when we come back from the measure.
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 123) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                //Uri thedata = data.getData();
                theproject = (Project) data.getSerializableExtra("Project");
                //redrawMe(theproject);
                updateMe(data.getBooleanExtra("HasNotes", false), data.getIntExtra("trackNum", 0), data.getIntExtra("measureNum", 0), data.getIntExtra("guiSNAP", 0), (ArrayList<int[]>) data.getSerializableExtra("filledNotes"));
            }
        }
    }

    /**
     * Write the 8bit PCM audio to the wav file format to the public music dir.
     * http://www-mmsp.ece.mcgill.ca/Documents/AudioFormats/WAVE/WAVE.html
     * @param proj
     * @param filename
     */

    private void exportAsWav(Project proj, String filename){
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), filename + ".wav");
            DataOutputStream os = new DataOutputStream(new FileOutputStream(file));

            // get project length
            int pLength = proj.export().getBytes().length;

            os.writeBytes("RIFF"); // Chunk ID 'RIFF'
            os.write(intToByteArray(36+pLength), 0, 4); //file size
            os.writeBytes("WAVE"); // Wave ID
            os.writeBytes("fmt "); // Chunk ID 'fmt '
            os.write(intToByteArray(16), 0, 4); // chunk size
            os.write(intToByteArray(1), 0, 2);  // pcm audio
            os.write(intToByteArray(1), 0, 2);  // mono
            os.write(intToByteArray(8000), 0, 4); // samples/sec
            os.write(intToByteArray(pLength), 0, 4); // bytes/sec
            os.write(intToByteArray(1), 0, 2);    // 1 byte/sample
            os.write(intToByteArray(8), 0, 2); // 8 bits/sample
            os.writeBytes("data"); // Chunk ID 'data'
            os.write(intToByteArray(pLength), 0, 4); // size of data chunk
            os.write(proj.export().getBytes()); // write the data
            os.close();
            Toast.makeText(this, "Saved file to: "+file.getPath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "IOException while saving project.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Convert int to a byte array for making the wav header.
     * @param i
     * @return
     */

    private byte[] intToByteArray(int i)
    {
        byte[] b = new byte[4];
        b[0] = (byte) (i & 0x00FF);
        b[1] = (byte) ((i >> 8) & 0x000000FF);
        b[2] = (byte) ((i >> 16) & 0x000000FF);
        b[3] = (byte) ((i >> 24) & 0x000000FF);
        return b;
    }

    /**
     * Save the project with a title.
     * @param title
     */

    private void saveProject(String title){
        String fTitle = title + ".retro";
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(fTitle, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(new ProjectWrapper(theproject, new TimeSignature(TS_BEATS, TS_NOTES), PROJECT_TITLE, PROJECT_TEMPO, getMeasureTags()));
            os.close();
            fos.close();
            Toast.makeText(this, "Saved as "+fTitle, Toast.LENGTH_SHORT).show();
        }catch(IOException e){
            Toast.makeText(this, "IOException while saving project.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Prompt the user for a title and call saveProject(title).
     */

    private void saveProject(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        final View save_dialog = inflater.inflate(R.layout.save_dialog, null);
        final EditText sd = (EditText) save_dialog.findViewById(R.id.projectTitleField);
        sd.setText(PROJECT_TITLE);

        builder.setTitle("Project Name")
                .setView(save_dialog)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String title = sd.getText().toString();
                        PROJECT_TITLE = title;
                        saveProject(PROJECT_TITLE);

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();

        dialog.show();

    }

    /**
     * Iterate through all the measures and return a list of measure tag group objects.
     * The list is serialized out to the .retro project file.
     * @return ArrayList<MeasureTagGroup>
     */

    private ArrayList<MeasureTagGroup> getMeasureTags(){

        ArrayList<MeasureTagGroup> mtaglist = new ArrayList<>();

        LinearLayout track_layout = (LinearLayout) findViewById(R.id.track_layout);

        //iterate through all the measures
        for(int t = 0; t < track_layout.getChildCount(); t++){
            //get hscrollview
            HorizontalScrollView hchild = (HorizontalScrollView) track_layout.getChildAt(t);
            for(int h = 0; h < hchild.getChildCount(); h++){
                //get gridlayout
                GridLayout gchild = (GridLayout) hchild.getChildAt(h);
                for(int g = 0; g < gchild.getChildCount(); g++){
                    //get measure
                    if(gchild.getChildAt(g) instanceof ImageView){
                        ImageView ivchild = (ImageView) gchild.getChildAt(g);
                        Drawable measureFill = ivchild.getDrawable();
                        if (measureFill.getConstantState().equals(ContextCompat.getDrawable(getApplicationContext(), R.drawable.measure_new_empty).getConstantState())){
                            mtaglist.add(new MeasureTagGroup((int) ivchild.getTag(R.id.TAG_ROW),
                                    (int) ivchild.getTag(R.id.TAG_COLUMN), false,
                                    (int) ivchild.getTag(R.id.TAG_GUISNAP), (ArrayList<int[]>) ivchild.getTag(R.id.TAG_FILLED_NOTES)));
                        }else if(measureFill.getConstantState().equals(ContextCompat.getDrawable(getApplicationContext(), R.drawable.measure_new_filled).getConstantState())){
                            mtaglist.add(new MeasureTagGroup((int) ivchild.getTag(R.id.TAG_ROW),
                                    (int) ivchild.getTag(R.id.TAG_COLUMN), true,
                                    (int) ivchild.getTag(R.id.TAG_GUISNAP), (ArrayList<int[]>) ivchild.getTag(R.id.TAG_FILLED_NOTES)));
                        }
                    }

                }
            }
        }

        return mtaglist;
    }

    /**
     * Show the user the tempo setting alert dialog.
     */

    private void tempoPrompt(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        final View tempo_picker = inflater.inflate(R.layout.tempo_picker, null);
        final EditText tv = (EditText) tempo_picker.findViewById(R.id.tempoPickerField);
        tv.setText(Double.toString(PROJECT_TEMPO));

        builder.setTitle("Set Project Tempo")
                .setMessage("Type a value.")
                .setView(tempo_picker)
                .setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String et = tv.getText().toString();
                        PROJECT_TEMPO = Double.parseDouble(et);
                        theproject.tempo(PROJECT_TEMPO);

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    /**
     * Redraw the project when loading from a file.
     * @param p (Project)
     */

    private void redrawMe(Project p) {
        int numtracks = p.size();
        ImageView t = null;
        for (int i = 0; i < numtracks; i++) {
            SoundWave tracktype = p.track(i).soundWave();
            if(tracktype instanceof SineWave){
                t = addTrack(R.drawable.sine_wave, true);
            }else if(tracktype instanceof SquareWave){
                t = addTrack(R.drawable.square_wave, true);
            }else if(tracktype instanceof NoiseWave){
                t = addTrack(R.drawable.noise_wave, true);
            }else if(tracktype instanceof TriangleWave){
                t = addTrack(R.drawable.triangle_wave, true);
            }else if(tracktype instanceof SawtoothWave) {
                t = addTrack(R.drawable.saw_wave, true);
            }else if(tracktype instanceof InverseSawtoothWave) {
                t = addTrack(R.drawable.inv_saw_wave, true);
            }
            int nummeasures = p.track(i).size();
            for (int m = 0; m < nummeasures; m++) {
                addMeasure(t, true);
            }
        }
    }

    /**
     * Toggle Immersive Mode for more room on small devices.
     */

    private void toggleHideyBar() {

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

    /**
     * Start MeasureEditor with an intent with all the data.
     * @param view
     */

    private void editMeasure(View view) {

        Intent intent = new Intent(this, MeasureEditor.class);

        intent.putExtra("Project", theproject);
        ImageView viewi = (ImageView) view;
        intent.putExtra(MEASURE_TRACK, (int) viewi.getTag(R.id.TAG_ROW));
        intent.putExtra(MEASURE, (int) viewi.getTag(R.id.TAG_COLUMN));
        intent.putExtra(MEASURE_TITLE, Integer.toString((int) viewi.getTag(R.id.TAG_COLUMN)) + "," + Integer.toString((int) viewi.getTag(R.id.TAG_ROW)));
        intent.putExtra("measureID", viewi.getId());
        intent.putExtra("SourceActivity", "EditorLandscape");
        intent.putExtra("guiSNAP", (int) viewi.getTag(R.id.TAG_GUISNAP));
        intent.putExtra("filledNotes", (ArrayList<int[]>) viewi.getTag(R.id.TAG_FILLED_NOTES));
        intent.putExtra("tsBeats", TS_BEATS);
        intent.putExtra("tsNotes", TS_NOTES);
        startActivityForResult(intent, 123);
    }

    /**
     * Process information returned from the MeasureEditor.
     * @param hasNotes
     * @param row
     * @param column
     * @param guiSNAP
     * @param filledNotes
     */

    private void updateMe(boolean hasNotes, int row, int column, int guiSNAP, ArrayList<int[]> filledNotes){
        //mark the views that contain notes and remember the guiSNAP
        ImageView clickedmeasure = (ImageView) findMeasureByCoords(row, column);
        if(hasNotes) {
            clickedmeasure.setImageResource(R.drawable.measure_new_filled);
        }
        clickedmeasure.setTag(R.id.TAG_GUISNAP, guiSNAP);
        clickedmeasure.setTag(R.id.TAG_FILLED_NOTES, filledNotes);
    }

    /**
     * Create all the views associated with a track.
     * @param waveDrawableID
     * @param projectLoad
     * @return
     */

    private ImageView addTrack(int waveDrawableID, boolean projectLoad) {
        //add the track with the measure adder to the view
        //get layout
        LinearLayout track_layout = (LinearLayout) findViewById(R.id.track_layout);

        //create track container
        HorizontalScrollView track_container = new HorizontalScrollView(getApplicationContext());
        track_container.setLayoutParams(new HorizontalScrollView.LayoutParams(HorizontalScrollView.LayoutParams.MATCH_PARENT, displaysize.y/4));
        track_container.setBackground(getResources().getDrawable(R.color.track_container_bg));

        //create grid layout
        GridLayout track_grid = new GridLayout(getApplicationContext());
        track_grid.setColumnCount(100);
        track_grid.setRowCount(1);
        track_grid.setOrientation(GridLayout.HORIZONTAL);
        track_grid.setId(R.id.track_grid);

        //create linear layout for track id and wave
        LinearLayout track_identifier = new LinearLayout(getApplicationContext());
        track_identifier.setLayoutParams(new LinearLayout.LayoutParams(displaysize.x / 14, displaysize.y / 4));
        track_identifier.setOrientation(LinearLayout.VERTICAL);
        track_identifier.setBackgroundColor(getResources().getColor(R.color.black_overlay));

        //create textview for linear layout
        TextView track_num = new TextView(getApplicationContext());
        track_num.setText("1");
        track_num.setTextSize(45);
        track_num.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);

        //create imageview for linear layout
        ImageView track_type = new ImageView(getApplicationContext());
        track_type.setImageResource(waveDrawableID);
        track_type.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        //create "add measure" for grid layout
        ImageView add_measure = new ImageView(getApplicationContext());
        add_measure.setImageResource(R.drawable.measure_new);
        add_measure.setLayoutParams(new LinearLayout.LayoutParams((int) (displaysize.x / 3.32), LinearLayout.LayoutParams.MATCH_PARENT));
        if(projectLoad){
            add_measure.setTag(R.id.TAG_ROW, trackReloadCounter);
            add_measure.setId(trackReloadCounter + 4200);
        }else{
            add_measure.setTag(R.id.TAG_ROW, theproject.size() - 1);
            add_measure.setId(theproject.size() - 1 + 4200);
        }

        add_measure.setTag(R.id.TAG_COLUMN, 0);
        add_measure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMeasure(v, false);

            }
        });

        track_identifier.addView(track_num);
        if(projectLoad){
            track_num.setText(Integer.toString(trackReloadCounter+1));
            trackReloadCounter += 1;
        }else{
            track_num.setText(Integer.toString(theproject.size()));
        }
        track_num.setTextSize(45);
        track_identifier.addView(track_type);

        track_grid.addView(track_identifier);
        track_grid.addView(add_measure);

        track_container.addView(track_grid);

        track_layout.addView(track_container);

        return add_measure;

    }

    /**
     * Create all the views associated with a measure.
     * @param v
     * @param projectLoad
     */

    private void addMeasure(View v, boolean projectLoad){
        int whichtrack = (int) v.getTag(R.id.TAG_ROW);
        int whichmeasure = (int) v.getTag(R.id.TAG_COLUMN);

        if(!projectLoad) {
            theproject.track(whichtrack).addMeasure();
        }
        GridLayout myparent = (GridLayout) v.getParent();

        ImageView measure = new ImageView(getApplicationContext());
        measure.setImageResource(R.drawable.measure_new_empty);
        measure.setLayoutParams(new LinearLayout.LayoutParams((int) (displaysize.x / 3.32), LinearLayout.LayoutParams.MATCH_PARENT));
        measure.setTag(R.id.TAG_ROW, whichtrack);
        measure.setTag(R.id.TAG_COLUMN, whichmeasure);
        measure.setTag(R.bool.TAG_HASNOTES, false);
        measure.setTag(R.id.TAG_GUISNAP, 0);
        measure.setTag(R.id.TAG_FILLED_NOTES, new ArrayList<int[]>());
        measure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ///
                int whichtrack = (int) v.getTag(R.id.TAG_ROW);
                int whichmeasure = (int) v.getTag(R.id.TAG_COLUMN);
                editMeasure(v);
            }
        });
        myparent.addView(measure, whichmeasure + 1);

        v.setTag(R.id.TAG_COLUMN, whichmeasure + 1);
    }

    /**
     * Find a measure view object by its coordinates.
     * @param row
     * @param column
     * @return
     */

    private View findMeasureByCoords(int row, int column){

        LinearLayout track_layout = (LinearLayout) findViewById(R.id.track_layout);
//
        HorizontalScrollView track_container = (HorizontalScrollView) track_layout.getChildAt(row+1);
        GridLayout track = (GridLayout) track_container.getChildAt(0);


        int numChildren = track.getChildCount();
        for (int i = 0; i < numChildren; i++) {
            View child = track.getChildAt(i);
            if(child.getTag(R.id.TAG_ROW) == row && child.getTag(R.id.TAG_COLUMN) == column){
                return child;
            }
        }
        return null;
    }

}
