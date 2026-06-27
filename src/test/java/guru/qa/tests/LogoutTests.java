package guru.qa.tests;

import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.logout.LogoutBodyModel;
import models.logout.WithoutRefreshTokenLogoutBodyModel;
import models.logout.WithoutRefreshTokenLogoutResponseModel;
import models.logout.WrongReusedRefreshTokenResponseModel;
import models.registration.RegistrationBodyModel;
import org.junit.jupiter.api.Test;
import testData.TestData;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static testData.TestData.*;

public class LogoutTests extends TestBase {


    TestData td = new TestData();

    @Test
    public void successfulLogoutTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(td.username, td.password);

        step("Зарегистрировать пользователя", () -> {
            api.registration().registerUser(registrationData);
        });

        String refreshToken = step("Авторизоваться и получить refresh token", () -> {
            LoginBodyModel loginData = new LoginBodyModel(td.username, td.password);

            SuccessfulLoginResponseModel loginResponse = api.login().loginUser(loginData);
            return loginResponse.refresh();
        });
        step("Выполнить logout с refresh token", () -> {
            LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
            api.logout().logoutWithRefreshToken(logoutData);
        });
    }

    @Test
    public void logoutWithReusedRefreshTokenShouldReturn401Test() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(td.username, td.password);
        step("Зарегистрировать пользователя", () -> {

            api.registration().registerUser(registrationData);
        });

        String refreshToken = step("Авторизоваться и получить refresh token", () -> {
            LoginBodyModel loginData = new LoginBodyModel(td.username, td.password);
            SuccessfulLoginResponseModel loginResponse = api.login().loginUser(loginData);
            return loginResponse.refresh();

        });
        step("Выполнить logout с refresh token", () -> {
            LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
            api.logout().logoutWithRefreshToken(logoutData);
        });

        step("Повторно выполнить logout с тем же refresh token", () -> {
            LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
            WrongReusedRefreshTokenResponseModel logoutResponse = api.logout().logoutAgainWithSameRefreshToken(logoutData);

            String actualDetailReusedRefreshToken = logoutResponse.detail();
            String actualCodeReusedRefreshToken = logoutResponse.code();
            assertThat(actualDetailReusedRefreshToken).isEqualTo(EXPECTED_ERROR_TOKEN_IS_BLACKLISTED);
            assertThat(actualCodeReusedRefreshToken).isEqualTo(EXPECTED_TOKEN_NOT_VALID_CODE);
        });
    }

    @Test
    public void logoutWithoutRefreshTokenNegativeTest() {

        step("Проверить ошибку при logout без refresh token", () -> {
            WithoutRefreshTokenLogoutBodyModel logoutData = new WithoutRefreshTokenLogoutBodyModel();
            WithoutRefreshTokenLogoutResponseModel logoutResponse = api.logout().logoutWithoutRefreshToken(logoutData);


            String actualErrorWithoutRefreshToken = logoutResponse.refresh().get(0);
            assertThat(actualErrorWithoutRefreshToken).isEqualTo(EXPECTED_REQUIRED_FIELD);
        });
    }

    @Test
    public void accessTokenInsteadOfRefreshTokenNegativeTest() {

        step("Зарегистрировать пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(td.username, td.password);
            api.registration().registerUser(registrationData);
        });
        String accessToken = step("Авторизоваться и получить access token", () -> {
            LoginBodyModel loginData = new LoginBodyModel(td.username, td.password);
            SuccessfulLoginResponseModel loginResponse = api.login().loginUser(loginData);
            return loginResponse.access();
        });

        step("Проверить ошибку при logout с access token вместо refresh token", () -> {
            LogoutBodyModel logoutData = new LogoutBodyModel(accessToken);
            WrongReusedRefreshTokenResponseModel logoutResponse = api.logout().logoutWithAccessTokenInsteadOfRefreshToken(logoutData);


            String actualDetailReusedRefreshToken = logoutResponse.detail();
            String actualCodeReusedRefreshToken = logoutResponse.code();
            assertThat(actualDetailReusedRefreshToken).isEqualTo(EXPECTED_ERROR_WRONG_TOKEN_TYPE);
            assertThat(actualCodeReusedRefreshToken).isEqualTo(EXPECTED_TOKEN_NOT_VALID_CODE);
        });


    }
}
