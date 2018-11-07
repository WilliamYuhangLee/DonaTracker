package edu.gatech.donatracker.database;

/**
 * Created by Yuhang Li on 2018/11/05
 *
 * A Handler to handle query result, ideally to be used in a lambda expression.
 *
 * @param <T> the Object type to convert the query result into
 */
@FunctionalInterface
public interface QueryResultHandler<T> {
    void onQuerySuccess(T result);
}
