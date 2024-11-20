package rengar.parser.ast;

import com.alibaba.fastjson.JSONObject;

import rengar.parser.charutil.*;

public class SingleCharExpr extends CharExpr {
    private int c;
    public SingleCharExpr(int c) {
        this.c = c;
    }
    public int getChar() {
        return c;
    }
    @Override
    public SingleCharExpr copy() {
        SingleCharExpr singleCharExpr = new SingleCharExpr(c);
        singleCharExpr.setExprId(getExprId());
        singleCharExpr.setStr(string);
        return singleCharExpr;
    }

    public JSONObject genJsonExpression() {
        JSONObject json = new JSONObject();
        json.put("id", getExprId());
        json.put("type", "Char");
        json.put("char", CharUtil.toRegexString(c));
        return json;
    }

    @Override
    public String genString() {
        return CharUtil.toRegexString(c);
    }
}