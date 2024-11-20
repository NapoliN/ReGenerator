package rengar.parser.ast;

import com.alibaba.fastjson.JSONObject;

public class RegularGroupExpr extends GroupExpr {
    private final int index;
    public RegularGroupExpr(RegexExpr body, int index) {
        super(body);
        this.index = index;
    }

    @Override
    public JSONObject genJsonExpression() {
        JSONObject json = new JSONObject();
        json.put("id", getExprId());
        json.put("type", "Group");
        json.put("body", getBody().genJsonExpression());
        return json;
    }

    public int getIndex() {
        return index;
    }
    @Override
    public RegularGroupExpr copy() {
        RegularGroupExpr newRegularGroupExpr = new RegularGroupExpr(getBody().copy(), index);
        newRegularGroupExpr.setExprId(getExprId());
        return newRegularGroupExpr;
    }
}
