namespace AuthTraining.Controllers.lesson3;

public class User
{
    public string username { get; set; }
    public string password { get; set; }
    public List<UserRole> Role { get; set; }
}

public enum UserRole
{
    ADMIN,
    USER,
    VIEWER
}

public class UserSession
{
    public User User  { get; set; }
    public DateTime TimeCreated  { get; set; }
    public DateTime ExpiresAt { get; set; }
}

public record UserLoginRequest(string username, string password);