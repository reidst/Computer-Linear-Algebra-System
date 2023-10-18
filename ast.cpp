#include <vector>
#include <boost/rational.hpp>

class Statement {};

class StatementBlock : public Statement {
   std::vector<Statement> statements;
};

class Assignment : public Statement {
   Variable var;
   Expression value;
};

class Evaluate : public Statement {
    Expression exp;
};

class Expression {};

class Bop : public Expression {
    BOperator op;
    Expression left;
    Expression right;
};

class Uop : public Expression {
    UOperator op;
    Expression exp;
};

class Func : public Expression {
    Function func;
    std::vector<Expression> args;
};

class Variable : public Expression {
    std::vector<char> name;
};

class Mat : public Expression {
    std::vector<Vector> columnVectors;
};

class Vector : public Expression {
    std::vector<boost::rational<int>> values;
};

class Scalar : public Expression {
    boost::rational<int> value;
};

enum Function {
    INVERSE,
    RREF,
    EF,
    SPAN,
    DETERMINANT
};

enum UOperator {
    NEG
};

enum BOperator {
    ADD,
    MUL
};