package com.example.bitnotes;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = Note.class, version = 1)
public abstract class NoteDB extends RoomDatabase {
    private static NoteDB instance;

    public abstract NoteDao noteDao();

    public static synchronized NoteDB getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), NoteDB.class, "notes_database" ).fallbackToDestructiveMigration().addCallback(roomCallBack).build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallBack = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDBAsyncTask(instance).execute();
        }
    };

    private static class PopulateDBAsyncTask extends AsyncTask<Void, Void, Void>{
        private NoteDao noteDao;

        private PopulateDBAsyncTask(NoteDB ndb){
            noteDao = ndb.noteDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.insert(new Note("add note", "click the plus button and fill the required fields to add a new note.", 1));
            noteDao.insert(new Note("delete note", "swipe the note you want to delete in any horizontal direction.", 4));
            noteDao.insert(new Note("notes sorting", "notes are sorted by priority, other sorting options maybe available with new updates.", 3));
            noteDao.insert(new Note("edit note", "to edit a note simply click it and modify it.", 2));
            noteDao.insert(new Note("delete all notes", "to delete all notes click the delete button above", 5));
            return null;
        }
    }

}
