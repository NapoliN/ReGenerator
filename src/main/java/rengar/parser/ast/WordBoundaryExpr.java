package rengar.parser.ast;

import com.alibaba.fastjson.JSONObject;

public class WordBoundaryExpr extends AnchorExpr {
    @Override
    public JSONObject genJsonExpression() {
        JSONObject json = new JSONObject();
        json.put("id", getExprId());
        json.put("type", "Anchor");
        json.put("subtype", "WordBoundary");
        return json;
    }
    @Override
    public String genString() {
        return "\\b";
    }

    @Override
    public WordBoundaryExpr copy() {
        WordBoundaryExpr newWordBoundaryExpr = new WordBoundaryExpr();
        newWordBoundaryExpr.setExprId(getExprId());
        return newWordBoundaryExpr;
    }
}
