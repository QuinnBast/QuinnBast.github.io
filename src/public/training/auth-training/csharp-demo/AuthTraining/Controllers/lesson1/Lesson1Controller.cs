using Microsoft.AspNetCore.Mvc;

namespace AuthTraining.Controllers.lesson1;

[ApiController]
[Route("api/[controller]")]
public class Lesson1Controller : ControllerBase
{
    private UserDatabase db = new UserDatabase();
    
    [Route("login")]
    [HttpPost]
    public IActionResult Login(UserLoginRequest request)
    {
        if (db.VerifyUser(request.username, request.password))
        {
            return Ok("Success!");
        }
        return Unauthorized("Failure.");
    }
    
    [HttpGet]
    [Route("")]
    public string Test()
    {
        return "Test!";
    }
}
