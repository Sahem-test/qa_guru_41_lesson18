package guru.qa.tests;

import models.login.*;
import models.registration.RegistrationBodyModel;
import org.junit.jupiter.api.Test;
import testData.TestData;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static testData.TestData.*;

public class LoginTests extends TestBase {

    TestData td = new TestData();

    @Test
    public void successfulLoginTest() {

        step("Зарегистрировать пользователя", () -> {
            RegistrationBodyModel registrationData =
                    new RegistrationBodyModel(td.username, td.password);
            api.registration().registerUser(registrationData);
        });
        step("Авторизоваться и проверить access и refresh token", () -> {
            LoginBodyModel loginData = new LoginBodyModel(td.username, td.password);
            SuccessfulLoginResponseModel loginResponse = api.login().loginUser(loginData);

            String expectedTokenPart = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
            String actualAccess = loginResponse.access();
            String actualRefresh = loginResponse.refresh();

            assertThat(actualAccess).startsWith(expectedTokenPart);
            assertThat(actualRefresh).startsWith(expectedTokenPart);
            assertThat(actualAccess).isNotEqualTo(actualRefresh);
        });
    }

    @Test
    public void invalidCredentialsLoginTest() {

        step("Проверить ошибку при авторизации с неверным password", () -> {
            LoginBodyModel loginData = new LoginBodyModel(td.username, td.wrongPassword);
            InvalidCredentialsLoginResponseModel loginResponse = api.login().loginUserWithWrongPassword(loginData);

            String actualErrorInvalidUsernameOrPassword = loginResponse.detail();
            assertThat(actualErrorInvalidUsernameOrPassword).isEqualTo(EXPECTED_ERROR_INVALID_USERNAME_OR_PASSWORD);
        });
    }

    @Test
    public void emptyRefreshTokenLoginNegativeTest() {

        step("Проверить ошибку при обновлении токена без refresh token", () -> {
            WithoutRefreshTokenLoginBodyModel emptyRefreshToken = new WithoutRefreshTokenLoginBodyModel();
            WithoutRefreshTokenLoginResponseModel emptyRefreshResponseModel = api.login().updateTokenWithoutRefreshToken(emptyRefreshToken);

            String actualRefresh = emptyRefreshResponseModel.refresh().get(0);
            assertThat(actualRefresh).isEqualTo(EXPECTED_REQUIRED_FIELD);
        });
    }

    @Test
    public void invalidRefreshTokenLoginNegativeTest() {

        step("Проверить ошибку при обновлении токена с невалидным refresh token", () -> {
            InvalidRefreshTokenBodyModel invalidTokenBodyModel = new InvalidRefreshTokenBodyModel(EXPECTED_ERROR_INVALID_REFRESH_TOKEN);
            InvalidRefreshTokenResponseModel loginResponse = api.login().refreshAccessTokenWithInvalidRefreshToken(invalidTokenBodyModel);

            String actualDetailInvalidRefreshToken = loginResponse.detail();
            String actualCodeInvalidRefreshToken = loginResponse.code();

            assertThat(actualDetailInvalidRefreshToken).isEqualTo(EXPECTED_ERROR_VALID_TOKEN);
            assertThat(actualCodeInvalidRefreshToken).isEqualTo(EXPECTED_TOKEN_NOT_VALID_CODE);
        });
    }

    @Test
    public void accessTokenInsteadRefreshTokenLoginNegativeTest() {

        step("Зарегистрировать пользователя", () -> {
            RegistrationBodyModel registrationData =
                    new RegistrationBodyModel(td.username, td.password);
            api.registration().registerUser(registrationData);

        });

        String accessToken = step("Авторизоваться и получить access token", () -> {
            LoginBodyModel loginData = new LoginBodyModel(td.username, td.password);
            SuccessfulLoginResponseModel loginResponse = api.login().loginUser(loginData);
            return loginResponse.access();
        });

        step("Проверить ошибку при обновлении токена с access token вместо refresh token", () -> {
            InvalidRefreshTokenBodyModel invalidTokenBodyModel =
                    new InvalidRefreshTokenBodyModel(accessToken);

            InvalidRefreshTokenResponseModel refreshTokenResponse = api.login().refreshAccessTokenWithAccessTokenInsteadOfRefreshToken(invalidTokenBodyModel);

            String actualDetailInvalidRefreshToken = refreshTokenResponse.detail();
            String actualCodeInvalidRefreshToken = refreshTokenResponse.code();

            assertThat(actualDetailInvalidRefreshToken).isEqualTo(EXPECTED_ERROR_WRONG_TOKEN_TYPE);
            assertThat(actualCodeInvalidRefreshToken).isEqualTo(EXPECTED_TOKEN_NOT_VALID_CODE);
        });
    }

}
