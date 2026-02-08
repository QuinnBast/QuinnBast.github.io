using Microsoft.AspNetCore.Authentication.Cookies;

namespace AuthTraining.Controllers.lesson3;

public class Program
{
    public static void MainThree(string[] args)
    {
        var builder = WebApplication.CreateBuilder(args);
        builder.Logging.ClearProviders();
        builder.Logging.AddConsole();
        
        // // Configure Sessions
        // builder.Services.AddDistributedMemoryCache();
        //
        // builder.Services.AddSession(options =>
        // {
        //     options.IdleTimeout = TimeSpan.FromSeconds(60);
        //     options.Cookie.IsEssential = true;
        // });
        
        builder.Services.AddAuthentication(CookieAuthenticationDefaults.AuthenticationScheme)
            .AddCookie();

        // Add Controllers
        builder.Services.AddControllers();
        
        // Swagger
        builder.Services.AddEndpointsApiExplorer();
        builder.Services.AddSwaggerGen();
        builder.Services.AddHttpLogging(o => { });

        var app = builder.Build();
        
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
        // app.UseSession();

        app.Run();
    }
}