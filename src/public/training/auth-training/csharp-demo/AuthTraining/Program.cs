using AuthTraining.Controllers.lesson7.auth;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.Cookies;
using BasicAuthHandler = AuthTraining.Controllers.lesson6.auth.BasicAuthHandler;

namespace AuthTraining;

public class Program
{
    public static void Main(string[] args)
    {
        var builder = WebApplication.CreateBuilder(args);
        builder.Logging.ClearProviders();
        builder.Logging.AddConsole();

        // Add Controllers
        builder.Services.AddControllers();
        
        // Swagger
        builder.Services.AddEndpointsApiExplorer();
        builder.Services.AddSwaggerGen();
        builder.Services.AddHttpLogging(o => { });

        var app = builder.Build();

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
        app.UseSession();

        app.Run();
    }
}
