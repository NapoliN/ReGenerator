package rengar.parser.ast;

public class WordBoundaryExpr extends AnchorExpr {
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
        return "\\b";
    }

    @Override
    public WordBoundaryExpr copy() {
        WordBoundaryExpr newWordBoundaryExpr = new WordBoundaryExpr();
        newWordBoundaryExpr.setExprId(getExprId());
        return newWordBoundaryExpr;
    }
}
