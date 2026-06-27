package api;

public class ApiClient  {

    private final RegistrationApiClient registrationApiClient = new RegistrationApiClient();
    private final LoginApiClient loginApiClient = new LoginApiClient();

    public RegistrationApiClient registration(){
        return registrationApiClient;
    }

    public LoginApiClient login(){
        return loginApiClient;
    }


}
