package rengar.parser.ast;

import com.alibaba.fastjson.JSONObject;

// (?:X)
public class AnonymousGroupExpr extends GroupExpr {
    public AnonymousGroupExpr(RegexExpr body) {
        super(body);
    }

    @Override
    public JSONObject genJsonExpression(){
        JSONObject json = new JSONObject();
        json.put("id", getExprId());
        json.put("type", "Anonymous");
        json.put("body", getBody().genJsonExpression());
        return json;
    }

    @Override
    public String genString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(?:");
        sb.append(getBody().genString());
        sb.append(')');
        return sb.toString();
    }

    @Override
    public AnonymousGroupExpr copy() {
        RegexExpr body = getBody().copy();
        AnonymousGroupExpr newAnonymousGroupExpr = new AnonymousGroupExpr(body);
        body.setParent(newAnonymousGroupExpr);
        newAnonymousGroupExpr.setExprId(getExprId());
        return newAnonymousGroupExpr;
    }
}