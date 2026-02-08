# Authentication Protocols and Identity Providers

Now that we've ensured our application is secure, we can continue implementing additional features of our application.  
The most desired feature is to implement a management UI. This will allow administrators of the application to manage
our users and their access to our application.

## Management API

As an admin, we want to be able to log in to our application, view all of our users, update their roles, passwords,
enforce password policies, and so on.  
This will allow us to have more control over our users and their access to our application.

To implement these features, we are going to have to add a new set of API routes that allow us to manage our users.  
These routes should be fairly straightforward to implement, so go ahead and try it yourself.

Add routes for `GET /api/users`, which returns a list of users, and `PUT /api/user` where, if the user exists, updates
their roles, otherwise, adds a new user account.

### Solution

```kotlin
get("/users") {
    call.respond(HttpStatusCode.OK, userDatabase.users.values)
}
post("/user") {
    val userRequest = call.receive<UserRequest>()
    val existingUser = userDatabase.getUser(userRequest.username)
    if (existingUser != null) {
        // Update an existing user's roles
        userDatabase.users[userRequest.username] = userDatabase.users[userRequest.username]!!
            .copy(roles = userRequest.roles)
        call.respond(HttpStatusCode.OK, "Updated user roles")
    } else {
        userDatabase.addUser(userRequest.username, userRequest.password, userRequest.roles)
        call.respond(HttpStatusCode.OK, "User added")
    }
}
```

Now that we have added these routes, we can create a new webpage that will allow us to manage our users.

### Don't you love Javascript?

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Auth-Demo</title>
</head>

<body>
<div>
    <label>
        Username
        <input type="text" id="new_username"/>
    </label>
    <label>
        Password
        <input type="text" id="new_password"/>
    </label>
    <label>
        Role
        <select id="new_role">
            <option value="ADMIN">Admin</option>
            <option value="USER">User</option>
            <option value="VIEWER">Viewer</option>
        </select>
    </label>

    <button onclick="createUser()">New User</button>

    <div id="userData"></div>
</div>
</body>

<script>
    function getUsers() {
        let userData = document.getElementById("userData");
        fetch("https://localhost:9443/api/users").then((success) => {
            success.json().then((data) => {
                let users = data.map((user) => {
                    return `<div>${user.username} - ${user.roles.join(", ")}
                    <button onclick="makeViewer('${user.username}')">Make Viewer</button></div>`;
                });
                userData.innerHTML = users.join("");
            });
            console.log("Success: ", success);
        }).catch((error) => {
            userData.innerHTML = "Error while fetching users." + error;
            console.log("Error: ", error);
        });
    }

    function createUser() {
        fetch("https://localhost:9443/api/user", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                username: document.getElementById("new_username").value,
                password: document.getElementById("new_password").value,
                roles: [document.getElementById("new_role").value],
            }),
        }).then((success) => {
            console.log("Success: ", success);
            getUsers();
        }).catch((error) => {
            console.log("Error: ", error);
        });
    }

    // Call this on load.
    getUsers();
</script>
</html>
```

This creates a webpage that allows us to view all of the users in our application, what their current role is, as well
as create new users.  
Of course, this is a very basic example.

![User Management](../images/userManagement.png)

We would need to spend more time on this to make it more user-friendly.  
For example, we are missing:

- The ability to delete users
- Reset passwords
- Update user roles
- Enforce password policies

This is shaping up to be a lot of work... do we really want to have to do this?

## Identity Access and Management (IAM)

What we created in the previous section is known as an Identity Access and Management (IAM) system.  
IAM is a framework of policies and technologies to ensure that the right people in an enterprise have the appropriate
access to technology resources.

Typically, this is done through a user management interface like the one we just created.

Typically, this is a desired feature when implementing a software system, as it allows for a more secure and manageable
system.  
But implementing it on your own can be a daunting task, and it's not something that you want to get wrong.  
As a result, many companies choose to use existing solutions that have been tried and tested by the community and are
known to be secure by following best practices and standards.

### Identity Providers

In the real world, we don't want to have to implement all of these security features ourselves.  
We want to be able to leverage existing solutions that have been tried and tested by the community and are known to be
secure by following best practices and standards.  
This is where Identity Providers come in.

Identity Providers are 3rd party services that provide authentication and authorization services to applications.  
They allow you to outsource the management of your users and their access to your application to a 3rd party service.

There are many Identity Providers available, each with their own set of features and capabilities.  
Some of the most popular Identity Providers include:

- Auth0
- Okta
- AWS Cognito
- Azure Active Directory
- Google Identity Platform
- Keycloak

These Identity Providers provide a wide range of features, including:

- User management
- Authentication
- Authorization
- Single Sign-On (SSO)
- Multi-Factor Authentication (MFA)
- Passwordless Authentication
- Social Login
- Identity Federation
- User Provisioning
- User De-Provisioning
- User Roles and Permissions
- Audit Logging
- Reporting and Analytics
- And much more...

And to think that after only a few hours of work we barely have a subset of these features!

Now, before we can start diving into these Identity Providers, we need to understand how we can use them.  
In our application, we are just using a simple username and password to authenticate our users.  
This is not really a known authentication protocol, at least, not in the way that we are using it.  
This means that we are going to have to modify our application to support a known authentication protocol if we want to
be able to support integration with an Identity Provider.

Each authentication protocol has a defined set of rules and standards to ensure that any application utilizing that
protocol can interoperate with any Identity Provider that supports that protocol.  
For example, if you want to support JWTs (Bearer Tokens), you can select any Identity Provider that supports the "
Bearer" authentication scheme.

### Authentication Protocols

- Basic
- Bearer
- Digest
- Form-Based
- JWTs (Bearer)
- LDAP
- OIDC
- SAML
- Kerberos

We can integrate directly with all of these protocols, so that we can easily integrate with any Identity Provider.  
Let's take a look at how to implement each of these protocols.

### Basic Authentication

Basic Authentication is the simplest form of authentication.  
It involves sending a base64 encoded username and password in the HTTP headers of a request.  
So far, we have just been using a custom HTTP request and sending data in the request body to our server with the
username and password.

Let's update our server to support Basic Authentication.

**NOTE**: ASP.NET Security will not include Basic Authentication middleware due to its potential insecurity and
performance problems.  
We will instead attempt to emulate it the best we can.

#### Kotlin

```kotlin
basic("basic") {
    realm = "Basic Auth"
    validate { credentials ->
        val user = userDatabase.getUser(credentials.name)

        if (user != null && userDatabase.verifyUser(credentials.name, credentials.password)) {
            UserSession(
                user,
                UUID.randomUUID().toString(),
                System.currentTimeMillis(),
                System.currentTimeMillis() + 1000 * 60 * 60
            )
        } else {
            null
        }
    }
}
```

This should look very familiar.  
We added a `basic` authentication provider, and in the `validate` function, we are checking if the provided username and
password are valid.  
If they are, we return a `UserSession` object, otherwise, we return `null`.

To apply Basic auth protection to a route, let's make a new endpoint and apply the authentication to it:

```kotlin
authenticate("basic") {
    get("/basic") {
        call.respond(HttpStatusCode.OK, "Authenticated through Basic auth!")
    }
}
```

#### C#

C# has no out-of-the-box support for Basic auth because of the inherent security concerns.  
As a result, we are going to have to create our own Authentication Middleware!

Let's create a new file, `BasicAuthHandler.cs` that will be our Middleware.  
To start creating our middleware, create a class, `BasicAuthHandler` that extends
`AuthenticationHandler<AuthenticationSchemeOptions>`, like so:

```csharp
public class BasicAuthenticationHandler : AuthenticationHandler<AuthenticationSchemeOptions>
```

Using the IDE, we can auto-populate the required methods and we just need to implement the required methods:

```csharp
public class BasicAuthHandler : AuthenticationHandler<AuthenticationSchemeOptions>
{
    public BasicAuthHandler(IOptionsMonitor<AuthenticationSchemeOptions> options, ILoggerFactory logger, UrlEncoder encoder, ISystemClock clock) : base(options, logger, encoder, clock)
    {
    }

