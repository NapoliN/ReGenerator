package rengar.parser.ast;

// ^
public class BeginExpr extends AnchorExpr {

    @Override
    public String genJsonExpression(){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(String.format("\"id\": %d,",getExprId()));
        sb.append("\"type\": \"Anchor\", ");
        sb.append("\"subtype\": \"Begin\"");
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String genString() {
        return "^";
    }

    @Override
    public BeginExpr copy() {
        BeginExpr newBeginExpr = new BeginExpr();
        newBeginExpr.setExprId(getExprId());
        return newBeginExpr;
    }
}
