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
            noteDao.insert(new Note("adding notes", "click the plus button and fill the required fields to add a new note.", 1));
            noteDao.insert(new Note("deleting notes", "swipe the note you want to delete in any horizontal direction.", 2));
            noteDao.insert(new Note("sorting notes", "notes are sorted by priority, other sorting options maybe available with new updates.", 4));
            noteDao.insert(new Note("editing notes", "to edit a note simply click it and modify it.", 3));
            return null;
        }
    }

}
