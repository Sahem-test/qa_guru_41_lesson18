package guru.qa.tests;

import api.ApiClient;
import models.registration.*;
import org.junit.jupiter.api.Test;
import testData.TestData;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static testData.TestData.*;


public class RegistrationTests extends TestBase {
    TestData td = new TestData();
    ApiClient api = new ApiClient();

    @Test
    public void successfulRegistrationTest() {

        step("Проверить успешную регистрацию пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(td.username, td.password);
            SuccessfulRegistrationResponseModel registrationResponseModel = api.registration().registerUser(registrationData);

            String actualUsername = registrationResponseModel.username();

            assertThat(actualUsername).isEqualTo(td.username);
            assertThat(registrationResponseModel.firstName()).isEmpty();
            assertThat(registrationResponseModel.id()).isGreaterThan(0);
            assertThat(registrationResponseModel.lastName()).isEmpty();
            assertThat(registrationResponseModel.email()).isEmpty();
            assertThat(registrationResponseModel.remoteAddr()).matches(IP_ADDRESS_REGEXP);
        });
    }

    @Test
    public void existingUserRegistrationNegativeTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(td.username, td.password);

        step("Зарегистрировать пользователя и проверить username в ответе", () -> {
            SuccessfulRegistrationResponseModel registrationResponseModel = api.registration().registerUser(registrationData);
            String actualUsername = registrationResponseModel.username();
            assertThat(actualUsername).isEqualTo(td.username);
        });

        step("Проверить ошибку при повторной регистрации существующего пользователя", () -> {
            ExistingUserResponseModel secondRegistrationResponse = api.registration().registerExistingUser(registrationData);

            String actualError = secondRegistrationResponse.username().get(0);
            assertThat(actualError).isEqualTo(EXPECTED_ERROR_EXISTING_USER);
        });
    }

    @Test
    public void unsupportedMediaTypeRegistrationNegativeTest() {

        step("Проверить ошибку при регистрации с неподдерживаемым Content-Type", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(td.username, td.password);
            UnsupportedMediaTypeRegistrationBodyModel unsupportedMediaTypeResponseModel = api.registration().checkMediaType(registrationData);

            String actualError = unsupportedMediaTypeResponseModel.detail();
            assertThat(actualError).isEqualTo(EXPECTED_ERROR_UNSUPPORTED_MEDIA_TYPE);
        });
    }

    @Test
    public void emptyUsernameRegistrationNegativeTest() {

        step("Проверить ошибку при регистрации с пустым username", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel("", td.password);
            EmptyFieldUsernameResponseModel emptyFieldUsernameResponseModel = api.registration().emptyFieldUsernameResponseModel(registrationData);

            String actualError = emptyFieldUsernameResponseModel.username().get(0);
            assertThat(actualError).isEqualTo(EXPECTED_ERROR_NOT_BE_BLANK);
        });
    }

    @Test
    public void emptyPasswordRegistrationNegativeTest() {

        step("Проверить ошибку при регистрации с пустым password", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(td.username, "");
            WrongPasswordResponseModel wrongPasswordResponseModel = api.registration().wrongPasswordResponseModel(registrationData);

            String actualError = wrongPasswordResponseModel.password().get(0);
            assertThat(actualError).isEqualTo(EXPECTED_ERROR_NOT_BE_BLANK);
        });
    }

    @Test
    public void passwordLongerRequiredLengthRegistrationNegativeTest() {

        step("Проверить ошибку при регистрации со слишком длинным password", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(td.username, td.tooLongPassword);
            WrongPasswordResponseModel wrongPasswordResponseModel = api.registration().registerUserWithTooLongPassword(registrationData);

            String actualError = wrongPasswordResponseModel.password().get(0);
            assertThat(actualError).isEqualTo(EXPECTED_ERROR_LONGER_REQUIRED_LENGTH_PASSWORD);
        });
    }
}
