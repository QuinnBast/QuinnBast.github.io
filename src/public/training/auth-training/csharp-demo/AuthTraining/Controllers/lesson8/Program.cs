using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using Microsoft.AspNetCore.Authentication.Cookies;

namespace AuthTraining.Controllers.lesson8;

public class Program
{
    public static void MainFour(string[] args)
    {
        var builder = WebApplication.CreateBuilder(args);
        builder.Logging.ClearProviders();
        builder.Logging.AddConsole();
        
        builder.Services.AddAuthentication(options =>
            {
              // If an authentication cookie is present, use it to get authentication information
              options.DefaultScheme = "Cookies";

              // If authentication is required, and no cookie is present, use OAuth (configured below) to sign in
              options.DefaultChallengeScheme = "OAuth";
            })
            .AddCookie("Cookies")
            .AddOpenIdConnect("OAuth", options =>
            {
                options.SignInScheme = "Cookies"; // Tell OAuth that we are going to use Cookies to store the user session.
                
                // Configure some OAuth Settings
                options.ClientId = "csharpapp";
                options.ClientSecret = "7Maq8jxPl1C2w4LJOFPGCpfYXtjj7VHh";
                options.Authority = "http://localhost:8080/realms/master";
                options.RequireHttpsMetadata = false; // Because we are not using a TLS Keycloak.
                options.ResponseType = "code";
                options.SaveTokens = true;

                // Configure the Scopes
                // Scopes tell us what information we are requesting from Keycloak
                options.Scope.Add("openid");
                options.Scope.Add("profile");
                options.Scope.Add("email");
                options.Scope.Add("offline_access");
                options.Scope.Add("roles");

                // After the user signs in, an authorization code will be sent to a callback
                // in this app. The OAuth middleware will intercept it
                options.CallbackPath = new PathString("/api/lesson8/callback");

                options.Events.OnTokenValidated = async ctx =>
                {
                    // For some reason, the access token's claims are not getting added to the user in C#
                    // So this method hooks into the TokenValidation and adds it manually...
                    // This definitely seems like a bug to me.
                    var token = ctx.TokenEndpointResponse.AccessToken;
                    var handler = new JwtSecurityTokenHandler();
                    var parsedJwt = handler.ReadJwtToken(token);
                    
                    // For some reason, we need to convert the "role" claims to use the actual Microsoft `ClaimTypes.Role` enum for them to be properly picked up...
                    // So convert them here I guess...
                    var updatedClaims = parsedJwt.Claims.ToList().Select(c =>
                    {
                        return c.Type == "role" ? new Claim(ClaimTypes.Role, c.Value) : c;
                    });
                    
                    
                    // Add the claims to the identity.
                    ctx.Principal.AddIdentity(new ClaimsIdentity(updatedClaims));
                };

                // This option will auto-detect fields from the JWT. However, we want to specify custom mappings to use.
                options.MapInboundClaims = false;
                options.TokenValidationParameters.NameClaimType = "preferred_username"; // Extract name
                options.TokenValidationParameters.RoleClaimType = "role"; // Extract Role
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