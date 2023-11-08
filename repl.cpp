#include <iostream>
#include "parser.cpp"

using namespace std;

int repl()
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
        result = parse(response);
        cout << result;
    }
    return 0;
}