namespace AuthTraining.Controllers.lesson1;

public class User
{
    public string username { get; set; }
    public string password  { get; set; }
}

public record UserLoginRequest(string username, string password);
public record UserLoginResponse(string status);