package com.kwm.android.firebase.service.interfaces;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public interface IGetDocumentResult {
    void result(Task<DocumentSnapshot> task);
}
