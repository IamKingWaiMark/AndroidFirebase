package com.kwm.android.firebase.service.interfaces;


import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

public interface IGetCollectionResult {
    void result(Task<QuerySnapshot> task);
}
