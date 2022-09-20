package com.kwm.android.firebase.service;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AndroidFireStorage {

    public StorageReference getReference(String path) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        if(path == null) {
            return storage.getReference();
        } else {
            if(path.startsWith("gs:")) {
                return storage.getReferenceFromUrl(path);
            } else if(path.startsWith("http") || path.startsWith("https")) {
                return storage.getReferenceFromUrl(path);
            } else {
                StorageReference storageRef = storage.getReference();
                return storageRef.child(path);
            }

        }
    }

}
