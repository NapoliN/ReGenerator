package rengar.parser.ast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class RegularBackRefExpr extends BackRefExpr {
    private final int index;
    public RegularBackRefExpr(int index) {
        this.index = index;
    }
    public int getIndex() {
        return index;
    }

    @Override
    public JSONObject genJsonExpression(){
        JSONObject json = new JSONObject();
        json.put("id", getExprId());
        json.put("type", "BackRef");
        json.put("index", index);
        return json;
    }

    @Override
    public String genString() {
        return String.format("\\%d", index);
    }

    @Override
    public RegularBackRefExpr copy() {
        RegularBackRefExpr newRegularBackRefExpr = new RegularBackRefExpr(index);
        newRegularBackRefExpr.setExprId(getExprId());
        return newRegularBackRefExpr;
    }
}
