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

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Override onCreate to setup the view.
     * @param savedInstanceState is for caching.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create the material design button.
        com.getbase.floatingactionbutton.AddFloatingActionButton button = (com.getbase.floatingactionbutton.AddFloatingActionButton) findViewById(R.id.openEditorButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openEditor();
            }
        });

        // populate the list view with projects.
        refreshFileList();

        // link the list view and the context menu.
        ListView lv = (ListView) findViewById(R.id.songList);
        lv.setOnItemClickListener(this);
        registerForContextMenu(lv);

        // API level 23+ requires this as well as a declaration in the manifest.
        verifyStoragePermissions(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String filename = (String) ((TextView) view).getText();
        ProjectWrapper pw = null;
        try {
            FileInputStream fis = getApplicationContext().openFileInput(filename);
            ObjectInputStream is = new ObjectInputStream(fis);
            pw = (ProjectWrapper) is.readObject();
            is.close();
            fis.close();
        } catch (IOException e) {
            Toast.makeText(this, "There was an error opening the file.", Toast.LENGTH_SHORT).show();
        } catch (ClassNotFoundException ce) {
            Toast.makeText(this, "ProjectWrapper class not found.", Toast.LENGTH_SHORT).show();
        }
        openEditor(pw);
    }

    @Override
    public void onCreateContextMenu(android.view.ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.project_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.deleteItem:
                ListView lv = (ListView) findViewById(R.id.songList);
                TextView clickeditem = (TextView) lv.getChildAt(info.position);
                if(deleteProjectFile(clickeditem.getText().toString())){
                    Toast.makeText(this, "Deleted project.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Could not delete project.", Toast.LENGTH_SHORT).show();
                }
                refreshFileList();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        restoreActionBar();
        //return true;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about){
            showAbout();
        }

        if (id == R.id.action_viewlicense){
            showLicense();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * API 23+ storage permission handling
     */
    private static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    /**
     * Deletes a project file in our internal storage directory.
     * @param filename
     * @return boolean
     */

    private boolean deleteProjectFile(String filename){
        File proj = new File(getApplicationContext().getFilesDir().toString()+"/"+filename);
        return proj.delete();
    }

    /**
     * Update the list of projects and the list view.
     */
    private void refreshFileList() {
        File dir = getApplicationContext().getFilesDir();
        File[] filelist = dir.listFiles();
        String[] flist = new String[filelist.length];
        for (int i = 0; i < flist.length; i++) {
            flist[i] = filelist[i].getName();
        }
        ListView lv = (ListView) findViewById(R.id.songList);
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_selectable_list_item, flist));
    }

    /**
     * Setup the action bar.
     */

    private void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.app_name);
    }

    /**
     * Starts a new project and switches to the editor activity.
     */

    private void openEditor() {
        // create the intent
        final Intent intent = new Intent(this, EditorLandscape.class);

        // save windowcontext to use later
        final Context windowcontext = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        View ts_picker = inflater.inflate(R.layout.time_sig_picker, null);
        final EditText et = (EditText) ts_picker.findViewById(R.id.top_timesig);

        final EditText etb = (EditText) ts_picker.findViewById(R.id.bot_timesig);

        // This is the time signature prompt, an AlertDialog.

        builder.setTitle(R.string.time_sig_picker_title)
                .setMessage(R.string.time_sig_picker_msg)
                .setView(ts_picker)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String beats = et.getText().toString();
                        String notes = etb.getText().toString();
                        int numBeats = Integer.parseInt(beats);
                        int numNotes = Integer.parseInt(notes);

                        //check if valid time signature
                        // 1 < beats <= 8
                        // 1, 2, 4, 8, 16, 32
                        if ((0 < numBeats) && (numBeats <= 8) && ((numNotes == 1 || numNotes == 2
                                || numNotes == 4 || numNotes == 8
                                || numNotes == 16 || numNotes == 32))) {
                            //String message = "test song name";
                            //intent.putExtra(SONG_NAME, message);
                            intent.putExtra("SourceActivity", "MainActivity");
                            intent.putExtra("TimeSigBeats", numBeats);
                            intent.putExtra("TimeSigNotes", numNotes);
                            startActivity(intent);
                        } else {
                            dialog.cancel();
                            AlertDialog.Builder errorb = new AlertDialog.Builder(windowcontext);
                            errorb
                                    .setTitle("Invalid Time Signature.")
                                    .setMessage("Number of beats: A number in the range 1-8. \n Number of notes: 1, 2, 4, 8, 16, 32.")
                                    .setCancelable(false)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog error = errorb.create();
                            error.show();
                        }
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
     * Open an editor instance with a project wrapper instead of a new one.
     */

    private void openEditor(final ProjectWrapper pw) {
        final Intent intent = new Intent(this, EditorLandscape.class);
        intent.putExtra("SourceActivity", "MainActivity");
        intent.putExtra("project", pw);
        startActivity(intent);
    }

    /**
     * Show app information in a popup.
     */

    private void showAbout(){
        AlertDialog.Builder about = new AlertDialog.Builder(this);
        about
                .setTitle("About")
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(Html.fromHtml(getString(R.string.about_us_html)))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog aboutpop = about.create();
        aboutpop.show();
        // make links clickable
        ((TextView)aboutpop.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Show license information in a popup.
     */

    private void showLicense(){
        AlertDialog.Builder licensepopup = new AlertDialog.Builder(this);
        licensepopup
                .setTitle("GNU GPL v3")
                .setMessage(R.string.GNU_GPL)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog licensepop = licensepopup.create();
        licensepop.show();
    }
}
