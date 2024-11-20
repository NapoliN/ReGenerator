package rengar.parser.ast;

import com.alibaba.fastjson.JSONObject;

// (?>X)
// https://stackoverflow.com/questions/50524/what-is-a-regex-independent-non-capturing-group
public class IndependentGroupExpr extends GroupExpr {
    public IndependentGroupExpr(RegexExpr body) {
        super(body);
    }

    @Override
    public JSONObject genJsonExpression(){
        JSONObject json = new JSONObject();
        json.put("id", getExprId());
        json.put("type", "Group");
        json.put("body", getBody().genJsonExpression());
        return json;
    }

    @Override
    public String genString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(?>");
        sb.append(getBody().genString());
        sb.append(')');
        return sb.toString();
    }

    @Override
    public IndependentGroupExpr copy() {
        IndependentGroupExpr newIndependentGroupExpr = new IndependentGroupExpr(getBody().copy());
        newIndependentGroupExpr.setExprId(getExprId());
        return newIndependentGroupExpr;
    }
}