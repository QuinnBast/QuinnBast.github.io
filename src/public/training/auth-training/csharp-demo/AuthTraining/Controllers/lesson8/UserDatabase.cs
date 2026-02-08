using System.Security.Cryptography;
using System.Text;

namespace AuthTraining.Controllers.lesson8;

public interface IUserDatabase
{
    public void AddUser(string username, string password, List<UserRole> roles);
    public User GetUser(string username);
    public bool VerifyUser(string username, string password);
}

public class UserDatabase : IUserDatabase
{

    public List<User> Users { get; } =
    [
        // new User { username = "Quinn", password = "Bast", Role = [ UserRole.ADMIN, UserRole.USER ] },
        // new User { username = "QuinnClone", password = "Bast", Role = [ UserRole.ADMIN, UserRole.USER ] },
        // new User { username = "Alice", password = "Wonderland", Role = [ UserRole.VIEWER ] },
        // new User { username = "AliceClone", password = "Wonderland", Role = [ UserRole.VIEWER ] },
        // new User { username = "Bobby", password = "Tables", Role = [ UserRole.ADMIN ] },
        // new User { username = "BobbyClone", password = "Tables", Role = [ UserRole.ADMIN ] },
    ];

    public UserDatabase()
    {
        AddUser("Quinn", "Bast", [ UserRole.ADMIN, UserRole.USER ]);
        AddUser("Alice", "Wonderland", [ UserRole.VIEWER ]);
        AddUser("Bobby", "Tables", [ UserRole.ADMIN ]);
    }
    
    public void AddUser(string username, string password, List<UserRole> roles)
    {
        byte[] buffer = RandomNumberGenerator.GetBytes(1024);
        string salt = BitConverter.ToString(buffer);
        
        Users.Add(new User
        {
            username = username,
            salt = salt,
            hashedPassword = HashString(password, salt),
            Role = roles,
        });
    }

    public User GetUser(string username)
    {
        return Users.Find((user) => user.username == username);
    }

    public bool VerifyUser(string username, string password)
    {
        return Users.Any((user) => user.username == username &&  user.hashedPassword == HashString(password, user.salt));
    }

    public string HashString(string value, string salt)
    {
        var saltedPassword = value + salt;
        
        StringBuilder sb = new StringBuilder();
        using (HashAlgorithm algorithm = SHA256.Create())
        {
            foreach (byte b in algorithm.ComputeHash(Encoding.UTF8.GetBytes(saltedPassword)))
            {
                sb.Append(b.ToString("X2"));
            }
        }

        return sb.ToString();
    }
}