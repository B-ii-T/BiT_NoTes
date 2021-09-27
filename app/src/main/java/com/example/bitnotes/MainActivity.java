package com.example.bitnotes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.bitnotes.NoteAdapter;
import com.example.bitnotes.NoteViewModel;
import com.example.bitnotes.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.muddzdev.styleabletoast.StyleableToast;

import java.util.List;

import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;


public class MainActivity extends AppCompatActivity {

    public static final int ADD_NOTE_REQ = 1;
    public static final int EDIT_NOTE_REQ = 2;
    private LinearLayout no_nontes;

    private NoteViewModel noteViewModel;
//    public static FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("");

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);
        if(firstStart){
        showStartDialog();
        }

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        no_nontes = findViewById(R.id.empty);

        NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                if(notes.isEmpty()) {no_nontes.setVisibility(View.VISIBLE);}
                else{
                    no_nontes.setVisibility(View.GONE);
                }
                adapter.submitList(notes);
//                Toast.makeText(MainActivity.this, "changed", Toast.LENGTH_SHORT).show();
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                AlertDialog.Builder Notealert = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogStyle);
                Notealert.setTitle("This note will be deleted");
                Notealert.setIcon(R.drawable.ic_delete);
                Notealert.setCancelable(false);
                Notealert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
//                Toast.makeText(MainActivity.this, "NoTe DeLeTeD", Toast.LENGTH_SHORT).show();
//                StyleableToast.makeText(MainActivity.this, "NoTe DeLeTeD", Toast.LENGTH_SHORT, R.style.deletedNote).show();
                        Toasty.info(MainActivity.this, "Note deleted", Toasty.LENGTH_SHORT).show();
                    }
                });
                Notealert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.notifyDataSetChanged();
                    }
                });
                AlertDialog NotealertDialog = Notealert.create();
                NotealertDialog.show();
                NotealertDialog.getButton(NotealertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#00FFB3"));
                NotealertDialog.getButton(NotealertDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);

            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                intent.putExtra(AddNoteActivity.EXTRA_ID, note.getId());
                intent.putExtra(AddNoteActivity.EXTRA_TITLE, note.getTitle());
                intent.putExtra(AddNoteActivity.EXTRA_NOTE_TEXT, note.getText());
                intent.putExtra(AddNoteActivity.EXTRA_PR, note.getPriority());

                startActivityForResult(intent, EDIT_NOTE_REQ);
                CustomIntent.customType(MainActivity.this, "fadein-to-fadeout");
            }
        });

//        fab = findViewById(R.id.add_note);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
//                startActivityForResult(intent, ADD_NOTE_REQ);
//                CustomIntent.customType(MainActivity.this, "bottom-to-up");
//            }
//        });
    }

    private void showStartDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogStyle);
        alert.setTitle("Welcome to BiT NoTes !");
        alert.setMessage("\n- This is the BiT Notes V1.4\n- Fixed bugs\n- New features\n\nRead the instructions to know how the app works");
        alert.setCancelable(false);
        alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#00FFB3"));

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQ && resultCode == RESULT_OK) {
            String title = data.getStringExtra(AddNoteActivity.EXTRA_TITLE);
            String note_text = data.getStringExtra(AddNoteActivity.EXTRA_NOTE_TEXT);
            int priority = data.getIntExtra(AddNoteActivity.EXTRA_PR, 1);

            Note note = new Note(title, note_text, priority);
            noteViewModel.insert(note);
//            Toast.makeText(this, "NoTe SaVeD", Toast.LENGTH_SHORT).show();
            Toasty.success(MainActivity.this, "New note saved", Toasty.LENGTH_SHORT).show();

        } else if (requestCode == EDIT_NOTE_REQ && resultCode == RESULT_OK){
            int id = data.getIntExtra(AddNoteActivity.EXTRA_ID, -1);
            if(id == 1){
//                Toast.makeText(this, "NoTe CaN'T bE uPdaTed !", Toast.LENGTH_SHORT).show();
                Toasty.error(MainActivity.this, "Note can't be updated !", Toasty.LENGTH_SHORT).show();

                return;
            }
            String title = data.getStringExtra(AddNoteActivity.EXTRA_TITLE);
            String note_text = data.getStringExtra(AddNoteActivity.EXTRA_NOTE_TEXT);
            int priority = data.getIntExtra(AddNoteActivity.EXTRA_PR, 1);

            Note note = new Note(title, note_text, priority);
            note.setId(id);
            noteViewModel.update(note);
//            Toast.makeText(this, "NoTe uPdaTed", Toast.LENGTH_SHORT).show();
            Toasty.success(MainActivity.this, "Note updated", Toasty.LENGTH_SHORT).show();

        }
//         else {
//             Toast.makeText(this, "NoTe N0T SaveD !", Toast.LENGTH_SHORT).show();
//         }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all:
                Context context;
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogStyle);
                alert.setTitle("Would you like to delete all notes");
                alert.setIcon(R.drawable.ic_delete);
                alert.setCancelable(true);
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        noteViewModel.deleteAll();
//                Toast.makeText(this, "alL NoTes aRe deLeTeD", Toast.LENGTH_SHORT).show();
                        Toasty.info(MainActivity.this, "All notes deleted", Toasty.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                if(no_nontes.getVisibility() == View.VISIBLE){
                    Toasty.info(MainActivity.this, "No notes to delete", Toasty.LENGTH_SHORT).show();
//                    Toast.makeText(MainActivity.this, "no notes to delete", Toast.LENGTH_SHORT).show();
                }
                else {
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                    alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#00FFB3"));
                    alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
                }
                return true;

            case R.id.settings:
                Intent settingsIntent = new Intent(MainActivity.this, Settings.class);
                startActivity(settingsIntent);
                CustomIntent.customType(this, "up-to-bottom");
                return true;

            case R.id.addNote:
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQ);
                CustomIntent.customType(MainActivity.this, "bottom-to-up");

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}