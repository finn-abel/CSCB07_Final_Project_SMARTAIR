package com.example.cscb07_final_project_smartair.Views;

import com.example.cscb07_final_project_smartair.Users.ChildSpinnerOption;

import java.util.List;

public interface PEFView {
    void navigateToMainActivity();
    void showPEFError(String msg);
    void showSuccess();
    void showFailure(String s);
    String getCurrent();
    String getPre();
    String getPost();

    void updateSpinner(List<ChildSpinnerOption> childrenList);

    void showError(String message);
    boolean isParent();

    ChildSpinnerOption getSpinnerOption();
}
