#include <lexy/dsl.hpp>
#include <lexy/action/parse.hpp>
#include <lexy/input/string_input.hpp>
#include "ast.hpp"
#include "ast.hpp"

namespace
{
namespace grammar
{
    namespace dsl = lexy::dsl;

    struct production
    {
        static constexpr auto whitespace = dsl::ascii::space;

        static constexpr auto rule = [] {
            auto integer = dsl::integer<int>;
            return dsl::twice(integer, dsl::sep(LEXY_LIT("/")));
        }();
        static constexpr auto value = lexy::construct<Scalar>;
    };
}
}

Scalar parse(string response)
{
    auto input = lexy::string_input(response);
    Scalar r = lexy::parse<grammar::value>(input)
    return r;
}