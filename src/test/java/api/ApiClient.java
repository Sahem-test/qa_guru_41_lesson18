package api;

import static io.restassured.RestAssured.given;

public class ApiClient  {

    private final RegistrationApiClient registrationApiClient = new RegistrationApiClient();

    public RegistrationApiClient registration(){
        return registrationApiClient;
    }


}
