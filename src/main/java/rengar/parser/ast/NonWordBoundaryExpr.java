package rengar.parser.ast;

public class NonWordBoundaryExpr extends AnchorExpr {

    @Override
    public String genJsonExpression() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(String.format("\"id\": %d,",getExprId()));
        sb.append("\"type\": \"Anchor\"");
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String genString() {
        return "\\B";
    }

    @Override
    public AnchorExpr copy() {
        NonWordBoundaryExpr newNonWordBoundaryExpr = new NonWordBoundaryExpr();
        newNonWordBoundaryExpr.setExprId(getExprId());
        return newNonWordBoundaryExpr;
    }
}
