package AbstractSyntaxTree;

public sealed interface Expression permits Assignment, BinaryOperation, FunctionExpression, UnaryOperation, ValueExpression, Variable { }
