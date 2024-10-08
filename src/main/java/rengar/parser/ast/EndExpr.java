package rengar.parser.ast;

// $
public class EndExpr extends AnchorExpr {

    @Override
    public String genJsonExpression(){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(String.format("\"id\": %d,",getExprId()));
        sb.append("\"type\": \"Anchor\", ");
        sb.append("\"subtype\": \"End\"");
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String genString() {
        return "$";
    }

    @Override
    public EndExpr copy() {
        EndExpr newEndExpr = new EndExpr();
        newEndExpr.setExprId(getExprId());
        return newEndExpr;
    }
}