package com.example.bitnotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private NoteViewModel noteViewModel;
    public static FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("");

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
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
                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
//                Toast.makeText(MainActivity.this, "NoTe DeLeTeD", Toast.LENGTH_SHORT).show();
//                StyleableToast.makeText(MainActivity.this, "NoTe DeLeTeD", Toast.LENGTH_SHORT, R.style.deletedNote).show();
                Toasty.info(MainActivity.this, "NoTe DeLeTeD", Toasty.LENGTH_SHORT).show();
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

        fab = findViewById(R.id.add_note);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQ);
                CustomIntent.customType(MainActivity.this, "bottom-to-up");
            }
        });
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
            Toasty.success(MainActivity.this, "NoTe SaVeD", Toasty.LENGTH_SHORT).show();

        } else if (requestCode == EDIT_NOTE_REQ && resultCode == RESULT_OK){
            int id = data.getIntExtra(AddNoteActivity.EXTRA_ID, -1);
            if(id == 1){
//                Toast.makeText(this, "NoTe CaN'T bE uPdaTed !", Toast.LENGTH_SHORT).show();
                Toasty.error(MainActivity.this, "NoTe CaN'T bE uPdaTed !", Toasty.LENGTH_SHORT).show();

                return;
            }
            String title = data.getStringExtra(AddNoteActivity.EXTRA_TITLE);
            String note_text = data.getStringExtra(AddNoteActivity.EXTRA_NOTE_TEXT);
            int priority = data.getIntExtra(AddNoteActivity.EXTRA_PR, 1);

            Note note = new Note(title, note_text, priority);
            note.setId(id);
            noteViewModel.update(note);
//            Toast.makeText(this, "NoTe uPdaTed", Toast.LENGTH_SHORT).show();
            Toasty.success(MainActivity.this, "NoTe uPdaTed", Toasty.LENGTH_SHORT).show();

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
                noteViewModel.deleteAll();
//                Toast.makeText(this, "alL NoTes aRe deLeTeD", Toast.LENGTH_SHORT).show();
                Toasty.info(MainActivity.this, "alL NoTes aRe deLeTeD", Toasty.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}