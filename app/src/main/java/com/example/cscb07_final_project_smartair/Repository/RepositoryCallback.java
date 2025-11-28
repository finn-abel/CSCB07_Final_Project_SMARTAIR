package com.example.cscb07_final_project_smartair.Repository;

public interface RepositoryCallback<T> {
    void onSuccess(T result);
    void onFailure(Exception e);
}
