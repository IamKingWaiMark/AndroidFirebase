package com.kwm.android.firebase.service.interfaces;

import com.google.firebase.firestore.QuerySnapshot;

public interface IListenerToCollection {
    void onUpdate(QuerySnapshot queryDocumentSnapshots);
}