    public BasicAuthHandler(IOptionsMonitor<AuthenticationSchemeOptions> options, ILoggerFactory logger, UrlEncoder encoder) : base(options, logger, encoder)
    {
    }

    protected override Task<AuthenticateResult> HandleAuthenticateAsync()
    {
        throw new NotImplementedException();
    }
}
```

We want to implement the `HandleAuthenticateAsync()` method. This method will get called when the server calls
`LoginAsync`.  
We want to fill this method in so that we can handle the Basic auth protocol.  
If you are not familiar, Basic auth involves sending an HTTP header, `Authorize` with the contents `Basic <>`.  
All we need to do is handle this.

We could implement this manually, or, we can find
a [tutorial online](https://www.roundthecode.com/dotnet-tutorials/what-is-basic-authentication-how-to-add-in-asp-net-core)
and fill in the details.

With a little modification, we end up with the following handler:

```csharp
protected override Task<AuthenticateResult> HandleAuthenticateAsync()
{
    // No authorization header, so throw no result.
    if (!Request.Headers.ContainsKey("Authorization"))
    {
        return Task.FromResult(AuthenticateResult.Fail("Missing Authorization header"));
    }

    var authorizationHeader = Request.Headers["Authorization"].ToString();

    // If authorization header doesn't start with basic, throw no result.
    if (!authorizationHeader.StartsWith("Basic ", StringComparison.OrdinalIgnoreCase))
    {
        return Task.FromResult(AuthenticateResult.Fail("Authorization header does not start with 'Basic'"));
    }

    // Decrypt the authorization header and split out the client id/secret which is separated by the first ':'
    var authBase64Decoded = Encoding.UTF8.GetString(Convert.FromBase64String(authorizationHeader.Replace("Basic ", "", StringComparison.OrdinalIgnoreCase)));
    var authSplit = authBase64Decoded.Split(new[] { ':' }, 2);

    // No username and password, so throw no result.
    if (authSplit.Length != 2)
    {
        return Task.FromResult(AuthenticateResult.Fail("Invalid Authorization header format"));
    }

    // Store the client ID and secret
    var username = authSplit[0];
    var password = authSplit[1];

    // Client ID and secret are incorrect
    if (db.VerifyUser(username, password))
    {
        var user = db.GetUser(username);
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
        var claimsIdentity = new ClaimsIdentity(claims, Scheme.Name);
        
        return Task.FromResult(AuthenticateResult.Success(new AuthenticationTicket(new ClaimsPrincipal(claimsIdentity), Scheme.Name)));
    }
    
    return Task.FromResult(AuthenticateResult.Fail("Unauthorized"));
}
```

Previously, we were using Cookies to handle our auth.  
We need to update our Auth configuration to configure Basic auth instead:

```csharp
// Change this old line:
// builder.Services.AddAuthentication(CookieAuthenticationDefaults.AuthenticationScheme)
//            .AddCookie();

// To this:
builder.Services.AddAuthentication()
            .AddScheme<AuthenticationSchemeOptions, BasicAuthHandler>(BasicAuthHandler.AuthenticationScheme, null);
