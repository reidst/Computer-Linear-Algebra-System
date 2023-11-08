package AbstractSyntaxTree;

import java.util.List;

public class StatementBlock {
    List<Statement> statements;

    public StatementBlock(List<Statement> statements){
        this.statements = statements;
    }

    public List<Statement> getStatements() { return statements; }
}
