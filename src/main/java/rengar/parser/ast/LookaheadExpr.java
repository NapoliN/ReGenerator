package rengar.parser.ast;

import com.alibaba.fastjson.JSONObject;

public class LookaheadExpr extends LookaroundExpr {
    public LookaheadExpr(RegexExpr cond, boolean isNot) {
        super(cond, isNot);
    }

    @Override
    public JSONObject genJsonExpression(){
        JSONObject json = new JSONObject();
        json.put("id", getExprId());
        json.put("type", "Lookahead");
        json.put("body", cond.genJsonExpression());
        json.put("isNot", isNot);
        return json;
    }

    @Override
    public String genString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(?");
        if (isNot)
            sb.append('!');
        else
            sb.append('=');
        sb.append(cond.genString());
        sb.append(')');
        return sb.toString();
    }

    @Override
    public LookaheadExpr copy() {
        LookaheadExpr newLookaheadExpr = new LookaheadExpr(cond.copy(), isNot);
        newLookaheadExpr.setExprId(getExprId());
        return newLookaheadExpr;
    }
}
