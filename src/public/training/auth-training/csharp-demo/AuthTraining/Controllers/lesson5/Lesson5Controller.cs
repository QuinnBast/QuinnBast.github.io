using System.Security.Claims;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace AuthTraining.Controllers.lesson5;

[ApiController]
[Route("api/[controller]")]
public class Lesson5Controller : ControllerBase
{
    private UserDatabase db = new UserDatabase();
    
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
            
            return Redirect("/api/lesson5/auth");
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
    
    [Route("auth")]
    [HttpGet]
    [Authorize(Roles = "ADMIN")]
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
