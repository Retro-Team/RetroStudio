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

import com.retroteam.studio.retrostudio.pcm.*;

import android.annotation.TargetApi;

import android.app.ActionBar;
import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.content.ContextCompat;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;

import android.view.View;
import android.view.MenuItem;
import android.widget.ImageView;

import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class MeasureEditor extends Activity {

    private String[] notesdisplay = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    private boolean pencil = false;
    private Menu actionMenu;
    private Project theproject;

    private int guiSNAP = 4;
    private int guiSnapIndex = 1;

    private int noteSUB = 4;

    private Measure thisMeasure;

    private int measureNum;
    private int trackNum;
    private int guiSNAPFromIntent;
    private int measureID;
    private ArrayList<int[]> filledNotesFromIntent = new ArrayList<int[]>();
    private ArrayList<int[]> filledNotes = new ArrayList<int[]>();

    private int TS_BEATS;
    private int TS_NOTES;

    private ThreadedPlayback audioplayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the note positions from the intent
        Intent intent = getIntent();
        theproject = (Project) intent.getSerializableExtra("Project");
        String message = intent.getStringExtra(EditorLandscape.MEASURE_INFO);
        String titlebar = intent.getStringExtra(EditorLandscape.MEASURE_TITLE);

        trackNum = intent.getIntExtra(EditorLandscape.MEASURE_TRACK, 0);
        measureNum = intent.getIntExtra(EditorLandscape.MEASURE, 0);
        measureID = intent.getIntExtra("measureID", 0);
        guiSNAPFromIntent = intent.getIntExtra("guiSNAP", 0);
        filledNotesFromIntent = (ArrayList<int[]>) intent.getSerializableExtra("filledNotes");

        TS_BEATS = intent.getIntExtra("tsBeats", 4);
        TS_NOTES = intent.getIntExtra("tsNotes", 4);

        //setContentView(R.layout.activity_trackedit);
        setContentView(R.layout.activity_measureedit);
        setupActionBar(titlebar); //cannot use this if we have no action bar

        //final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        //toggleHideyBar(); //start immersive mode



        // show a toast with the info
        //Toast.makeText(this, "REALLY editing measure " + trackNum + "," + measureNum, Toast.LENGTH_SHORT).show();

        final CharSequence[] guisnapopts = {"4", "8", "16", "32"};

        if(filledNotesFromIntent.size() > 0){
            filledNotes = filledNotesFromIntent;
        }

        if(guiSNAPFromIntent == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Choose Note Precision")
                    .setItems(guisnapopts, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            guiSNAP = Integer.parseInt(guisnapopts[which].toString());
                            drawGrid();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }else{
            guiSNAP = guiSNAPFromIntent;
            drawGrid();
        }





        //thismeasure.length()



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
            //actionMenu = measure;
            measure.setDisplayHomeAsUpEnabled(true);
            measure.setTitle("Editing Measure " + title.split(",")[0] + ", " + title.split(",")[1]);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.measure_activity_actions, menu);
        actionMenu = menu;
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
            Intent i = getIntent(); //gets the intent that called this intent
            i.putExtra("Project", theproject);
            if(hasNotes()){
                i.putExtra("HasNotes", true);
            }else{
                i.putExtra("HasNotes", false);
            }
            i.putExtra("guiSNAP", guiSNAP);
            i.putExtra("measureNum", measureNum);
            i.putExtra("trackNum", trackNum);
            i.putExtra("measureID", measureID);
            i.putExtra("filledNotes", filledNotes);
            this.setResult(Activity.RESULT_OK, i);
            this.finish();
            //NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        if (id == R.id.action_penciloff) {
            //Toast.makeText(this, "Tell Gabe to implement this!", Toast.LENGTH_SHORT).show();
            togglePencilTool(findViewById(R.id.pencilTool));
            item.setVisible(false);
        }

        if (id == R.id.action_togglefs) {
            toggleHideyBar();
        }

        if (id == R.id.action_octave){
            octavePrompt();
        }

        if(id == R.id.action_play){
            new Thread(new Runnable() {
                public void run() {
                    ThreadedPlayback audioplayer2 = new ThreadedPlayback(theproject.export(measureNum));
                    audioplayer = audioplayer2;
                    audioplayer.play();
                }
            }).start();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Calculate the number of notes to draw.
     * @return
     */

    private List<List<Integer>> numNotesFromGuiSnap(){

        //the measure length
        int mlength = (int) ((((double) TS_BEATS/(double) TS_NOTES)) * 32);
        boolean useRange = true;
        int maxNotesToDraw;


        //decide if we can use the user's input guiSNAP
        if(guiSNAP > mlength || (mlength % 4) > 0){
            //if not, then we will draw each individual note
            //this is for irregular time signatures
            maxNotesToDraw = mlength;
            useRange = false;
        }else{
            maxNotesToDraw = (int) ((((double) TS_BEATS/(double) TS_NOTES)) * guiSNAP);
        }

        ArrayList<Integer> therange = new ArrayList<>(mlength);
        List<List<Integer>> rangelist = new LinkedList<List<Integer>>();

        //produce a list of the range of notes to draw
        //i.e., if 8, produce [0,1,2,3,4,5,6,7]

        for (int i = 0; i < mlength; i += 1){
            therange.add(i);
        }

        if(useRange) {
            for (int i = 0; i < therange.size(); i += therange.size() / maxNotesToDraw) {
                rangelist.add(therange.subList(i,
                        Math.min(i + therange.size() / maxNotesToDraw, therange.size())));
            }
        }

        return rangelist;
    }

    /**
     * Show the user the octave changer prompt.
     */
    private void octavePrompt(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        final View tempo_picker = inflater.inflate(R.layout.octave_picker, null);
        final NumberPicker np = (NumberPicker) tempo_picker.findViewById(R.id.octavePickerField);
        np.setMaxValue(8);
        np.setMinValue(0);
        np.setValue(noteSUB);

        builder.setTitle("Set Measure Octave")
                .setView(tempo_picker)
                .setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        int sub = np.getValue();
                        noteSUB = sub;

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

    /**
     * Toggle the ability to draw notes.
     * @param view
     */
    private void togglePencilTool(View view) {

        com.getbase.floatingactionbutton.FloatingActionButton ptool = (com.getbase.floatingactionbutton.FloatingActionButton) view;

        if(pencil) {
            pencil = false;
            ptool.setVisibility(View.VISIBLE);
        } else {
            pencil = true;
            ptool.setVisibility(View.INVISIBLE);
            MenuItem cross = actionMenu.findItem(R.id.action_penciloff);
            cross.setVisible(true);

        }
    }

    /**
     * Assign a note visually and in the project.
     * @param view
     */
    private void paintNote(View view) {
        com.getbase.floatingactionbutton.FloatingActionButton ptool = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.pencilTool);
        ImageView iview = (ImageView) view;
        Drawable notestatus = iview.getDrawable();
        if (pencil) {
            if (notestatus.getConstantState().equals(ContextCompat.getDrawable(getApplicationContext(), R.drawable.note_filled).getConstantState())) {

                //blank all other notes in the column
                TableRow parent = (TableRow) iview.getParent();
                TableLayout layoutparent = (TableLayout) parent.getParent();
                int notedrawlen = layoutparent.getChildCount();
                for (int x = 0; x < notedrawlen; x++) {
                    TableRow noterow = (TableRow) layoutparent.getChildAt(x);
                    for (int i = 0; i < noterow.getChildCount(); i++) {
                        ImageView note = (ImageView) noterow.getChildAt(i);
                        if(note.getTag(R.id.TAG_COLUMN) == iview.getTag(R.id.TAG_COLUMN)) {
                            note.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.measure_outline));
                            for (int n = 0; n < filledNotes.size(); n++) {
                                int[] comp = filledNotes.get(n);
                                if(comp[0] == (int) note.getTag(R.id.TAG_ROW) && comp[1] == (int) note.getTag(R.id.TAG_COLUMN)){
                                    filledNotes.remove(n);
                                }
                            }
                        }
                    }
                }
                //set the drawable
                iview.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.measure_outline));
                filledNotes.remove(new int[]{(int) iview.getTag(R.id.TAG_ROW), (int) iview.getTag(R.id.TAG_COLUMN)});

                // set the other notes to rests
                List<Integer> guiSNAPRange = (List<Integer>) iview.getTag(R.id.TAG_GUISNAPRANGE);
                for(int z = guiSNAPRange.get(0); z <= guiSNAPRange.get(guiSNAPRange.size() - 1); z++){
                    theproject.track(trackNum).measure(measureNum).replace(z, new Note(Note.REST, noteSUB));
                }
            } else {
                //blank all other notes in the column
                TableRow parent = (TableRow) iview.getParent();
                TableLayout layoutparent = (TableLayout) parent.getParent();
                int notedrawlen = layoutparent.getChildCount();
                for (int x = 0; x < notedrawlen; x++) {
                    TableRow noterow = (TableRow) layoutparent.getChildAt(x);
                    for (int i = 0; i < noterow.getChildCount(); i++) {
                        ImageView note = (ImageView) noterow.getChildAt(i);
                        if(note.getTag(R.id.TAG_COLUMN) == iview.getTag(R.id.TAG_COLUMN)) {
                            note.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.measure_outline));
                            for (int n = 0; n < filledNotes.size(); n++) {
                                int[] comp = filledNotes.get(n);
                                if(comp[0] == (int) note.getTag(R.id.TAG_ROW) && comp[1] == (int) note.getTag(R.id.TAG_COLUMN)){
                                    filledNotes.remove(n);
                                }
                            }
                        }
                    }
                }
                //set the drawable
                iview.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.note_filled));
                filledNotes.add(new int[]{(int) iview.getTag(R.id.TAG_ROW), (int) iview.getTag(R.id.TAG_COLUMN)});

                //set the note in the data structure
                double notetype = stringToNoteDouble((String) iview.getTag(R.id.TAG_NOTE));
                List<Integer> guiSNAPRange = (List<Integer>) iview.getTag(R.id.TAG_GUISNAPRANGE);
                for(int z = guiSNAPRange.get(0); z <= guiSNAPRange.get(guiSNAPRange.size() - 1); z++){
                    theproject.track(trackNum).measure(measureNum).replace(z, new Note(notetype, noteSUB));
                }
            }
        }
    }

    /**
     * Convert a string to a note value.
     * @param notestr
     * @return
     */
    private double stringToNoteDouble(String notestr) {

        double noteret;

        switch(notestr) {
            case "C":
                noteret = Note.C;
                break;

            case "C#":
                noteret = Note.CSHARP;
                break;

            case "D":
                noteret = Note.D;
                break;

            case "D#":
                noteret = Note.DSHARP;
                break;

            case "E":
                noteret = Note.E;
                break;

            case "F":
                noteret = Note.F;
                break;

            case "F#":
                noteret = Note.FSHARP;
                break;

            case "G":
                noteret = Note.G;
                break;

            case "G#":
                noteret = Note.GSHARP;
                break;

            case "A":
                noteret = Note.A;
                break;

            case "A#":
                noteret = Note.ASHARP;
                break;

            case "B":
                noteret = Note.B;
                break;

            default:
                noteret =  0;
                break;
        }
        return noteret;
    }

    /**
     * Play a short note preview.
     * @param view
     */
    private void previewNote(View view){

        TextView tv = (TextView) view;

        SoundWave wavetype = theproject.track(trackNum).soundWave();

        Project tempProject = new Project(new TimeSignature(4, 4));
        tempProject.tempo(60.0);
        tempProject.addTrack(wavetype);
        tempProject.track(0).addMeasure();
        tempProject.track(0).measure(0).replace(0, new Note(stringToNoteDouble(tv.getText().toString()), noteSUB));
        tempProject.track(0).measure(0).replace(1, new Note(stringToNoteDouble(tv.getText().toString()), noteSUB));
        tempProject.track(0).measure(0).replace(2, new Note(stringToNoteDouble(tv.getText().toString()), noteSUB));
        tempProject.track(0).measure(0).replace(3, new Note(stringToNoteDouble(tv.getText().toString()), noteSUB));
        tempProject.track(0).measure(0).replace(4, new Note(stringToNoteDouble(tv.getText().toString()), noteSUB));
        tempProject.track(0).measure(0).replace(5, new Note(stringToNoteDouble(tv.getText().toString()), noteSUB));
        tempProject.track(0).measure(0).replace(6, new Note(stringToNoteDouble(tv.getText().toString()), noteSUB));
        tempProject.track(0).measure(0).replace(7, new Note(stringToNoteDouble(tv.getText().toString()), noteSUB));
        tempProject.track(0).measure(0).replace(8, new Note(stringToNoteDouble(tv.getText().toString()), noteSUB));

        try {
            ThreadedPlayback audioplayer2 = new ThreadedPlayback(tempProject.export());
            audioplayer2.play();
        }catch (IllegalStateException e){
            Toast.makeText(this, "Failed to preview note.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Check if the measure contains notes.
     * @return
     */
    private boolean hasNotes(){

        Measure me = theproject.track(trackNum).measure(measureNum);

        for (int i = 0; i < me.length(); i++) {
            Note note = me.getNote(i);
            if(note.pitch() != Note.REST) {
                return true;
            }
        }
        return false;
    }

    /**
     * Draw the grid.
     */
    private void drawGrid(){
        //draw the grid based on project info
        thisMeasure = theproject.track(trackNum).measure(measureNum);
        TableLayout notedraw = (TableLayout) findViewById(R.id.notedraw);
        int notedrawlen = notedraw.getChildCount();

        //get note scale
        final float dscale = getApplicationContext().getResources().getDisplayMetrics().density;
        int notewidth = (int) (144 * dscale + 0.5f);
        int noteheight = (int) (75 * dscale + 0.5f);

        for (int x = 0; x < notedrawlen; x++) {
            //TableRow noterow = new TableRow(getApplicationContext());
            //noterow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            TableRow noterow = (TableRow) notedraw.getChildAt(x);
            List<List<Integer>> rangelist = numNotesFromGuiSnap();
            for (int i = 0; i < rangelist.size(); i++) {
                ImageView note = new ImageView(getApplicationContext());
                note.setLayoutParams(new TableRow.LayoutParams(notewidth, noteheight));

                note.setTag(R.id.TAG_ROW, x);
                note.setTag(R.id.TAG_COLUMN, i);
                note.setTag(R.id.TAG_NOTE, notesdisplay[x]);

//                switch(guiSNAP){
//                    case 4:
//                        note.setTag(R.id.TAG_GUISNAPRANGE, guiSNAPRangeFour[i]);
//                        break;
//                    case 8:
//                        note.setTag(R.id.TAG_GUISNAPRANGE, guiSNAPRangeEight[i]);
//                        break;
//                    case 16:
//                        note.setTag(R.id.TAG_GUISNAPRANGE, guiSNAPRangeSixteen[i]);
//                        break;
//                    case 32:
//                        note.setTag(R.id.TAG_GUISNAPRANGE, new int[] {i, i});
//                        break;
//                    default:
//                        note.setTag(R.id.TAG_GUISNAPRANGE, new int[] {i, i});
//                        break;
//                }
                note.setTag(R.id.TAG_GUISNAPRANGE, rangelist.get(i));

                //int[] snaprange =
                //note.setTag(R.id.TAG_GUISNAPRANGE, getGUISnapRange())
                note.setImageResource(R.drawable.measure_outline);
                if(filledNotesFromIntent.size() > 0){
                    for (int z = 0; z < filledNotesFromIntent.size(); z++) {
                        if((filledNotesFromIntent.get(z)[0] == x) && (filledNotesFromIntent.get(z)[1] == i)){
                            note.setImageResource(R.drawable.note_filled);
                        }
                    }
                }


                note.setBackgroundColor(getResources().getColor(R.color.note_rest));
                //note.getLayoutParams().height = 75;
                //note.getLayoutParams().width = 144;
                //note.setMaxWidth(144);
                //note.setMaxHeight(75);
                //note.setOnClickListener();
                note.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ///
                        //int index = (GridLayout) v.getParent().getC
                        paintNote(v);

                    }
                });

                noterow.addView(note);
            }
            //notedraw.addView(noterow, x);
        }
    }

}
