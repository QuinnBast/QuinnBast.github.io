using Microsoft.AspNetCore.Mvc;

namespace AuthTraining.Controllers;

[ApiController]
[Route("api")]
public class DemoController : ControllerBase
{
    [HttpGet]
    [Route("")]
    public string Test()
    {
        return "Test!";
    }
}
