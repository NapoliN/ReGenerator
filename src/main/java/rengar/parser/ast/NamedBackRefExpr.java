package rengar.parser.ast;

public class NamedBackRefExpr extends BackRefExpr {
    private final String name;

    public NamedBackRefExpr(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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
        StringBuilder sb = new StringBuilder();
        sb.append("\\k<");
        sb.append(name);
        sb.append('>');
        return sb.toString();
    }

    @Override
    public NamedBackRefExpr copy() {
        NamedBackRefExpr newNamedBackRefExpr = new NamedBackRefExpr(new String(name));
        newNamedBackRefExpr.setExprId(getExprId());
        return newNamedBackRefExpr;
    }
}
