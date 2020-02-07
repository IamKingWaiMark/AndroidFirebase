package com.kwm.android.firebase.service;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

import androidx.annotation.NonNull;

    public class Where {
        public enum Condition {
            EQUAL_TO,
            GREATER_THAN,
            LESS_THAN,
            GREATER_THAN_EQUAL_TO,
            LESS_THAN_EQUAL_TO,
            CONTAINS,
            LIMIT,
            ORDER_BT
        }

        public enum Direction {
            ASC,
            DESC
        }
        private String where;
        private Condition condition;
        private Object argument;
        private int limit;
        private String orderBy;
        private Direction direction;

        public Where(@NonNull String where, @NonNull Condition condition, @NonNull Object argument){
            if(!isMatchQuery(condition)) throw new RuntimeException("Attempted to use " + condition.toString() + " for MATCH query.");

            this.where = where;
            this.condition = condition;
            this.argument = argument;
        }
        public Where(@NonNull Condition condition,@NonNull int limit){
            if(condition != Condition.LIMIT) throw new RuntimeException("Attempted to use LIMIT where query but LIMIT is not found.");
            this.condition = condition;
            this.limit = limit;
        }
        public Where(@NonNull Condition condition, @NonNull String orderBy, @NonNull Direction direction){
            if(condition != Condition.ORDER_BT) throw new RuntimeException("Attempted to use ORDER_BY where query but ORDER_BY is not found.");
            this.condition = condition;
            this.orderBy = orderBy;
            this.direction = direction;
        }

        public Query concatQuery(Query query){
            switch (condition){
                case EQUAL_TO:
                    return query.whereEqualTo(where, argument);
                case GREATER_THAN:
                    return query.whereGreaterThan(where, argument);
                case LESS_THAN:
                    return query.whereLessThan(where, argument);
                case GREATER_THAN_EQUAL_TO:
                    return query.whereGreaterThanOrEqualTo(where, argument);
                case LESS_THAN_EQUAL_TO:
                    return query.whereLessThanOrEqualTo(where, argument);
                case CONTAINS:
                    return query.whereArrayContains(where, argument);
                case LIMIT:
                    return query.limit(limit);
                case ORDER_BT:
                    return query.orderBy(orderBy, genOrderDirection());
            }

            return null;
        }

        public Query getQuery(CollectionReference collectionReference){
            switch (condition){
                case EQUAL_TO:
                    return collectionReference.whereEqualTo(where, argument);
                case GREATER_THAN:
                    return collectionReference.whereGreaterThan(where, argument);
                case LESS_THAN:
                    return collectionReference.whereLessThan(where, argument);
                case GREATER_THAN_EQUAL_TO:
                    return collectionReference.whereGreaterThanOrEqualTo(where, argument);
                case LESS_THAN_EQUAL_TO:
                    return collectionReference.whereLessThanOrEqualTo(where, argument);
                case CONTAINS:
                    return collectionReference.whereArrayContains(where, argument);
                case LIMIT:
                    return collectionReference.limit(limit);
                case ORDER_BT:
                    return collectionReference.orderBy(orderBy, genOrderDirection());
            }
            return null;
        }

        private Query.Direction genOrderDirection(){
            switch (direction){
                case ASC:
                    return Query.Direction.ASCENDING;
                case DESC:
                    return Query.Direction.DESCENDING;
            }

            return Query.Direction.ASCENDING;
        }


        private boolean isMatchQuery(Condition condition){
            switch (condition){
                case CONTAINS: return true;
                case LESS_THAN_EQUAL_TO: return true;
                case GREATER_THAN_EQUAL_TO: return true;
                case LESS_THAN: return true;
                case GREATER_THAN: return true;
                case EQUAL_TO: return true;
            }
            return false;
        }




}
