package rengar.parser.ast;

import com.alibaba.fastjson.JSONObject;

import rengar.parser.ast.LoopExpr.LoopType;

public class LoopExpr extends Expr {
    public enum LoopType {
        Greedy, Lazy, Possessive
    }
    private int min;
    private int max;
    private final LoopType type;
    private Expr body;

    public LoopExpr(int min, int max, LoopType type, Expr body) {
        this.min = min;
        this.max = max;
        this.type = type;
        this.body = body;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() { return min; }

    public int getMax() { return max; }

    public LoopType getType() { return type; }

    public Expr getBody() { return body; }

    public void setBody(Expr expr) {
        body = expr;
    }

    public JSONObject genJsonExpression() {
        JSONObject json = new JSONObject();
        json.put("id", getExprId());
        json.put("type", "Loop");
        json.put("min", min);
        json.put("max", max);
        json.put("strategy", type.toString());
        json.put("body", body.genJsonExpression());
        return json;        
    }

    @Override
    public String genString() {
        StringBuilder sb = new StringBuilder();
        sb.append(body.genString());
        if (min == 0 && max == -1)
            sb.append('*');
        else if (min == 1 && max == -1)
            sb.append('+');
        else if (min == 0 && max == 1)
            sb.append('?');
        else if (min == max)
            sb.append(String.format("{%d}", min));
        else if (max == -1)
            sb.append(String.format("{%d,}", min));
        else
            sb.append(String.format("{%d,%d}", min, max));
        if (type == LoopType.Lazy)
            sb.append('?');
        else if (type == LoopType.Possessive)
            sb.append('+');
        return sb.toString();
    }

    public LoopExpr copy() {
        Expr newExpr = body.copy();
        LoopExpr newLoopExpr = new LoopExpr(min, max, type, newExpr);
        newLoopExpr.setExprId(getExprId());
        newExpr.setParent(newLoopExpr);
        return newLoopExpr;
    }
}
