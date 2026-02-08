using System.Text;
using AuthTraining.Controllers.lesson6.auth;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;

namespace AuthTraining.Controllers.lesson6;

public class Program
{
    public static void MainFour(string[] args)
    {
        var builder = WebApplication.CreateBuilder(args);
        builder.Logging.ClearProviders();
        builder.Logging.AddConsole();
        
        // Use this to test Basic Auth:
        // builder.Services.AddAuthentication()
        //    .AddScheme<AuthenticationSchemeOptions, BasicAuthHandler>(BasicAuthHandler.AuthenticationScheme, null);
        
        // Use this to test JWT Bearers:
        builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
            .AddJwtBearer(options =>
            {
                options.TokenValidationParameters = new TokenValidationParameters
                {
                    ValidateIssuer = true,
                    ValidateAudience = true,
                    ValidateLifetime = true,
                    ValidateIssuerSigningKey = true,
                    ValidIssuer = JwtUtils.Issuer,
                    ValidAudience = JwtUtils.Issuer,
                    IssuerSigningKey = new SymmetricSecurityKey(JwtUtils.Key)
                };
            });
        
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