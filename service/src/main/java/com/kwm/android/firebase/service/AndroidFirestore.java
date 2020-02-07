package com.kwm.android.firebase.service;



import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.kwm.android.firebase.service.interfaces.IGetCollectionResult;
import com.kwm.android.firebase.service.interfaces.IGetDocumentResult;
import com.kwm.android.firebase.service.interfaces.IListenerToCollection;
import com.kwm.android.firebase.service.interfaces.IListenerToDocument;

import androidx.annotation.NonNull;

public class AndroidFirestore {

    private HashMap<String, ListenerRegistration> listenerIndex;

    public AndroidFirestore (){
        listenerIndex = new HashMap<>();
    }
    /**
     * Adds an document to a collection.
     * @param ref
     */
    public void create(String [] ref, Object data, @NonNull OnCompleteListener<Void> onCompleteListener){
        if(data == null) throw new RuntimeException("Error creating data. Data is null.");
        if(isCollectionPath(ref)) {
            genCollectionReference(ref).document().set(data).addOnCompleteListener(onCompleteListener);
        } else if(isDocumentPath(ref)){
            genDocumentReference(ref).set(data).addOnCompleteListener(onCompleteListener);
        }

    }

    /**
     * Delete a document.
     * @param document
     * @param onCompleteListener
     */
    public void delete(String [] document, @NonNull OnCompleteListener<Void> onCompleteListener){
        // Check if it is a valid document reference.
        checkDocumentPathValidity(document);
        // Delete data.
        genDocumentReference(document).delete().addOnCompleteListener(onCompleteListener);
    }

