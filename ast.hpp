#include <vector>
#include <valarray>
#include <boost/rational.hpp>


struct Statement {};

struct StatementBlock {
    std::vector<Statement> statements;
};

struct Assignment : Statement {
    Variable var;
    Expression value;
};

struct Evaluate : Statement {
    Expression exp;
};

struct Expression {};

struct Bop : Expression {
    BOperator op;
    Expression left;
    Expression right;
};

struct Uop : Expression {
    UOperator op;
    Expression exp;
};

struct Func : Expression {
    Function func;
    std::vector<Expression> args;
};

struct Variable : Expression {
    std::vector<char> name;
};

struct ValueExp : Expression {
    Value value;
};

class Value {};

class Matrix : public Value {
    public:
        std::valarray<Scalar> values;
        int row_size;
        int col_size;
        Matrix(std::valarray<Scalar> ivalues, int irow_size, int icol_size)
            : values(ivalues)
            , row_size(irow_size)
            , col_size(icol_size)
            {}
        // Identity Matrix Initializer
        Matrix(int dim)
            : values(initIdentity(dim))
            , row_size(dim)
            , col_size(dim)
            {}
        std::valarray<Scalar> initIdentity(int dim)
        {
            std::valarray<Scalar> vals = std::valarray<Scalar>(Scalar(0), dim);
            for (int i = 0; i < dim; i++)
            {
                vals[i*dim+i] = Scalar(1);
            }
            return vals;
        }
};

class Scal : public Value {
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