package rengar.parser.ast;

import com.alibaba.fastjson.JSONObject;

// ^
public class BeginExpr extends AnchorExpr {

    @Override
    public JSONObject genJsonExpression(){
        JSONObject json = new JSONObject();
        json.put("id", getExprId());
        json.put("type", "Anchor");
        json.put("subtype", "Begin");
        return json;
    }

    @Override
    public String genString() {
        return "^";
    }

    @Override
    public BeginExpr copy() {
        BeginExpr newBeginExpr = new BeginExpr();
        newBeginExpr.setExprId(getExprId());
        return newBeginExpr;
    }
}