    /**
     * Update a document.
     * @param document
     * @param data
     * @param onCompleteListener
     */
    public void update(String [] document, Map<String, Object> data, @NonNull OnCompleteListener<Void> onCompleteListener){
        // Check if it is a valid document reference.
        checkDocumentPathValidity(document);
        // Delete data.
        genDocumentReference(document).update(data).addOnCompleteListener(onCompleteListener);
    }
    /**
     * Listens to document reference.
     * @param name Name of the listener
     * @param from String array containing path to the document. Collection > Document > Collection > Document > ...
     * @param iListenerToDocument A Listener to listen to the changes.
     */
    public ListenerRegistration listenTo(@NonNull String name, String [] from, @NonNull final IListenerToDocument iListenerToDocument){
        validateListenerName(name);

        listenerIndex.put(
                name, // Name of this listener.
                genDocumentReference(from).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@NonNull DocumentSnapshot documentSnapshot, @NonNull FirebaseFirestoreException e) {
                        iListenerToDocument.onUpdate(documentSnapshot);
                    }
                })
        );
        return listenerIndex.get(name);
    }

    /**
     * Listents the a collection reference.
     * @param name Name of the listener
     * @param from String array containing path to the collection. Collection > Document > Collection > ...
     * @param where String array to build the query.
     * @param listenerToCollection A listener to listen to the changes.
     */
    public ListenerRegistration listenTo(@NonNull String name, String [] from, Where [] where, @NonNull final IListenerToCollection listenerToCollection){
        validateListenerName(name);

        CollectionReference cf = genCollectionReference(from);
        // Parameter.
        if(where != null && where.length > 0){
            Query query = null;
            for(int i = 0; i < where.length; i++){
                if(query == null) query = where[i].getQuery(cf);
                else query = where[i].concatQuery(query);
            }
            listenerIndex.put(
                    name, // Name of this listener.
                    query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@NonNull QuerySnapshot queryDocumentSnapshots, @NonNull FirebaseFirestoreException e) {
                            listenerToCollection.onUpdate(queryDocumentSnapshots);
                        }
                    })
            );
            return listenerIndex.get(name);
        } else {
            listenerIndex.put(
                    name, // Name of this listener.
                    cf.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@NonNull QuerySnapshot queryDocumentSnapshots, @NonNull FirebaseFirestoreException e) {
                            listenerToCollection.onUpdate(queryDocumentSnapshots);
                        }
                    })
            );
            return listenerIndex.get(name);
        }

    }

    /**
     * Get a set of items from a collection.
     * @param from
     * @param where
     * @param getCollectionResult
     */
    public CompletableFuture<Task<QuerySnapshot>> get(String [] from, Where [] where, @NonNull final IGetCollectionResult getCollectionResult){
        final CompletableFuture<Task<QuerySnapshot>> promise = new CompletableFuture<>();
        CollectionReference cf = genCollectionReference(from);

        // Parameter.
        if(where != null && where.length > 0){
            Query query = null;
            for(int i = 0; i < where.length; i++){
                if(query == null) query = where[i].getQuery(cf);
                else query = where[i].concatQuery(query);
            }
            query.get().addOnCompleteListener(
                    new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                try{
                                    getCollectionResult.result(task);
                                } catch (Exception err) {}
                                promise.complete(task);
                            } else {
                                promise.completeExceptionally(task.getException());
                            }
                        }
                    }
            );
        } else {
            cf.get().addOnCompleteListener(
                    new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                try{
                                    getCollectionResult.result(task);
                                } catch (Exception err) {}
                                promise.complete(task);
                            } else {
                                promise.completeExceptionally(task.getException());
                            }
                        }
                    }
            );
        }

        return promise;
    }

    /**
     * Get a set of items from a collection.
     * @param from
     * @param where
     */
    public CompletableFuture<Task<QuerySnapshot>> get(String [] from, Where [] where){
        return get(from, where, null);
    }
    public void get(String [] from, @NonNull final IGetDocumentResult getDocumentResult){
        genDocumentReference(from).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                getDocumentResult.result(task);
            }
        });
    }

    /**
     * Generate a document reference from a path to the database.
     * @param from
     * @return
     */
    private DocumentReference genDocumentReference(String [] from){
        // Check if path is referencing a document.
        checkDocumentPathValidity(from);
        // Generate reference.
        FirebaseFirestore db = getInstance();
        CollectionReference cf = null;
        DocumentReference df = null;
        for(int i = 0; i < from.length; i++){
            // 0 is document, 1 is collection
            if(i % 2 == 0){
                if(cf == null) cf = db.collection(from[i]);
                else cf = df.collection(from[i]);
            } else {
                df = cf.document(from[i]);
            }
        }
        return df;
    }

    private CollectionReference genCollectionReference(String [] from) {
        // Check if the path is referencing a collection.
        checkCollectionPathValidity(from);

        FirebaseFirestore db = getInstance();
        CollectionReference cf = null;
        DocumentReference df = null;

        for(int i = 0; i < from.length; i++){
            // 0 is document, 1 is collection
            if(i % 2 == 0){
                if(cf == null) cf = db.collection(from[i]);
                else cf = df.collection(from[i]);
            } else {
                df = cf.document(from[i]);
            }
        }

        return cf;
    }
    /**
     * Check if the path to the collection is valid.
     * @param from
     */
    private void checkCollectionPathValidity(String [] from){
        if(from == null || !isCollectionPath(from)) throw new RuntimeException(this.getClass().getName() + " Not a valid path to a collection");
    }

    /**
     * Check to see if the path is a collection collection.
     * @param from
     * @return
     */
    private boolean isCollectionPath(String [] from){
        return from.length % 2 == 1;
    }

    /**
     * Checks if the path to the document is valid.
     * @param from
     */
    private void checkDocumentPathValidity(String [] from){
        if(from == null || !isDocumentPath(from)) throw new RuntimeException(this.getClass().getName() + " Not a valid path to a document");
    }

    /**
     * Check to see if the path is a document reference.
     * @param from
     * @return
     */
    private boolean isDocumentPath(String [] from){
        return from.length % 2 == 0;
    }
    public FirebaseFirestore getInstance(){
        return FirebaseFirestore.getInstance();
    }

    /**
     * Removes the listener that is listening to a document or collection reference.
     * @param name Name of the listener to remove.
     */
    public void stopListeningTo(String name){
        try{
            listenerIndex.get(name).remove();
            listenerIndex.remove(name);
        } catch (Exception err) {}
    }

    /**
     * Stops all listeners.
     */
    public void stopListeningToAll(){
        for(String key: listenerIndex.keySet()){
            stopListeningTo(key);
        }
    }

    /**
     * Validates the listener name.
     * @param listenerName
     */
    private void validateListenerName(String listenerName){
        if(listenerName == null || listenerName.trim().length() <= 0) throw new RuntimeException("Listener name cannot be empty.");
        else if(listenerIndex.containsKey(listenerName)) throw new RuntimeException(listenerName + " is already in used." +
                " Must use a different name for the listener or stop listening to it by calling the " +
                "stopListeningTo method.");
    }




}
