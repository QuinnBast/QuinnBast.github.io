using System.Text.Json;
using Microsoft.AspNetCore.Mvc;

namespace AuthTraining.Controllers.lesson2;

[ApiController]
[Route("api/[controller]")]
public class Lesson2Controller : ControllerBase
{
    private UserDatabase db = new UserDatabase();
    
    [Route("login")]
    [HttpPost]
    public IActionResult Login(UserLoginRequest request)
    {
        if (db.VerifyUser(request.username, request.password))
        {
            HttpContext.Session.SetString(
                "UserSession",
                JsonSerializer.Serialize(new UserSession {
                    User = db.GetUser(request.username),
                    TimeCreated = DateTime.Now,
                    ExpiresAt = DateTime.Now.AddHours(6),
                })
            );
            
            return Ok("Success!");
        }
        return Unauthorized("Failure.");
    }

    [Route("session")]
    [HttpGet]
    public IActionResult Session()
    {
        var session = HttpContext.Session.GetString("UserSession");
        if (session != null)
        {
            return Ok(session);
        }

        return Unauthorized("Not logged in.");
    }

    [Route("logout")]
    [HttpGet]
    public IActionResult Logout()
    {
        HttpContext.Session.Clear();
        return Ok("Logged out");
    }

    [HttpGet]
    [Route("")]
    public string Test()
    {
        return "Test!";
    }
}
