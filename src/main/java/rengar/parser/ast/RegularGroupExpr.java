package rengar.parser.ast;

public class RegularGroupExpr extends GroupExpr {
    private final int index;
    public RegularGroupExpr(RegexExpr body, int index) {
        super(body);
        this.index = index;
    }

    @Override
    public String genJsonExpression() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(String.format("\"id\": %d,",getExprId()));
        sb.append("\"type\": \"Group\",");
        sb.append("\"body\": ");
        sb.append(getBody().genJsonExpression());
        sb.append("}");
        return sb.toString();
    }

    public int getIndex() {
        return index;
    }
    @Override
    public RegularGroupExpr copy() {
        RegularGroupExpr newRegularGroupExpr = new RegularGroupExpr(getBody().copy(), index);
        newRegularGroupExpr.setExprId(getExprId());
        return newRegularGroupExpr;
    }
}
