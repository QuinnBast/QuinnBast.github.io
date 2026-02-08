using System.Security.Claims;
using AuthTraining.Controllers.lesson8.auth;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Authentication.OpenIdConnect;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace AuthTraining.Controllers.lesson8;

[ApiController]
[Route("api/[controller]")]
public class Lesson8Controller : ControllerBase
{
    private UserDatabase db = new UserDatabase();
    private LdapAuth ldap = new LdapAuth();

    [Route("basic")]
    [HttpPost, BasicAuth]
    async public Task<IActionResult> Login()
    {
        return Ok("Logged in.");
    }

    [Route("login")]
    [HttpPost]
    async public Task<IActionResult> Login(UserLoginRequest request)
    {
        if (db.VerifyUser(request.username, request.password))
        {
            // Get the user after they logged in.
            var user = db.GetUser(request.username);

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
            var claimsIdentity = new ClaimsIdentity(claims, CookieAuthenticationDefaults.AuthenticationScheme);

            // Register the user with ASP.NET
            await HttpContext.SignInAsync(
                CookieAuthenticationDefaults.AuthenticationScheme,
                new ClaimsPrincipal(claimsIdentity),
                new AuthenticationProperties
                {
                    IsPersistent = true,
                    ExpiresUtc = DateTime.UtcNow.AddMinutes(20)
                }
            );

            return Redirect("/api/lesson6/auth");
        }

        return Unauthorized("Failure.");
    }

    [Route("loginJwt")]
    [HttpPost]
    async public Task<IActionResult> LoginJwt(UserLoginRequest request)
    {
        if (db.VerifyUser(request.username, request.password))
        {
            // Get the user after they logged in.
            var user = db.GetUser(request.username);

            return Ok(new { token = JwtUtils.GenerateJSONWebToken(user) });
        }

        return Unauthorized("Failure.");
    }

    [Route("loginForm")]
    [HttpPost]
    async public Task<IActionResult> LoginForm([FromForm] UserLoginRequest request)
    {
        if (db.VerifyUser(request.username, request.password))
        {
            // Get the user after they logged in.
            var user = db.GetUser(request.username);

            // Do whatever you want here to save the user's state.
            // Let's continue returning JWTs for now...
            return Ok(new { token = JwtUtils.GenerateJSONWebToken(user) });
        }

        return Unauthorized("Failure.");
    }

    [Route("loginLdap")]
    [HttpPost]
    public async Task<IActionResult> LoginLdap(UserLoginRequest request)
    {
        // Now we call some Ldap methods here...
        var userAttributeList = ldap.GetLdapUser(request.username, request.password);
        if (userAttributeList.Count == 1)
        {
            var userRoles = ldap.GetUserRoles(request.username);
            var roles = new List<string>();
            foreach (var userRole in userRoles)
            {
                roles.Add(userRole["cn"]);
            }
            
            // Create some "Claims" for ASP.NET to know about our user's information
            var claims = new List<Claim>
            {
                new Claim(ClaimTypes.Name, userAttributeList[0]["cn"]),
                new Claim(ClaimTypes.Email, userAttributeList[0]["mail"]),
                // Etc....
            };

            // Add roles to the claim...
            foreach (var userRole in roles)
            {
                claims.Add(new Claim(ClaimTypes.Role, userRole));
            }
            
            // Create an identity that uses a cookie
            var claimsIdentity = new ClaimsIdentity(claims, CookieAuthenticationDefaults.AuthenticationScheme);
            
            // Register the user's cookie with ASP.NET
            await HttpContext.SignInAsync(
                CookieAuthenticationDefaults.AuthenticationScheme,
                new ClaimsPrincipal(claimsIdentity),
                new AuthenticationProperties
                {
                    IsPersistent = true,
                    ExpiresUtc = DateTime.UtcNow.AddMinutes(20)
                }
            );

            return Ok("Successfully authenticated with LDAP!");
        }
        
        return Unauthorized("Failure.");
    }

    [Route("session")]
    [HttpGet]
    public IActionResult Session()
    {
        var user = HttpContext.User;
        if (user != null)
        {
            return Ok(String.Join(",", user.Claims));
        }
    
        return Unauthorized("Not logged in.");
    }
    
    [Route("testAuth")]
    [HttpGet]
    [Authorize]
    public async Task<IActionResult> TestAuth()
    {  
        var token = await HttpContext.GetTokenAsync("access_token");
        var claims = User.Claims;
        foreach (var claim in claims)
        {
            Console.WriteLine($"{claim.Type}: {claim.Value}");
        }
        return Ok($"Authorized. {token}");
    }
        
    [Route("auth")]
    [HttpGet]
    [Authorize(Roles = "admin")]
    public IActionResult Auth()
    {
        return Ok("Authorized.");
    }

    [Route("logout")]
    [HttpGet]
    async public Task<IActionResult> Logout()
    {
        await HttpContext.SignOutAsync();
        return Ok("Logged out");
    }
    
    [Route("unauthorized")]
    [HttpGet]
    public IActionResult Unauth()
    {
        return Unauthorized("Unauthorized");
    }

    [HttpGet]
    [Route("")]
    public string Test()
    {
        var user = HttpContext.User.Identity.Name;
        return "Hi, " + user;
    }

    private bool UserHasRole(ClaimsPrincipal pricipal, UserRole verifyRole)
    {
        var hasMatchingClaim = pricipal.Claims.FirstOrDefault((claim) =>
        {
            if (claim.Type == ClaimTypes.Role)
            {
                Enum.TryParse(claim.Value, out UserRole userRole);
                return userRole == verifyRole;
            }

            return false;
        });
        return hasMatchingClaim != null;
    }
}
