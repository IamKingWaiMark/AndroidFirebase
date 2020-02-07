package com.kwm.android.firebase.service.interfaces;

import com.google.firebase.firestore.DocumentSnapshot;

public interface IListenerToDocument {
    void onUpdate(DocumentSnapshot documentSnapshot);
}
