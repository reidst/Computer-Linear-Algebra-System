#include <iostream>

using namespace std;

string eval(string response)
{
    string r = response;
    return r;
}

int main()
{
    cout << "Welcome to the REPL.";
    while (true)
    {
        cout << "\n>> ";
        string response;
        getline(cin, response);
        if (response == "exit()") {
            break;
        }
        string result;
        result = eval(response);
        cout << result;
    }
    return 0;
}