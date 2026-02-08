# Data at Rest

**NOTE:** This Document is maintained in Gitlab and mirrored to Confluence.  
Make changes to the [Gitlab repository](https://gitlab.com/calianat/software/training/auth-training).

Data at rest refers to data that is stored on a physical medium, such as JSON files, databases, or other storage
systems.  
In our demo application, this refers to our hard-coded user data.

This data is vulnerable to theft through unauthorized access, and must be protected.  
Corporations need to be careful when managing data at rest, as this is a common target for hackers.  
[In 2023, 32% of cyberattacks were data breaches](https://www.ibm.com/downloads/cas/L0GKXDWJ).

In this section, we will look at some of the ways we can prevent data theft and, if theft occurs, how we can minimize
the damage.

## Encryption Methods

Just as we encrypt data in transit, we can also encrypt data at rest.  
However, the methods used to encrypt data at rest are different from those used to encrypt data in transit.

Data in transit needs to be accessible by both the sender and the receiver, so the encryption method must be
reversible.  
Data at rest, on the other hand, does not need to be accessed by anyone other than the application.  
This means that certain parts of the data can be encrypted in a way that is not reversible.

This is important because if a hacker gains access to the data, they will not be able to decrypt it.  
This is especially important for sensitive data, such as passwords, credit card information, and other data that should
never be stored in plaintext.

### Hashing

Hashing is a one-way method of encrypting data. This means that once data is hashed, it cannot be decrypted.  
This is important, as it ensures that anyone who has access to the data will never be able to read it.

#### C#

Let's take a look
at [how to hash in C#](https://learn.microsoft.com/en-us/troubleshoot/developer/visualstudio/csharp/language-compilers/compute-hash-values).

This tutorial makes use of 2 libraries, but the main one of interest is `System.Security.Cryptography.Algorithms`.

After importing this NuGet package, we can create a temporary function to operate on the passwords that we currently
have stored in our `UserDatabase`.  
Let's create a new method there called `HashString` that accepts a string and returns the hash of the string:

```csharp
public string HashString(string value)
{
    StringBuilder sb = new StringBuilder();
    using (HashAlgorithm algorithm = SHA256.Create())
    {
        foreach (byte b in algorithm.ComputeHash(Encoding.UTF8.GetBytes(value)))
        {
            sb.Append(b.ToString("X2"));
        }
    }

    return sb.ToString();
}
```

This method takes a string as input, performs a 256SHA hash against the string, and then returns the encoded string.  
To view the output of this method, let's just add this to our main function and iterate over all of the passwords in the
`UserDatabase`.

**NOTE**: Let's also make the user list public for now.

Something like this:

```csharp
var userDatabase = new AuthTraining.Controllers.lesson5.UserDatabase();
foreach (var user in userDatabase.Users)
{
    Console.Out.WriteLine(user.username + " -> " + userDatabase.HashString(user.password));
}
```

#### Kotlin

Let's take a look at [how we can hash data in Java](https://www.baeldung.com/java-password-hashing).

Let's start by going to the `UserDatabase` class and creating a new method called `hashString`.  
This function will accept a string and return a hashed version of that string.

```kotlin
fun hashString(value: String): String {
    val spec: KeySpec = PBEKeySpec(value.toCharArray(), "salt".toByteArray(), 65536, 128)
    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
    val hash: ByteArray = factory.generateSecret(spec).encoded
    return Base64.getEncoder().encodeToString(hash)
}
```

Next, let's just create a temporary `main` function to test our hashing function.

```kotlin
fun main() {
    val userDatabase = UserDatabase()
    for (user in userDatabase.users) {
        println(user.username + " -> " + userDatabase.hashString(user.password))
    }
}
```

---

Now, when we run our application we will see the hashed passwords:

#### Kotlin (PBKDF2WithHmacSHA1)

```
Quinn -> hZl8TOz/nmz52YxBeK4A8Q==
Alice -> r0lneU03AFSnZUM4gs/EAw==
Bobby -> ObfHqAjteJGVsRpOQu+oFQ==
```

CSharp looks different because we used a different hashing algorithm (SHA256), but the concept is the same:

```
Quinn -> 65FB8582FB061341A2B897BC4D1C3A9992BF6026987DFEEE573F858CF500E4E1
Alice -> 251F48AF727FCB58047EEE0E2CF87B0E9F6146900A98143148F9D5C0F6AE84F3
Bobby -> E3FE2A3FE52C9C35874478D22328281EBF754F1B35747152C13E441A9EED92DF
```

As you can see, the hashed passwords are not readable and don't look like the original passwords at all.
This is exactly what we want, as it ensures that even if a hacker gains access to the data, they will not be able to read it.

However... let's think about this a little bit more.
What if we have two users with the same password?
If we hash the password, the hashed value will be the same for both users.
This means that if a hacker gains access to the data, they will be able to see that two users have the same password.

For example, let's add some new users that share passwords:

### Kotlin

```kotlin
// Add users with the same passwords
val users = mutableListOf<User>(
    User("Quinn", "Bast", listOf(Role.ADMIN, Role.USER)),
    User("QuinnClone", "Bast", listOf(Role.ADMIN, Role.USER)),
    User("Alice", "Wonderland", listOf(Role.VIEWER)),
    User("AliceClone", "Wonderland", listOf(Role.VIEWER)),
    User("Bobby", "Tables", listOf(Role.ADMIN)),
    User("BobbyClone", "Tables", listOf(Role.ADMIN)),
)
```

### C#

```csharp
public List<User> Users { get; } =
[
    new User { username = "Quinn", password = "Bast", Role = [ UserRole.ADMIN, UserRole.USER ] },
    new User { username = "QuinnClone", password = "Bast", Role = [ UserRole.ADMIN, UserRole.USER ] },
    new User { username = "Alice", password = "Wonderland", Role = [ UserRole.VIEWER ] },
    new User { username = "AliceClone", password = "Wonderland", Role = [ UserRole.VIEWER ] },
    new User { username = "Bobby", password = "Tables", Role = [ UserRole.ADMIN ] },
    new User { username = "BobbyClone", password = "Tables", Role = [ UserRole.ADMIN ] },
];
```

---

Let's re-run our program and see the hashed passwords now!

### Kotlin (PBKDF2WithHmacSHA1)

```plaintext
Quinn -> hZl8TOz/nmz52YxBeK4A8Q==
QuinnClone -> hZl8TOz/nmz52YxBeK4A8Q==
Alice -> r0lneU03AFSnZUM4gs/EAw==
AliceClone -> r0lneU03AFSnZUM4gs/EAw==
Bobby -> ObfHqAjteJGVsRpOQu+oFQ==
BobbyClone -> ObfHqAjteJGVsRpOQu+oFQ==
```

### C# (SHA256)

```plaintext
Quinn -> 65FB8582FB061341A2B897BC4D1C3A9992BF6026987DFEEE573F858CF500E4E1
QuinnClone -> 65FB8582FB061341A2B897BC4D1C3A9992BF6026987DFEEE573F858CF500E4E1
Alice -> 251F48AF727FCB58047EEE0E2CF87B0E9F6146900A98143148F9D5C0F6AE84F3
AliceClone -> 251F48AF727FCB58047EEE0E2CF87B0E9F6146900A98143148F9D5C0F6AE84F3
Bobby -> E3FE2A3FE52C9C35874478D22328281EBF754F1B35747152C13E441A9EED92DF
BobbyClone -> E3FE2A3FE52C9C35874478D22328281EBF754F1B35747152C13E441A9EED92DF
```

Now it is very clear to anyone looking at this data that these users have the same password.
If some hacker got a dump of our database, it makes it significantly easier for them to crack.
The hacker now only needs to crack one of the hashes to gain access to multiple accounts.

Now let's also consider the scenario where our database is storing millions of users.

If we have millions of users, our database has a large enough sample size that a hacker can use a dictionary attack to guess what the most commonly used passwords are.
For example:

| Hash                     | Percentage of Occurrences | Most Common Password |
|--------------------------|---------------------------|-----------------------|
| hZl8TOz/nmz52YxBeK4A8Q== | 50%                       | password              |
| r0lneU03AFSnZUM4gs/EAw== | 30%                       | 123456                |
| ObfHqAjteJGVsRpOQu+oFQ== | 20%                       | qwerty                |

This allows a hacker to very easily guess the "top" passwords for accounts who appear to be sharing a password the most frequently.
In addition, if the hacker knows that the password is hashed, they can use a [rainbow table](https://en.wikipedia.org/wiki/Rainbow_table) to quickly crack the hash.

So how do we prevent this?

### Salting

Salting is a method of adding random data to something before hashing it.
This random data is called a salt, and it ensures that even if two users have the same password, their hashes will be
different.

Salting is important because it prevents a hacker from using a dictionary attack to guess the most commonly used
passwords, and also prevents using a rainbow table to crack the hash.

In order to use a salt, we need to generate some random data that we add on to the end of the password string before we
hash the data.
This random data is unique for each user, and is stored in the database along with the hashed password.

Let's see what this looks like:

#### Kotlin

```kotlin
fun hashString(value: String): String {
    val random: SecureRandom = SecureRandom()
    val salt = ByteArray(16)
    random.nextBytes(salt)

    val spec: KeySpec = PBEKeySpec(value.toCharArray(), salt, 65536, 128)
    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
    val hash: ByteArray = factory.generateSecret(spec).encoded
    return Base64.getEncoder().encodeToString(hash)
}
```

#### C#

```csharp
public string HashString(string value)
{
    byte[] buffer = RandomNumberGenerator.GetBytes(1024);
    string salt = BitConverter.ToString(buffer);
    var saltedPassword = value + salt;
    
    StringBuilder sb = new StringBuilder();
    using (HashAlgorithm algorithm = SHA256.Create())
    {
        foreach (byte b in algorithm.ComputeHash(Encoding.UTF8.GetBytes(saltedPassword)))
        {
            sb.Append(b.ToString("X2"));
        }
    }

    return sb.ToString();
}
```

---

In this example, we have created a random salt for each user and we are applying the salt to our hashing function.
Now if we run the main function, we can see that our hashed passwords are completely different despite our users having
the same passwords:

#### Kotlin Output

```plaintext
Quinn -> VzHtmqoDBSKGzDIR4yLQiA==
QuinnClone -> MjWRKhFTmaGHrV3PC6VwCg==
Alice -> 9ESX987UFJhgiFFkKu/GCA==
AliceClone -> oHbNbi5kyRE50oF62PCoEQ==
Bobby -> U5JtxJp7i82IrQC4h/bhTg==
BobbyClone -> LMklxwZmm3dqBZrEx2Ud6Q==
```

#### C# Output (SHA256)

```plaintext
Quinn -> 2506F076592AC79FE0BBA5BDEC53623E9493404B3976ED53122A84B21EB52CF5
QuinnClone -> 970E5284F2954F8C974BCFE2DA5C6ECEA3F1F87B0B5C7949F13CF7FD9EEDAA2C
Alice -> AA4963AA6ED78B157245A7EDC962E25447281918C6355268933D33DCC61C9C3F
AliceClone -> 62DB535B266E7635DD6842D23EA3C1F2FD6EF4C86E0DA22B92AB8C650BA35FC9
Bobby -> 8963315EF4CAE94D1E2F36A1173C494076AC31669896DC238B5026410B4E14BD
BobbyClone -> D77A43145EFEE9C8D7D022D497D017317B9EEF54F846E05CAE12A6A15DD4B7BB
```

Woohoo! We have mitigated the possibility that a hacker could use a Dictionary attack against our users.

However, this brings up another problem.
In the function we created, we are using a different randomly generated hash for each user.
If our hash is different for each user, how do we verify a user's password when they log in?

The answer: Also store the salt.
When a user logs in, we can retrieve the salt from the database, apply it to the password, and then hash the password.
Storing the salt provides no threat to the security of the password, as the salt is not a secret value.

Salting is able to prevent rainbow table attacks because the salt is unique for each user.
Even if the hacker knows the salt, they don't know how that salt was applied to the password.

For example: Assume a password of "Password" and a salt of "1234".
The hacker could guess the salt was applied at the end: "Password1234", or at the beginning: "1234Password", or even in
the middle: "Pass1234word", but there is no real way of knowing how the salt got applied before the hash was generated.
This prevents the hacker from using a rainbow table to crack the hash.
Additionally, adding a salt to the password makes the hash vary wildly, so again, a hacker cannot easily "crack" or
determine what the source would have been just by knowing the salt.

Now that we know how salts work, we need to store them in our database.
Let's make some changes.

#### Kotlin
We need to add a new field to our `User` class to store the salt. Additionally, we are going to need to explicitly
create a `LoginRequest` data type, because we need to differentiate our login request from our user data.

```kotlin
@Serializable
data class User(
    val username: String,
    val salt: String,
    val hashedPassword: String,
    val roles: List<Role> = listOf()
)

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)
```

And now we can just fix the remaining errors:

- Change the `UserDatabase` class to store the salt along with the hashed password.
- Change the `UserDatabase` class to verify the user's password using the hash.
- Update our server to use the new `LoginRequest` data type.

Additionally, I also now loaded the users into the database using the `addUser` method, as this will auto-generate a
salt for each user. Try implementing these updates yourself!

.Or see the whole class here

```kotlin
package com.calian.at.demoAuth.solutions.lesson5_hashing

import java.security.SecureRandom
import java.security.spec.KeySpec
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


interface UserDatabaseInterface {
    fun getUser(username: String): User?
    fun addUser(username: String, password: String, roles: List<Role>)
    fun verifyUser(username: String, password: String): Boolean
}

class UserDatabase : UserDatabaseInterface {

    // Pretend we are a database.
    val users = mutableMapOf<String, User>()

    init {
        addUser("Quinn", "Bast", listOf(Role.ADMIN, Role.USER))
        addUser("Alice", "Wonderland", listOf(Role.VIEWER))
        addUser("Joe", "Momma", listOf(Role.ADMIN))
    }

    override fun getUser(username: String): User? {
        return users[username]
    }

    override fun addUser(username: String, password: String, roles: List<Role>) {
        val random: SecureRandom = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)

        users[username] = User(
            username,
            Base64.getEncoder().encodeToString(salt),
            hashString(password, salt),
            roles
        )
    }

    override fun verifyUser(username: String, password: String): Boolean {
        val user = users[username]
        if (user != null) {
            return hashString(password, Base64.getDecoder().decode(user.salt)) == user.hashedPassword
        }
        return false
    }

    private fun hashString(value: String, salt: ByteArray): String {
        val spec: KeySpec = PBEKeySpec(value.toCharArray(), salt, 65536, 128)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val hash: ByteArray = factory.generateSecret(spec).encoded
        return Base64.getEncoder().encodeToString(hash)
    }
}

fun main() {
    val db = UserDatabase()
    for (user in db.users) {
        val user = user.value
        val decodedSalt = user.salt
        println(user.username + "," + decodedSalt + "," + user.hashedPassword)
    }
}
```

#### C#

Let's add a new field to our `User` class to store the salt and hashed password:

```csharp
public class User
{
    public string username { get; set; }
    public string salt { get; set; }
    public string hashedPassword { get; set; }
    public List<UserRole> Role { get; set; }
}
```

Now let's create a new `AddUser` method that is able to add new users to our database given their original password.
This will ensure that the user's salt and hash string are properly stored in the database.

```csharp
public void AddUser(string username, string password, List<UserRole> roles)
{
    byte[] buffer = RandomNumberGenerator.GetBytes(1024);
    string salt = BitConverter.ToString(buffer);
    
    Users.Add(new User
    {
        username = username,
        salt = salt,
        hashedPassword = HashString(password, salt),
    });
}
```

NOTE: Here, we also updated our `HashString` function to accept a salt instead of generate one randomly.

Finally, we want to clear the default user list, and instantiate our users in the constructor (for now).

```csharp
public List<User> Users { get; } =
[
    // new User { username = "Quinn", password = "Bast", Role = [ UserRole.ADMIN, UserRole.USER ] },
    // new User { username = "QuinnClone", password = "Bast", Role = [ UserRole.ADMIN, UserRole.USER ] },
    // new User { username = "Alice", password = "Wonderland", Role = [ UserRole.VIEWER ] },
    // new User { username = "AliceClone", password = "Wonderland", Role = [ UserRole.VIEWER ] },
    // new User { username = "Bobby", password = "Tables", Role = [ UserRole.ADMIN ] },
    // new User { username = "BobbyClone", password = "Tables", Role = [ UserRole.ADMIN ] },
];

public UserDatabase()
{
    AddUser("Quinn", "Bast", [ UserRole.ADMIN, UserRole.USER ]);
    AddUser("Alice", "Wonderland", [ UserRole.VIEWER ]);
    AddUser("Bobby", "Tables", [ UserRole.ADMIN ]);
}
```

Finally, we just need to update the `VerifyUser` method. Previously, we were just checking if the username and password
matched. Now, we just need to update the verification to ensure that the hashed password matches instead.

```csharp
public bool VerifyUser(string username, string password)
{
    return Users.Any((user) => user.username == username && user.hashedPassword == HashString(password, user.salt));
}
```

---

TODO: Lookup how to encrypt other sensitive information that is not one-way hashed

- End-to-end encryption
- Homomorphic Encryption?
- Other?

## Database Security

In addition to hashing and salting our passwords, there are other ways we can secure our data at rest. The most obvious
of these is to ensure that our database can only be accessed by authorized users. This means that we need to have strong
access controls in place to prevent unauthorized access to the database.

In addition to access controls, we also need to ensure that our database is logging all transactions. This means that we
need to keep a record of who accessed the database, when they accessed it, and what they did while they were there. This
is important because it allows us to track down any unauthorized accesses and take action against them.

### Access Controls

Controlling who can access your database is the first step into preventing unauthorized access. This can be done by
setting up user accounts and passwords, and ensuring that only authorized users have access to the database.

In addition to user accounts and passwords, you can also set up roles and permissions to control what users can do once
they have access to the database. For example, you can completely block a user from accessing certain tables, or only
allow them to read data but not write data.

Since our demo application does not have a database, we cannot demonstrate how to set up access controls. Doing this
would depend on the database you are using and the tools that are available to you.

For example, in PostgreSQL, you can create a view-only role with the following SQL commands:

```sql
CREATE ROLE data_viewer;
GRANT CONNECT ON DATABASE my_database TO data_viewer;
GRANT USAGE ON SCHEMA public TO data_viewer;
GRANT SELECT ON TABLE x,y,z TO data_viewer;
```

### Transaction Logs

The next step in securing your database is to keep a record of all transactions that occur. This means that you need to
keep a log of who accessed the database, when they accessed it, and what they did while they were there. Keeping track
of this information allows you to know:

- If someone accessed the database without authorization.
- If someone accessed the database at an unusual time.
- What data someone is accessing and if they are making any changes to it.

This information is important because it allows us to track down any unauthorized accesses and take action against them.
For example, if you see that someone accessed the database at 3 AM, you can investigate why they were accessing the
database at that time and take action if necessary.

Again, this is going to depend on the database that you are using, and the tools that are available to you. However,
with PostgreSQL, you can enable transaction logging by setting the `log_statement` parameter:

```sql
ALTER SYSTEM SET log_statement = 'all';
```

Once set, PostgreSQL server will log all transactions as output.

```plaintext
2022-08-20 22:09:12.549 JST [26756] LOG:  duration: 0.025 ms  statement: BEGIN
2022-08-20 22:09:12.550 JST [26756] LOG:  duration: 1.156 ms  statement: SELECT "store_person"."id", "store_person"."first_name", "store_person"."last_name" FROM "store_person" WHERE "store_person"."id" = 33 LIMIT 21
2022-08-20 22:09:12.552 JST [26756] LOG:  duration: 0.178 ms  statement: UPDATE "store_person" SET "first_name" = 'Bill', "last_name" = 'Gates' WHERE "store_person"."id" = 33
2022-08-20 22:09:12.554 JST [26756] LOG:  duration: 0.784 ms  statement: INSERT INTO "django_admin_log" ("action_time", "user_id", "content_type_id", "object_id", "object_repr", "action_flag", "change_message") VALUES ('2022-08-20T13:09:12.553273+00:00'::timestamptz, 1, 20, '33', 'Bill Gates', 2, '[]') RETURNING "django_admin_log"."id"
2022-08-20 22:09:12.557 JST [26756] LOG:  duration: 1.799 ms  statement: COMMIT
```

### Server Monitoring

In addition to securing your database, an additional step you can take is to secure your server as well. By making use
of Linux's `auditd` service, you can monitor all system calls and file accesses that occur on your server. This allows
you to track down any unauthorized accesses and take action against them.

Let's try this on our machines.

First, we need to install the `auditd` service:

```shell
sudo apt install auditd
```

Once installed, we can start the service and configure it to log all `execve` system calls:

```shell
$ sudo systemctl start auditd
$ auditctl -a exit,always -S execve
WARNING - 32/64 bit syscall mismatch, you should specify an arch
```

**NOTE**: Ignore the warning.

Using this program, all commands that are executed get logged to `/var/log/audit/audit.log`. We can run some random
commands, and then check to see if they were recorded.

In order to access the audit log, we need to use the `ausearch` command:

```shell
sudo ausearch -m execve
```

Typing this command, we are shown a list of all the commands that were executed on the system. Granted, the output is a
bit verbose, but it is a good way to see what is happening on your server.

```plaintext
type=EXECVE msg=audit(1721075706.064:1392): argc=3 a0="ausearch" a1="-m" a2="execve"
type=SYSCALL msg=audit(1721075706.064:1392): arch=c000003e syscall=59 success=yes exit=0 a0=55a75b6d99c8 a1=55a75b6e2318 a2=55a75b6dfc30 a3=0 items=2 ppid=247727 pid=247728 auid=1001 uid=0 gid=0 euid=0 suid=0 fsuid=0 egid=0 sgid=0 fsgid=0 tty=pts0 ses=3 comm="ausearch" exe="/usr/sbin/ausearch" subj=unconfined key=(null)
```

Here we see my own command to run `ausearch` being audited and logged by the server.

**NOTE**: To stop the auditd service, you can use `sudo systemctl disable --now auditd`.

## Conclusion

Securing your data at rest is an important step to protecting your data from unauthorized access. While it doesn't
prevent breaches, it makes it so that if a breach occurs, the data is less useful to the hacker. In addition to hashing
and salting your passwords, you should also set up access controls, transaction logs, and server monitoring to ensure
that your data is secure.

In the previous two sections, we took steps to ensure that our data was secure in transit and at
rest. [In the next section](./6_auth_protocols.md), we will begin to
look at different authentication methods that we can use in order to better secure our application. There are a number
of different authentication methods that we can use, and each has its own strengths and weaknesses.