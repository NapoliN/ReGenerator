package rengar.parser.ast;

// just a wrapper
public class RegexExpr extends Expr {
    private final BranchExpr expr;
    public RegexExpr(BranchExpr expr) {
        this.expr = expr;
    }

    public BranchExpr getExpr() {
        return expr;
    }

    public String genJsonExpression() {
        StringBuilder sb = new StringBuilder();
        sb.append(expr.genJsonExpression());
        return sb.toString();
    }

    @Override
    public String genString() {
        return expr.genString();
    }

    public RegexExpr copy() {
        RegexExpr newRegexExpr = new RegexExpr(expr.copy());
        newRegexExpr.setExprId(getExprId());
        return newRegexExpr;
    }
}
