package api;

public class ApiClient {

    private final RegistrationApiClient registrationApiClient = new RegistrationApiClient();
    private final LoginApiClient loginApiClient = new LoginApiClient();
    private final LogoutApiClient logoutApiClient = new LogoutApiClient();


    public RegistrationApiClient registration() {
        return registrationApiClient;
    }

    public LoginApiClient login() {
        return loginApiClient;
    }

    public LogoutApiClient logout() {
        return logoutApiClient;
    }


}