```

Finally, we just need to create a custom `Attribute` that we can use to label our routes:

```csharp
public class BasicAuthAttribute : AuthorizeAttribute
{
    public BasicAuthAttribute()
    {
        AuthenticationSchemes = BasicAuthHandler.AuthenticationScheme;
    }
}
```

And, now that the attribute is created, we can create a new route in our controller and label the route with our new
attribute to protect it with `BasicAuth`:

```csharp
[Route("basic")]
[HttpPost, BasicAuth]
async public Task<IActionResult> Login()
{
    return Ok("Logged in.");
}
```

---

Restart the server and immediately try to access the `/api/basic` route.  
You should immediately be prompted for a username and password.

**NOTE**: For C# users, you will not get a username/password prompt and will immediately get rejected. Just follow
along.  
However, this prompt/popup box is a classic sign of Basic Auth. Microsoft just doesn't support it.

![Basic Auth Prompt Screen](../images/basicAuth.png)

Enter the username and password of a user in our database and you should be able to access the route.  
After we have logged in, let's take a look at the network call that was made!

![Network Call Screenshot](../images/basicAuthNetwork.png)

In the network call, we see that our browser is taking our username and password and is sending them as part of the
`Authorization` header.  
This is the Basic Authentication protocol in action, simply sending the username and password encoded in the HTTP
headers.

**NOTE**: Basic auth is just encoded in base64, it is not encrypted or secure!

We can verify this by using a base64 decoder to decode the contents of our `Authorization` header.  
In my case, my header was `UXVpbm46QmFzdA==`. Decoding this results in `Quinn:Bast`.

**NOTE**: For C# users, we can instead run a CURL command with the header manually:

```bash
curl.exe -k -v -X POST -H 'Authorization: Basic UXVpbm46QmFzdA==' https://localhost:9443/api/lesson6/basic
```

You should get:

```plaintext
* Host localhost:9443 was resolved.
* IPv6: ::1
* IPv4: 127.0.0.1
*   Trying [::1]:9443...
* Connected to localhost (::1) port 9443
* schannel: disabled automatic use of client certificate
* ALPN: curl offers http/1.1
* ALPN: server accepted http/1.1
* using HTTP/1.x
> POST /api/lesson6/basic HTTP/1.1
> Host: localhost:9443
> User-Agent: curl/8.9.1
> Accept: */*
> Authorization: Basic UXVpbm46QmFzdA==
>
* Request completely sent off
< HTTP/1.1 200 OK
< Content-Type: text/plain; charset=utf-8
< Date: Mon, 28 Oct 2024 17:17:51 GMT
< Server: Kestrel
< Transfer-Encoding: chunked
<
Logged in.
* Connection #0 to host localhost left intact
```

If you want to manually instrument Basic Authentication, you can simply encode your username and password in base64 and
send it as part of the `Authorization` header.  
Any server that supports Basic Authentication will be able to decode this header and authenticate you.

Let's now try to access the `/api/session` endpoint.  
You will notice that we get the `Not authenticated` message.  
This is because we are not setting the user's session after they have authenticated.  
However, you will notice that our browser is still sending the `Authorization` header with our username and password.

This means that we are still authenticated, and we would be able to access any other route that is protected by Basic
Authentication.  
However, we don't have a session, so we can't access any routes that are protected by session authentication.  
Typically, we want to use a combination of both authentication and sessions to ensure that we don't go into an infinite
loop of authenticating the user.

We will learn more about authentication flows at the end of this lesson.

## Bearer Authentication

Bearer Authentication is a more secure form of authentication that involves sending a token in the HTTP headers of a
request.
This scheme involves sending security tokens in the `Authorization` header of an HTTP request.
These tokens are typically JWTs (JSON Web Tokens), however, they can be any type of token that you create.

For example, in lesson 2 we encoded the user's session information as a cookie?  
We could use that same encoding for our Bearer token provided that our server can decode it.

**IMPORTANT:** Bearer tokens cannot contain spaces or special characters!  
As a result, we cannot encode our `UserSession` to JSON.

What we will do is encode the `UserSession` to JSON, and then encode that JSON string into a base64 string.  
Thus, to decode this, we must first decode the base64 string, then decode the JSON string to get the user session.

Let's update our server to support Bearer Authentication.

### Kotlin

```kotlin
bearer("bearer") {
    realm = "Bearer Auth"
    authenticate { tokenCredential ->
        val decoded = Base64.getDecoder().decode(tokenCredential.token.toByteArray())
        val userSession = Json.decodeFromString<UserSession>(String(decoded))
        if (userSession != null) {
            userSession
        } else {
            null
        }
    }
}
```

This is a simple example, but you could use any type of serializer and deserializer to create any token that you want as
long as it doesn't have spaces or special characters.  
Typically, you would use JWTs here, but we will cover those in a future section.

Once we have added the Bearer Authentication protocol, we can apply it to a route in the same way that we did with Basic
Authentication:

```kotlin
authenticate("bearer") {
    get("/bearer") {
        call.respond(HttpStatusCode.OK, "Authenticated through Bearer auth!")
    }
}
```

### C#

**NOTE:** C# is going to jump right into JWTs because they are the most typical way of using Bearer tokens, and ASP.NET
doesn't really support custom bearer tokens.  
We will explain more details about how JWTs work later in this section, but for now, just understand them as a kind of
access token.

To implement Bearer auth in C#, we just need to change our authentication configuration to use a JWT Bearer instead.  
This requires adding the NuGet package `Microsoft.AspNetCore.Authentication.JwtBearer`.

First, let's create a new class, `JwtUtils`, to hold some utilities.  
The first thing we will add to this class is our secret key and our Issuer name used to hash the token:

```csharp
public class JwtUtils
{
    public static byte[] Key = "MySecretKeyThatIsExtremelySecureAndCannotBeBroken"u8.ToArray();
    public static string Issuer = "Calian";
}
```

Once added, we can do:

```csharp
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
```

This configures our application to ensure that any routes with the `[Authorize]` attribute will verify that the user has
a valid JWT before being able to access the route.

Next, we need to hand out our JWTs when a user logs in.  
This requires us to create a new utility function to generate JWTs.  
Add this method to `JwtUtils` to create a JWT for a user:

```csharp
public static string GenerateJSONWebToken(User user)
{
    var securityKey = new SymmetricSecurityKey(Key);
    var credentials = new SigningCredentials(securityKey, SecurityAlgorithms.HmacSha256);

    var token = new JwtSecurityToken(Issuer,
        Issuer,
        null,
        expires: DateTime.Now.AddMinutes(120),
        signingCredentials: credentials);

    return new JwtSecurityTokenHandler().WriteToken(token);
}
```

Once we have a method to generate JWTs, we need to update our login route a little bit to return the JWT to the
client.  
For posterity, I am just going to create an entirely new route to avoid clobbering the old one.

```csharp
[Route("loginJwt")]
[HttpPost]
async public Task<IActionResult> LoginJwt(UserLoginRequest request)
{
    if (db.VerifyUser(request.username, request.password))
    {
        // Get the user after they logged in.
        var user = db.GetUser(request.username);

        return Ok(new { token = JwtUtils.GenerateJSONWebToken(user) });
    }
    return Unauthorized("Failure.");
}
```

This route will simply check if the user's credentials are correct, and, if they are, will return a JWT access token for
future use.  
I also created another route to test our auth:

```csharp
[Route("testAuth")]
[HttpGet]
[Authorize]
public IActionResult TestAuth()
{
    return Ok("Authorized.");
}
```

Finally, we need to update our `index.html` to hit the new JWT endpoint for testing.  
Update the `index.html` to hit `/api/lesson6/loginJwt`.

```csharp
fetch("/api/lesson6/loginJwt", {
```

---

We can now try to test and see if our application is working.  
First, let's try and verify that a logged-out user cannot access the locked-down route.

Kotlin: Restart the server and immediately try to access the `/api/bearer` route.

C#: Restart the server and try to hit `/api/lesson6/testAuth` 

We will get a 401 response from the server, indicating that we need to provide a token to be granted access.
Unfortunately, we can't provide a token in the browser, so we will have to use other methods to test this.

### Kotlin Only

In order to test this endpoint, we are going to create some tokens to test (since we are not yet using JWTs). Let's create some temporary tokens to test our login:

```kotlin
fun main() {
    val db = UserDatabase()
    for (user in db.users) {
        val user = user.value
        val decodedSalt = user.salt
        println(user.username + "," + decodedSalt + "," + user.hashedPassword)

        val userSession = UserSession(
            user,
            UUID.randomUUID().toString(),
            System.currentTimeMillis(),
            System.currentTimeMillis() + 1000 * 60 * 60
        )

        // Create our tokens here <----------------------
        val base64 = Base64.getEncoder().encodeToString(Json.encodeToString(userSession).toByteArray())
        val token = Json.encodeToString(base64)
        println("Token: $token")
    }
}
```

#### Output:

```text
Quinn,QWjauRISqgp99S5WB1A5KQ==,1Jr7G1r3HaGwcbtwnFwv+Q==
Token: "eyJ1c2VyIjp7InVzZXJuYW1lIjoiUXVpbm4iLCJzYWx0IjoiUVdqYXVSSVNxZ3A5OVM1V0IxQTVLUT09IiwiaGFzaGVkUGFzc3dvcmQiOiIxSnI3RzFyM0hhR3djYnR3bkZ3ditRPT0iLCJyb2xlcyI6WyJBRE1JTiIsIlVTRVIiXX0sInNlc3Npb25JZCI6ImU2Mzg0MmZjLTE2NzItNDU3OS1hNDg1LWJkZGYwYjQ2NTlmYSIsInRpbWVDcmVhdGVkIjoxNzIxMDg1MDAyNjIyLCJleHBpcmVzQXQiOjE3MjEwODg2MDI2MjJ9"
Alice,x0UrPcVZYLECL3CkHSzDXA==,LBvdx43/vRQpx6Db5jZLaA==
Token: "eyJ1c2VyIjp7InVzZXJuYW1lIjoiQWxpY2UiLCJzYWx0IjoieDBVclBjVlpZTEVDTDNDa0hTekRYQT09IiwiaGFzaGVkUGFzc3dvcmQiOiJMQnZkeDQzL3ZSUXB4NkRiNWpaTGFBPT0iLCJyb2xlcyI6WyJWSUVXRVIiXX0sInNlc3Npb25JZCI6ImRkN2E1NzNiLTMxYTMtNDI3OS05NzcwLTliZDc1OTMxMjg5MyIsInRpbWVDcmVhdGVkIjoxNzIxMDg1MDAyOTE4LCJleHBpcmVzQXQiOjE3MjEwODg2MDI5MTh9"
Bobby,7eaYVjz1CnuxQAwCujQLyw==,1ueYrX+ch4BwkzMkXaErFw==
Token: "eyJ1c2VyIjp7InVzZXJuYW1lIjoiSm9lIiwic2FsdCI6IjdlYVlWanoxQ251eFFBd0N1alFMeXc9PSIsImhhc2hlZFBhc3N3b3JkIjoiMXVlWXJYK2NoNEJ3a3pNa1hhRXJGdz09Iiwicm9sZXMiOlsiQURNSU4iXX0sInNlc3Npb25JZCI6ImNjMDdiN2NiLTA1OWItNGQ3Zi1iZTY3LWZjMTA1MmY2ZDRiZiIsInRpbWVDcmVhdGVkIjoxNzIxMDg1MDAyOTE5LCJleHBpcmVzQXQiOjE3MjEwODg2MDI5MTl9"
```

Next, let's just update our `index.html` to add a button to test our Bearer token.

```html
<button onclick="testBearer()">Test Bearer</button>

<script>
function testBearer() {
    fetch("https://localhost:9443/api/bearer", {
        method: "GET",
        headers: {
            Authorization: "Bearer eyJ1c2VyIjp7InVzZXJuYW1lIjoiUXVpbm4iLCJzYWx0IjoiUVdqYXVSSVNxZ3A5OVM1V0IxQTVLUT09IiwiaGFzaGVkUGFzc3dvcmQiOiIxSnI3RzFyM0hhR3djYnR3bkZ3ditRPT0iLCJyb2xlcyI6WyJBRE1JTiIsIlVTRVIiXX0sInNlc3Npb25JZCI6ImU2Mzg0MmZjLTE2NzItNDU3OS1hNDg1LWJkZGYwYjQ2NTlmYSIsInRpbWVDcmVhdGVkIjoxNzIxMDg1MDAyNjIyLCJleHBpcmVzQXQiOjE3MjEwODg2MDI2MjJ9",
        }
    }).then((success) => {
        console.log("Success Bearer: ", success);
    }).catch((error) => {
        console.log("Error Bearer: ", error);
    });
}
</script>
```

---

Now, when we attempt to login, we should see a `200 OK` response from the server, indicating that you have successfully
authenticated using our token through Bearer auth!

For Kotlin users, this works alright, but there are some limitations. Specifically, this is a custom hashed token. Our
browser is unable to parse this token and thus cannot know any information about the user to make conditional rendering
possible in the UI. This is fairly limiting and eventually JWTs were created to avoid this limitation. We will talk more
about JWTs a bit later in this section.

## Form-Based Authentication

Form-Based Authentication is a more user-friendly form of authentication that involves sending a username and password.
Form-based authentication uses the HTML `<form>` element's `action` parameter as the primary mechanism for
authenticating users.

In order to use form-based auth, you must have a `<form>` element on your page that contains fields for the username and
password. When the user submits the form, the form's `onsubmit` action will send the required data to the backend server
which will then verify the user.

Form-Based Authentication is similar to Basic Authentication; however, the mechanisms involved are slightly different.

### Note the following comparisons:

- **Basic Auth** shows a dialog box, while **Form-Based Auth** requires a pre-existing HTML form.
- **Basic Auth** sends the data in HTTP headers, while **Form-Based Auth** sends the form data in the request body.
- **Basic Auth** is encoded in Base64, while **Form-Based Auth** is sent as form data and not encoded.

### Let's update our server to support Form-Based Authentication.

#### Kotlin

First, we configure `ktor` to support form-based authentication.

```kotlin
form("form") {
    userParamName = "username"
    passwordParamName = "password"
    validate { credentials ->
        val user = userDatabase.getUser(credentials.name)

        if (user != null && userDatabase.verifyUser(credentials.name, credentials.password)) {
            UserSession(
                user,
                UUID.randomUUID().toString(),
                System.currentTimeMillis(),
                System.currentTimeMillis() + 1000 * 60 * 60
            )
        } else {
            null
        }
    }
    challenge {
        call.respond(HttpStatusCode.Unauthorized, "Credentials are not valid")
    }
}
```

This is very similar to the other methods, but here, we configured the `userParamName` and `passwordParamName` to be the
names of the form fields that we will send.

Next, let's create a new route that will be protected by Form-Based Authentication:

```kotlin
authenticate("form") {
    post("/form") {
        call.respond(HttpStatusCode.OK, "Authenticated through Form-based auth!")
    }
}
```

Finally, let's make a new HTML page that will contain a form to allow us to authenticate using Form-Based
Authentication:

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Auth-Demo</title>
</head>

<body>
<div>
    <form action="/api/form" method="post">
        <input name="username" />
        <input name="password" type="password" />
        <input type="submit" value="Login" />
    </form>
</div>
</body>
</html>
```

#### C#

C# has no built-in method to accept form-based auth, but luckily it is very easy to handle. The first thing we will do
is make a new C# controller route that will accept data from the form body. To do this, we will use the `[FromForm]`
attribute:

```csharp
[Route("loginForm")]
[HttpPost]
async public Task<IActionResult> LoginForm([FromForm]UserLoginRequest request)
{
    if (db.VerifyUser(request.username, request.password))
    {
        // Get the user after they logged in.
        var user = db.GetUser(request.username);
        
        // Do whatever you want here to save the user's state.
        // Let's continue returning JWTs for now...
        return Ok(new { token = JwtUtils.GenerateJSONWebToken(user) });
    }
    return Unauthorized("Failure.");
}
```

Now, to call this, we simply need to create a new HTML page that includes a `<form>` element with the data we want to
submit:

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Auth-Demo</title>
</head>

<body>
<div>
    <form action="/api/lesson6/loginForm" method="post">
        <input name="username" />
        <input name="password" type="password" />
        <input type="submit" value="Login" />
    </form>
</div>
</body>
</html>
```

---

This form does not require any JavaScript to work, and all of the intricacies are built-in to the `onSubmit` method of
the `<form>` element itself.

Now, if we navigate to our new HTML page, we should see a form that allows us to enter our username and password.

![Form Authentication](../images/formAuth.png)

After inputting our credentials, clicking "Login" will send a POST request to the server with our username and password
as form data.

![Form Authentication Network](../images/formAuthNetwork.png)

This is the Form-Based Authentication protocol.  
This protocol is a fairly simple protocol and requires a minimal amount of effort if you are just trying to put
something together.

## Digest Authentication

The Digest Authentication protocol is a more secure form of authentication that involves sending a hashed password in
the HTTP headers of a request.  
This is in contrast to Basic Authentication, which sends the username and password encoded in base64.

Digest uses MD5 hashing along with a continuously increasing nonce value to ensure that the password is secure and
protected from "replay" attacks.  
If the server accepts a request that uses an old nonce value, the server will know that it is a "replay" (or
man-in-the-middle attempt), and prevent the login since the nonce will not be valid anymore.  
The nonce is similar to a salt, but it is not stored in the database, it is generated on the fly by the server and sent
to your client.  
As a result, a man-in-the-middle will not be able to sniff your packets and try to login with the same packets that were
captured since the nonce will already be out of date.

Because the password is hashed, it is more secure than Basic Authentication, and you can use Digest authentication
without TLS, but it is not recommended.

The generated hash is:

```plaintext
HA1 = MD5("username:realm:password") // String 1
HA2 = MD5("method:digestURI")        // String 2

response = MD5(HA1:nonce:HA2)        // Final token is concatenated with the nonce
```

A typical Digest Authentication flow would look like this:

1. The client sends a request to the server.
2. The server responds with a `401 Unauthorized` response and a `WWW-Authenticate` header that indicates the parameters
   to use for Digest authentication, including the `realm` and `nonce` values.
3. The client sends a new request with the `Authorization` header that contains the hashed `response` value.

Let's update our server to support Digest Authentication.

> **Note**: C# does not support Digest Auth. For good reason. It's a bit outdated, fairly complex, and requires your
> database to store passwords as plaintext to validate against.  
> Microsoft seems to simply recommend either JWTs or using an OAuth service, and I would have to agree with their
> direction.  
> However, if you are
> interested, [this article implements Digest in C# by hand](https://callumhoughton18.github.io/Personal-Site/blog/digest-auth-in-dotnet/).  
> Give it a shot if you want to learn about it.

### Kotlin

```kotlin
digest("digest") {
    realm = "Digest Realm"
    digestProvider { userName, realm ->
        // Needs to respond with an MD5 hash of the `HA1` value, ie, `md5(username:realm:password)`
        val md5Encoder = MessageDigest.getInstance("MD5")
        md5Encoder.digest("$userName:$realm:${userDatabase.getUser(userName)?.hashedPassword}".toByteArray(UTF_8))
    }
    validate { credentials ->
        // The digest provider will provide the `HA1` value, so we just need to check if the user exists after being validated.
        val user = userDatabase.getUser(credentials.userName)
        if (user != null) {
            UserSession(
                userDatabase.getUser(credentials.userName)!!,
                UUID.randomUUID().toString(),
                System.currentTimeMillis(),
                System.currentTimeMillis() + 1000 * 60 * 60
            )
        } else {
            null
        }
    }
}
```

Here, we configure our `digestProvider` which is used to generate the MD5 hash to validate the user's MD5 hash against.
We also configure the `validate` function to check if the user exists after being validated.

Let's give this a try. We will need to create a new route that is protected by Digest Authentication:

```kotlin
authenticate("digest") {
    get("/digest") {
        call.respond(HttpStatusCode.OK, "Authenticated through Digest auth!")
    }
}
```

Try accessing the Digest route, and you should be prompted for a username and password. Unfortunately, no matter what
you enter, you will not be able to authenticate. Click "Cancel" and we will see the `401 Unauthorized` response from the
server, as well as see the `WWW-authenticate` header which told the browser to trigger Digest Auth.

![WWW-authenticate header](../images/DigestWwwAuthHeader.png)

### Why couldn't we login?

We are storing our passwords as hashed values in our database. When we configured our `digestProvider` to generate the
`HA1` value, we used the hashed password from the database.

This is a problem because the `HA1` value is the MD5 hash of the `username:realm:password` string, but we don't know the
password!

This means in order for the server to authenticate the user using Digest Auth, the server needs to know about the
plaintext passwords for every user account. This is insecure as it violates our data at rest security. We don't want to
store plaintext passwords in our database. So... lesson learned, don't use Digest Auth!

But, to see how this works, let's just manually hard-code a password here:

```kotlin
// Needs to respond with an MD5 hash of the `HA1` value, ie, `md5(username:realm:password)`
val md5Encoder = MessageDigest.getInstance("MD5")
md5Encoder.digest("$userName:$realm:Password".toByteArray(UTF_8))
```

We set `Password` to be the password for every user here.

Let's re-run our server and try to access the `/api/digest` route. If we login with ANY username and the password
`Password`, we should be able to authenticate.

![Digest Authentication](../images/DigestAuth.png)

Digest Authentication is not typically used.

## JSON Web Tokens (JWTs)

Now that the basics are out of the way, we are starting to get into the good stuff!  
While the previously mentioned protocols are simple, they are not typically used in modern applications and
frameworks.  
The next three protocols, JWTs, LDAP, and OIDC, are the most commonly used in modern applications and are typically the
most supported by Identity Providers.

[JSON Web Token (JWT)](https://jwt.io/) is an open standard that defines a way for securely transmitting information
between parties as a JSON object.  
The information that is sent can be trusted since it is signed using a shared secret (with the HS256 algorithm) or a
public/private key pair (for example, RS256).

JWTs are typically used alongside the Bearer Authentication scheme, where the token is sent in the `Authorization`
header of an HTTP request.  
While JWTs can use the Bearer Authentication protocol, Ktor and C# both have first-class support for using JWTs because
they are so commonly used.  
Let's instrument our server to provide JWTs to our users.

The flow for using JWTs, however, is slightly different.  
Recall, when using the Bearer Authentication protocol, we needed to generate "tokens" for each user ahead of time?  
With JWTs, the users log in to our application first, and then, after they have logged in, we generate them a JWT.  
This JWT is then sent to the user, and they can use this JWT to authenticate themselves with our server on subsequent
requests.

This is how most software applications work today.  
So, let's update our server to support JWTs.

The first thing we need to do is update our `/login` route to generate a JWT for the user after they have logged in.

### Kotlin

```kotlin
if (user != null && userDatabase.verifyUser(login.username, login.password)) {
    // Create a JWT Token after validating the user
    val token = JWT.create()
        .withAudience("auth-demo")
        .withIssuer("0.0.0.0")
        .withClaim("username", user.username)
        .withExpiresAt(Date(System.currentTimeMillis() + 60000)) // 60 Seconds
        .sign(Algorithm.HMAC256("1234567890"))

    call.respond(HttpStatusCode.OK, hashMapOf("token" to token))
}
```

**Note:** Using `.withClaim` is how you can add custom data to your JSON object. Each "claim" is a key-value pair that
is added to the JWT.

Now that we have generated a JWT for the user, we need to update our server to support JWTs.  
We can do this by adding a `jwt` authentication provider to our server.

```kotlin
jwt("jwt") {
    realm = "Jwt Auth"
    // Ensure that the JWT is signed with the correct algorithm and secret.
    verifier(
        JWT
        .require(Algorithm.HMAC256("1234567890"))
        .withAudience("auth-demo")
        .withIssuer("0.0.0.0")
        .build()
    )
    // Validate the JWT payload
    validate { credential ->
        // Here, since we are only generating tokens after valid logins, we will just check if the token is expired or not
        val username = credential.payload.getClaim("username").asString()!!
        if (credential.expiresAt!!.after(Date())) {
            println("Validated JWT token. User: $username")
            UserSession(
                userDatabase.getUser(username)!!,
                UUID.randomUUID().toString(),
                System.currentTimeMillis(),
                System.currentTimeMillis() + 1000 * 60 * 60
            )
        } else {
            null
        }
    }
    // Finally, if the JWT is invalid, we will respond with a 401 Unauthorized
    challenge { defaultScheme, realm ->
        call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
    }
}
```

Now, we just need to protect some routes with JWT authentication.  
This will require the user to provide their JWT in the `Authorization` header of the request in order to be verified.

```kotlin
authenticate("jwt") {
    get("/jwt") {
        call.respond(HttpStatusCode.OK, "Authenticated through JWT auth!")
    }
}
```

### C#

We already have an endpoint that logs in a user and returns a JWT token. However, we are missing something.  
Our JWT generation doesn't include any data about the user!

Looking at the `JwtUtils` class, we have the following:

```csharp
var token = new JwtSecurityToken(Issuer,
    Issuer,
    null,
    expires: DateTime.Now.AddMinutes(120),
    signingCredentials: credentials);
```

The `null` value here is actually supposed to be an ASP.NET Claims Principal that is able to describe our user.  
If we log in to our application and look at our JWT, we can decode the JWT to find out that it contains no user
information.

Throw this into [JWT.io](https://jwt.io/) and see for yourself.

```plaintext
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MzAxNjE1NjYsImlzcyI6IkNhbGlhbiIsImF1ZCI6IkNhbGlhbiJ9.hgyQ0-L_wAG-H0okxyScR7EroSDQ0aY_dotBitDHDfU
```

To add some user information to this JWT, we need to create a Claims Principal and populate it:

```csharp
// Create some "Claims" for ASP.NET to know about our user's information
var claims = new List<Claim>
{
    new Claim(ClaimTypes.Name, user.username),
    new Claim(ClaimTypes.Expiration, new DateTime().AddMinutes(120).ToString(CultureInfo.InvariantCulture)),
};

foreach (var userRole in user.Role)
{
    claims.Add(new Claim(ClaimTypes.Role, userRole.ToString()));
}

var token = new JwtSecurityToken(Issuer,
    Issuer,
    claims,
    expires: DateTime.Now.AddMinutes(120),
    signingCredentials: credentials);
```

---

Now that we have given some Claims, let's try to log in again.

Once thing to notice is that, because we cannot set HTTP headers in the browser, we will also need to update our `index.html` to handle this.

We will need to:
- Add a variable that stores the JWT we get after logging in
- Add a function that tests sending the JWT in the `Authorization` header

The new code is shown below:

```html
<button onclick="testJwt()">Test JWT</button>

<script>

let jwt = "";

// Note: Store the JWT after parsing it from the login endpoint
function login() {
    var inputUsername = document.getElementById("login_username").value;
    var inputPassword = document.getElementById("login_password").value;

    fetch("https://localhost:9443/api/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            username: inputUsername,
            password: inputPassword,
        }),
    }).then((success) => {
        // Read the JWT from the response body.
        // Store it in global JWT variable
        success.json().then((data) => {
            jwt = data.token;
            console.log("JWT: ", jwt);
        });
        console.log("Success: ", success);
    }).catch((error) => {
        console.log("Error: ", error);
    });
}

// New function to test JWT header
function testJwt() {
    fetch("https://localhost:9443/api/jwt", {
        method: "GET",
        headers: {
            Authorization: "Bearer " + jwt,
        }
    }).then((success) => {
        console.log("Success JWT: ", success);
    }).catch((error) => {
        console.log("Error JWT: ", error);
    });
}
</script>
```

Now that we have added everything to our client, let's give it a try!
Restart the server, and navigate to `index.html`.
After logging in, we can view the network call and see that our server has replied with a `token` in the response body.

![JWT Token](../images/JwtToken.png)

Now, if we click the `Test JWT` button, we should see a `200 OK` response from the server, indicating that we have
successfully authenticated using a JWT.

![JWT Success](../images/JwtSuccess.png)

### Components of a JWT

JWTs are used almost everywhere. Luckily, there is a website that allows you to encode and decode JWTs yourself.

Let's try it out and see how JWTs are made! Navigate to the [JWT.io](https://jwt.io) website and paste the token we
received from the server into the "Encoded" section.

![JWT Decoder](../images/JwtDecoder.png)

Using the decoder, we can see the various components that make up the JWT.
JWTs have three parts:

- **Header**: Contains the algorithm and type of the token.
- **Payload**: Contains the data that is being sent.
- **Signature**: Contains the signature of the token.

Note the "Verify signature" box:

```plaintext
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret
)
```

This shows us the various components of a JWT. We see that the header and payload are both just base64 encoded strings,
while the signature itself is a hash of the header and payload using the server's secret key.

We can verify this ourselves. Let's decode the JWT's header and payload components using a base64 decoder. As you would
expect, the header and payload come out to be exactly what the JWT.io website shows us.

This means that for any JWT you receive, you can determine the header and payload by simply decoding them as base64
strings. As a result, you should not send any sensitive information in the JWT's payload. Typically, you just want to
include your user's username, maybe the user ID, their roles, to name a few. You should not be sending any sensitive
information like passwords or credit card numbers in the JWT's payload.

So how do we verify that the JWT is valid? It all comes down to the signature. The signature is a hash of the header and
payload using the server's secret key. If the hash of the signature matches what the server hashes the contents as, it
means that the JWT's hash was generated using the same secret key as the server. Otherwise, if the hashes do not match,
it means the JWT is invalid, and the payload was manipulated.

It should be obvious, but do not share your secret key and encryption algorithm, otherwise anyone can generate a JWT
token and authenticate as any user in your system. The JWT.io website allows you to set your secret keys and algorithms,
so you can test your JWTs with different configurations.

By default, the secret keys are not set, but we know that our server is using a secret key of "1234567890". Let's fill
in our secret key in the JWT.io website.

Next, let's try to change our JWT's payload. We can try changing our `username` to attempt to hijack a session of a
different user.

- Change the `username` claim to "Alice" on the JWT website.
- Update the `exp` field to be `9999999999`, some time way in the future, so that our token is not expired.

Once we have made these changes, we can copy the new encoded JWT token from the left side of the website. Let's try to
use this new JWT to log in to our server. What we will do is update our `index.html` website to hard-code this new
token.

![JWT Manipulation](../images/JwtManipulation.png)

Now let's try. Restart the server, and navigate to `index.html`. Since we have hard-coded our JWT, we can immediately
click the "Test JWT" button.

![JWT Hijack](../images/JwtHijack.png)

Our server has successfully authenticated us as Alice, despite us never knowing Alice's password!

#### Why did this happen?

We were able to do this because of two things:

1. We knew the secret key that the server was using to sign the JWT.
2. We knew our server's validation method.

This was our server's validation method:

```kotlin
if (credential.expiresAt!!.after(Date())) { // <-----------------
    println("Validated JWT token. User: $username")
    UserSession(
        userDatabase.getUser(username)!!, // <----------------------
    )
}
```

We only check two things: the expiration, and if the username is in our database. Because we knew this information, we
were able to generate a new JWT token that had valid information in it.

#### Is this concerning?

No! We are using a secret key to sign our JWTs, and we explicitly told the JWT.io website our secret key. Thus, the
token that JWT.io generated is just as valid as any token that our server could generate.

Just don't tell people your secret key and you're safe!

## Summary

So far we have covered 4 of the 6 different authentication protocols that you can use.  
Each of these protocols has their own use cases and are used in different scenarios.  
Here is a quick summary of each of the protocols that we have covered so far:

| Method     | Protocol Description                                                                                                                                                                      |
|------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Basic      | Sends an `Authorization` HTTP header with the following contents: `Basic <data>` where `<data>` is a base64 encoding of the "username:password" string.                                   |
| Bearer     | Sends an `Authorization` HTTP header with the following contents: `Bearer <token>` where `<token>` is any serializable token that your server can handle.                                 |
| Form-Based | Sends the username and password as form data in the request body.                                                                                                                         |
| Digest     | Sends an encrypted MD5 Hash of the `username:realm:password` string in the `Authorization` HTTP header. Insecure because it requires the server to know the user's raw unhashed password. |
| JWTs       | An extension of `Bearer`. Sends an `Authorization` HTTP Header with the following contents: `Bearer <token>` where `<token>` is a JWT.                                                    |

There are three more Authentication Protocols that we did not cover, LDAP, Kerberos, SAML, and OIDC.

Both LDAP and OIDC require running separate third-party servers in order to integrate authentication with them.  
As a result, they are a bit more involved and will be covered in their own sections.

In the [next lesson](./7_ldap.md), we will cover the LDAP protocol and explain how you can use it to authenticate users using an
external LDAP server.  
[Following that](./8-oidc.md), we will cover the OpenID Connect (OIDC) protocol and explain how you can use OIDC to authenticate with
3rd party authentication providers.