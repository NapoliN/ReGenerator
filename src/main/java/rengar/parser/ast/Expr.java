package rengar.parser.ast;

import java.util.*;

public abstract class Expr {
    // used to cache the result of toString()
    protected String string;

    public Expr parent;

    public abstract String genString();

    public abstract String genJsonExpression();
    private List<Integer> ancestors; // used for cache

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

    public void setParent(Expr parent){
        this.parent = parent;
    }

    // 先祖への参照を昇順で設定する
    // ancestors[0] がrootになる
    protected List<Integer> getAncestors(){
        if (ancestors != null)
            return ancestors;
        Expr current = this;
        List<Integer> ancestors = new ArrayList<>();
        while (current != null){
            ancestors.add(0,current.getExprId());
            current = current.parent;
        }
        this.ancestors = ancestors;
        return ancestors;
        // FIXME なんかグループのとこで探索がとまる
        // たぶんgroupに親が設定されてない
    }
}