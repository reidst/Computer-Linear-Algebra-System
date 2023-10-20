#include <vector>
#include <valarray>
#include <boost/rational.hpp>


class Statement {};

class StatementBlock {
    public:
        std::vector<Statement> statements;
        StatementBlock(std::vector<Statement> istatements)
            : statements(istatements)
            {}
};

class Assignment : public Statement {
    public:
        Variable var;
        Expression value;
        Assignment(Variable ivar, Expression ivalue)
            : var(ivar)
            , value(ivalue)
            {}
};

class Evaluate : public Statement {
    public:
        Expression exp;
        Evaluate(Expression iexp)
            : exp(iexp)
            {}
};

class Expression {};

class Bop : public Expression {
    public:
        BOperator op;
        Expression left;
        Expression right;
        Bop(BOperator iop, Expression ileft, Expression iright)
            : op(iop)
            , left(ileft)
            , right(iright)
            {}
};

class Uop : public Expression {
    public:
        UOperator op;
        Expression exp;
        Uop(UOperator iop, Expression iexp)
            : op(iop)
            , exp(iexp)
            {}
};

class Func : public Expression {
    public:
        Function func;
        std::vector<Expression> args;
        Func(Function ifunc, std::vector<Expression> iargs)
            : func(ifunc)
            , args(iargs)
            {}
};

class Variable : public Expression {
    public:
        std::vector<char> name;
        Variable(std::vector<char> iname)
            : name(iname)
            {}
};

class Matrix : public Expression {
    public:
        std::valarray<Scalar> values;
        int row_size;
        int col_size;
        Matrix(std::valarray<Scalar> ivalues, int irow_size, int icol_size)
            : values(ivalues)
            , row_size(irow_size)
            , col_size(icol_size)
            {}
};

class Scal : public Expression {
    public:
        Scalar value;
        Scal(Scalar ivalue)
            : value(ivalue)
            {}
};

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
    SUB,
    MUL,
    DIV
};

enum VarType {
    MatrixT,
    ScalarT
};