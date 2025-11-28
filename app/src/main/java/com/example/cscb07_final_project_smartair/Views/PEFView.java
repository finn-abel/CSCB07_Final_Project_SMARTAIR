package com.example.cscb07_final_project_smartair.Views;

public interface PEFView {
    void navigateToMainActivity();
    void showPEFError(String msg);
    void showSuccess();
    void showFailure(String s);
    public String getCurrent();
    public String getPre();
    public String getPost();
}
