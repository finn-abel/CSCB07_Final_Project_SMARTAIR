package com.example.cscb07_final_project_smartair;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.cscb07_final_project_smartair.Presenters.LoginPresenter;
import com.example.cscb07_final_project_smartair.Models.LoginModel;
import com.example.cscb07_final_project_smartair.Views.LoginView;

import static org.mockito.Mockito.*;

/**
 * Unit tests for the LoginPresenter class.
 *Mockito is used to simulate the LoginModel and LoginView classes for testing purposes.
 *
 */

@ExtendWith(MockitoExtension.class)
public class LoginPresenterTests {

    @Mock
    private LoginModel mockLoginModel;

    @Mock
    private LoginView mockLoginView;

    private LoginPresenter loginPresenter;

    @BeforeEach
    public void setUp() {
        loginPresenter = new LoginPresenter(mockLoginView,mockLoginModel);
    }

    @Test
    void onLoginButtonClickedWithEmptyEmailButValidPassword() {
        when(mockLoginView.getEmail()).thenReturn("");
        when(mockLoginView.getPassword()).thenReturn("ValidPassword");
        loginPresenter.onLoginButtonClicked();
        verify(mockLoginModel, never()).signInUser(anyString(), anyString(), any());
        verify(mockLoginView).showValidationError("Email cannot be empty.");
    }

    @Test
    void onLoginButtonClickedWithEmptyPasswordButValidEmail() {
        when(mockLoginView.getPassword()).thenReturn("");
        when(mockLoginView.getEmail()).thenReturn("ValidEmail");
        loginPresenter.onLoginButtonClicked();
        verify(mockLoginModel, never()).signInUser(anyString(), anyString(), any());
        verify(mockLoginView).showValidationError("Password cannot be empty.");
    }

    @Test
    void onLoginButtonClickedWithEmptyPasswordAndEmptyEmail() {
        when(mockLoginView.getPassword()).thenReturn("");
        when(mockLoginView.getEmail()).thenReturn("");
        loginPresenter.onLoginButtonClicked();
        verify(mockLoginModel, never()).signInUser(anyString(), anyString(), any());
        verify(mockLoginView).showValidationError("Please enter login credentials.");
    }

    @Test
    void onLoginButtonClickedWithValidPasswordAndValidEmail() {
        String email = "valid@test.com";
        String password = "Validpassword1!";
        when(mockLoginView.getEmail()).thenReturn(email);
        when(mockLoginView.getPassword()).thenReturn(password);
        loginPresenter.onLoginButtonClicked();
        verify(mockLoginModel).signInUser(eq(email), eq(password), eq(loginPresenter));
    }

    @Test
    void onForgotPasswordButtonClickedWithEmptyEmail() {
        when(mockLoginView.getEmail()).thenReturn("");
        loginPresenter.onForgotPasswordButtonClicked();
        verify(mockLoginView).showPasswordResetFailure("Email cannot be empty.");
        verify(mockLoginModel, never()).sendPasswordResetEmail(anyString(), any());
    }

    @Test
    void onForgotPasswordButtonClickedWithValidEmail() {
        String email = "valid@test.com";
        when(mockLoginView.getEmail()).thenReturn(email);
        loginPresenter.onForgotPasswordButtonClicked();
        verify(mockLoginModel).sendPasswordResetEmail(eq(email), eq(loginPresenter));
    }

    @Test
    void onSignUpButtonClicked() {
        loginPresenter.onSignUpButtonClicked();
        verify(mockLoginView).navigateToSignUpScreen();
    }

    @Test
    void onLoginSuccess() {
        loginPresenter.onLoginSuccess();
        verify(mockLoginView).showLoginSuccess("Sign in successful!");
        verify(mockLoginView).navigateToMainScreen();
    }

    @Test
    void onLoginFailure() {
        String errorMessage = "Login failed";
        loginPresenter.onLoginFailure(errorMessage);
        verify(mockLoginView).showLoginFailure(errorMessage);
    }

    @Test
    void onResetPasswordSuccess() {
        loginPresenter.onResetPasswordSuccess("Password reset email sent. Please check your email.");
        verify(mockLoginView).showPasswordResetSuccess("Password reset email sent. Please check your email.");
    }

    @Test
    void onResetPasswordFailure() {
        loginPresenter.onResetPasswordFailure("Password reset failed.");
        verify(mockLoginView).showPasswordResetFailure("Password reset failed.");
    }


}
