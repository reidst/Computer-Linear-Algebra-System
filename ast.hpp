#include <vector>
#include <boost/rational.hpp>

class Statement {};

class StatementBlock {
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
    Matrix mat;
};

class Scal : public Expression {
    Scalar value;
};

typedef std::vector<Vector> Matrix;

typedef std::vector<Scalar> Vector;

typedef boost::rational<int> Scalar;

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

enum VarType {
    MatrixT,
    ScalarT
};