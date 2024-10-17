package rengar.parser.ast;

import java.util.*;

// just a wrapper
public class RegexExpr extends Expr {
    private final BranchExpr expr;

    // cache index to each expr
    private Map<Integer, Expr> exprMap = new HashMap<>();

    public RegexExpr(BranchExpr expr) {
        this.expr = expr;
        this.setExprId(0);
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
        System.out.println("Copy RegexExpr");
        RegexExpr newRegexExpr = new RegexExpr(expr.copy());
        newRegexExpr.setExprId(getExprId());
        return newRegexExpr;
    }

    public Expr getCommonAncestor(int id1, int id2) {
        Expr expr1 = getExprById(id1, expr);
        Expr expr2 = getExprById(id2, expr);
        if (expr1 == null || expr2 == null) {
            return null;
        }
        expr1.setAncestors();
        expr2.setAncestors();
        int i = 0;
        while (i < expr1.ancestors.size() && i < expr2.ancestors.size() && expr1.ancestors.get(i) == expr2.ancestors.get(i)) {
            i++;
        }
        return expr1.ancestors.get(i - 1);
    }

    private Expr getExprById(int id, Expr expr) {
        if (exprMap.containsKey(id)) {
            return exprMap.get(id);
        }
        Expr tmp = expr;
        exprMap.put(tmp.getExprId(), tmp);
        if (tmp.getExprId() == id) {
            return tmp;
        }
        switch (tmp) {
            case BranchExpr branchExpr:
                for (Expr e : branchExpr.getBranchs()) {
                    var result = getExprById(id, e);
                    if (result != null)
                        return result;
                }
                break;
            case LoopExpr loopExpr:
                var result = getExprById(id, loopExpr.getBody());
                if (result != null)
                    return result;
                break;
            case SequenceExpr sequenceExpr:
                for (Expr e : sequenceExpr.getExprs()) {
                    result = getExprById(id, e);
                    if (result != null)
                        return result;
                    }
                break;
            default:
                break;
        }
        return null;
    }
}
