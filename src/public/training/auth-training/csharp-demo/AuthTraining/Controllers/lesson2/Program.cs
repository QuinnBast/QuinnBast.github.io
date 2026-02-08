
namespace AuthTraining.Controllers.lesson2;

public class Program
{
    public static void MainTwo(string[] args)
    {
        var builder = WebApplication.CreateBuilder(args);
        builder.Logging.ClearProviders();
        builder.Logging.AddConsole();
        
        // Configure Sessions
        builder.Services.AddDistributedMemoryCache();

        builder.Services.AddSession(options =>
        {
            options.IdleTimeout = TimeSpan.FromSeconds(60);
            options.Cookie.IsEssential = true;
        });

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
