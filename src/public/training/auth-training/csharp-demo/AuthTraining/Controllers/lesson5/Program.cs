using Microsoft.AspNetCore.Authentication.Cookies;

namespace AuthTraining.Controllers.lesson5;

public class Program
{
    public static void MainFour(string[] args)
    {
        var builder = WebApplication.CreateBuilder(args);
        builder.Logging.ClearProviders();
        builder.Logging.AddConsole();
        
        builder.Services.AddAuthentication(CookieAuthenticationDefaults.AuthenticationScheme)
            .AddCookie();
        
        builder.Services.AddHttpsRedirection(options =>
        {
            options.HttpsPort = 9443;
        });

        // Add Controllers
        builder.Services.AddControllers();
        
        // Swagger
        builder.Services.AddEndpointsApiExplorer();
        builder.Services.AddSwaggerGen();
        builder.Services.AddHttpLogging(o => { });

        var app = builder.Build();

        app.UseHttpsRedirection();
        app.UseAuthentication();
        app.UseAuthorization();

        // Configure the HTTP request pipeline.
        if (app.Environment.IsDevelopment())
        {
            app.UseSwagger();
            app.UseSwaggerUI();
        }
        
        // Configure the app to load HTML files from wwwroot
        app.UseStaticFiles();
        app.UseHttpLogging();
        
        app.MapControllers();

        app.Run();
    }
}