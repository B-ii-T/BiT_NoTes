package com.example.bitnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class AddNoteActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "com.example.bitnotes.EXTRA_TITLE";
    public static final String EXTRA_NOTE_TEXT = "com.example.bitnotes.EXTRA_NOTE_TEXT";
    public static final String EXTRA_PR = "com.example.bitnotes.EXTRA_PR";
    public static final String EXTRA_ID = "com.example.bitnotes.EXTRA_ID";

    String intent_type;

    private EditText title, note_text, priority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        title = findViewById(R.id.title_edit_text);
        note_text = findViewById(R.id.note_edit_text);
        priority = findViewById(R.id.number_picker);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            setTitle("Edit note");
            title.setText(intent.getStringExtra(EXTRA_TITLE));
            note_text.setText(intent.getStringExtra(EXTRA_NOTE_TEXT));
            priority.setText( String.valueOf(intent.getIntExtra(EXTRA_PR, 1)));
            intent_type = "fadein-to-fadeout";
        } else {
            setTitle("New note");
            intent_type = "up-to-bottom";
        }

    }
    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, intent_type+"");
    }

    private void saveNote() {

        String title_edit_text = title.getText().toString();
        String note_edit_text = note_text.getText().toString();
        String pr_edit_text = priority.getText().toString();
        int pr = 0;
        try {
            pr = Integer.parseInt(pr_edit_text);
        } catch (NumberFormatException e) {
            pr = 0;
        }

        if (pr == 0) {
//            Toast.makeText(this, "InserT a CorrecT VaLue F0r PrioRiTY !", Toast.LENGTH_SHORT).show();
            Toasty.error(AddNoteActivity.this, "unaccepted priority value !", Toasty.LENGTH_SHORT).show();
            return;
        }
        if (title_edit_text.trim().isEmpty() || note_edit_text.trim().isEmpty() || pr_edit_text.trim().equals("")) {
//            Toast.makeText(this, "AlL FieLDs aRe ReQuiReD !", Toast.LENGTH_SHORT).show();
            Toasty.info(AddNoteActivity.this, "all fields required !", Toasty.LENGTH_SHORT).show();
            return;
        }
        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title_edit_text);
        data.putExtra(EXTRA_NOTE_TEXT, note_edit_text);
        data.putExtra(EXTRA_PR, pr);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1){
            data.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, data);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}