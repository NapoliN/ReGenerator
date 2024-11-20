package rengar.parser.ast;

import com.alibaba.fastjson.JSONObject;

public class NonWordBoundaryExpr extends AnchorExpr {

    @Override
    public JSONObject genJsonExpression() {
        JSONObject json = new JSONObject();
        json.put("id", getExprId());
        json.put("type", "Anchor");
        json.put("subtype", "NonWordBoundary");
        return json;
    }

    @Override
    public String genString() {
        return "\\B";
    }

    @Override
    public AnchorExpr copy() {
        NonWordBoundaryExpr newNonWordBoundaryExpr = new NonWordBoundaryExpr();
        newNonWordBoundaryExpr.setExprId(getExprId());
        return newNonWordBoundaryExpr;
    }
}
