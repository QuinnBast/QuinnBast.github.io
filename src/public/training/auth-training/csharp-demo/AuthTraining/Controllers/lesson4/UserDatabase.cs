namespace AuthTraining.Controllers.lesson4;

public interface IUserDatabase
{
    public void AddUser(User user);
    public User GetUser(string username);
    public bool VerifyUser(string username, string password);
}

public class UserDatabase : IUserDatabase
{

    private List<User> Users { get; } =
    [
        new User { username = "Quinn", password = "Bast", Role = [ UserRole.ADMIN, UserRole.USER ] },
        new User { username = "Alice", password = "Wonderland", Role = [ UserRole.VIEWER ] },
        new User { username = "Bobby", password = "Tables", Role = [ UserRole.ADMIN ] },
    ];
    
    public void AddUser(User user)
    {
        Users.Add(user);
    }

    public User GetUser(string username)
    {
        return Users.Find((user) => user.username == username);
    }

    public bool VerifyUser(string username, string password)
    {
        return Users.Any((user) => user.username == username && user.password == password);
    }
}