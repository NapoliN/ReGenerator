package rengar.parser.ast;

import com.alibaba.fastjson.JSONObject;

// $
public class EndExpr extends AnchorExpr {

    @Override
    public JSONObject genJsonExpression(){
        JSONObject json = new JSONObject();
        json.put("id", getExprId());
        json.put("type", "Anchor");
        json.put("subtype", "End");
        return json;
    }

    @Override
    public String genString() {
        return "$";
    }

    @Override
    public EndExpr copy() {
        EndExpr newEndExpr = new EndExpr();
        newEndExpr.setExprId(getExprId());
        return newEndExpr;
    }
}