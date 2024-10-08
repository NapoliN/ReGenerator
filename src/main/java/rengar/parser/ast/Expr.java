package rengar.parser.ast;

import java.util.*;

public abstract class Expr {
    // used to cache the result of toString()
    protected String string;

    protected List<Expr> ancestors = new ArrayList<Expr>();

    public abstract String genString();

    public abstract String genJsonExpression();

    private int exprId = -1;

    public int getExprId() {
        return exprId;
    }

    public void setExprId(int id){
        exprId = id;
    }

    @Override
    public String toString() {
        if (string == null)
            string = genString();
        return string;
    }

    public abstract Expr copy();
    @Override
    public boolean equals(Object expr) {
        if (expr == null || this.getClass() != expr.getClass())
            return false;
        if (this == expr)
            return true;
        return genString().equals(((Expr) expr).genString());
    }

    @Override
    public int hashCode() {
        string = genString();
        return string.hashCode();
    }

    // 先祖への参照を昇順で設定する
    // ancestors[0] がrootになる
    public void setAncestors(Expr parent, List<Expr> highers){
        ancestors.addAll(highers);
        ancestors.add(parent);
    }

    public int getDepth(){
        return ancestors.size();
    }
    
    // 2つのExpressionの最近共通祖先を返す
    public static Expr getCommonAncestorExpr(Expr expr1, Expr expr2) {
        // 同じExpressionの場合はそれを返す
        if (expr1.exprId == expr2.exprId)
            return expr1;

        int d1 = expr1.getDepth();
        int d2 = expr2.getDepth();
        for (int i = Math.min(d1, d2); i >= 0; i--) {
            if (expr1.ancestors.get(i).exprId == expr2.ancestors.get(i).exprId){
                return expr1.ancestors.get(i);
            }
        }
        return null;
    }
}