package rengar.parser.ast;

public class RegularBackRefExpr extends BackRefExpr {
    private final int index;
    public RegularBackRefExpr(int index) {
        this.index = index;
    }
    public int getIndex() {
        return index;
    }

    @Override
    public String genJsonExpression(){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(String.format("\"id\": %d,",getExprId()));
        sb.append("\"type\": \"BackRef\"");
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String genString() {
        return String.format("\\%d", index);
    }

    @Override
    public RegularBackRefExpr copy() {
        RegularBackRefExpr newRegularBackRefExpr = new RegularBackRefExpr(index);
        newRegularBackRefExpr.setExprId(getExprId());
        return newRegularBackRefExpr;
    }
}
