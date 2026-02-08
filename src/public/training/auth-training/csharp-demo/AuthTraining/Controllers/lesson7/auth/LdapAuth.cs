using System.Collections;
using System.DirectoryServices.Protocols;

namespace AuthTraining.Controllers.lesson7.auth;

public class LdapAuth
{
    private string _ldapHostname = "localhost";
    private int _ldapPort = 389;
    private string _adminUsername = "cn=admin,dc=calian,dc=com";
    private string _adminPassword = "calian";

    private LdapConnection connection = null;

    private bool Bind(string userDn, string password)
    {
        connection = new LdapConnection(new LdapDirectoryIdentifier(_ldapHostname, _ldapPort))
        {
            AuthType = AuthType.Basic,
            Credential = new(userDn, password)
        };
        
        connection.SessionOptions.ProtocolVersion = 3;
        // If the connection is LDAPS, set this.
        //connection.SessionOptions.SecureSocketLayer = true;

        try
        {
            connection.Bind();
            return true;
        }
        catch (Exception e)
        {
            Console.Out.WriteLine(e);
            return false;
        }
    }

    public List<Dictionary<String, String>> GetLdapUser(string username, string password)
    {
        var userDn = $"""cn={username},ou=Employees,dc=calian,dc=com""";
        // First, attempt to login as the user.
        if (Bind(userDn, password))
        {
            // Rebind as an admin so we can query the user's entity and return their account details.
            Bind(_adminUsername, _adminPassword);
            return LdapSearch("ou=Employees,dc=calian,dc=com", $"""(cn={username})""");
        }

        return null;
    }

    public List<Dictionary<String, String>> GetUserRoles(string username)
    {
        Bind(_adminUsername, _adminPassword);
        return LdapSearch("ou=Roles,dc=calian,dc=com", $"""(roleoccupant=cn={username},ou=Employees,dc=calian,dc=com)""");
    }

    private List<Dictionary<String, String>> LdapSearch(string searchDn, string filter)
    {
        var results = new List<Dictionary<String, String>>();
        var request = new SearchRequest(searchDn, filter, SearchScope.Subtree, null);
        var response = (SearchResponse)connection.SendRequest(request);
        
        foreach (SearchResultEntry entry in response.Entries)
        {
            var dict = new Dictionary<String, String>();
            foreach (DictionaryEntry userAttribute in entry.Attributes)
            {
                var key = (string)userAttribute.Key;
                var value = entry.Attributes[key][0].ToString();
                dict[key] = value;
            }
            results.Add(dict);
        }

        return results;
    }
}