package guru.qa.tests;

import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.user.*;
import org.junit.jupiter.api.Test;
import testData.TestData;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static testData.TestData.EXPECTED_REQUIRED_FIELD;
import static testData.TestData.EXPECTED_UNAUTHORIZED_ERROR;

public class UpdateUserTests extends TestBase {
    TestData td = new TestData();

    @Test
    public void successfulFullUpdateUserWithPutTest() {

        step("Зарегистрировать пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(td.username, td.password);
            api.registration().registerUser(registrationData);
        });

        String accessToken = step("Авторизоваться и получить access token", () -> {
            LoginBodyModel loginData = new LoginBodyModel(td.username, td.password);
            SuccessfulLoginResponseModel loginResponse = api.login().loginUser(loginData);
            return loginResponse.access();
        });

        step("Полностью обновить данные пользователя через PUT и проверить ответ", () -> {
            UpdateUserBodyModel updateUserData = new UpdateUserBodyModel(td.username, td.firstName,
                    td.lastName, td.email);
            SuccessfulUpdateUserResponseModel responseUpdateUser = api.updateUser().updateUserWithPut(updateUserData, accessToken);

            String actualUsername = responseUpdateUser.username();
            String actualFirstName = responseUpdateUser.firstName();
            String actualLastName = responseUpdateUser.lastName();
            String actualEmail = responseUpdateUser.email();

            assertThat(responseUpdateUser.id()).isPositive();
            assertThat(actualUsername).isEqualTo(td.username);
            assertThat(actualFirstName).isEqualTo(td.firstName);
            assertThat(actualLastName).isEqualTo(td.lastName);
            assertThat(actualEmail).isEqualTo(td.email);
            assertThat(responseUpdateUser.remoteAddr()).isNotBlank();
        });
    }

    @Test
    public void successfulFullUpdateUserWithPatchTest() {

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

        step("Обновить данные пользователя через PATCH и проверить ответ", () -> {
            UpdateUserBodyModel updateUserData = new UpdateUserBodyModel(td.username, td.firstName,
                    td.lastName, td.email);
            SuccessfulUpdateUserResponseModel responseUpdateUser = api.updateUser().updateUserWithPatch(updateUserData, accessToken);

            String actualUsername = responseUpdateUser.username();
            String actualFirstName = responseUpdateUser.firstName();
            String actualLastName = responseUpdateUser.lastName();
            String actualEmail = responseUpdateUser.email();

            assertThat(responseUpdateUser.id()).isPositive();
            assertThat(actualUsername).isEqualTo(td.username);
            assertThat(actualFirstName).isEqualTo(td.firstName);
            assertThat(actualLastName).isEqualTo(td.lastName);
            assertThat(actualEmail).isEqualTo(td.email);
            assertThat(responseUpdateUser.remoteAddr()).isNotBlank();
        });
    }

    @Test
    public void successfulPartialUpdateUserWithPatchTest() {
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

        step("Частично обновить данные пользователя через PATCH и проверить ответ", () -> {
            PartialUpdateUserBodyModel updateUserData =
                    new PartialUpdateUserBodyModel(td.username, td.email);
            SuccessfulUpdateUserResponseModel responseUpdateUser =
                    api.updateUser().partiallyUpdateUserWithPatch(updateUserData, accessToken);

            String actualUsername = responseUpdateUser.username();
            String actualEmail = responseUpdateUser.email();

            assertThat(responseUpdateUser.id()).isPositive();
            assertThat(actualUsername).isEqualTo(td.username);
            assertThat(actualEmail).isEqualTo(td.email);
            assertThat(responseUpdateUser.remoteAddr()).isNotBlank();
        });
    }

    @Test
    public void partialUpdateUserWithPutNegativeTest() {

        step("Зарегистрировать пользователя", () -> {
            RegistrationBodyModel registrationData =
                    new RegistrationBodyModel(td.username, td.password);

            api.registration().registerUser(registrationData);
        });
        String accessToken = step("Авторизоваться и получить access token", () -> {
            LoginBodyModel loginData = new LoginBodyModel(td.username, td.password);

            SuccessfulLoginResponseModel responseLogin = api.login().loginUser(loginData);
            return responseLogin.access();
        });
        step("Проверить ошибку при частичном обновлении пользователя через PUT", () -> {
            PartialUpdateUserBodyModel updateUserData = new PartialUpdateUserBodyModel(td.username, td.email);

            UnsuccessfulPartialUpdateUserResponseModel responseUpdateUser = api.updateUser().partiallyUpdateUserWithPutExpectingError(updateUserData, accessToken);

            String actualFirstName = responseUpdateUser.firstName().get(0);
            String actualLastName = responseUpdateUser.lastName().get(0);

            assertThat(actualFirstName).isEqualTo(EXPECTED_REQUIRED_FIELD);
            assertThat(actualLastName).isEqualTo(EXPECTED_REQUIRED_FIELD);
            assertThat(responseUpdateUser.username()).isNull();
            assertThat(responseUpdateUser.email()).isNull();
        });
    }

    @Test
    public void withoutRequiredAuthorizationHeaderUpdateUserNegativeTest() {

        step("Проверить ошибку при обновлении пользователя без Authorization header", () -> {
            UpdateUserBodyModel updateUserData = new UpdateUserBodyModel(td.username, td.firstName,
                    td.lastName, td.email);
            UnauthorizedResponseModel responseUpdateUser = api.updateUser().updateUserWithoutAuthorizationHeader(updateUserData);

            String actualDetail = responseUpdateUser.detail();
            assertThat(actualDetail).isEqualTo(EXPECTED_UNAUTHORIZED_ERROR);
        });
    }
}




