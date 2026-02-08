using System.Globalization;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.IdentityModel.Tokens;

namespace AuthTraining.Controllers.lesson6.auth;

public class JwtUtils
{
    public static byte[] Key = "MySecretKeyThatIsExtremelySecureAndCannotBeBroken"u8.ToArray();
    public static string Issuer = "Calian";
    
    public static string GenerateJSONWebToken(User user)
    {
        var securityKey = new SymmetricSecurityKey(Key);
        var credentials = new SigningCredentials(securityKey, SecurityAlgorithms.HmacSha256);
        
        // Create some "Claims" for ASP.NET to know about our user's information
        var claims = new List<Claim>
        {
            new Claim(ClaimTypes.Name, user.username),
            new Claim(ClaimTypes.Expiration, new DateTime().AddMinutes(120).ToString(CultureInfo.InvariantCulture)),
        };
            
        foreach (var userRole in user.Role)
        {
            claims.Add(new Claim(ClaimTypes.Role, userRole.ToString()));
        }

        var token = new JwtSecurityToken(Issuer,
            Issuer,
            claims,
            expires: DateTime.Now.AddMinutes(120),
            signingCredentials: credentials);

        return new JwtSecurityTokenHandler().WriteToken(token);
    }
}