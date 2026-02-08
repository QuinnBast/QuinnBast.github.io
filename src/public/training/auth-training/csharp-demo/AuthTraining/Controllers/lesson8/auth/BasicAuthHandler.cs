using System.Security.Claims;
using System.Text;
using System.Text.Encodings.Web;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authorization;
using Microsoft.Extensions.Options;

namespace AuthTraining.Controllers.lesson8.auth;

public class BasicAuthHandler : AuthenticationHandler<AuthenticationSchemeOptions>
{
    private UserDatabase db = new UserDatabase();
    public const string AuthenticationScheme = "Basic";
    
    public BasicAuthHandler(IOptionsMonitor<AuthenticationSchemeOptions> options, ILoggerFactory logger, UrlEncoder encoder, ISystemClock clock) : base(options, logger, encoder, clock)
    {
    }

    public BasicAuthHandler(IOptionsMonitor<AuthenticationSchemeOptions> options, ILoggerFactory logger, UrlEncoder encoder) : base(options, logger, encoder)
    {
    }

    protected override Task<AuthenticateResult> HandleAuthenticateAsync()
    {
        // No authorization header, so throw no result.
        if (!Request.Headers.ContainsKey("Authorization"))
        {
            return Task.FromResult(AuthenticateResult.Fail("Missing Authorization header"));
        }

        var authorizationHeader = Request.Headers["Authorization"].ToString();

        // If authorization header doesn't start with basic, throw no result.
        if (!authorizationHeader.StartsWith("Basic ", StringComparison.OrdinalIgnoreCase))
        {
            return Task.FromResult(AuthenticateResult.Fail("Authorization header does not start with 'Basic'"));
        }

        // Decrypt the authorization header and split out the client id/secret which is separated by the first ':'
        var authBase64Decoded = Encoding.UTF8.GetString(Convert.FromBase64String(authorizationHeader.Replace("Basic ", "", StringComparison.OrdinalIgnoreCase)));
        var authSplit = authBase64Decoded.Split(new[] { ':' }, 2);

        // No username and password, so throw no result.
        if (authSplit.Length != 2)
        {
            return Task.FromResult(AuthenticateResult.Fail("Invalid Authorization header format"));
        }

        // Store the client ID and secret
        var username = authSplit[0];
        var password = authSplit[1];

        // Client ID and secret are incorrect
        if (db.VerifyUser(username, password))
        {
            var user = db.GetUser(username);
            // Create some "Claims" for ASP.NET to know about our user's information
            var claims = new List<Claim>
            {
                new Claim(ClaimTypes.Name, user.username),
            };
            
            foreach (var userRole in user.Role)
            {
                claims.Add(new Claim(ClaimTypes.Role, userRole.ToString()));
            }

            // Create an identity that uses a cookie
            var claimsIdentity = new ClaimsIdentity(claims, Scheme.Name);
            
            return Task.FromResult(AuthenticateResult.Success(new AuthenticationTicket(new ClaimsPrincipal(claimsIdentity), Scheme.Name)));
        }
        
        return Task.FromResult(AuthenticateResult.Fail("Unauthorized"));
    }
}

public class BasicAuthAttribute : AuthorizeAttribute
{
    public BasicAuthAttribute()
    {
        AuthenticationSchemes = BasicAuthHandler.AuthenticationScheme;
    }
}