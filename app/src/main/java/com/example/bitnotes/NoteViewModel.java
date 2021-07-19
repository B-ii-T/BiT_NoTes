package com.example.bitnotes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {

    private NoteRepo repo;
    private LiveData<List<Note>> allNotes;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repo = new NoteRepo(application);
        allNotes = repo.getAllNotes();
    }

    public void insert(Note note){
        repo.insert(note);
    }
    public void update(Note note){
        repo.update(note);
    }
    public void delete(Note note){
        repo.delete(note);
    }
    public void deleteAll(){
        repo.deleteAll();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }
}
