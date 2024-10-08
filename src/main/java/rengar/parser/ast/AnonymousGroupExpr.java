package rengar.parser.ast;

// (?:X)
public class AnonymousGroupExpr extends GroupExpr {
    public AnonymousGroupExpr(RegexExpr body) {
        super(body);
    }

    @Override
    public String genJsonExpression(){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(String.format("\"id\": %d,",getExprId()));
        sb.append("\"type\": \"Group\", ");
        sb.append("\"subtype\": \"Anonymous\", ");
        sb.append("\"body\": ");
        sb.append(getBody().genJsonExpression());
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String genString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(?:");
        sb.append(getBody().genString());
        sb.append(')');
        return sb.toString();
    }

    @Override
    public AnonymousGroupExpr copy() {
        AnonymousGroupExpr newAnonymousGroupExpr = new AnonymousGroupExpr(getBody().copy());
        newAnonymousGroupExpr.setExprId(getExprId());
        return newAnonymousGroupExpr;
    }
}